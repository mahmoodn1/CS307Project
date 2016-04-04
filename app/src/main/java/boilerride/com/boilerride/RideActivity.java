package boilerride.com.boilerride;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.NotificationManager;
import android.app.Notification;
import android.support.v4.app.*;
import android.content.Context;
import android.app.PendingIntent;
import android.app.AlarmManager;
import android.os.SystemClock;

public class RideActivity extends AppCompatActivity {

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

}
