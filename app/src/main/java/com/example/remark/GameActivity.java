package com.example.remark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.remark.api.Bluetooth;
import com.example.remark.ui.game.QuestionFragment;


public class GameActivity extends AppCompatActivity {
    private static ActivityTask activityTask;

    public static ActivityTask getActivityTask() {
        return activityTask;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generic);

        activityTask = new ActivityTask(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, QuestionFragment.newInstance())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
                    .setTitle(getResources().getString(R.string.quit_game))
                    .setMessage(getResources().getString(R.string.are_you_sure_quit_this_game))
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            superOnBackPressed();
                        }
                    })
                    .setPositiveButton("No", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.quit_game))
                    .setMessage(getResources().getString(R.string.are_you_sure_quit_this_game))
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            superOnBackPressed();
                        }
                    })
                    .setPositiveButton("No", null)
                    .show();
        }
    }

    private void superOnBackPressed() {
        if(Bluetooth.getInstance().getServer() != null) {
            try{
                Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_END_GAME, new JSONObject()));

            } catch (JSONException ignore) {

            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Bluetooth.getInstance().closeClient();
        Bluetooth.getInstance().closeServer();
    }

    private void endGame() {
        finish();
    }

    public static class ActivityTask {
        private GameActivity activity;

        ActivityTask(Activity activity) {
            this.activity = (GameActivity)activity;
        }

        public void endGame() {
            activity.endGame();
        }
    }
}
