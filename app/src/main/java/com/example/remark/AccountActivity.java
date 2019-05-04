package com.example.remark;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.remark.ui.account.ProfileFragment;
import com.example.remark.ui.account.RegisterFragment;

public class AccountActivity extends AppCompatActivity {

    public static final String REGISTER_PARAM = "isRegistering";

    private ProfileFragment profileFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_generic);

        Bundle b = getIntent().getExtras();
        boolean isRegistering = false;
        if (b != null) {
            isRegistering = b.getBoolean(REGISTER_PARAM);
        }

        if (isRegistering) {
            goToRegisterFragment();
        } else {
            goToProfileFragment();
        }
    }

    private void goToRegisterFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, new RegisterFragment())
                .commit();
    }
    
    @Override
    public void onBackPressed() {
        String tag = getSupportFragmentManager().findFragmentById(R.id.activity_main_vg_fragment).getTag();
        String tag2 = tag != null ? tag : "";

        if (tag2.equals("taunt")) {
            goToProfileFragment();
        } else if (tag2.equals("profile")) {
            if (profileFragment == null || profileFragment.canExit()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void goToProfileFragment() {
        profileFragment = new ProfileFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, profileFragment, "profile")
                .commit();
    }
}
