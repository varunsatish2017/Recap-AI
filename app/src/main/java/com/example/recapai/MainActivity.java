package com.example.recapai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.util.LocalePreferences;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;


public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private final String API_KEY = BuildConfig.API_KEY;
    private Button promptRequest;
    private String prompt;
    private GenerativeModel model;
    private GenerativeModelFutures modelFutures;
    private ProgressBar progressBar;
    private GeminiAPI geminiAPI;
    private Quiz quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Intro stuff
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //AI stuff
        model = new GenerativeModel("gemini-1.5-pro-001", API_KEY);
        modelFutures = GenerativeModelFutures.from(model);

        textView = findViewById(R.id.helloTextField);
        promptRequest = findViewById(R.id.genButton);
        promptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPromptDialog();
            }
        });

        if (savedInstanceState != null) {
            textView.setText(savedInstanceState.getString("JSON"));
        }

        progressBar = findViewById(R.id.progBar);
        progressBar.setVisibility(View.GONE);

        //Initialize Gemini API - sample parameters for now
        geminiAPI = GeminiAPI.initialize(API_KEY, model.getModelName(), "English", 5);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("JSON", textView.getText().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
//        TextView json = findViewById(R.id.Question);
//        json.setText(geminiAPI.getComputerResponse());
    }

    private String loadAPIkey() {
        try {
            Properties props = new Properties();
            InputStream inpStream = getAssets().open("local.properties");
            props.load(inpStream);
            return props.getProperty("API_KEY");
        }
        catch (IOException e) {
            return "null";
        }

    }

    /**
     * Sets up the dialog box asking the user for text input
     */
    private void requestPromptDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enter prompt to Gemini: ");
        dialog.setCancelable(false);
        EditText input = new EditText(this);
        input.setHint("What question do you want to ask Gemini?");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialog.setView(input);
        dialog.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                prompt = input.getText().toString();
                geminiCall();
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * Makes a call to Gemini with the String "prompt" using GeminiAPI
     */
    private void geminiCall() {
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        progressBar.setVisibility(View.VISIBLE);
        progressBar.setContentDescription("Generating Quiz");

        geminiAPI.sendMessage(prompt, true, new GeminiAPI.GeminiCallback() {
            @Override
            public void onResponse(String response) {
                try {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonElement element = JsonParser.parseString(response);
                    progressBar.setVisibility(View.GONE);
                    quiz = gson.fromJson(response, Quiz.class);
                    textView.setText(gson.toJson(element));
                    Log.i("On Gemini Response", "onResponse: " + response);
                } catch (JsonSyntaxException e) {
                    Log.d("GeminiCallback onResponse", "Invalid Json file");
                    e.printStackTrace();
                    //Display snackbar error message
                }
                //unpackJSON(geminiAPI.getComputerResponse());
                //runQuiz();

                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                intent.putExtra("quizData", response);

                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
//                Snackbar.make(getApplicationContext(), getCurrentFocus(), "Unable to generate quiz at this time, please try again", 5)
//                        .show();
                Toast.makeText(getApplicationContext(), "Unable to generate quiz, please try again", Toast.LENGTH_LONG)
                        .show();
                Log.d("Gemini Response Error", "Failed to generate response");
            }
        });
    }

//    public void unpackJSON(String json) {
//
//    }
//
//    public void runQuiz() {
//
//    }
}