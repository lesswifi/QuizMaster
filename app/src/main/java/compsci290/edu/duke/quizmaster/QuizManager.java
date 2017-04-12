package compsci290.edu.duke.quizmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * Created by harrisonlundberg on 2/22/17.
 *
 * QuizManager encapsulates all of the current state of the current quiz. It directly communicates
 * with GameActivity whenever GameActivity must update the UI. However, no state about the current
 * quiz is stored in GameActivity. Whenever we need to save the state of the current quiz, we simply
 * must save necessary state in the QuizManager instance.
 */

public class QuizManager {
    private GameActivity mGameActivity;
    private Quiz mQuiz;
    private ArrayAdapter mAdapter;

    private int mScore;
    private int mIndex;

    private int mCorrectAnswerIndex;

    //This attribute is currently not used, but will be used in the future when we dynamically
    //reorder questions.
    private int[] mOrder;

    private boolean hasJustRestored = false;

    public static final String SCORE = "quiz_manager_current_score";
    public static final String INDEX = "quiz_manager_current_index";
    public static final String CORRECT_ANSWER_INDEX = "quiz_manager_current_correct_index";
    public static final String QUIZ_TITLE = "quiz_manager_quiz_title";

    public static final String SHARED_PREFERENCES = "quiz_manager_shared_preferences";


    /**
     *
     * @param g
     * @param quizIndex
     * @param savedInstanceState
     * @param shouldRestore
     *
     * This constructor is passed the index of the quiz to be played. It uses this index to
     * access MyQuizzes. Additionally the savedInstanceState bundle is passed from GameActivity
     * in the event that we need to restore quiz state from the bundle. Additionally, the boolean
     * shouldRestore is passed in which tells the constructor whether or not it should go to
     * SharedPreferences to restore previous quiz state.
     */
    public QuizManager(GameActivity g, int quizIndex, Bundle savedInstanceState, boolean shouldRestore) {
        mGameActivity = g;
        mQuiz = MyQuizzes.getQuizAt(mGameActivity, quizIndex);

        if (shouldRestore) {
            Log.d(TAG, "should restore is true");
            SharedPreferences sp = g.getSharedPreferences(SHARED_PREFERENCES, 0);
            mScore = sp.getInt(SCORE, -1);
            Log.d(TAG, "the score read is " + mScore);
            mIndex = sp.getInt(INDEX, 0);
            mCorrectAnswerIndex = sp.getInt(CORRECT_ANSWER_INDEX, 0);
            hasJustRestored = true;
        } else {
            if (savedInstanceState == null) {
                mScore = 0;
                mIndex = 0;
            } else {
                mScore = savedInstanceState.getInt(SCORE);
                mIndex = savedInstanceState.getInt(INDEX);
                mCorrectAnswerIndex = savedInstanceState.getInt(CORRECT_ANSWER_INDEX);
                hasJustRestored = true;
            }
        }

        mAdapter = new AnswersAdapter(g, R.layout.list_text_item);
        setNextQuestion();
    }


    /**
     *
     * @return
     * This method is called by GameActivity and returns the adapter which supplies GameActivity's
     * listView with all of the answers for the current question.
     */
    public ArrayAdapter getAdapter() {
        return mAdapter;
    }


    /**
     *
     * @param index
     * This callback is invoked whenever one of the answers in the ListView is clicked on.
     * It is determined whether or not the user clicked on the right answer, and the
     * setNextQuestion method is called.
     */
    public void onAnswerClick(int index) {
        if (index == mCorrectAnswerIndex) {
            mScore++;
            mGameActivity.displayMessage(true);
        } else {
            mGameActivity.displayMessage(false);
        }
        mIndex++;
        setNextQuestion();
    }


    /**
     * This method sets up the next question in the quiz by first checking whether or not the
     * quiz has just been completed. If the quiz is now complete, FinalActivity is started and passed
     * information about the just-completed quiz.
     *
     * If the quiz has questions remaining, the UI is updated through the call to GameActivity's
     * updateUI.
     *
     * Additionally, the adapter is updated to hold all of the answers for the current question.
     */
    private void setNextQuestion() {
        if (mIndex < mQuiz.size()) {
            Question currentQuestion = mQuiz.getQuestionAtIndex(mIndex);

            mGameActivity.updateUI(currentQuestion.getQuestion(), mScore, mQuiz.size() - mIndex);

            if (hasJustRestored) {
                hasJustRestored = false;
            } else {
                mCorrectAnswerIndex = new Random().nextInt(currentQuestion.getWrongAnswers().size());
            }
            //Create array of answers and set it to the adapter
            List<Answer> currentAnswers = getAnswerArray(currentQuestion, mCorrectAnswerIndex);
            mAdapter.clear();

            //this may need to change eventually
            mAdapter.addAll(currentAnswers);
            mAdapter.notifyDataSetChanged();
        } else {
            //Switch to final Activity
            //********************Should this instead be done inside gameActivity??
            //*******************
            Intent fActivity = new Intent(mGameActivity, FinalActivity.class);
            fActivity.putExtra(FinalActivity.PREV_QUIZ, mQuiz.getTitle());
            fActivity.putExtra(FinalActivity.PREV_SCORE, mScore);
            mGameActivity.startActivity(fActivity);
            Log.d(TAG, "Switch to finalActivity");
            mGameActivity.finish();
            return;
        }
    }

    /**
     *
     * @param q
     * @param correctIndex
     * @return This method returns a list of all of the answers to the current question.
     * A list of the wrong answers is returned with the correct answer inserted at the
     * specified index.
     */
    private List<Answer> getAnswerArray(Question q, int correctIndex) {
        Answer correct = q.getCorrectAnswer();
        List<Answer> wrongs = q.getWrongAnswers();

        //by just adding to wrongs, we modify the question, this was a bug
        //instead we need to copy wrongs into a new collection and add correctIndex to it
        //wrongs.add(correctIndex, correct);
        List<Answer> answers = new ArrayList<>(wrongs);
        answers.add(correctIndex, correct);

        return answers;
    }

    /**
     *
     * @param outState
     * This method is called by GameActivity and tells the QuizManager instance to save
     * necessary quiz state to the bundle.
     */
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SCORE, mScore);
        outState.putInt(INDEX, mIndex);
        outState.putInt(CORRECT_ANSWER_INDEX, mCorrectAnswerIndex);
    }


    /**
     * This method is called by GameActivity and tells the QuizManager instance to save
     * necessary quiz state to the SharedPreferences.
     */
    public void savePersistentState() {
        SharedPreferences sp = mGameActivity.getSharedPreferences(SHARED_PREFERENCES, 0);
        SharedPreferences.Editor e = sp.edit();
        //Save three things from onSaveInstance state
        e.putInt(SCORE, mScore);
        e.putInt(INDEX, mIndex);
        e.putInt(CORRECT_ANSWER_INDEX, mCorrectAnswerIndex);
        //Save quiz's name
        e.putString(QUIZ_TITLE, mQuiz.getTitle());
        Log.d(TAG, "Title saved:" + mQuiz.getTitle());
        e.commit();
    }
}
