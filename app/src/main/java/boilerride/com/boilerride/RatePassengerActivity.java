package boilerride.com.boilerride;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RatePassengerActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText tv_textview;
    private Button btnSubmit;
    private double score;
    private ArrayAdapter adapter;
    public ListView list;
    public String passengerUid;
    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/ratings");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_passenger);


        tv_textview = (EditText)findViewById(R.id.rate_passenger);

       // list = (ListView) findViewById(R.id.passenger_listView);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, CentralData.peopleInRides) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        // Here, you set the data in your ListView
        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                passengerUid = CentralData.peopleInRides.get(position);
                Toast.makeText(getApplicationContext(), "Rating User: " + passengerUid,
                        Toast.LENGTH_LONG).show();
            }
        });
        addListenerOnRatingBar();
        addListenerOnButton();

    }
    public void addListenerOnRatingBar() {

        ratingBar = (RatingBar) findViewById(R.id.rate_passenger_ratingBar);
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

        ratingBar = (RatingBar) findViewById(R.id.rate_passenger_ratingBar);
        btnSubmit = (Button) findViewById(R.id.rate_passenger_submit);

        //if click on me, then display the current rating value.
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Firebase userComments = myFirebase.child(passengerUid);
                String comment = tv_textview.getText().toString();
                Map<String, String> post1 = new HashMap<String, String>();
                post1.put("reviewer", CentralData.uid);
                post1.put("reviewed", passengerUid);
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

