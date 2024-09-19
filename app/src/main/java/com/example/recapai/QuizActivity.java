package com.example.recapai;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.recapai.databinding.ActivityQuizBinding;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private int currentQuestionIndex = 0;
    private List<Question> questionsArray;
    private Quiz quiz;
    private TextView questionText;
    private RadioGroup radioGroup;
    private Button nextButton;
    private int correctAnswers = 0;
    private int answeredQuestions = 0;
    private List<UserAnswer> userAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("QuizActivity", "RadioGroup is " +
                (findViewById(R.id.radioGroup) == null ? "null":"not null"));

        nextButton = findViewById(R.id.nextButton);
        questionText = findViewById(R.id.questionText);
        radioGroup = findViewById(R.id.radioGroup);

        String quizData = getIntent().getStringExtra("quizData");
        try {
            quiz = JSONDecoder.decodeJSONToQuiz(quizData);
        } catch (JsonSyntaxException e) {
            Log.d("QuizActivity", "onCreate: Failed to parse quiz");
            e.printStackTrace();
        }

        questionsArray = quiz.getQuestions();
        loadQuestion(currentQuestionIndex);

        nextButton.setOnClickListener(view -> handleNextButtonClick());
        radioGroup.setVisibility(View.VISIBLE);
    }

    private void loadQuestion(int index) {
        try {
            Question questionObject = questionsArray.get(index);
            String question = questionObject.getQuestion();
            List<AnswerChoice> answerChoices = questionObject.getOptions();
            Log.d("QuizActivity", "Answer choice list: " + (answerChoices == null ? "null":"not null"));

            questionText.setText(question);
            radioGroup.removeAllViews();

            for (AnswerChoice choice : answerChoices) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(choice.getAnswerText());
                radioButton.setId(View.generateViewId());
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0 ,0, 0, 16);
                radioButton.setLayoutParams(params);
                radioGroup.addView(radioButton);
            }


            nextButton.setText("Submit");
            Log.i("QuizActivity", "Number of Ans choices added: " + radioGroup.getChildCount());
        } catch (Exception e) {
            Log.e("QuizActivity", "Unable to load question: ", e);
        }
    }

    private void handleNextButtonClick() {
        if ("Submit".equals(nextButton.getText())) {
            checkAnswer();
            nextButton.setText("Next Question");
            if (currentQuestionIndex == questionsArray.size() - 1) {
                nextButton.setText("Finish Quiz");
            }
        } else if ("Next Question".equals(nextButton.getText())) {
            currentQuestionIndex++;
            loadQuestion(currentQuestionIndex);
        } else if ("Finish Quiz".equals(nextButton.getText())) {
            finishQuiz();
        }
    }

    private void checkAnswer() {
        RadioButton selectedAnswer = findViewById(radioGroup.getCheckedRadioButtonId());
        if (selectedAnswer == null) return;

        Question currentQuestion = questionsArray.get(currentQuestionIndex);
        boolean isCorrect = false;
        List<String> userSelectedAnswers = new ArrayList<>();
        userSelectedAnswers.add(selectedAnswer.getText().toString());

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton button = (RadioButton) radioGroup.getChildAt(i);
            AnswerChoice choice = currentQuestion.getOptions().get(i);

            if (choice.isCorrect()) {
                button.setButtonTintList(ColorStateList.valueOf(Color.GREEN));
                if (button == selectedAnswer) {
                    isCorrect = true;
                    correctAnswers++;
                }
            } else {
                button.setButtonTintList(ColorStateList.valueOf(Color.RED));
            }
        }

        answeredQuestions++;
        userAnswers.add(new UserAnswer(currentQuestion, userSelectedAnswers, isCorrect));

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void finishQuiz() {
        // Here you would typically start a new activity to show results
        // For now, we'll just log the results
        Log.d("QuizActivity", "Quiz Finished!");
        Log.d("QuizActivity", "Correct Answers: " + correctAnswers + "/" + answeredQuestions);

        startActivity(new Intent(QuizActivity.this, MainActivity.class));
        // You can add code here to show a results screen or dialog
    }
}