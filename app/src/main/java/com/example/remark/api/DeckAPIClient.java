package com.example.remark.api;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

import com.example.remark.model.Deck;


public class DeckAPIClient {
    private static final String FILENAME = "question_answer.json";
    private static DeckAPIClient sSharedInstance;
    private ArrayList<Deck> mDeck;
    private HashMap<String, ArrayList<Deck>> decks;
    private String currentDeck;
    private int mCounter;

    private DeckAPIClient(@NonNull Context context) {
        decks = new HashMap<>();
        populateQuestions(context);
    }

    // Singleton New Instance
    public static synchronized DeckAPIClient newInstance(@NonNull Context context) {
        if (sSharedInstance == null)
            sSharedInstance = new DeckAPIClient(context);

        return sSharedInstance;
    }

    // Singleton Get Instance
    public static synchronized DeckAPIClient getInstance() {
        return sSharedInstance;
    }

    private void populateQuestions(Context context) {
        try {
            JSONArray root = new JSONArray(loadJSONFromAsset(context));

            for (int i = 0; i < root.length(); i++) {
                JSONObject deck = root.getJSONObject(i);
                String title = deck.getString("title");
                JSONArray data = deck.getJSONArray("data");

                ArrayList<Deck> list = new ArrayList<>();
                decks.put(title, list);

                for (int j = 0; j < data.length(); j++) {
                    JSONObject questionAnswerSet = data.getJSONObject(j);
                    String question = questionAnswerSet.getString("question");
                    String answer = questionAnswerSet.getString("answer");
                    list.add(new Deck(question, answer));
                }
            }
        } catch (JSONException ignore) {
        }
    }

    private String loadJSONFromAsset(Context context) {
        try (Scanner in = new Scanner(context.getAssets().open(FILENAME))) {
            return in.useDelimiter("\\A").next();
        } catch (IOException ignore) {
        }

        return null;
    }

    public Deck getDeck() {
        if (currentDeck.equals("Customize Deck"))
            return null;
        else
            return mDeck.get(mCounter++);
    }

    public void setCurrentDeck(String currentDeck) {
        this.currentDeck = currentDeck;
        mCounter = 0;
        if (!currentDeck.equals("Customize Deck")) {
            mDeck = decks.get(currentDeck);
            Collections.shuffle(mDeck, new Random(System.nanoTime()));
        }
    }

    public boolean isRemaining() {
        return currentDeck.equals("Customize Deck") || mDeck.size() != mCounter;
    }
}
