package com.example.recapai;

public class AnswerChoice {

    private boolean correct;
    private String text;

    public AnswerChoice(boolean correct, String answerText) {
        this.correct = correct;
        this.text = answerText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getAnswerText() {
        return text;
    }

    public void setAnswerText(String answerText) {
        this.text = answerText;
    }
}
