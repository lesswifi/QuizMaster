package compsci290.edu.duke.quizmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


/**
 * Created by harrisonlundberg on 2/23/17.
 *
 * This class encapsulates all of the quizzes which are available to the user. This class is never
 * instantiated, but rather static methods are used to access a single copy of the list of quizzes.
 * We decided to use static methods because there is no reason to have more than one copy of the
 * quizzes.
 *
 * One of the current limitations of this implementation of quizzes is that we assume that
 * a given quiz name is unique. If each quiz does not have a unique name, problems can arise.
 *
 * One of the reasons we save the quiz's name and not the quiz's index in sharedPreferences is
 * that we do not assume that the available list of quizzes cannot change across reboots.
 * If the list were to change across reboots, then the index of the previous quiz could be incorrect,
 * so instead we save the name of the previous quiz.
 */

public class MyQuizzes {
    private static final String TAG = "MyQuizzes";

    private static String ASSETS_PATH = "quizzes";

    public static final String OLD_URL_1 = "oldUrl1";
    public static final String OLD_URL_2 = "oldUrl2";
    public static final String OLD_URL_3 = "oldUrl3";

    public static final String SHARED_PREFERENCES_URLS = "sharedUrls";

    private static ArrayList<Quiz> mQuizzes = null;


    /**
     *
     * @param c
     * @return
     *
     * This method is called by MainActivity and returns the list of all available quizzes.
     */
    public static List<Quiz> getQuizzes(Context c) {
        if (mQuizzes == null) {
            mQuizzes = new ArrayList<>();
            createQuizzes(c);

        }
        return mQuizzes;
    }


    /**
     * @param idx
     * @return
     *
     * This method is passed an index and returns the quiz at that given index. Whenever a
     * GameActivity is started, it is passed the index of the quiz to be played.
     */
    public static Quiz getQuizAt(Context c, int idx) {
        if (mQuizzes == null) {
            getQuizzes(c);
        }
        return mQuizzes.get(idx);
    }

    public static List<String> getGenresList(Context c) {
        List<String> mGenres = new ArrayList<>();
        if (mQuizzes == null) {
            getQuizzes(c);
        }
        for (Quiz q : mQuizzes) {
            if (! mGenres.contains(q.getGenres())) {
                mGenres.add(q.getGenres());
            }
        }
        return mGenres;
    }

    public static List<Quiz> getQuizByGenres(String sameGenres, Context c) {
        List<Quiz> GenresQuiz = new ArrayList<>();
        if (mQuizzes == null) {
            getQuizzes(c);
        }
        for (Quiz q : mQuizzes){
            if (sameGenres.equals(q.getGenres())){
                GenresQuiz.add(q);
            }
        }
        Log.d(TAG, "Length of genresQuiz is " + GenresQuiz.size());
        return GenresQuiz;
    }



    /**
     *
     * @param c
     * @param title
     * @return
     *
     * This method is passed the title of a quiz and returns that quiz. Null is returned if that
     * quiz does not exist. In fact, this method is currently only used to determine whether
     * or not a given quiz actually exists, which is in MainActivity.
     */
    public static Quiz getQuizByTitle(Context c, String title) {
        if (mQuizzes == null) {
            getQuizzes(c);
        }
        for (Quiz q : mQuizzes) {
            if (title.equals(q.getTitle())) {
                return q;
            }
        }
        return null;
    }


    /**
     *
     * @param c
     * @param title
     * @return
     *
     * This method is passed a title of a quiz and returns the current index of that quiz in the
     * ArrayList of quizzes. This method is necessary because the title of a quiz is what is
     * saved to SharedPreferences, so if we want to continue that quiz in the future, we need to
     * be able to get the current index of that quiz.
     */
    public static int getIndexFromTitle(Context c, String title) {
        if (mQuizzes == null) {
            getQuizzes(c);
        }
        for (int i = 0; i < mQuizzes.size(); i++) {
            String currentTitle = mQuizzes.get(i).getTitle();
            if (title.equals(currentTitle)) {
                return i;
            }
        }
        return -1;
    }




    /**
     *
     * @param c
     *
     * This method is called whenever mQuizzes is null. This method iterates through the
     * local assets folder called "quizzes", creating a Quiz instance for each of those files.
     * All of the local quiz files are stored in assets/quizzes
     */
    private static void createQuizzes(Context c) {
        try {
            String[] localQuizzes = c.getResources().getAssets().list(ASSETS_PATH);
            for (String quiz : localQuizzes) {
                Log.d(TAG, "createQuiz called with " + quiz + " as the name");
                createQuiz(c, quiz);
            }
            //createQuizFromUrl(c, "http://people.duke.edu/~hgl2/remotePracticeQuiz.json");
            //We now look at shared preferences to see if we have any saved urls, if so we fetch those quizzes as well
            SharedPreferences sp = c.getSharedPreferences(SHARED_PREFERENCES_URLS, 0);
            String[] oldUrls = new String[3];
            oldUrls[0] = sp.getString(OLD_URL_1, "");
            oldUrls[1] = sp.getString(OLD_URL_2, "");
            oldUrls[2] = sp.getString(OLD_URL_3, "");

            for(String url : oldUrls) {
                Log.d(TAG, "the url is " + url);
                if (!url.equals("")) {
                    createQuizFromUrl(c, url);
                }
            }

        } catch (Exception e){
            e.getMessage();
        }
        //We could now load quizzes from the internet here
    }


    /**
     *
     * @param c
     * @param name
     *
     * This method is passed the file name of a quiz stored locally. That file is opened,
     * the format is determined, and the appropriate parser is used to create a Quiz instance for
     * that file.
     */
    private static void createQuiz(Context c, String name) {
        try {
            InputStream fileStream = c.getResources().getAssets().open(ASSETS_PATH + "/" + name);
            Log.d(TAG, "file successfully opened");
            String extension = getFileExtension(name);
            if (extension.equals("xml")) {
                //Call the XMLQuizParser
                Log.d(TAG, "file is XML");
                XMLQuizParser xParser = new XMLQuizParser();
                mQuizzes.add(xParser.parse(fileStream));
            } else if (extension.equals("json")) {
                //Call JSONQuizParser
                Log.d(TAG, "file is JSON");
                JSONQuizParser jParser = new JSONQuizParser();
                mQuizzes.add(jParser.parse(fileStream));
            } else {
                //Add to log that this quiz could not be parsed
                Log.d(TAG, "file is neither of the allowed types");
                Log.d(TAG, "file is of type " + extension);
            }
            fileStream.close();
        } catch (Exception e) {
            //Should we handle these exceptions differently?!
            Log.d(TAG, "createQuiz: catch block entered due to " + e.getMessage());
            e.getMessage();
        }
    }






    /**
     *
     * @param name
     * @return
     * This method is called by createQuiz and is used to determine the file extension of
     * a given file. This information is then used to determine which of the parsers should be
     * used on the current file.
     */
    private static String getFileExtension(String name) {
        //return the file extension of name
        String[] nameSplit = name.split("\\.");
        return nameSplit[nameSplit.length - 1];
    }




    //Currently this method simply calls getJSON
    //Eventually we could modify this method to see that if getJSON fails, we could then try calling
    //getXML instead, to support more formats
    public static void createQuizFromUrl(Context c, String url) {
        //First try to read the quiz as json
        getJSON(c, url);

    }

    private static String getJSON(final Context c, final String url) {
        RequestQueue queue = Volley.newRequestQueue(c);

        StringRequest stringRequest =
                new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        //Call JSON parser with this string
                        try {
                            Quiz urlQuiz = new JSONQuizParser().parse(response);
                            //Add urlQuiz to quizList and notify main activity to update adapter
                            mQuizzes.add(urlQuiz);
                            Log.d(TAG, "remote quiz added");

                            //Add this url to shared preferences
                            if (c instanceof QuizUrlActivity) {
                                QuizUrlActivity g = (QuizUrlActivity) c;
                                SharedPreferences sp = g.getSharedPreferences(SHARED_PREFERENCES_URLS, 0);
                                String old1 = sp.getString(OLD_URL_1, "");
                                String old2 = sp.getString(OLD_URL_2, "");

                                SharedPreferences.Editor e = sp.edit();
                                Log.d(TAG, "the url saved is " + url);
                                e.putString(OLD_URL_1, url);
                                e.putString(OLD_URL_2, old1);
                                e.putString(OLD_URL_3, old2);
                                e.commit();

                                g.onQuizCreateSuccess();
                            } else if (c instanceof MainActivity) {
                                //We need to notify main activity that potentially this quiz could be the one for which we saved prev quiz state
                                MainActivity m = (MainActivity) c;
                                m.checkIfUpdateUI();
                            }
                        } catch (Exception e) {
                            //Handle the fact that response may not be in correct json format
                            Log.d(TAG, "getJSON: exception from parsing response " + e.getMessage());
                            if (c instanceof QuizUrlActivity) {
                                QuizUrlActivity g = (QuizUrlActivity) c;
                                g.onQuizCreateFailure();
                            }
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error for the url not working
                        Log.d(TAG, "getJSON error in getting Volley response");
                        if (c instanceof QuizUrlActivity) {
                            QuizUrlActivity g = (QuizUrlActivity) c;
                            g.onQuizCreateFailure();
                        }
                    }
                });
        queue.add(stringRequest);
        return "";
    }
}
