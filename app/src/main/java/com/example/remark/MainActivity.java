package com.example.remark;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.remark.api.Bluetooth;
import com.example.remark.api.DeckAPIClient;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.model.UserManager;
import com.example.remark.ui.MainFragment;

import com.example.remark.R;

public class MainActivity extends AppCompatActivity {
    private static String sBluetoothAddress;

    public static String getBluetoothAddress() {
        return sBluetoothAddress;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Own Bluetooth Address
        sBluetoothAddress = android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");

        // Singleton
        DeckAPIClient.newInstance(this);
        PlayerAPIClient.getInstance();
        UserManager.newInstance(this);

        // Configure Bluetooth
        Bluetooth bluetooth = Bluetooth.getInstance();
        if (!bluetooth.isAvailable())
            new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.bluetooth_is_not_available)).setMessage(getResources().getString(R.string.bluetooth_requested)).setPositiveButton("Ok", null).show();
        if (!bluetooth.isEnabled())
            bluetooth.enableBluetooth(this);

        if (UserManager.getInstance().getName().equals("")) {
            Bundle b = new Bundle();
            b.putBoolean(AccountActivity.REGISTER_PARAM, true);
            Intent intent = new Intent(this, AccountActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_vg_fragment, MainFragment.newInstance(), "main")
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        String tag = getSupportFragmentManager().findFragmentById(R.id.activity_main_vg_fragment).getTag();
        if (tag != null && tag.equals("main")) {
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_vg_fragment, new MainFragment(), "main")
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, new MainFragment(), "main")
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Bluetooth.getInstance().closeClient();
        Bluetooth.getInstance().closeServer();
    }
}
