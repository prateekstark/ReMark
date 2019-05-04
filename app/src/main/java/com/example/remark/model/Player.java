package com.example.remark.model;

import android.graphics.Bitmap;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Player {
    public String address;
    public String name;
    public Bitmap picture;
    public int score;
    public int addingScore;

    public Player(String address, String name, Bitmap picture) {
        this.address = address;
        this.name = name;
        this.picture = picture;
    }

    private String bitmapToString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        picture.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] byteArray = out.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("address", address);
            json.put("name", name);
            json.put("picture", bitmapToString());
        } catch (JSONException ignore) {
        }

        return json;
    }
}
