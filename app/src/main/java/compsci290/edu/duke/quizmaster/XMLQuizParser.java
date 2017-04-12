package compsci290.edu.duke.quizmaster;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by harrisonlundberg on 2/24/17.
 *
 * This class is used to parse input in the XML format specified in class.
 *
 * This class was created using the tips presented in the XML Parsing page on the Android Developers
 * website. We used the skip method that was presented in class and was also available on the
 * website. Other than that method, we wrote the rest of this class by following the tutorial online.
 */

public class XMLQuizParser {
    private static final String ns = null;
    private static final String TAG = "XMLQuizParser";

    /**
     *
     * @param in
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     *
     * This method is passed an input stream, which is in the XML format presented in class.
     * The readQuiz method is called and the created Quiz is returned.
     */
    public Quiz parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            Log.d(TAG, "parse has been called");
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readQuiz(parser);
        } finally {
            in.close();
        }
    }

    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     *
     * This method is called by parse. A parser object is passed and this method begins
     * reading the XML to create the quiz object. Whenever a tag such as <title> or <question>
     *  is encountered, the appropriate method is called.
     */
    private Quiz readQuiz(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.d(TAG, "readQuiz has been called");
        parser.require(XmlPullParser.START_TAG, ns, "quiz");
        String title = null;
        String genre = null;
        List<Question> questions = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);
            } else if (name.equals("genre")){
                genre = readGenre(parser);
            }
            else if (name.equals("question")) {
                questions.add(readQuestion(parser));
            } else {
                skip(parser);
            }
        }
        return new Quiz(title, questions, genre);
    }


    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     * This method is called by readQuiz whenever the <title> tag is encountered. It returns
     * the title as a String.
     */
    private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "title");
        return title;
    }

    private String readGenre(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "genre");
        String genre = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "genre");
        return genre;
    }

    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     *
     * This method is called by readQuiz whenever the <question> tag is encountered.
     * It reads through the question and calls the appropriate methods when necessary to construct
     * a question object which is then returned.
     */
    private Question readQuestion(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "question");
        String text = "";
        Answer correctAnswer = null;
        List<Answer> wrongAnswers = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("text")) {
                text = readText(parser);
            } else if (name.equals("correctanswer")) {
                correctAnswer = new Answer(Answer.answerType.STRING, readText(parser));
            } else if (name.equals("wronganswer")) {
                wrongAnswers.add(new Answer(Answer.answerType.STRING, readText(parser)));
            } else {
                skip(parser);
            }
        }

        return new Question(text, correctAnswer, wrongAnswers.toArray(new Answer[] {}));
    }

    /**
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     *
     * This method is called by readTitle and readQuestion and returns the text read as a String.
     */
    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text;
    }

    /**
     *
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     *
     * This method was obtained from the Android Developers website in the XML parser tutorial page.
     * It is called whenever we encounter an XML tag that we do not care about.
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
