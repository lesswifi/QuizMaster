package compsci290.edu.duke.quizmaster;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class QuizUrlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_url);
    }


    public void onConfirmClick(View view) {
        //Get url from edit text and try to create a json quiz from it
        EditText input = (EditText) findViewById(R.id.activity_quiz_url_input);
        String url = input.getText().toString();
        MyQuizzes.createQuizFromUrl(this, url);
    }

    public void onQuizCreateSuccess() {
        //Display message that quiz has successfully been created is available in the main menu
        TextView t = (TextView) findViewById(R.id.activity_quiz_url_message);
        t.setText("Quiz has been successfully created and is available from the main menu!");
        t.setVisibility(View.VISIBLE);
    }

    public void onQuizCreateFailure() {
        //Display error message
        TextView t = (TextView) findViewById(R.id.activity_quiz_url_message);
        t.setText("There was an error in creating this quiz. Please be sure that this is a valid URL and that the quiz is specified in an appropriate format.");
        t.setVisibility(View.VISIBLE);
    }
}
