package boilerride.com.boilerride;

/**
 * Created by nadeemmahmood on 3/23/16.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        if (ShowRidesActivity.matches.size() > 0) {
            ArrayAdapter adapter = new ArrayAdapter<Ride>(context, android.R.layout.simple_spinner_item, ShowRidesActivity.matches) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                    text.setTextColor(Color.BLACK);
                    return view;
                }
            };
            ShowRidesActivity.list.setAdapter(adapter);
        }
        if ((ShowRidesActivity.content) != null && CentralData.notifications)
            notificationManager.notify(id, notification);

    }
}