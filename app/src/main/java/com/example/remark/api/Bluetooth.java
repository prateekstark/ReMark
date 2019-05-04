package com.example.remark.api;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class Bluetooth {
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    public static final int DATA_TYPE_PLAYER_INFORMATION = 0;
    public static final int DATA_TYPE_START_GAME = 1;
    public static final int DATA_TYPE_QUESTION = 2;
    public static final int DATA_TYPE_ANSWER = 3;
    public static final int DATA_TYPE_START_SELECT_ANSWER = 4;
    public static final int DATA_TYPE_SELECTED_ANSWER = 5;
    public static final int DATA_TYPE_VOTE = 6;
    public static final int DATA_TYPE_START_LEADERBOARD = 7;
    public static final int DATA_TYPE_START_NEXT_ROUND = 8;
    public static final int DATA_TYPE_END_GAME = 9;
    public static final String UUID_VALUE = "859f02dd-e6f5-4d56-826c-40f1e1bceea8";
    private static Bluetooth bluetooth;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothClient client;
    private BluetoothServer server;

    private Bluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static Bluetooth getInstance() {
        if (bluetooth == null)
            bluetooth = new Bluetooth();

        return bluetooth;
    }

    public boolean isAvailable() {
        return bluetoothAdapter != null;
    }

    public boolean isEnabled() {
        return isAvailable() && bluetoothAdapter.isEnabled();
    }

    public void enableBluetooth(Activity activity) {
        if (isAvailable())
            activity.startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BLUETOOTH);
    }

    public void setDiscoverable(Activity activity) {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            activity.startActivity(intent);
        }
    }

    public void startDiscovery() {
        bluetoothAdapter.startDiscovery();
    }

    public void stopDiscovery() {
        bluetoothAdapter.cancelDiscovery();
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BluetoothServer newBluetoothServer() {
        server = new BluetoothServer();
        server.start();

        return server;
    }

    public BluetoothClient newBluetoothClient(String address) {
        client = new BluetoothClient(address);
        client.start();

        return client;
    }

    public BluetoothClient getClient() {
        return client;
    }

    public BluetoothServer getServer() {
        return server;
    }

    public void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    public void closeServer() {
        if (server != null) {
            server.close();
        }
    }

    public static String wrapMessage(int type, JSONObject data) throws JSONException {
        JSONObject json = new JSONObject();

        json.put("dataType", type);
        json.put("data", data);

        return json.toString();
    }
}
