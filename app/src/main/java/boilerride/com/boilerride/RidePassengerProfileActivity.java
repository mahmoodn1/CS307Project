package boilerride.com.boilerride;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import fr.tkeunebr.gravatar.Gravatar;

public class RidePassengerProfileActivity extends AppCompatActivity {

    private TextView tv_firstname;
    private TextView tv_lastname;
    private TextView tv_phone;
    private TextView tv_email;
    private ImageView iv_phone;
    private ImageView iv_texting;
    private ImageView iv_email;
    private ArrayList<String> comments = new ArrayList<String>();
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private GetCommentTask mAuthTask = null;
    public ArrayAdapter adapter;
    public ListView list;

    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ridecreator);
        getSupportActionBar().setTitle("User profile");
        Firebase.setAndroidContext(getApplicationContext());
        attemptPull();

        tv_firstname=(TextView)findViewById(R.id.rideCreator_firstnameF);
        tv_lastname=(TextView)findViewById(R.id.rideCreator_lastnameF);
        tv_email=(TextView)findViewById(R.id.rideCreator_emailF);
        tv_phone=(TextView)findViewById(R.id.rideCreator_phoneF);


        iv_phone = (ImageView)findViewById(R.id.phone_fab);
        iv_texting = (ImageView)findViewById(R.id.text_fab);
        iv_email = (ImageView)findViewById(R.id.email_fab);
        iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String phone_no = tv_phone.getText().toString().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(callIntent);
                } catch (SecurityException se) {

                }
            }
        });

        iv_texting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String phone_no = tv_phone.getText().toString().replaceAll("-", "");
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + phone_no));
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(sendIntent);
                } catch(SecurityException se) {

                }
            }
        });

        iv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String email = tv_email.getText().toString();
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("mailto:" + email));
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(sendIntent);
                } catch(SecurityException se) {

                }
            }
        });

        list = (ListView)findViewById(R.id.comments_listView);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, comments) {
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
            mAuthTask = new GetCommentTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetCommentTask extends AsyncTask<Void, Void, Boolean> {

        public GetCommentTask() {
        }

        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                //Query queryRef = myFirebase.orderByChild("timePosted");
                // Attach an listener to read the data at our rides reference
                        /*CHANGE TO RIDE LINK*/
                Query queryRef = myFirebase.child("users").child(CentralData.passengerKey);

                queryRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                        } else {
                            Log.d("SNAPSHOT EXISTS:", "YAY");
                            User user = snapshot.getValue(User.class);
                            //String email = snapshot.child("email").getValue().toString(); //THIS WORKS TOO
                            System.out.println("EMAIL IS " + user.getEmail());

                            if (user.getEmailPublic().equals("1")) {
                                tv_email.setText(user.getEmail());
                            } else {
                                tv_email.setText("Email not available");
                            }
                            if (user.getFirstNamePublic().equals("1")){
                                tv_firstname.setText(user.getFirstName());
                                tv_lastname.setText(user.getLastName());
                            }else{
                                tv_firstname.setText("Name not available");
                                tv_lastname.setText("");
                            }
                            /**
                            if(user.getLastNamePublic().equals("1")){
                                tv_lastname.setText(user.getLastName());
                            }else{
                                tv_lastname.setText("Not available");
                            }
                             */
                            if(user.getPhoneNumberPublic().equals("1")){
                                tv_phone.setText(user.getPhoneNumber());
                            }else{
                                tv_phone.setText("Phone not available");
                            }

                            ImageView gravatarImage = (ImageView)findViewById(R.id.img_gravatar);
                            String userGravLink = Gravatar.init().with(user.getEmail()).size(400).build();
                            Picasso.with(getApplicationContext()).load(userGravLink).into(gravatarImage);
                            if (userGravLink.equals("http://www.gravatar.com/avatar/67f9aac2bf854f0624076502a264dc44?&size=400")) {
                                TextView tv = (TextView)findViewById(R.id.gravatarLink);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        System.out.println("The read failed: " + firebaseError.getMessage());
                    }
                });



                Query queryComments = myFirebase.child("ratings").child(CentralData.passengerKey);
                queryComments.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null) {
                            Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST comments");
                        } else {
                            Log.d("SNAPSHOT EXISTS:", "comments");
                            for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                System.out.println("POSTSNAPSHOT IS " + postSnapshot.getValue());
                                System.out.println("THE KEY IS " + postSnapshot.getKey());
                                String aux = postSnapshot.child("comment").getValue().toString();
                                System.out.println(aux);
                                comments.add(aux);
                            }
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
        }

        protected void onCancelled() {
            mAuthTask = null;
        }
    }

}
