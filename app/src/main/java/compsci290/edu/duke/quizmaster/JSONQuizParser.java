package compsci290.edu.duke.quizmaster;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by harrisonlundberg on 2/24/17.
 *
 * This class is used to parse input as the JSON format we created, and creates a Quiz from that
 * input.
 */

public class JSONQuizParser {

    private static final String TAG = "JSONQuizParser";


    /**
     *
     * @param str
     * @return
     * @throws JSONException
     *
     * This method takes a string as input and uses JSONObjects and JSONArrays to parse
     * our custom JSON Quiz format.
     */
    public Quiz parse(String str) throws JSONException {
        JSONObject all = new JSONObject(str);
        String title = all.getString("title");
        String genre = all.getString("genre");
        JSONArray questions = all.getJSONArray("questions");
        ArrayList<Question> questionList = new ArrayList<>();
        for (int i = 0; i < questions.length(); i++) {
            JSONObject q = questions.getJSONObject(i);
            String text = q.getString("text");
            JSONObject correctAnswerObj = q.getJSONObject("correctanswer");

            //Create correct answer
            Answer correctAnswer = createAnswer(correctAnswerObj);


            JSONArray wrongAnswers = q.getJSONArray("wronganswers");
            Answer[] wrongsArray = new Answer[wrongAnswers.length()];
            for (int j = 0; j < wrongAnswers.length(); j++) {
                Answer wrong = createAnswer(wrongAnswers.getJSONObject(j));
                wrongsArray[j] = wrong;
            }
            questionList.add(new Question(text, correctAnswer, wrongsArray));
        }
        return new Quiz(title, questionList, genre);
    }


    /**
     *
     * @param in
     * @return
     * @throws IOException
     * @throws JSONException
     *
     * This method takes in an input stream, converts it to a single string, and calls the other
     * implementation of the parse method. This method is necessary because when we open files,
     * they are opened as InputStreams, so they must first be converted to strings before using
     * our parse method.
     */
    public Quiz parse(InputStream in) throws IOException, JSONException {
        String str = readString(in);
        return parse(str);
    }


    /**
     *
     * @param in
     * @return
     * @throws IOException
     *
     * This method takes an input stream and reads it line by line, creating a single string
     * to be returned.
     */
    private String readString(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader b = new BufferedReader(new InputStreamReader(in));
        String line = b.readLine();
        while (line != null) {
            sb.append(line);
            line = b.readLine();
        }
        return sb.toString();
    }

    /**
     *
     * @param j
     * @return
     * @throws JSONException
     *
     * This method is passed a JSONObject and instantiates a new Answer object. It uses the
     * translateAnswer method to translate a string into a Answer.answerType.
     */
    private Answer createAnswer(JSONObject j) throws JSONException {
        return new Answer(translateAnswerType(j.getString("type")), j.getString("text"));
    }

    /**
     *
     * @param str
     * @return
     *
     * This method is used by our createAnswer method, and is used to determine which kind type of answer
     * the current string represents. It takes a string as the input and returns a member of the
     * Answer.answerType enum.
     */
    private Answer.answerType translateAnswerType(String str) {
        if (str.equals("string")) {
            return Answer.answerType.STRING;
        } else if (str.equals("image")) {
            return Answer.answerType.IMAGE;
        } else if (str.equals("imageUrl")) {
            return Answer.answerType.IMAGE_URL;
        } else {
            Log.d(TAG, "translateAnswerType passed bogus type");
            return Answer.answerType.STRING;
        }
    }
}
