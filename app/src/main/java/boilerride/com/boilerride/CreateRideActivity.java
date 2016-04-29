package boilerride.com.boilerride;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A login screen that offers login via email/password.
 */
public class CreateRideActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    // TimePicker tp;
    //  DatePicker dp;
    public ListView list;
    public double passengers = 1;
    public double fare = 0.3;
    public double maxPassengers = 3;
    Switch sw;
    int year = 2016;
    int month = 6;
    int day = 2;
    int hour = 18;
    int minute = 30;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private CreateRideTask mAuthTask = null;
    // UI references.
    private EditText mDestView;
    private EditText mTitleView;
    private EditText mOriginView;
    private View mProgressView;
    private View mLoginFormView;
    private Ride ride;
    private boolean type;
    private TextView Tv_price;
    private SeekBar mSeekbar;
    private TextView Tv_passengers;
    private SeekBar mSeekbar2;
    private TextView Tv_rideSeekers;
    private SeekBar mSeekbar3;
    private Button DelButton;
    private Button DateButton;
    private Button TimeButton;
    private Firebase myFirebase;
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            year = arg1;
            month = arg2;
            day = arg3;

            DateButton.setText("Date: " + String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
        }
    };
    private TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
            // arg1 = year
            // arg2 = month
            // arg3 = day
            hour = hourOfDay;
            minute = minuteOfHour;

            TimeButton.setText("Time: " + String.valueOf(hour) + ":" + String.valueOf(minute));
        }
    };

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createride);

        Firebase.setAndroidContext(this);
        myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/");

        // Set up the login form.
        mTitleView = (EditText) findViewById(R.id.createride_title);
        mOriginView = (EditText) findViewById(R.id.createride_origin);
        mDestView = (EditText) findViewById(R.id.createride_destination);


        Button DelButton = (Button) findViewById(R.id.createride_BtnDelete);
        DelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteRide();
            }
        });


        Button PostButton = (Button) findViewById(R.id.createride_post);
        PostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPost();
            }
        });

        DateButton = (Button) findViewById(R.id.createride_BtnDate);
        DateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(999);
            }
        });

        TimeButton = (Button) findViewById(R.id.createride_BtnTime);
        TimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(998);
            }
        });

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        Date dt = new Date();
        //year = dt.getYear();
        //month = dt.getMonth();
        //day = dt.getDay();

        hour = dt.getHours();
        minute = dt.getMinutes();


        Tv_price = (TextView) findViewById(R.id.createride_tv_price);
        mSeekbar = (SeekBar) findViewById(R.id.createride_seekBar);

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fare = ((double) progress) / 100;
                Tv_price.setText(Double.toString(fare));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Tv_passengers = (TextView) findViewById(R.id.createride_tv_passengers);

        mSeekbar2 = (SeekBar) findViewById(R.id.createride_seekBar2);

        mSeekbar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxPassengers = (progress + 1);
                Tv_passengers.setText(Integer.toString(progress + 1));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        Tv_rideSeekers = (TextView) findViewById(R.id.createride_tv_NumberSeekers);

        mSeekbar3 = (SeekBar) findViewById(R.id.createride_seekBar3);

        mSeekbar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                passengers = (progress + 1);
                Tv_rideSeekers.setText(Integer.toString(progress + 1));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sw = (Switch) findViewById(R.id.createride_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                TextView tv1 = (TextView) findViewById(R.id.createride_textView1);
                TextView tv2 = (TextView) findViewById(R.id.createride_textView2);
                TextView tv3 = (TextView) findViewById(R.id.createride_textView3);

                type = isChecked;
                if (isChecked) {
                    mSeekbar.setVisibility(View.GONE);
                    mSeekbar2.setVisibility(View.GONE);
                    mSeekbar3.setVisibility(View.VISIBLE);
                    Tv_price.setVisibility(View.GONE);
                    Tv_passengers.setVisibility(View.GONE);
                    Tv_rideSeekers.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.GONE);
                    tv2.setVisibility(View.GONE);
                    tv3.setVisibility(View.VISIBLE);
                } else {
                    mSeekbar.setVisibility(View.VISIBLE);
                    mSeekbar2.setVisibility(View.VISIBLE);
                    mSeekbar3.setVisibility(View.GONE);
                    Tv_price.setVisibility(View.VISIBLE);
                    Tv_passengers.setVisibility(View.VISIBLE);
                    Tv_rideSeekers.setVisibility(View.GONE);
                    tv1.setVisibility(View.VISIBLE);
                    tv2.setVisibility(View.VISIBLE);
                    tv3.setVisibility(View.GONE);
                }
            }
        });


        //  tp = (TimePicker) findViewById(R.id.dlgDateTimePickerTime);
        //  dp = (DatePicker) findViewById(R.id.dlgDateTimePickerDate);


        Bundle extras = getIntent().getExtras();
        int rideId;

        if (extras != null) {
            getSupportActionBar().setTitle("Edit ride");

            rideId = extras.getInt("ChangeRide");
            Ride ride = CentralData.RideList.get(rideId);
            mTitleView.setText(ride.title);
            mOriginView.setText(ride.origin);
            mDestView.setText(ride.destination);
            sw.setChecked(ride.type);

            if (!ride.type) {
                int progr = (int) (ride.fare * 100);
                mSeekbar.setProgress(progr);
                mSeekbar2.setProgress(((int) ride.maxPassengers) - 1);
            } else {
                mSeekbar3.setProgress((int) ride.numOfPassengers - 1);
            }

            int pos = ride.departTime.indexOf(' ');

            String depart_time = ride.departTime.substring(0, pos);
            pos = ride.departTime.indexOf(',');
            String depart_date = ride.departTime.substring(pos + 1);

            pos = depart_time.indexOf(':');

            String Hour = depart_time.substring(0, pos);
            if (ride.departTime.contains("PM")) {
                int hour = Integer.parseInt(Hour);
                hour = hour + 12;
                Hour = String.valueOf(hour);
            }
            String Minute = depart_time.substring(pos + 1);

            pos = depart_date.indexOf(' ');

            String Day = depart_date.substring(0, pos);
            String Month = "";
            if (depart_date.contains("Jan"))
                Month = "1";
            if (depart_date.contains("Feb"))
                Month = "2";
            if (depart_date.contains("Mar"))
                Month = "3";
            if (depart_date.contains("Apr"))
                Month = "4";
            if (depart_date.contains("May"))
                Month = "5";
            if (depart_date.contains("Jun"))
                Month = "6";
            if (depart_date.contains("Jul"))
                Month = "7";
            if (depart_date.contains("Aug"))
                Month = "8";
            if (depart_date.contains("Sep"))
                Month = "9";
            if (depart_date.contains("Oct"))
                Month = "10";
            if (depart_date.contains("Nov"))
                Month = "11";
            if (depart_date.contains("Dec"))
                Month = "12";

            depart_date = depart_date.substring(pos + 1);
            pos = depart_date.indexOf(' ');

            String Year = depart_date.substring(pos + 1);

            // tp.setCurrentHour(Integer.parseInt(hour));
            // tp.setCurrentMinute(Integer.parseInt(minute));

            year = Integer.parseInt(Year);
            month = Integer.parseInt(Month);
            day = Integer.parseInt(Day);

            hour = Integer.parseInt(Hour);
            minute = Integer.parseInt(Minute);

            // dp.updateDate(year, month, day);

            // and get whatever type user account id is
        } else {
            //DelButton.setVisibility(View.GONE);
            View b = findViewById(R.id.createride_BtnDelete);
            b.setVisibility(View.GONE);
        }


        DateButton.setText("Date: " + String.valueOf(month) + "/" + String.valueOf(day) + "/" + String.valueOf(year));
        TimeButton.setText("Time: " + String.valueOf(hour) + ":" + String.valueOf(minute));

    }

    private void DeleteRide() {


        myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");

        Firebase node = myFirebase.child(CentralData.rideKey);
        node.setValue(null);
        // node.removeValue()

        finish();
        startShowRidesActivity();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        if (id == 998) {
            return new TimePickerDialog(this, myTimeListener, hour, minute, true);
        }
        return null;
    }

    /**
     * Attempts to post ride specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual post attempt is made.
     */
    private void attemptPost() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mDestView.setError(null);

        // Store values at the time of the post attempt.
        String destination = mDestView.getText().toString();
        String origin = mOriginView.getText().toString();
        String title = mTitleView.getText().toString();
        String time = new SimpleDateFormat("HH:mm, MM.dd.yyyy").format(Calendar.getInstance().getTime());
        System.out.println("Date::" + new SimpleDateFormat("HH:mm,MM.dd.yyyy").format(Calendar.getInstance().getTime()));
        String time1 = new SimpleDateFormat("h:mm a").format(Calendar.getInstance().getTime());

        System.out.println("timeposted" + time1);

        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, hour, minute);
        String depart = new SimpleDateFormat("h:mm a ,d MMM yyyy").format(cal.getTime());

        boolean cancel = false;
        View focusView = null;

        // Check for a valid Destination
        if (TextUtils.isEmpty(destination)) {
            mDestView.setError(getString(R.string.error_field_required));
            focusView = mDestView;
            cancel = true;
        }

        if (TextUtils.isEmpty(origin)) {
            mDestView.setError(getString(R.string.error_field_required));
            focusView = mOriginView;
            cancel = true;
        }

        if (TextUtils.isEmpty(title)) {
            mDestView.setError(getString(R.string.error_field_required));
            focusView = mTitleView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            double lat1, lat2, long1, long2;
            lat1 = get_lat(origin);
            long1 = get_loc(origin);
            lat2 = get_lat(destination);
            long2 = get_loc(destination);


            double distance = (calculateDistance(lat1, long1, lat2, long2));
            double fare1 = fare;
            fare1 = fare1 * distance;
            fare1 = round(fare1, 2);
            String distanced;
            showProgress(true);
            mAuthTask = new CreateRideTask(passengers, fare1, distance, origin, destination, maxPassengers, depart, "forever", time,
                    title, type, CentralData.uid);
            mAuthTask.execute((Void) null);
        }
    }

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

    private void startShowRidesActivity() {
        Intent intent = new Intent(this, ShowRidesActivity.class);
        startActivity(intent);
        finish();
    }

    public double calculateDistance(double lat1, double long1, double lat2, double long2) {

        Location locA = new Location("locA");
        locA.setLatitude(lat1);
        locA.setLongitude(long1);
        Location locB = new Location("locB");
        locB.setLatitude(lat2);
        locB.setLongitude(long2);

        double distance = locA.distanceTo(locB);
        Log.i("Distance in meters: ", distance + "");
        distance = distance / 1600;
        distance = round(distance, 2);
        return distance;
    }

    public double get_loc(String myLocation) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(myLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Address address = addresses.get(0);
        if (addresses.size() > 0) {

            double longitude = addresses.get(0).getLongitude();

            return longitude;
        }
        return 0;
    }

    public double get_lat(String myLocation) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());


        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(myLocation, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses.size() > 0) {
            double latitude = addresses.get(0).getLatitude();


            return latitude;
        }
        return 0;
    }

    public String timechange(String time) {
        String newtime = "";
        for (int i = 9; i < time.length(); i++) {
            newtime = newtime + time.charAt(i);

        }
        for (int i = 0; i < newtime.length(); i++) {
            newtime = newtime + time.charAt(i);

            if (i == 2) {
                newtime = newtime + ":";
                i--;
            }


        }

        return newtime;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class CreateRideTask extends AsyncTask<Void, Void, Boolean> {
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
        private String uid;

        CreateRideTask(double numOfPassengers, double fare, double distance, String origin, String destination, double maxPassengers, String departTime, String arrivalTime, String timePosted,
                       String title, boolean type, String uid) {
            this.numOfPassengers = numOfPassengers;
            this.fare = fare;
            this.distance = distance;
            this.origin = origin;
            this.destination = destination;
            this.maxPassengers = maxPassengers;
            this.departTime = departTime;
            this.arrivalTime = arrivalTime;
            this.timePosted = timePosted;
            this.title = title;
            this.type = type;
            this.uid = uid;
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.


            Bundle extras = getIntent().getExtras();

            if (extras == null) {

                try {
                    Firebase fireRide = myFirebase.child("rides");
                    Map<String, String> ride1 = new HashMap<String, String>();
                    ride1.put("numOfPassengers", String.valueOf(numOfPassengers));
                    ride1.put("fare", String.valueOf(fare));
                    ride1.put("distance", String.valueOf(distance));
                    ride1.put("origin", origin);
                    ride1.put("destination", destination);
                    ride1.put("maxPassengers", String.valueOf(maxPassengers));
                    ride1.put("departTime", departTime);
                    ride1.put("arrivalTime", arrivalTime);
                    ride1.put("timePosted", timePosted);
                    ride1.put("title", title);
                    ride1.put("uid", uid);
                    ride1.put("completed", "false");
                    if (type == false) {
                        ride1.put("type", "offer");
                    } else
                        ride1.put("type", "request");

                    fireRide.push().setValue(ride1);
                    // Simulate network access.
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return false;
                }


            } else {

                myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");

                Firebase node = myFirebase.child(CentralData.rideKey);


                Map<String, Object> newTitle = new HashMap<String, Object>();
                newTitle.put("title", title);
                node.updateChildren(newTitle);

                Map<String, Object> newOrigin = new HashMap<String, Object>();
                newOrigin.put("origin", origin);
                node.updateChildren(newOrigin);

                Map<String, Object> newNumOfPassengers = new HashMap<String, Object>();
                newNumOfPassengers.put("numOfPassengers", numOfPassengers);
                node.updateChildren(newNumOfPassengers);

                Map<String, Object> newFare = new HashMap<String, Object>();
                newFare.put("fare", fare);
                node.updateChildren(newFare);

                Map<String, Object> newDistance = new HashMap<String, Object>();
                newDistance.put("distance", distance);
                node.updateChildren(newDistance);

                Map<String, Object> newDestination = new HashMap<String, Object>();
                newDestination.put("origin", origin);
                node.updateChildren(newDestination);

                Map<String, Object> newMaxPassengers = new HashMap<String, Object>();
                newMaxPassengers.put("maxPassengers", maxPassengers);
                node.updateChildren(newMaxPassengers);

                Map<String, Object> newDepartTime = new HashMap<String, Object>();
                newDepartTime.put("departTime", departTime);
                node.updateChildren(newDepartTime);

                Map<String, Object> newArrivalTime = new HashMap<String, Object>();
                newArrivalTime.put("arrivalTime", arrivalTime);
                node.updateChildren(newArrivalTime);


            }


            // TODO: register the new account here.
            return true;
        }

        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                startShowRidesActivity();
            } else {
                mTitleView.setError("Ride could not be created. Try again.");
                mTitleView.requestFocus();
            }
        }

        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

    }
}

