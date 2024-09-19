package com.example.recapai;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JSONDecoder {


    public static Quiz decodeJSONToQuiz(String jsonStr) throws JsonSyntaxException {
        Gson gson = new Gson();
        //JsonReader reader = gson.newJsonReader(new StringReader(jsonStr));
        //List<Question> questionList = gson.fromJson(jsonStr, new TypeToken<List<Question>>() {}.getType());

        return gson.fromJson(jsonStr, Quiz.class);
    }

    /**
     * Checks that the Json generates a valid quiz by
     * ensuring that no parameters in Quiz are null
     * @param obj any object
     * @return boolean valid/invalid Json
     */
    public static boolean isValidJson(Object obj) {
        if (obj == null) return false;


        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true); //makes private vars accessible
        }
        return true;
    }

    public static void main(String[] args) throws JSONException {
        String jsonObject ="{\n" +
                "  \"quiz_title\": \"Sample Quiz\",\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"type\": \"multiple_choice\",\n" +
                "      \"question\": \"What is the capital of France?\",\n" +
                "      \"options\": \n[" +
                "        {\"text\": \"Paris\", \"correct\": true},\n" +
                "        {\"text\": \"London\",  \"correct\": false},\n" +
                "        {\"text\": \"Berlin\", \"correct\": false}\n," +
                "        {\"text\": \"Rome\", \"correct\": false}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        System.out.println(decodeJSONToQuiz(jsonObject));
    }
}
