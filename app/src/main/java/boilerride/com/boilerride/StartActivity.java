package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.text.Html;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class StartActivity extends AppCompatActivity{


    private View mProgressView;
    private View mLoginFormView;
    public static String content;
    private GetRideTask mAuthTask = null;
    public Firebase myFirebase;
    public Context context;
    public ArrayList<Ride> listofRides = new ArrayList <Ride>();
    int type;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        context = getApplicationContext();
        Firebase.setAndroidContext(this);
        myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
        attemptPull();

        TextView tv = (TextView)findViewById(R.id.startTitle);
        String text = "<font color=#CE9A21>Boiler</font><font color=#ffffff>Ride</font>";
        tv.setText(Html.fromHtml(text));

        // Set up the login form.
        Button SignInButton = (Button) findViewById(R.id.activity_start_sign_in);
       SignInButton.setOnClickListener(new OnClickListener() {
           @Override
           public void onClick(View view) {
               startLoginActivity();
           }
       });

        Button SignUpButton = (Button) findViewById(R.id.activity_start_sign_up);
        SignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startSignUpActivity();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }




    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    private void startSignUpActivity() {

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Attempts to post ride specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual post attempt is made.
     */
    public void attemptPull() {
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
            //showProgress(true);
            mAuthTask = new GetRideTask();
            mAuthTask.execute((Void) null);
        }
    }

    public Notification getNotification(int type) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.pu_train)
                        .setContentTitle("BoilerRide Update")
                        .setContentText(content)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(content));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, ShowRidesActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ShowRidesActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        //NotificationManager mNotificationManager =
        //        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        //mNotificationManager.notify(1, mBuilder.build());
        mBuilder.setAutoCancel(true);
        return mBuilder.build();
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
                Query q = myFirebase.child("rides");
                String empty = "";
                q.addChildEventListener(new ChildEventListener() {
                    // Retrieve new posts as they are added to the database
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                        //String key = snapshot.getKey();
                        //if (CentralData.userRides.contains(key)) {
                        //type = 2;
                        //}
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                        String key = snapshot.getKey();

                        if (CentralData.myRides.contains(key))
                                content = "A ride you're part of was modified. Check now!";

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = 1;
                            if (ShowRidesActivity.matches.size() > 0) {
                                adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                            }

                            Notification notification = getNotification(type);
                            if (content != null && CentralData.notifications)
                                notificationManager.notify(id, notification);
                        }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {
                        String key = snapshot.getKey();

                        if (CentralData.myRides.contains(key))
                            content = "A ride you're part of was cancelled. Check now!";

                        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                        int id = 1;
                        if (ShowRidesActivity.matches.size() > 0) {
                            adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                                    text.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };
                        }

                        Notification notification = getNotification(type);
                        if (content != null && CentralData.notifications)
                            notificationManager.notify(id, notification);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String empty) {
                        //String title = (String) snapshot.child("title").getValue();
                        //System.out.println("The blog post titled " + title + " has been deleted");
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                    //... ChildEventListener also defines onChildChanged, onChildRemoved,
                    //    onChildMoved and onCanceled, covered in later sections.
                });


                Query query = myFirebase.child("peopleInRides");
                query.addChildEventListener(new ChildEventListener() {
                    // Retrieve new posts as they are added to the database
                    @Override
                    public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                        String key = snapshot.getKey();
                        if (CentralData.userRides.contains(key)) {
                            content = "Someone joined your ride! Check now!";

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = 1;
                            if (ShowRidesActivity.matches.size() > 0) {
                                adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                            }

                            Notification notification = getNotification(type);
                            if (content != null && CentralData.notifications)
                                notificationManager.notify(id, notification);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                        String key = snapshot.getKey();
                        if (CentralData.userRides.contains(key)) {
                            content = "Someone left or joined your ride! Check now!";

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = 1;
                            if (ShowRidesActivity.matches.size() > 0) {
                                adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                            }

                            Notification notification = getNotification(type);
                            if (content != null && CentralData.notifications)
                                notificationManager.notify(id, notification);
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot snapshot) {
                        String key = snapshot.getKey();
                        if (CentralData.userRides.contains(key)) {
                            content = "Someone left your ride! Check now!";

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = 1;
                            if (ShowRidesActivity.matches.size() > 0) {
                                adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                            }

                            Notification notification = getNotification(type);
                            if (content != null && CentralData.notifications)
                                notificationManager.notify(id, notification);
                        }
                    }

                    @Override
                    public void onChildMoved(DataSnapshot snapshot, String empty) {
                        //String title = (String) snapshot.child("title").getValue();
                        //System.out.println("The blog post titled " + title + " has been deleted");
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                    //... ChildEventListener also defines onChildChanged, onChildRemoved,
                    //    onChildMoved and onCanceled, covered in later sections.
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
            //ShowRidesActivity.list.setAdapter(adapter);
        }

        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}

