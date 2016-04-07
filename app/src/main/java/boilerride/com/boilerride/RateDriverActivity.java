package boilerride.com.boilerride;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RateDriverActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText tv_textview;
    private Button btnSubmit;
    private double score;
    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/ratings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_driver);
        tv_textview = (EditText)findViewById(R.id.rate_driver123);
        addListenerOnRatingBar();
        addListenerOnButton();

    }
    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.rate_driver_ratingBar);
        //if rating value is changed,
        //display the current rating value in the result (textview) automatically
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                score = rating;
            }
        });
    }

    public void addListenerOnButton() {

        ratingBar = (RatingBar) findViewById(R.id.rate_driver_ratingBar);
        btnSubmit = (Button) findViewById(R.id.rate_driver_submit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Firebase userComments = myFirebase.child(CentralData.rideCreatorUid);
                String comment = tv_textview.getText().toString();
                Map<String, String> post1 = new HashMap<String, String>();
                post1.put("rate", String.valueOf(score));
                post1.put("comment", comment);
                userComments.push().setValue(post1);
                /*
                Toast.makeText(RateDriverActivity.this,
                        String.valueOf(ratingBar.getRating()), Toast.LENGTH_SHORT).show();
                        */
            }
        });
    }

}
