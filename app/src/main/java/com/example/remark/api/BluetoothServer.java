package com.example.remark.api;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.remark.MainActivity;
import com.example.remark.model.Player;

public class BluetoothServer extends Thread {
    private BluetoothServerSocket server;
    private CopyOnWriteArrayList<Client> clients;

    BluetoothServer() {
        clients = new CopyOnWriteArrayList<>();
    }

    @Override
    public void run() {
        //looping to accept client
        while (true) {
            try (BluetoothServerSocket server = Bluetooth.getInstance().getBluetoothAdapter().listenUsingRfcommWithServiceRecord("shitake", UUID.fromString(Bluetooth.UUID_VALUE))) {
                this.server = server;

                BluetoothSocket client;
                while ((client = server.accept()) == null)
                    ;
                clients.add(new Client(client));
            } catch (IOException e) {
                close();
            }
        }
    }

    private void parseMessage(String message, Client thisClient) {
        try {
            JSONObject json = new JSONObject(message);
            switch (json.getInt("dataType")) {
                case Bluetooth.DATA_TYPE_PLAYER_INFORMATION:
                    playerInformationProcess(json.getJSONObject("data"), thisClient);
                    break;
                case Bluetooth.DATA_TYPE_ANSWER:
                    answer(json.getJSONObject("data"), thisClient);
                    break;
                case Bluetooth.DATA_TYPE_SELECTED_ANSWER:
                    selectedAnswer(json.getJSONObject("data"), thisClient);
                    break;
                case Bluetooth.DATA_TYPE_VOTE:
                    vote(json.getJSONObject("data"), thisClient);
                    break;
            }
        } catch (JSONException ignore) {
        }
    }

    private void playerInformationProcess(JSONObject data, Client thisClient) throws JSONException {
        //get new player info
        String address = data.getString("address");
        String name = data.getString("name");
        byte[] byteArray = Base64.decode(data.getString("picture"), Base64.DEFAULT);
        Bitmap picture = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        //create new Player
        Player player = new Player(address, name, picture);
        PlayerAPIClient.getInstance().addPlayer(player);

        //send other players information to new player
        for (Client client : clients)
            if (client != thisClient)
                sendTo(thisClient, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_PLAYER_INFORMATION, PlayerAPIClient.getInstance().get(client.getAddress()).toJSON()));
        sendTo(thisClient, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_PLAYER_INFORMATION, PlayerAPIClient.getInstance().get(MainActivity.getBluetoothAddress()).toJSON()));

        //send new player information to other players
        sendExclude(thisClient, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_PLAYER_INFORMATION, player.toJSON()));
    }

    private void answer(JSONObject data, Client thisClient) throws JSONException {
        //get answer
        String address = data.getString("address");
        String answer = data.getString("answer");

        //set answer
        AnswerAPIClient.getInstance().addAnswer(address, answer);

        //send this player answer to other players
        sendExclude(thisClient, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_ANSWER, data));
    }

    private void selectedAnswer(JSONObject data, Client thisClient) throws JSONException {
        //get address
        String address = data.getString("address");
        String answer = data.getString("answer");

        //set score
        //if (answer.equals("correct"))
            PlayerAPIClient.getInstance().get(address).addingScore += GameAPIClient.CORRECT_ANSWER_SCORE;
        //else
          //  PlayerAPIClient.getInstance().get(answer).addingScore += GameAPIClient.CORRECT_ANSWER_SCORE;
    }

    private void vote(JSONObject data, Client thisClient) throws JSONException {
        //get address
        String address = data.getString("address");
        String vote = data.getString("vote");

        //set score
        PlayerAPIClient.getInstance().get(vote).addingScore += GameAPIClient.VOTE_SCORE;

        //send this player vote to other players
        sendExclude(thisClient, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_VOTE, data));

        //add address
        ResultAPIClient.getInstance().addAddress(address);
    }

    public void sendTo(Client thisClient, String s) {
        thisClient.write(s);
    }

    public void sendExclude(Client thisClient, String s) {
        for (Client client : clients) {
            if (client != thisClient)
                client.write(s);
        }
    }

    public void close() {
        try {
            server.close();
        } catch (IOException ignore) {
        }

        for (Client client : clients) {
            client.close();
        }
    }

    private class Client {
        private BluetoothSocket client;
        private BufferedReader in;
        private OutputStreamWriter out;
        private String address;

        Client(final BluetoothSocket client) {
            this.client = client;
            address = client.getRemoteDevice().getAddress();

            try {
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out = new OutputStreamWriter(client.getOutputStream());
            } catch (IOException e) {
                close();
            }

            read();
        }

        private void read() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String message = in.readLine();
                            parseMessage(message, Client.this);
                        }
                    } catch (IOException e) {
                        close();
                    }
                }
            }).start();
        }

        private void write(final String s) {
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

        private void close() {
            try {
                clients.remove(this);
                PlayerAPIClient.getInstance().removePlayer(client.getRemoteDevice().getAddress());
                client.close();
            } catch (IOException ignore) {
            }
        }

        private String getAddress() {
            return address;
        }
    }
}
