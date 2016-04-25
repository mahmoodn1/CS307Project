package boilerride.com.boilerride;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class RidePassengerListActivity extends AppCompatActivity {

    public static ArrayList<String> peopleInRides = new ArrayList<String>();
    public static ArrayList<String> peopleNamesInRides = new ArrayList<String>();
    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/peopleInRides");
    private GetRideTask mAuthTask = null;
    private static ListView list;
    public ArrayAdapter adapter;
    private TextView nopeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_list);

        nopeople=(TextView)findViewById(R.id.nopassengers);

        getSupportActionBar().setTitle("People in ride");

        list = (ListView) findViewById(R.id.rate_passenger_listView);
        attemptPull();
        if(peopleInRides.isEmpty()){
            nopeople.setVisibility(View.VISIBLE);
        }else{
            nopeople.setVisibility(View.GONE);
        }


        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, peopleNamesInRides) {
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
                //CentralData.rideKey = listofRidesKeysFiltered.get(position);
                System.out.println("KEY OF THE RIDE IS " + CentralData.rideKey);
                CentralData.passengerKey = peopleInRides.get(position);
                Intent intent = new Intent(getApplicationContext(), RidePassengerProfileActivity.class);
                startActivity(intent);
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
                Query queryRef2 = myFirebase.child(CentralData.rideKey);
                queryRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                        } else {
                            Log.d("SNAPSHOT EXISTS:", "YAY2");
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                System.out.println("POSTSNAPSHOT IS " + postSnapshot.getValue());
                                System.out.println("THE KEY IS " + postSnapshot.getKey());
                                String aux = postSnapshot.getKey().toString();
                                System.out.println(aux);
                                if(!peopleInRides.contains(aux)){
                                    peopleInRides.add(aux);
                                    Firebase userProfile = new Firebase("https://luminous-torch-1510.firebaseio.com/users");
                                    Query getName = userProfile.child(aux);
                                    getName.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snap) {
                                            if (snap == null) {
                                                Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                                                peopleNamesInRides.add("Name not available");
                                            } else {
                                                Log.d("SNAPSHOT EXISTS:", "YAY");
                                                User user = snap.getValue(User.class);
                                                if(user==null){
                                                    System.out.println("Error user null");
                                                }
                                                String name = user.getFirstName() + " " + user.getLastName();
                                                peopleNamesInRides.add(name);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            System.out.println("The read failed: " + firebaseError.getMessage());
                                        }
                                    });
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });

                if(peopleInRides.isEmpty()){
                    System.out.println("This is so shitty peopleInRides is empty");
                }else{
                    System.out.println("This is not shitty peopleInRides is NOT empty");
                    for(String i : peopleInRides){
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

}
