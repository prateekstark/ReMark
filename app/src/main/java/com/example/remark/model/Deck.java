package com.example.remark.model;

public class Deck {
    private String question;
    private String answer;

    public Deck(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
