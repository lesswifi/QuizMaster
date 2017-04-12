package compsci290.edu.duke.quizmaster;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This Activity is run whenever a quiz is actively being played.
 */

public class GameActivity extends AppCompatActivity {

    private static final String TAG = "GameActivity";

    private int mQuizIndex;
    private QuizManager mManager;

    private ListView mListView;

    private TextView mQuestionText;
    private TextView mScoreText;


    /**
     *
     * @param savedInstanceState
     *
     * On creation, this activity is passed the index of the quiz to be played.
     * Additionally, the onCreate method checks whether or not state should be restored from
     * SharedPreferences.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mListView = (ListView) findViewById(R.id.activity_game_list_view);
        mQuestionText = (TextView) findViewById(R.id.activity_game_question_text);
        mScoreText = (TextView) findViewById(R.id.activity_game_score);

        Bundle b = getIntent().getExtras();
        mQuizIndex = b.getInt(MainActivity.QUIZ_INDEX);
        boolean shouldRestore = b.getBoolean(MainActivity.QUIZ_RESTORE, false);



        setupQuiz(mQuizIndex, savedInstanceState, shouldRestore);

    }



    /**
     * In onPause we save state to SharedPreferences by calling savePersistentState on the
     * QuizManager instance. It is necessary to save to SharedPreferences in onPause rather than
     * in onStop because of the order in which these callbacks are invoked by android. Specifically,
     * we want for when the user presses back while playing a quiz, the current quiz state will be
     * saved so that a user can continue from where he/she left off by clicking continue in the main
     * menu.
     *
     * In android, when you are switching from activity A to activity B, first activity A's onPause
     * is invoked. Then activity B's onCreate, onStart, and onResume are invoked. Finally,
     * activity A's onStop is invoked. Because we need to have the state saved to sharedPreferences
     * before MainActivity's onStart method is invoked, we are required to save this state in
     * the onPause method.
     */
    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "OnPause called");

        mManager.savePersistentState();
    }


    /**
     *
     * @param quizIndex
     * @param savedInstanceState
     * @param shouldRestore
     *
     * This method is invoked by onCreate. It first creates a new QuizManager instance with
     * the quiz to be taken. It also passes to the quiz manager information about savedInstanceState
     * and a boolean saying whether or not quiz state should be restored from the SharedPreferences.
     *
     * Additionally, the adapter for the answers' ListView is obtained from the QuizManager instance
     * and the OnClickListener is set up so that whenever an answer in the ListView is clicked,
     * the appropriate method in QuizManager is invoked.
     */
    private void setupQuiz(int quizIndex, Bundle savedInstanceState, boolean shouldRestore) {
        mManager = new QuizManager(this, quizIndex, savedInstanceState, shouldRestore);
        //Call QuizManager's method to get a reference to the adapter
        ListAdapter ourAdapter = mManager.getAdapter();
        mListView.setAdapter(ourAdapter);

        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //Call into QuizManager saying that position was clicked
                mManager.onAnswerClick(position);
            }
        };

        mListView.setOnItemClickListener(mMessageClickedHandler);
    }


    /**
     *
     * @param newQuestion
     * @param currentScore
     * @param questionsRemaining
     *
     * This method is invoked by the QuizmManager instance whenever the UI needs to be updated
     * in response to the user answering a question.
     * This method updates the question displayed on screen as well as the current score and
     * number of remaining questions displayed.
     */
    public void updateUI(String newQuestion, int currentScore, int questionsRemaining) {
        //Set TextView to display newQustion
        mQuestionText.setText(newQuestion);
        //Update current score and questionsRemaining
        updateScoreUI(currentScore, questionsRemaining);
    }


    /**
     *
     * @param currentScore
     * @param questionsRemaining
     *
     * This method is called by updateUI and updates the current score and number of questions
     * remaining displayed on screen.
     */
    private void updateScoreUI(int currentScore, int questionsRemaining) {
        String scoreStr =
                String.format("The current score is %d, and there are %d questions remaining", currentScore, questionsRemaining);
        mScoreText.setText(scoreStr);
    }

    /**
     *
     * @param outState
     * This method is invoked whenever the activity is about to be destroyed and it simply calls
     * the QuizManager's method to saveInstanceState. To save the state of the quiz, only the state
     * of the QuizManager instance needs to be saved, because the GameActivity in fact holds no
     * necessary information to restore the state of the quiz.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //What all do we need to save here? The QuizManager?

        //We need to only tell QuizManager to save its necessary state
        mManager.onSaveInstanceState(outState);
    }


    /**
     *
     * @param wasCorrect
     *
     * This method is invoked by the QuizManager and displays a toast on screen either saying
     * the answer was correct or incorrect.
     */
    public void displayMessage(boolean wasCorrect) {
        //This should be called from update UI, need to add a parameter wasCorrect to updateUI
        String str;
        if (wasCorrect) {
            str = "That is correct!";
        } else {
            str = "That is wrong!";
        }
        Toast t = Toast.makeText(this, str, Toast.LENGTH_SHORT);
        t.show();
    }
}
