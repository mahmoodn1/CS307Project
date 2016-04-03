package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.Iterator;
import java.util.logging.Filter;
=======
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
>>>>>>> nadeem

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ShowRidesActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogFragmentListener {

=======
import android.widget.AdapterView.OnItemClickListener;
import android.view.*;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Query;

import static android.Manifest.permission.READ_CONTACTS;

public class ShowRidesActivity extends AppCompatActivity{
>>>>>>> nadeem


    public Firebase myFirebase;
    private ArrayList<Ride> listofRides = new ArrayList <Ride>();
    public int counter = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GetRideTask mAuthTask = null;
    ArrayAdapter adapter;

    // UI references.
    private AutoCompleteTextView mDestView;
    private EditText mTitleView;

    private ListView list;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showrides_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menustatus:
                menuStatus();
                return true;
            case R.id.menu_profile:
                menuSettings();
                return true;
            case R.id.menu_filter:
                menuFilter();
                return true;
            case R.id.type_all:
                filter(true, true);
                return true;
            case R.id.type_offer:
                filter(true, false);
                return true;
            case R.id.type_search:
                filter(false, true);
                return true;
            //case R.id.menu_offer:
            //    menuFilter();
            //    return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showrides);
        // Set up the login form.

        counter = 1;
        Firebase.setAndroidContext(this);
        myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");
        attemptPull();

        list = (ListView) findViewById(R.id.showrides_listView);

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<Ride>(getApplicationContext(), android.R.layout.simple_spinner_item, listofRides) {
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

        /*Ride ride1 = new Ride(1, 2, 200, "Purdue Union", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago airport", true);

        rides.add(ride1);*/
        // next thing you have to do is check if your adapter has changed

<<<<<<< HEAD


        Ride ride1 = new Ride(1, 2, 200, "Purdue Union", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago airport ORD", false, false);
        Ride ride2  = new Ride(1, 3, 100, "Purdue Airport", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago downtown", false, true);
        Ride ride3  = new Ride(1, 2, 200, "Purdue Union", "Indy", 5, "12 am", "2 pm", "10 pm",
                "Ride to Indy", false, false);
        Ride ride4  = new Ride(1, 2, 200, "Purdue Union", "FortWayne", 5, "12 am", "2 pm", "10 pm",
                "Ride to FortWayne", false, false);
        // next thing you have to do is check if your adapter has changed



        arrayList.add(ride1);
        arrayList.add(ride2);
        arrayList.add(ride3);
        arrayList.add(ride4);

        adapter.notifyDataSetChanged();
=======

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(getApplicationContext(), RideActivity.class);
                startActivity(intent);
            }
        });
>>>>>>> nadeem

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.showrides_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCreateRideActivity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * Attempts to post ride specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual post attempt is made.
     */
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
            showProgress(true);
            mAuthTask = new GetRideTask();
            mAuthTask.execute((Void) null);
        }
    }

    private void startCreateRideActivity()
    {
        Intent intent = new Intent(this, CreateRideActivity.class);
        startActivity(intent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    private void menuStatus(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
    private void menuSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
    private void menuFilter() {


        showFilterDialog();


        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);

    }



    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment editNameDialog = new FilterDialogFragment();

        editNameDialog.show(fm, "HiHiHifragment_edit_name");
    }

    @Override
    public void onFinishFilterDialogFragment(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }
<<<<<<< HEAD

    private void filter(Boolean offer, Boolean search)
    {
        ArrayList arrayList = new ArrayList<Ride>();
        ArrayList NewArrayList = new ArrayList<Ride>();



        Ride ride1 = new Ride(1, 2, 200, "Purdue Union", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago airport ORD", false, false);
        Ride ride2  = new Ride(1, 3, 100, "Purdue Airport", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago downtown", false, true);
        Ride ride3  = new Ride(1, 2, 200, "Purdue Union", "Indy", 5, "12 am", "2 pm", "10 pm",
                "Ride to Indy", false, false);
        Ride ride4  = new Ride(1, 2, 200, "Purdue Union", "FortWayne", 5, "12 am", "2 pm", "10 pm",
                "Ride to FortWayne", false, false);

        arrayList.add(ride1);
        arrayList.add(ride2);
        arrayList.add(ride3);
        arrayList.add(ride4);


        Iterator<Ride> iter = arrayList.iterator();
        while(iter.hasNext()){
            Ride item = iter.next();

            Boolean status = true;
            // Boolean status = item.status;

            if(((status == true) && (offer == true)) || ((status==false) && (search == true)))
                NewArrayList.add(item);

        }



        ArrayAdapter adapter = new ArrayAdapter<Ride>(getApplicationContext(), android.R.layout.simple_spinner_item, NewArrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        list.setAdapter(adapter);

    }

}
=======
>>>>>>> nadeem

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

/*            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            // mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            // mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetRideTask extends AsyncTask<Void, Void, Boolean> {
        private boolean type; // 0 = offer 1 = request
        private double numOfPassengers;
        private double fare;
        private double distance;
        private String origin;
        private String destination;
        private double maxPassengers;
        private String departTime;
        private String arrivalTime;
        private String timePosted;
        private String title;

        public GetRideTask() {
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //Query queryRef = myFirebase.orderByChild("timePosted");
                // Attach an listener to read the data at our rides reference
                myFirebase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        listofRides.clear();
                        System.out.println("There are " + snapshot.getChildrenCount() + " rides");
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Ride ride = postSnapshot.getValue(Ride.class);
                            String rideString = postSnapshot.getValue().toString();
                            String[] rideA = rideString.split(",");
                            String value;
                            for (int i = 0; i < rideA.length - 1; i++) {
                                rideA[i] = rideA[i].substring(rideA[i].indexOf("=") + 1);
                            }
                            rideA[rideA.length - 1] = rideA[rideA.length - 1].substring(rideA[rideA.length - 1].indexOf("=") + 1);
                            rideA[rideA.length - 1] = rideA[rideA.length - 1].substring(0, rideA[rideA.length - 1].indexOf("}"));


                            double numOfPassengers = Double.valueOf(rideA[8]);
                            double fare = Double.valueOf(rideA[10]);
                            double distance = Double.valueOf(rideA[7]);
                            String origin = rideA[6];
                            String destination = rideA[2];
                            double maxPassengers = Double.valueOf(rideA[5]);
                            String departTime = rideA[3];
                            String arrivalTime = rideA[0];
                            String timePosted = rideA[1];
                            String title = rideA[4];
                            String type1 = rideA[9];
                            boolean type;
                            if (type1.equals("offer"))
                                type = false;
                            else
                                type = true;


                            Ride ride = new Ride(numOfPassengers, fare, distance, origin, destination, maxPassengers, departTime, arrivalTime,
                                    timePosted, title, type);
                            listofRides.add(ride);
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });
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
            showProgress(false);

        }

        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void startShowRidesActivity()
    {
        Intent intent = new Intent(this, ShowRidesActivity.class);
        startActivity(intent);
        finish();
    }
}