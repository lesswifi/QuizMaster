package compsci290.edu.duke.quizmaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class encapsulates a single question, which is comprised of the question statement,
 * the correct answer, and one or more wrong answers stored in a list.
 */

public class Question {

    private String mQuestion;
    private Answer mCorrectAnswer;
    private List<Answer> mWrongAnswers;


    //This method returns the correct answer.
    public Answer getCorrectAnswer() {
        return mCorrectAnswer;
    }

    //This method returns the wrong answers as a list of Answer objects.
    public List<Answer> getWrongAnswers() {
        return mWrongAnswers;
    }

    //This method returns the question statement as a string.
    public String getQuestion() {
        return mQuestion;
    }

    //The constructor
    public Question(String question, Answer correct, Answer ... wrong) {
        mQuestion = question;
        mCorrectAnswer = correct;
        mWrongAnswers = new ArrayList<>(Arrays.asList(wrong));
    }
}
