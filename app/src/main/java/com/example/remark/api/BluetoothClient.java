package com.example.remark.api;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

import com.example.remark.GameActivity;
import com.example.remark.MainActivity;
import com.example.remark.model.Player;
import com.example.remark.ui.game.LeaderboardFragment;
import com.example.remark.ui.game.PendingFragment;
import com.example.remark.ui.game.QuestionFragment;
import com.example.remark.ui.lobby.WaitingFragment;

public class BluetoothClient extends Thread {
    private BluetoothSocket server;
    private BufferedReader in;
    private OutputStreamWriter out;

    BluetoothClient(String address) {
        try {
            BluetoothDevice device = Bluetooth.getInstance().getBluetoothAdapter().getRemoteDevice(address);
            server = device.createRfcommSocketToServiceRecord(UUID.fromString(Bluetooth.UUID_VALUE));
        } catch (IOException e) {
            close();
        }
    }

    @Override
    public void run() {
        try {
            server.connect();

            //stream
            in = new BufferedReader(new InputStreamReader(server.getInputStream()));
            out = new OutputStreamWriter(server.getOutputStream());

            //send self player information to server
            send(Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_PLAYER_INFORMATION, PlayerAPIClient.getInstance().get(MainActivity.getBluetoothAddress()).toJSON()));
        } catch (IOException | JSONException e) {
            close();
        }

        read();
    }

    private void parseMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            switch (json.getInt("dataType")) {
                case Bluetooth.DATA_TYPE_PLAYER_INFORMATION:
                    playerInformationProcess(json.getJSONObject("data"));
                    break;
                case Bluetooth.DATA_TYPE_START_GAME:
                    startGame();
                    break;
                case Bluetooth.DATA_TYPE_QUESTION:
                    question(json.getJSONObject("data"));
                    break;
                case Bluetooth.DATA_TYPE_ANSWER:
                    answer(json.getJSONObject("data"));
                    break;
                case Bluetooth.DATA_TYPE_START_SELECT_ANSWER:
                    startSelectAnswer();
                    break;
                case Bluetooth.DATA_TYPE_VOTE:
                    vote(json.getJSONObject("data"));
                    break;
                case Bluetooth.DATA_TYPE_START_LEADERBOARD:
                    startLeaderboard(json.getJSONObject("data"));
                    break;
                case Bluetooth.DATA_TYPE_START_NEXT_ROUND:
                    nextRound();
                    break;
                case Bluetooth.DATA_TYPE_END_GAME:
                    endGame();
                    break;
            }
        } catch (JSONException ignore) {
        }
    }

    private void playerInformationProcess(JSONObject data) throws JSONException {
        String address = data.getString("address");
        String name = data.getString("name");
        byte[] byteArray = Base64.decode(data.getString("picture"), Base64.DEFAULT);
        Bitmap picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        PlayerAPIClient.getInstance().addPlayer(new Player(address, name, picture));
    }

    private void startGame() {
        WaitingFragment.getFragmentTask().startGame();
    }

    private void question(JSONObject data) throws JSONException {
        //get question
        String question = data.getString("question");

        //wait fragment finish init
        while (QuestionFragment.getFragmentTask() == null)
            ;

        //set question
        QuestionFragment.getFragmentTask().updateQuestion(question);
        AnswerAPIClient.getInstance().getAnswers().put("correct", data.getString("answer"));
    }

    private void answer(JSONObject data) throws JSONException {
        //get answer
        String address = data.getString("address");
        String answer = data.getString("answer");

        //set answer
        AnswerAPIClient.getInstance().addAnswer(address, answer);
    }

    private void startSelectAnswer() {
        PendingFragment.getFragmentTask().showNextFragment();
    }

    private void vote(JSONObject data) throws JSONException {
        //get address
        String address = data.getString("address");

        //add address
        ResultAPIClient.getInstance().addAddress(address);
    }

    private void startLeaderboard(JSONObject data) throws JSONException {
        //get adding score
        JSONArray addingScore = data.getJSONArray("addingScore");
        for (int i = 0; i < addingScore.length(); i++) {
            JSONObject player = (JSONObject)addingScore.get(i);
            PlayerAPIClient.getInstance().get(player.getString("address")).addingScore += player.getInt("addingScore");
        }

        PendingFragment.getFragmentTask().showNextFragment();
    }

    private void endGame() {
        GameActivity.getActivityTask().endGame();
    }

    private void nextRound() {
        LeaderboardFragment.getFragmentTask().nextRound();
    }

    private void read() {
        try {
            while (true) {
                String message = in.readLine();
                parseMessage(message);
            }
        } catch (IOException e) {
            close();
        }
    }

    public void send(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    out.write(s + "\n");
                    out.flush();
                } catch (IOException e) {
                    close();
                }
            }
        }).start();
    }

    public void close() {
        try {
            server.close();
        } catch (IOException ignore) {
        }
    }
}
