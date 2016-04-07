package boilerride.com.boilerride;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.app.NotificationManager;
import android.app.Notification;
import android.support.v4.app.*;
import android.content.Context;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.w3c.dom.Text;

public class RideActivity extends AppCompatActivity {

    private TextView tv_completed;
    private TextView tv_arrivalTime;
    private TextView tv_departTime;
    private TextView tv_destination;
    private TextView tv_distance;
    private TextView tv_fare;
    private TextView tv_maxPassengers;
    private TextView tv_numOfPassengers;
    private TextView tv_origin;
    private TextView tv_timePosted;
    private TextView tv_title;
    private TextView tv_type;
    private Button endRide;
    private Button joinRideButton;
    private Button leaveRideButton;
    private Button rateUserButton;
    private String completed;
    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");

    private GetRideTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ride);

        tv_completed=(TextView)findViewById(R.id.completed);
        tv_arrivalTime=(TextView)findViewById(R.id.ride_arrivalF);
        tv_departTime=(TextView)findViewById(R.id.ride_departureF);
        tv_destination=(TextView)findViewById(R.id.ride_destinationF);
        tv_distance=(TextView)findViewById(R.id.ride_distanceF);
        tv_fare=(TextView)findViewById(R.id.ride_fareF);
        tv_maxPassengers=(TextView)findViewById(R.id.ride_maxpassengersF);
        tv_numOfPassengers=(TextView)findViewById(R.id.ride_numberpassengersF);
        tv_origin=(TextView)findViewById(R.id.ride_originf);
        tv_timePosted=(TextView)findViewById(R.id.ride_timepostedF);
        tv_title=(TextView)findViewById(R.id.ride_titleF);
        tv_type=(TextView)findViewById(R.id.ride_typeF);
        attemptPull();

        Button userProfileButton = (Button) findViewById(R.id.ride_userprofileButton);
        userProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((CentralData.uid).equals((CentralData.rideCreatorUid))){
                    attemptRidePassengersActivity();
                }else
                    attemptRideCreatorActivity();
            }
        });

        Button map = (Button)findViewById(R.id.mapbutton1);
        map.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptMapActivity();
            }
        });

        ImageButton androidPay = (ImageButton)findViewById(R.id.payment1_fab);
        androidPay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String packageName = "com.google.android.apps.walletnfcrel";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        ImageButton venmo = (ImageButton)findViewById(R.id.payment2_fab);
        venmo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String packageName = "com.venmo";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
        ImageButton chase = (ImageButton)findViewById(R.id.payment3_fab);
        chase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String packageName = "com.chase.sig.android";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });

        endRide = (Button)findViewById(R.id.end_fab);
        endRide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Firebase rideRef = myFirebase.child(CentralData.rideKey);
                Map<String, Object> currentRide = new HashMap<String, Object>();
                currentRide.put("completed", "true");
                rideRef.updateChildren(currentRide);
                Toast.makeText(getApplicationContext(), "This ride has been ended.",
                        Toast.LENGTH_LONG).show();
            }
        });

        joinRideButton = (Button) findViewById(R.id.ride_joinButton);
        joinRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptJoinRide();
            }
        });

        leaveRideButton = (Button) findViewById(R.id.ride_leaveButton);
        leaveRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLeaveRide();
            }
        });

        rateUserButton = (Button) findViewById(R.id.ride_rateUser);
        rateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if((CentralData.uid).equals((CentralData.rideCreatorUid))){
                    attemptRatePassengersActivity();
                }else
                    attemptRateDriverActivity();
            }
        });
    }

    private void attemptPull() {
        if (mAuthTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new GetRideTask();
            mAuthTask.execute((Void) null);
        }
    }

    private void attemptJoinRide() {

        Firebase myFirebase2 = new Firebase("https://luminous-torch-1510.firebaseio.com/peopleInRides");
        Firebase rideUsers = myFirebase2.child(CentralData.rideKey);
        if (!CentralData.uid.equals(CentralData.rideCreatorUid)) { //not the creator so he can join
            if(!CentralData.peopleInRides.contains(CentralData.uid)){
                Firebase node = myFirebase.child(CentralData.rideKey);
                double numPassengers = Double.parseDouble((tv_numOfPassengers.getText().toString()));
                double maxPassengers = Double.parseDouble((tv_maxPassengers.getText().toString()));
                if (numPassengers < maxPassengers) { //there are space for this ride
                    Map<String, Object> newNumPassengers = new HashMap<String, Object>();
                    numPassengers++;
                    String newPassengers = String.valueOf(numPassengers);
                    newNumPassengers.put("numOfPassengers", newPassengers);
                    node.updateChildren(newNumPassengers);
                    rideUsers.child(CentralData.uid).setValue(CentralData.uid);
                    System.out.println("Everything perfect");
                    Toast.makeText(getApplicationContext(), "Joined", Toast.LENGTH_LONG).show();
                }else{
                    System.out.println("Ride is full so you can't join");
                    Toast.makeText(getApplicationContext(), "Ride is full so you can't join", Toast.LENGTH_LONG).show();
                }

            }else{
                System.out.println("You already joined this ride");
                Toast.makeText(getApplicationContext(), "You already joined this ride", Toast.LENGTH_LONG).show();
            }

        } else { //Check if the user is trying to join twice. - it won't matter because in the database it is added in the same link
            System.out.println("YOU CAN'T JOIN YOUR OWN RIDE");
            Toast.makeText(getApplicationContext(), "You can't join this ride", Toast.LENGTH_LONG).show();
        }
    }

    private void attemptLeaveRide(){
        if(CentralData.peopleInRides.contains(CentralData.uid)) {
            /*Update Database - People in ride*/
            Firebase myFirebase2 = new Firebase("https://luminous-torch-1510.firebaseio.com/peopleInRides");
            Firebase rideUsers = myFirebase2.child(CentralData.rideKey);
            rideUsers.child(CentralData.uid).removeValue();

            /*Update Database - Ride*/
            Firebase node = myFirebase.child(CentralData.rideKey);
            double numPassengers = Double.parseDouble((tv_numOfPassengers.getText().toString()));
            double maxPassengers = Double.parseDouble((tv_maxPassengers.getText().toString()));
            Map<String, Object> newNumPassengers = new HashMap<String, Object>();
            numPassengers--;
            String newPassengers = String.valueOf(numPassengers);
            newNumPassengers.put("numOfPassengers", newPassengers);
            node.updateChildren(newNumPassengers);

            CentralData.peopleInRides.remove((CentralData.uid));
            System.out.println("RIDE LEFT");
            Toast.makeText(getApplicationContext(), "LEFT", Toast.LENGTH_LONG).show();
        }else{
            System.out.println("");
            Toast.makeText(getApplicationContext(), "You are not in this ride", Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetRideTask extends AsyncTask<Void, Void, Boolean> {

        public GetRideTask() {
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Attach an listener to read the data at our rides reference
                Query queryRef = myFirebase.child(CentralData.rideKey);
                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                        } else {
                            Log.d("SNAPSHOT EXISTS:", "YAY");
                            tv_arrivalTime.setText(snapshot.child("arrivalTime").getValue().toString());
                            tv_departTime.setText(snapshot.child("departTime").getValue().toString());
                            tv_destination.setText(snapshot.child("destination").getValue().toString());
                            tv_distance.setText(snapshot.child("distance").getValue().toString());
                            ;
                            tv_fare.setText(snapshot.child("fare").getValue().toString());
                            tv_maxPassengers.setText(snapshot.child("maxPassengers").getValue().toString());
                            tv_numOfPassengers.setText(snapshot.child("numOfPassengers").getValue().toString());
                            tv_origin.setText(snapshot.child("origin").getValue().toString());
                            tv_timePosted.setText(snapshot.child("timePosted").getValue().toString());
                            tv_title.setText(snapshot.child("title").getValue().toString());
                            tv_type.setText(snapshot.child("type").getValue().toString());
                            CentralData.rideCreatorUid = snapshot.child("uid").getValue().toString();
                            CentralData.origin = snapshot.child("origin").getValue().toString();
                            CentralData.destination = snapshot.child("destination").getValue().toString();
                            completed = snapshot.child("completed").getValue().toString();

                            if((CentralData.uid).equals((CentralData.rideCreatorUid)) &&
                                    completed.equals("false")) {
                                endRide.setVisibility(View.VISIBLE);
                                joinRideButton.setVisibility(View.GONE);
                                leaveRideButton.setVisibility(View.GONE);

                            }
                            else
                                endRide.setVisibility((View.GONE));

                            if(completed.equals("true")) {
                                tv_completed.setVisibility(View.VISIBLE);
                                joinRideButton.setVisibility(View.GONE);
                                leaveRideButton.setVisibility(View.GONE);
                                //rateUsersButton.setVisibility(View.VISIBLE);
                            }
                            else
                                tv_completed.setVisibility((View.GONE));
                                //joinRideButton.setVisibility(View.VISIBLE);
                                //leaveRideButton.setVisibility(View.VISIBLE);
                                //rateUsersButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

                Firebase people = new Firebase("https://luminous-torch-1510.firebaseio.com/peopleInRides");
                Query queryRef2 = people.child(CentralData.rideKey);
                queryRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                        } else {
                            Log.d("SNAPSHOT EXISTS:", "YAY2");
                            CentralData.peopleInRides.clear();
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                System.out.println("POSTSNAPSHOT IS " + postSnapshot.getValue());
                                System.out.println("THE KEY IS " + postSnapshot.getKey());
                                String aux = postSnapshot.getKey().toString();
                                System.out.println(aux);
                                CentralData.peopleInRides.add(aux);
                            }
                            if((CentralData.peopleInRides.contains(CentralData.uid) || (CentralData.uid).equals(CentralData.rideCreatorUid)) &&
                                    completed.equals("true")) {
                                rateUserButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                rateUserButton.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

                if(CentralData.peopleInRides.isEmpty()){
                    System.out.println("This is so shitty peopleInRides is empty");
                }else{
                    System.out.println("This is not shitty peopleInRides is NOT empty");
                    for(String i : CentralData.peopleInRides){
                        System.out.println("THE USER IN THIS RIDE IS " + i);
                    }
                }
                // Simulate network access.
                Thread.sleep(500);
            } catch (InterruptedException e) {
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void attemptRideCreatorActivity(){
        Intent intent = new Intent(this, RideCreatorActivity.class);
        startActivity(intent);
    }
    private void attemptRidePassengersActivity(){
        Intent intent = new Intent(this, RidePassengerListActivity.class);
        startActivity(intent);
    }
    private void attemptMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void attemptRatePassengersActivity(){
        Intent intent = new Intent(this, RatePassengerActivity.class);
        startActivity(intent);
    }
    private void attemptRateDriverActivity(){
        Intent intent = new Intent(this, RateDriverActivity.class);
        startActivity(intent);
    }

}
