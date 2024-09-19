package com.example.recapai;

import java.util.List;

public class UserAnswer {
    private Question question;
    private List<String> userAnswers;
    private boolean correct;

    public UserAnswer(Question question, List<String> userAnswers, boolean correct) {
        this.question = question;
        this.userAnswers = userAnswers;
        this.correct = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
