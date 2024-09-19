package com.example.recapai;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private String type;
    private String question;
    private List<AnswerChoice> options;

    public Question(String type, String question, List<AnswerChoice> options) {
        this.type = type;
        this.question = question;
        this.options = options;
    }

    public String toString() {
        return type + "\n" + question + "\n" + options.toString() + "\n";
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<AnswerChoice> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<AnswerChoice> options) {
        this.options = options;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
