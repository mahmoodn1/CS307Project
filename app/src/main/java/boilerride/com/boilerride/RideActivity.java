package boilerride.com.boilerride;

import android.content.Intent;
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
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

public class RideActivity extends AppCompatActivity {

    /*
    private TextView tv_;
    private TextView tv_lastname;
    private TextView tv_phone;
    private TextView tv_email;

    private TextView tv_oldpassword;
    private TextView tv_newpassword1;
    private TextView tv_newpassword2;

    private CheckBox emailChecked;
    private CheckBox firstNameChecked;
    private CheckBox lastNameChecked;
    private CheckBox phoneChecked;

    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        //tv_firstname=(EditText)findViewById(R.id.firstname_field);
        tv_lastname=(EditText)findViewById(R.id.lastname_field);
        tv_email=(TextView)findViewById(R.id.email_field);
        tv_phone=(EditText)findViewById(R.id.phone_field);
        emailChecked = (CheckBox)findViewById(R.id.checkBoxEmail);
        firstNameChecked = (CheckBox)findViewById(R.id.checkBoxFirstName);
        lastNameChecked = (CheckBox)findViewById(R.id.checkBoxLastName);
        phoneChecked = (CheckBox)findViewById(R.id.checkBoxPhoneNumber);

        tv_oldpassword= (EditText)findViewById(R.id.oldPassword);
        tv_newpassword1 = (EditText)findViewById(R.id.newPassword1);
        tv_newpassword2 = (EditText)findViewById(R.id.newPassword2);

        Query queryRef = myFirebase.child(CentralData.uid);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null) {
                    Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                } else {
                    Log.d("SNAPSHOT EXISTS:", "YAY");
                    User user = snapshot.getValue(User.class);
                    //String email = snapshot.child("email").getValue().toString(); //THIS WORKS TOO
                    System.out.println("EMAIL IS "+user.getEmail());
                    //old_email = user.getEmail();
                    //tv_firstname.setText(user.getFirstName());
                    tv_lastname.setText(user.getLastName());
                    tv_email.setText(user.getEmail());
                    tv_phone.setText(user.getPhoneNumber());

                    if(user.getEmailPublic().equals("1")){
                        emailChecked.setChecked(true);
                    }else{
                        emailChecked.setChecked(false);
                    }
                    if(user.getFirstNamePublic().equals("1")){
                        firstNameChecked.setChecked(true);
                    }else{
                        firstNameChecked.setChecked(false);
                    }
                    if(user.getLastNamePublic().equals("1")){
                        lastNameChecked.setChecked(true);
                    }else{
                        lastNameChecked.setChecked(false);
                    }
                    if(user.getPhoneNumberPublic().equals("1")){
                        phoneChecked.setChecked(true);
                    }else{
                        phoneChecked.setChecked(false);
                    }



                }
            }
            //{-KByr1nljsb6SOCvUYmo={phoneNumber=jskskd, firstName=jsksks, lastName=hfksks, email=mliuzhan@purdue.edu}}

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton payment = (FloatingActionButton) findViewById(R.id.payment_fab);
        payment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String packageName = "com.venmo";
                Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pu_train)
                        .setContentTitle("We have a ride match for you!")
                        .setContentText("Someone is offering a ride that matches your criteria, come check it out!");

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, RideActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(RideActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());

        return mBuilder.build();
    }
}
