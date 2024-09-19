package com.example.recapai;

import org.junit.Test;

import static org.junit.Assert.*;

import com.google.gson.JsonSyntaxException;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTesting {

    //JSONDecoder Tests
    @Test
    public void decodeJSONToQuiz_test() {
        Quiz quiz = JSONDecoder.decodeJSONToQuiz("{" +
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
                "}");
        assertNotNull(quiz.getQuizTitle());
        assertEquals("What is the capital of France?", quiz.getQuestions().get(0).getQuestion());
        assertNotNull(JSONDecoder.decodeJSONToQuiz("{" +
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
                "}"));
        assertNotNull(quiz.getQuestions());
        assertNotNull(quiz.getQuestions().get(0).getOptions());
        assertNull(JSONDecoder.decodeJSONToQuiz(""));
    }
}