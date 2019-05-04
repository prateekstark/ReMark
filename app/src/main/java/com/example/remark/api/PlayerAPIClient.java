package com.example.remark.api;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.example.remark.MainActivity;
import com.example.remark.R;
import com.example.remark.model.Player;
import com.example.remark.model.UserManager;
import com.example.remark.ui.lobby.WaitingFragment;

public class PlayerAPIClient {
    private static PlayerAPIClient playerAPIClient;
    private ArrayList<String> playersInOrder;
    private HashMap<String, Player> players;
    private Handler handler;

    private PlayerAPIClient() {
        playersInOrder = new ArrayList<>();
        players = new HashMap<>();
        handler = new Handler();
    }

    public static PlayerAPIClient getInstance() {
        if (playerAPIClient == null)
            playerAPIClient = new PlayerAPIClient();

        return playerAPIClient;
    }

    public void addSelf(Resources resources) {
        String address = MainActivity.getBluetoothAddress();
        String name = UserManager.getInstance().getName();
        Bitmap picture;
        String picturePath = UserManager.getInstance().getProfilePicture();
        if (!picturePath.equals(""))
            picture = BitmapFactory.decodeFile(picturePath);
        else
            picture = BitmapFactory.decodeResource(resources, R.drawable.no_picture);

        Player player = new Player(address, name, picture);
        players.put(address, player);
        if (!playersInOrder.contains(address))
            playersInOrder.add(address);
    }

    public void addPlayer(Player player) {
        players.put(player.address, player);
        if (!playersInOrder.contains(player.address))
            playersInOrder.add(player.address);

        handler.post(new Runnable() {
            @Override
            public void run() {
                WaitingFragment.getAdapter().notifyItemInserted(players.size() - 1);
            }
        });
    }

    public void removePlayer(String address) {
        final int index = playersInOrder.indexOf(address);
        players.remove(address);
        playersInOrder.remove(address);

        handler.post(new Runnable() {
            @Override
            public void run() {
                WaitingFragment.getAdapter().notifyItemRemoved(index);
            }
        });
    }

    public void clearPlayer() {
        players.clear();
        playersInOrder.clear();
    }

    public void sort() {
        Collections.sort(playersInOrder, new Comparator<String>() {
            @Override
            public int compare(String arg1, String arg2) {
                return players.get(arg2).score + players.get(arg2).addingScore - players.get(arg1).score - players.get(arg1).addingScore;
            }
        });
    }

    public HashMap<String, Player> getPlayers() {
        return players;
    }

    public Player get(int position) {
        return players.get(playersInOrder.get(position));
    }

    public Player get(String address) {
        return players.get(address);
    }
}
