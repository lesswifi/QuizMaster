package compsci290.edu.duke.quizmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * This class manages the behavior for the Main Menu screen.
 * The user is presented with a list view of the available quizzes.
 * Additionally, if the user previously started but did not finish a quiz, a continue button
 * will be displayed which allows the user to continue the quiz from where he/she left off.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private List<Quiz> mQuizzes;
    private ListAdapter mAdapter;
    private ListView mQuizList;
    private List<String> mGenres;
    private List<Quiz> mSameGenres;
    private View BackView;

    private String mCurrentGenreSelected;

    private String mPrevQuizTitle;
    private boolean isSelectGenre;

    public static final String QUIZ_INDEX = "main_activity_quiz_index";
    public static final String QUIZ_RESTORE = "main_activity_quiz_restore";




    /**
     *
     * @param savedInstanceState
     *
     * In onCreate, we first obtain a List of all the quizzes from MyQuizzes.
     * This list is used to supply the ArrayAdapter for our ListView, which in turn presents
     * the user with a list of possible quizzes that can be played.
     * The adapter is set up and the OnClickListener is set up as well so that when the user
     * clicks on one of the quizzes in the list, the startGame method is called.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackView = findViewById(R.id.activity_main_back_option);


        if (mQuizzes == null) {
            Log.d(TAG, "Create Quiz");
            mQuizzes = MyQuizzes.getQuizzes(this);
        }

    }


    /**
     * In onStart, we check the SharedPreferences to see if the previously started
     * quiz was not finished. If this is the case, a continue button is displayed on screen
     * which will allow the user to continue this quiz from where he/she left off.
     *
     * If there is no saved quiz state in the SharedPreferences, the continue button is hidden
     * from the screen.
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "OnStartCalled");

        isSelectGenre = true;
        BackView.setVisibility(View.GONE);


        setupGenreMenu();
        //Check shared preferences to see if there is saved state of a previously played quiz


        checkIfQuizToContinue();
    }


    private void UpdateOrStart(int position, View v){

        if (isSelectGenre){
            mCurrentGenreSelected = mGenres.get(position);
            setupQuizMenu();
            isSelectGenre = false;
            BackView.setVisibility(View.VISIBLE);
        }
        else{
            startGame(v);
        }

    }

    /**
     *
     * @param v
     *
     * This method is called whenever the user clicks on one of the quizzes in the ListView,
     * and starts a new GameActivity and passes it the index of the selected quiz in MyQuizzes.
     */
    private void startGame(View v) {
        TextView tView = (TextView) v;
        String QuizTitle = tView.getText().toString();
        int QuizIndex = MyQuizzes.getIndexFromTitle(this, QuizTitle);

        Intent game = new Intent(this, GameActivity.class);
        game.putExtra(QUIZ_INDEX, QuizIndex);
        startActivity(game);
    }

    public void onBackClick(View view) {

            ArrayAdapter backAdapter = (ArrayAdapter) mAdapter;
            mGenres = MyQuizzes.getGenresList(this);
            backAdapter.clear();
            backAdapter.addAll(mGenres);
            backAdapter.notifyDataSetChanged();
            BackView.setVisibility(View.GONE);
            isSelectGenre = true;
    }

    /**
     *
     * @param v
     *
     * This method is called if the user clicks on the continue button. A new GameActivity is
     * started and is passed the index of the quiz as well as information telling the GameActivity
     * that previous quiz state should be restored from the SharedPreferences.
     */
    public void onContinueClick(View v) {
        //How to implement this?
        //Just call basically like startGame except pass the name of quiz from SharedPreferences
        Intent game = new Intent(this, GameActivity.class);
        //Need to get the quiz index of the mPrevQuiz
        int prevQuizIndex = MyQuizzes.getIndexFromTitle(this, mPrevQuizTitle);
        game.putExtra(QUIZ_INDEX, prevQuizIndex);
        game.putExtra(QUIZ_RESTORE, true);
        startActivity(game);
    }


    public void onEnterUrlClick(View view) {
        //Start quizUrlActivity
        Intent urlIntent = new Intent(this, QuizUrlActivity.class);
        startActivity(urlIntent);
    }



    public void checkIfUpdateUI() {
        checkIfQuizToContinue();
        //Check if need to update genres/quiz list
        if (isSelectGenre) {
            setupGenreMenu();
        } else {
            setupQuizMenu();
        }
    }

    private void setupGenreMenu() {
        mGenres = MyQuizzes.getGenresList(this);

        //Now setup ListView with quiz Titles
        mAdapter = new ArrayAdapter(this, R.layout.list_text_item, mGenres);

        mQuizList = (ListView) findViewById(R.id.activity_main_quiz_select);

        mQuizList.setAdapter(mAdapter);

        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                //Start GameActivity and pass it the index of the quiz to be taken
                UpdateOrStart(position, v);
            }
        };

        mQuizList.setOnItemClickListener(mMessageClickedHandler);
    }

    private void setupQuizMenu() {
        ArrayAdapter adapter = (ArrayAdapter) mAdapter;
        mSameGenres = MyQuizzes.getQuizByGenres(mCurrentGenreSelected, this);
        adapter.clear();
        adapter.addAll(mSameGenres);
        adapter.notifyDataSetChanged();
    }

    private void checkIfQuizToContinue() {
        Log.d(TAG, "checkIfQuizToContinue is called");
        SharedPreferences sp = getSharedPreferences(QuizManager.SHARED_PREFERENCES, 0);
        mPrevQuizTitle = sp.getString(QuizManager.QUIZ_TITLE, null);
        View continueView = findViewById(R.id.activity_main_continue_option);
        if ((mPrevQuizTitle != null) && (MyQuizzes.getQuizByTitle(this, mPrevQuizTitle) != null)) {
            //Check if prevQuizTitle still exists in the list of quizzes
            //Keep active the continue part View, update its text
            Log.d(TAG, "prev quiz info has been found");
            Log.d(TAG, "Prev quiz title is:" + mPrevQuizTitle);
            continueView.setVisibility(View.VISIBLE);
        } else {
            //Set the continue view invisible
            Log.d(TAG, "prev quiz info not found");
            continueView.setVisibility(View.GONE);
        }
    }
}
