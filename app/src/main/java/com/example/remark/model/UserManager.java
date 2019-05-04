package com.example.remark.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Object for managing current user profile<br>
 * Saves information with SharedPreferences
 */

public class UserManager {

    private static UserManager userManager;

    private Activity activity;
    private User user;

    // preferences object
    private SharedPreferences.Editor editor;

    // static fields for saving a fetching
    private static final String USER_MANAGER_NAME = "UserAccount";
    private static final String USER_NAME = "UserName";
    private static final String USER_PROFILE_PIC = "UserTaunt";

    // Singleton New Instance
    public static synchronized UserManager newInstance(Activity activity) {
        if (userManager == null) {
            userManager = new UserManager(activity);
        }

        return userManager;
    }

    // Singleton Get Instance
    public static synchronized UserManager getInstance() {
        return userManager;
    }

    private UserManager(Activity activity) {
        this.activity = activity;
        restore();
    }

    private void restore() {
        SharedPreferences preferences = activity.getSharedPreferences(USER_MANAGER_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();

        String name = preferences.getString(USER_NAME, "");
        String taunt = preferences.getString(USER_PROFILE_PIC, "");

        user = new User(name, taunt);
    }

    public void setName(String name) {
        user.setmName(name);
        editor.putString(USER_NAME, name);
        editor.apply();
    }

    public void setProfilePicture(String profilePicture) {
        user.setmProfilePicture(profilePicture);
        editor.putString(USER_PROFILE_PIC, profilePicture);
        editor.apply();
    }

    public String getName() {
        return user.getmName();
    }

    public String getProfilePicture() {
        return user.getmProfilePicture();
    }
}
