package com.example.recapai;

import java.net.URL;
import java.util.List;
import com.google.gson.*;

public class Quiz {

    private String quiz_title;
    private List<Question> questions;
    //private String userPrompt;
    //private List<AnswerChoice> answerChoices;

    public Quiz(String quiz_title, List<Question> questions) {
        this.quiz_title = quiz_title;
        this.questions = questions;
    }

    public String getQuizTitle() {
        return quiz_title;
    }

    public void setQuizTitle(String quiz_title) {
        this.quiz_title = quiz_title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Writes the current quiz to Quiz History JSON File
     */
    public void saveQuiz() {
        Gson json = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override //overriding object's default toString method
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String toReturn = gson.toJson(this);
        return toReturn;
    }



}
