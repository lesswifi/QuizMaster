package compsci290.edu.duke.quizmaster;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by harrisonlundberg on 2/24/17.
 *
 * This is a custom adapter class which is used to supply views to the ListView of the current
 * question. It was necessary to create a custom adapter so that we could accurately display answers
 * of different types. We currently support answers that are Strings and Images, but in the future
 * we could easily support audio as well by adding another if statement to the getView method.
 */

public class AnswersAdapter extends ArrayAdapter {
    private Context mContext;

    private static final String TAG = "AnswersAdapter";

    public AnswersAdapter(Context c, int resource) {
        super(c, resource);
        mContext = c;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Answer a = (Answer) getItem(position);
        if (a.getType() == Answer.answerType.STRING) {
            //Instead of creating a basic text view, instantiate list_text_item
            TextView t = new TextView(mContext);
            t.setText(a.getText());
            t.setTextSize(24.0f);
            return t;
        } else if (a.getType() == Answer.answerType.IMAGE) {
            ImageView img = new ImageView(mContext);
            String path = a.getText();

            //Glide.with(this).load("https://an-awesome-image.com").into(img);

            int imageID = mContext.getResources().getIdentifier(path, "drawable", "compsci290.edu.duke.quizmaster");

            if (imageID == 0) {
                Log.d(TAG, "imageID is zero");
            }

            img.setImageResource(imageID);
            return img;
        } else if (a.getType() == Answer.answerType.IMAGE_URL) {
            String imageUrl = a.getText();
            ImageView img = new ImageView(mContext);
            //img.setLayoutParams(new ViewGroup.LayoutParams(100,100));
            Glide.with(mContext).load(imageUrl).override(500,500).into(img);
            return img;
        } else {
            Log.d(TAG, "An invalid answer type was passed in.");
            return null;
        }
    }
}
