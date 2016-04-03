package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;


import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class CreateRideActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    Switch sw;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private CreateRideTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mDestView;
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

    private Firebase myFirebase;
    public double passengers = 1;
    public double fare;
    public double maxPassengers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createride);

        Firebase.setAndroidContext(this);
        myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/");

        // Set up the login form.
        mTitleView = (EditText) findViewById(R.id.createride_title);
        mOriginView = (EditText) findViewById(R.id.createride_origin);
        mDestView = (AutoCompleteTextView) findViewById(R.id.createride_destination);
        populateAutoComplete();


        Button PostButton = (Button) findViewById(R.id.createride_post);
        PostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptPost();
            }
        });

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
                //passengers = (progress + 1);
                Tv_rideSeekers.setText(Integer.toString(progress + 1));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sw=(Switch) findViewById(R.id.createride_switch);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position

                TextView tv1=(TextView) findViewById(R.id.createride_textView1);
                TextView tv2=(TextView) findViewById(R.id.createride_textView2);
                TextView tv3=(TextView) findViewById(R.id.createride_textView3);

                type = isChecked;
                if(isChecked)
                {
                    mSeekbar.setVisibility(View.GONE);
                    mSeekbar2.setVisibility(View.GONE);
                    mSeekbar3.setVisibility(View.VISIBLE);
                    Tv_price.setVisibility(View.GONE);
                    Tv_passengers.setVisibility(View.GONE);
                    Tv_rideSeekers.setVisibility(View.VISIBLE);
                    tv1.setVisibility(View.GONE);
                    tv2.setVisibility(View.GONE);
                    tv3.setVisibility(View.VISIBLE);
                }
                else
                {
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


        Bundle extras = getIntent().getExtras();
        int rideId;

        if (extras != null) {
            rideId = extras.getInt("ChangeRide");
            Ride ride=CentralData.RideList.get(rideId);
            mTitleView.setText(ride.title);
            mOriginView.setText(ride.origin);
            mDestView.setText(ride.destination);
            sw.setChecked(ride.type);

            if(!ride.type)
            {
                int progr=(int)(ride.fare*100);
                mSeekbar.setProgress(progr);
                mSeekbar2.setProgress(((int)ride.numOfPassengers)-1);
            }
            else
            {
                mSeekbar3.setProgress((int)ride.numOfPassengers-1);
            }

            // and get whatever type user account id is
        }

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mDestView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
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
        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        DatePicker dp = (DatePicker) findViewById(R.id.dlgDateTimePickerDate);
        TimePicker tp = (TimePicker) findViewById(R.id.dlgDateTimePickerTime);
        Calendar cal = Calendar.getInstance();
        cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), tp.getCurrentHour(), tp.getCurrentMinute());
        String depart = new SimpleDateFormat("yyyyMMdd_HHmmss").format(cal.getTime());

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
            showProgress(true);
            mAuthTask = new CreateRideTask(passengers, fare, 1, origin, destination, maxPassengers, depart, "forever", time,
                    title, type);
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(CreateRideActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mDestView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
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

        CreateRideTask(double numOfPassengers, double fare, double distance, String origin, String destination, double maxPassengers, String departTime, String arrivalTime, String timePosted,
                       String title, boolean type) {
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
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

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
                if (type == false) {
                    ride1.put("type", "offer");
                }
                else
                    ride1.put("type", "request");

                fireRide.push().setValue(ride1);
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

    private void startShowRidesActivity()
    {
        Intent intent = new Intent(this, ShowRidesActivity.class);
        startActivity(intent);
        finish();
    }
}

