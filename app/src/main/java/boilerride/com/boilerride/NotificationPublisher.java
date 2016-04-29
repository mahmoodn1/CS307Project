package boilerride.com.boilerride;

/**
 * Created by nadeemmahmood on 3/23/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    Context context;
    public static String content;
    private GetRideTask mAuthTask = null;
    public Firebase myFirebase;
    public ArrayList<Ride> listofRides = new ArrayList <Ride>();
    int type;
    ArrayAdapter adapter;



    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Firebase.setAndroidContext(context);
        myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
        attemptPull(intent);


    }

    /**
     * Attempts to post ride specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual post attempt is made.
     */
    public void attemptPull(Intent intent) {
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
            mAuthTask = new GetRideTask(intent);
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
        Intent intent;

        public GetRideTask(Intent intent) {
            this.intent = intent;
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //Query queryRef = myFirebase.orderByChild("timePosted");
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
                        if (CentralData.userRides.contains(key)) {
                            content = ShowRidesActivity.findMatches();
                            if (type == 1) {
                                content = "Someone left or joined your ride! Check now!";
                            }
                            else if (type == 2) {
                                content = "A ride you're part of was modified or cancelled. Check now!";
                            }

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
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
                        //String title = (String) snapshot.child("title").getValue();
                        //System.out.println("The blog post titled " + title + " has been deleted");
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
                            type = 1;
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                        String key = snapshot.getKey();
                        if (CentralData.userRides.contains(key)) {
                            content = ShowRidesActivity.findMatches();
                            if (type == 1) {
                                content = "Someone left or joined your ride! Check now!";
                            }
                            else if (type == 2) {
                                content = "A ride you're part of was modified or cancelled. Check now!";
                            }

                            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

                            int id = intent.getIntExtra(NOTIFICATION_ID, 0);
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
                            type = 1;
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
            ShowRidesActivity.list.setAdapter(adapter);
        }

        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}