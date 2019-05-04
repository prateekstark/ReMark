package com.example.remark.model;



public class User {
    private String mName;
    private String mProfilePicture;


    public User(String mName, String mProfilePicture) {
        this.mName = mName;
        this.mProfilePicture = mProfilePicture;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProfilePicture() {
        return mProfilePicture;
    }

    public void setmProfilePicture(String mProfilePicture) {
        this.mProfilePicture = mProfilePicture;
    }
}
