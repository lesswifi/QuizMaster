package compsci290.edu.duke.quizmaster;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harrisonlundberg on 2/22/17.
 *
 * This class encapsulates a single quiz, which has a title, a difficulty, and a list of questions.
 * Currently the difficulty attribute is never used, but we anticipate is becoming important
 * in the future.
 */

public class Quiz {
    private String mTitle;
    private int mDifficulty;
    private List<Question> mQuestions;
    private String mGenres;


    //This constructor is currently not used, but we anticipate it being used in the future.
    public Quiz(String title, int difficulty) {
        mTitle = title;
        mDifficulty = difficulty;
        mQuestions = new ArrayList<>();
    }


    /**
     *
     * @param title
     * @param questions
     *
     * This is the constructor that is currently used all of the time.
     * It takes in the quiz's title and the list of questions.
     */
    public Quiz(String title, List<Question> questions, String Genre) {
        mTitle = title;
        mQuestions = questions;
        mDifficulty = 1;
        mGenres = Genre;
    }

    /**
     *
     * @param q
     *
     * This method can be used to dynamically add new questions to a given quiz. It is currently
     * not used but we anticipate its need in the future.
     */
    public void addQuestion(Question q) {
        mQuestions.add(q);
    }


    /**
     *
     * @param idx
     * @return
     *
     * This method simply returns the question at a given index.
     */
    public Question getQuestionAtIndex(int idx) {
        return mQuestions.get(idx);
    }


    /**
     *
     * @return
     * This method returns the title of the quiz.
     */
    public String getTitle() {
        return mTitle;
    }


    /**
     *
     * @return This method returns the difficulty of the quiz, but currently is not used.
     */
    public int getDifficulty() {
        return mDifficulty;
    }


    public String getGenres() {
        return mGenres;
    }
    /**
     *
     * @return This method returns the number of questions in the given quiz.
     */
    public int size() {
        return mQuestions.size();
    }


    /**
     *
     * @return
     * The toString method is overriden to return the quiz's title so that we can use the
     * ArrayAdapter in MainActivity to display a list of quiz's by title.
     */

    @Override
    public String toString() {
        return getTitle();
    }

}
