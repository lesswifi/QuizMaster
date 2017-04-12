package compsci290.edu.duke.quizmaster;

/**
 * Created by harrisonlundberg on 2/24/17.
 *
 * This class encapsulates a single answer for a given question.
 * Originally, answers were represented as strings. However, to support different types of
 * answers we decided to create the Answer class, where each instance has a type attribute
 * which is either STRING or IMAGE. This answer type is then used by the answersAdapter
 * which determines how to represent each answer in the list view for the current question.
 */

public class Answer {
    public enum answerType {STRING, IMAGE, IMAGE_URL}
    private answerType mType;
    private String mText;

    public Answer (answerType type, String text) {
        mType = type;
        mText = text;
    }

    public String getText() {
        return mText;
    }

    public answerType getType() {
        return mType;
    }
}
