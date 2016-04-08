package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import java.util.Iterator;
import java.util.logging.Filter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.*;
import android.widget.TimePicker;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Query;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ShowRidesActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogFragmentListener {



    public Firebase myFirebase;

    //ArrayList<Map<String, Ride>>listofRides2 = new ArrayList<Map<String,Ride>>();
    private ArrayList<String> listofRidesKeys = new ArrayList <String>();

    public static ArrayList<Ride> listofRides = new ArrayList <Ride>();
    public static ArrayList<Ride> matches = new ArrayList<Ride>();


    public static ArrayList<Ride> listofRidesFiltered = new ArrayList <Ride>();
    private ArrayList<String> listofRidesKeysFiltered = new ArrayList <String>();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GetRideTask mAuthTask = null;
    public ArrayAdapter adapter;

    public EditText etSearch;


    public static ListView list;
    public static String content;

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
            case R.id.menuProfile:
                menuSettings();
                return true;
            case R.id.not_on:
                menuNotifications(true);
                return true;
            case R.id.not_off:
                menuNotifications(false);
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
            case R.id.order_by_price:
                filterByPrice();
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

        Firebase.setAndroidContext(this);
        myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");
        attemptPull();

        list = (ListView) findViewById(R.id.showrides_listView);
        registerForContextMenu(list);

        listofRidesFiltered=(ArrayList)listofRides.clone();
        listofRidesKeysFiltered=(ArrayList)listofRidesKeys.clone();

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<Ride>(getApplicationContext(), android.R.layout.simple_spinner_item, listofRidesFiltered) {
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
                CentralData.rideKey = listofRidesKeysFiltered.get(position).toString();
                System.out.println("KEY OF THE RIDE IS " + CentralData.rideKey);
                Intent intent = new Intent(getApplicationContext(), RideActivity.class);
                startActivity(intent);
            }
        });
        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.showrides_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCreateRideActivity();
            }
        });

        etSearch = (EditText) findViewById(R.id.showrides_search);

        etSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                String a = "Hello";

                listofRidesFiltered.clear();
                listofRidesKeysFiltered.clear();


                for(int i = 0; i<listofRides.size();i++)
                {
                    if(listofRides.get(i).title.toLowerCase().contains(s.toString().toLowerCase()))
                    {
                        listofRidesFiltered.add(listofRides.get(i));
                        listofRidesKeysFiltered.add(listofRidesKeys.get(i));
                    }
                }
                adapter.notifyDataSetChanged();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        scheduleNotification(30000);
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

    private void UpdateArrayAdapter()
    {

        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        adapter = new ArrayAdapter<Ride>(getApplicationContext(), android.R.layout.simple_spinner_item, listofRidesFiltered) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.BLACK);
                return view;
            }
        };

        list.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }


    private void menuSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void menuNotifications(boolean status) {
        CentralData.notifications = status;
    }

    private void menuFilter() {
        showFilterDialog();
    }

    private void scheduleNotification(int delay) {
        Notification notification = getNotification();
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification() {
        content = findMatches();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.pu_train)
                        .setContentTitle("We have a ride match for you!")
                        .setContentText(content);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ShowRidesActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
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
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        //mNotificationManager.notify(1, mBuilder.build());
        mBuilder.setAutoCancel(true);
        return mBuilder.build();
    }

    public String findMatches() {
        ArrayList<Ride> userRides = new ArrayList<Ride>();
        for (Ride r : listofRidesFiltered) {
            if((r.createdByUser).equals(CentralData.uid)) {
                userRides.add(r);
            }
        }

        matches = new ArrayList<Ride>();
        for (Ride ride1 : listofRidesFiltered) {
            for (Ride ride2 : userRides) {
                if((ride1.destination.toLowerCase()).equals((ride2.destination.toLowerCase())) && !((ride1.createdByUser).equals(ride2.createdByUser))) {
                    matches.add(ride1);
                }
            }
        }

        if (matches.size() > 0)
            return matches.get(0).destination;

        return null;
    }

    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment editNameDialog = new FilterDialogFragment();

        editNameDialog.show(fm, "HiHiHifragment_edit_name");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.showrides_listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Hello");
            String[] menuItems = new String[1   ];
            menuItems[0] = "Edit ride";
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        //myList.get(listPosition).getTitle();//list item title


        // Check if my ride
        if(CentralData.uid.equals(listofRidesFiltered.get(listPosition).createdByUser)) {
            CentralData.RideList = (ArrayList) listofRidesFiltered.clone();
            Intent intent = new Intent(this, CreateRideActivity.class);
            intent.putExtra("ChangeRide", listPosition); // Pass ride id
            CentralData.rideKey = listofRidesKeysFiltered.get(listPosition).toString();
            startActivity(intent);
        }
        else
            Toast.makeText(this, "The ride is not your ride.", Toast.LENGTH_LONG).show();


        // Open new Activity

        return true;
    }

    @Override
    public void onFinishFilterDialogFragment(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }

    private void filter(Boolean offer, Boolean search)
    {
        ArrayList NewArrayList = new ArrayList<Ride>();
        ArrayList NewArrayList2 = new ArrayList<String>();


        Iterator<Ride> iter = listofRides.iterator();
        int i = 0;
        while(iter.hasNext()){
            Ride item = iter.next();
            Boolean status = item.type;

            if((offer && !status) || (search && status)) {
                NewArrayList.add(item);
                NewArrayList2.add(listofRidesKeys.get(i));
            }
            i = i+1;

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


        listofRidesFiltered=(ArrayList)NewArrayList.clone();
        listofRidesKeysFiltered=(ArrayList)NewArrayList2.clone();

    }

    private void filterByPrice(){
        ArrayList NewArrayList = new ArrayList<Ride>();
        ArrayList NewArrayList2 = new ArrayList<String>();

        ArrayList<Ride> auxList = new ArrayList <Ride>();
        ArrayList<String> auxKeyList = new ArrayList <String>();
        if(listofRidesFiltered.isEmpty()){
            auxList = (ArrayList)listofRides.clone();
            auxKeyList = (ArrayList)listofRidesKeys.clone();
        }else{
            auxList = (ArrayList)listofRidesFiltered.clone();
            auxKeyList = (ArrayList)listofRidesKeysFiltered.clone();
        }


        while(!auxList.isEmpty()){
            double price = 0;
            Ride add = null;
            int index = 0;
            /*Get the ride with the highest price*/
            for ( Ride aux : auxList ){
                if (aux.fare >= price){
                    add = aux;
                    price = aux.fare;
                    index = auxList.indexOf(aux);
                }
            }
            NewArrayList.add(add);
            NewArrayList2.add(auxKeyList.get(index));
            auxList.remove(index);
            auxKeyList.remove(index);
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
        listofRidesFiltered=(ArrayList)NewArrayList.clone();
        listofRidesKeysFiltered=(ArrayList)NewArrayList2.clone();
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
                        CentralData.userRides.clear();
                        System.out.println("There are " + snapshot.getChildrenCount() + " rides");
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            //Ride ride = postSnapshot.getValue(Ride.class);

                            double numOfPassengers = Double.valueOf(postSnapshot.child("numOfPassengers").getValue().toString());
                            double fare = Double.valueOf(postSnapshot.child("fare").getValue().toString());
                            double distance = Double.valueOf(postSnapshot.child("distance").getValue().toString());
                            String origin = postSnapshot.child("origin").getValue().toString();
                            String destination = postSnapshot.child("destination").getValue().toString();
                            double maxPassengers = Double.valueOf(postSnapshot.child("maxPassengers").getValue().toString());
                            String departTime = postSnapshot.child("departTime").getValue().toString();
                            String arrivalTime = postSnapshot.child("arrivalTime").getValue().toString();
                            String timePosted = postSnapshot.child("timePosted").getValue().toString();
                            String title = postSnapshot.child("title").getValue().toString();
                            String type1 = postSnapshot.child("type").getValue().toString();
                            String uid = postSnapshot.child("uid").getValue().toString();
                            String completed1 = postSnapshot.child("completed").getValue().toString();
                            boolean type;
                            if (type1.equals("offer"))
                                type = false;
                            else
                                type = true;

                            boolean completed;
                            if (completed1.equals("false"))
                                completed = false;
                            else
                                completed = true;

                            Ride ride = new Ride(numOfPassengers, fare, distance, origin, destination, maxPassengers, departTime, arrivalTime,
                                    timePosted, title, type, uid, completed);
                            listofRides.add(ride);
                            listofRidesKeys.add(postSnapshot.getKey());

                            if (uid.equals(CentralData.uid)) {
                                CentralData.userRides.add(postSnapshot.getKey());
                            }
                        }
                        listofRidesFiltered=(ArrayList)listofRides.clone();
                        listofRidesKeysFiltered=(ArrayList)listofRidesKeys.clone();
                        UpdateArrayAdapter();
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