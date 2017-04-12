package compsci290.edu.duke.quizmaster;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * This class manages the behavior of the Final Screen. The user arrives at this screen after
 * completing a quiz. When this activity is started, it is passed information
 * from the GameActivity about what the player's score was and which of the quizzes was just
 * completed. The user is presented with a button which can return him/her to the main menu.
 */
public class FinalActivity extends AppCompatActivity {

    public static final String PREV_QUIZ = "previousQuizPlayed";
    public static final String PREV_SCORE = "previousScore";

    private static final String TAG = "FinalActivity";

    private int mPrevScore;
    private String mPrevQuizTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);
        Intent passed = getIntent();

        //We should pass to final activity the name of the quiz just taken
        mPrevScore = passed.getIntExtra(PREV_SCORE, 0);
        mPrevQuizTitle = passed.getStringExtra(PREV_QUIZ);

        //Once we get here we should invalidate the SharedPreferences so that when we go back
        //to main it doesn't think that we want to resume this quiz we just completed
        SharedPreferences sp = getSharedPreferences(QuizManager.SHARED_PREFERENCES, 0);
        SharedPreferences.Editor e = sp.edit();
        e.clear();
        e.commit();

        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
        return true;
    }

    /**
     * If the user clicks on the "Main Menu" button, a new MainActivity will be started.
     */
    public void onMainClick(View v) {
        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }


    /**
     * This method displays to the user the score they just obtained in the completed quiz.
     */
    private void setupUI() {
        //locate the textView in finalActivity
        TextView tv = (TextView) findViewById(R.id.result_text);
        String message =
                String.format("Congratulations, you got a score of %d on %s", mPrevScore, mPrevQuizTitle);
        tv.setText(message);
    }
}
