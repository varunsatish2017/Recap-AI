package com.example.recapai;

import android.graphics.ColorSpace;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.ai.client.generativeai.Chat;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.*;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.type.BlockThreshold;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.FunctionCallingConfig;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.HarmCategory;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.SafetySetting;
import com.google.ai.client.generativeai.type.Schema;
import com.google.ai.client.generativeai.type.Tool;
import com.google.ai.client.generativeai.type.ToolConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.reactivestreams.Publisher;

import java.time.Duration;
import java.util.*;
import java.io.File;
import java.util.concurrent.Executor;

import org.json.JSONObject;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

public class GeminiAPI {

    private static GeminiAPI sharedInstance;
    public GenerativeModel model;
    private Chat chat;
    private ChatFutures chatFutures;
    private String computerResponse = "";

    /*
     *  Values intialized via Constructor
     */
    private String key;
    private String modelName;
    private int numberOfQuestions;
    private String selectedLanguage;

    /**
     * Initializes a model with a given API Key, Model name (<b>Gemini-1.5-pro ONLY</b> in our case),
     * a given language (for now, <b>ONLY English</b>), and a given number of questions (5-15)
     *
     * @param key               - API Key
     * @param modelName         - Model name (for now, Gemini-1.5-pro ONLY)
     * @param selectedLanguage  - For now, only ENGLISH
     * @param numberOfQuestions - A number between 5 and 15, inclusive
     */
    private GeminiAPI(String key, String modelName, String selectedLanguage, int numberOfQuestions) {
        this.key = key;
        this.modelName = modelName;
        this.numberOfQuestions = numberOfQuestions;
        this.selectedLanguage = selectedLanguage;
        initializeModel(modelName, selectedLanguage);
    }

    /**
     * Calls the private constructor for GeminiAPI class
     *
     * @param key
     * @param modelName
     * @param selectedLanguage
     * @param numberOfQuestions
     */
    public static GeminiAPI initialize(String key, String modelName, String selectedLanguage, int numberOfQuestions) {
        sharedInstance = new GeminiAPI(key, modelName, selectedLanguage, numberOfQuestions);
        return sharedInstance;
    }


    public String getComputerResponse() {
        return computerResponse;
    }

    public void clearChat() {
        if (chat != null) {
            chat.getHistory().clear();
        }
    }

    private void initializeModel(String modelName, String selectedLanguage) {

        GenerationConfig.Builder builder = new GenerationConfig.Builder();
        builder.responseMimeType = "application/json";
        GenerationConfig config = builder.build();

        List<String> stopSequences = List.of("}");

        List<SafetySetting> safetySettings = List.of(); //None for now — will add later
        RequestOptions options = new RequestOptions();
        List<Tool> tools = List.of(new Tool()); //not using for now - explore later
        ToolConfig toolConfig = new ToolConfig(new FunctionCallingConfig(FunctionCallingConfig.Mode.AUTO));
        Content systemInstruction = new Content.Builder()
                .addText("You are my teacher. Determine the subject of the notes and provide a json with possible questions relating to the notes BASED ON THE EXAMPLE JSON I GIVE YOU. You may be asked to provide an explanation for a question or be asked to generate an entire quiz (more likely). For Multiple choice questions, you can mark as many answers as true. Also, make sure to use the exact same property names, but just change the contents/values of each property based on the notes provided. Also make sure that all the information is true and taken purely from the notes. Use GitHub Flavored Markdown (no HTML markdown or LateX is supported) whenever possible in the questions and answers, but replace all occurences of ``` with <`>. The user would like the quiz and future chat messages to be generated in \" + (selectedLanguage != null ? selectedLanguage : \"English\") + \".\"")
                .build();
        this.model = new GenerativeModel(modelName, key, config, safetySettings, options, tools, toolConfig, systemInstruction);
//                .setName(modelName)
//                .setApiKey(key)
//                .setGenerationConfig(config)
//                .setSystemInstruction("You are my teacher. Determine the subject of the notes and provide a json with possible questions relating to the notes BASED ON THE EXAMPLE JSON I GIVE YOU. You may be asked to provide an explanation for a question or be asked to generate an entire quiz (more likely). For Multiple choice questions, you can mark as many answers as true. Also, make sure to use the exact same property names, but just change the contents/values of each property based on the notes provided. Also make sure that all the information is true and taken purely from the notes. Use GitHub Flavored Markdown (no HTML markdown or LateX is supported) whenever possible in the questions and answers, but replace all occurences of ``` with <`>. The user would like the quiz and future chat messages to be generated in " + (selectedLanguage != null ? selectedLanguage : "English") + ".")
//                .build();

        if (this.model != null) {
            //Creates the chat with no existing history (hence the blank list)
            this.chat = this.model.startChat(List.of());
            this.chatFutures = ChatFutures.from(chat);
        }
    }

    public void sendMessage (String userInput, boolean generateQuiz, GeminiCallback completion) {
        if (chat == null) {
            return; //exit
        }

        String quizPrompt = generateQuiz ?
                "\n\nUse this JSON schema to generate " + (numberOfQuestions == 0 ? 5 : numberOfQuestions) + " questions, and make sure to randomize the order of the options such that the correct answer is not always in the same place:\n\n" +
                        "{" +
                        "  \"quiz_title\": \"Sample Quiz\"," +
                        "  \"questions\": [" +
                        "    {" +
                        "      \"type\": \"multiple_choice\"," +
                        "      \"question\": \"What is the capital of France?\", " +
                        "      \"options\": [" +
                        "        {\"text\": \"Paris\", \"correct\": true}," +
                        "        {\"text\": \"London\",  \"correct\": false}," +
                        "        {\"text\": \"Berlin\", \"correct\": false}," +
                        "        {\"text\": \"Rome\", \"correct\": false}" +
                        "      ]" +
                        "    }," +
                        "    {" +
                        "      \"type\": \"multiple_choice\"," +
                        "      \"question\": \"Which of the following are gas giants in our solar system?\", " +
                        "      \"options\": [" +
                        "        {\"text\": \"Earth\", \"correct\": false}," +
                        "        {\"text\": \"Saturn\", \"correct\": true}," +
                        "        {\"text\": \"Jupiter\", \"correct\": true}," +
                        "        {\"text\": \"Uranus\", \"correct\": false}" +
                        "      ]" +
                        "    }," +
                        "    {" +
                        "      \"type\": \"multiple_choice\"," +
                        "      \"question\": \"Which of the following is a color?\", " +
                        "      \"options\": [" +
                        "        {\"text\": \"Red\", \"correct\": false}," +
                        "        {\"text\": \"Blue\", \"correct\": false}," +
                        "        {\"text\": \"Yellow\", \"correct\": false}," +
                        "        {\"text\": \"All of the above\", \"correct\": true}" +
                        "      ]" +
                        "    }," +
                        "  ]" +
                        "}" :
                "Please follow the example JSON EXACTLY";
        new Thread(() -> {
            try {
                Content msgContent = new Content.Builder()
                        .addText("Notes:" + " ")
                        .addText(userInput)
                        .addText(quizPrompt)
                        .build();
                ListenableFuture<GenerateContentResponse> response = chatFutures.sendMessage(msgContent);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        //Should check if valid JSON
                        completion.onResponse(result.getText());
                        computerResponse = result.getText();
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        completion.onError(t.getMessage());
                        t.printStackTrace();
                        Log.d("GeminiAPI:", "onFailure: " + t.getMessage());
                    }
                }, new Executor() {
                    @Override
                    public void execute(Runnable runnable) {
                        runnable.run();
                    }
                });
                Log.i("GeminiAPI", "geminiInteraction log: Response is " + (response.isDone() ? "Done" : "Not Done"));
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                Log.e("GeminiAPI", "Unable to generate response");
                //completion.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    public void explanation() {

    }

    public interface GeminiCallback {
        void onResponse(String response);
        void onError(String error);
    }
}

