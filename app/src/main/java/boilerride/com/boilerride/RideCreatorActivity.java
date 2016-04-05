package boilerride.com.boilerride;

import android.Manifest;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import fr.tkeunebr.gravatar.Gravatar;

public class RideCreatorActivity extends AppCompatActivity {

    private TextView tv_firstname;
    private TextView tv_lastname;
    private TextView tv_phone;
    private TextView tv_email;

    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ridecreator);

        ImageView gravatarImage = (ImageView)findViewById(R.id.img_gravatar);
        CentralData.gravatarURL= Gravatar.init().with(CentralData.email).size(400).build();
        Picasso.with(getApplicationContext()).load(CentralData.gravatarURL).into(gravatarImage);

        tv_firstname=(TextView)findViewById(R.id.rideCreator_firstnameF);
        tv_lastname=(TextView)findViewById(R.id.rideCreator_lastnameF);
        tv_email=(TextView)findViewById(R.id.rideCreator_emailF);
        tv_phone=(TextView)findViewById(R.id.rideCreator_phoneF);


        /*CHANGE TO RIDE LINK*/
        Query queryRef = myFirebase.child(CentralData.rideCreatorUid);

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
                        tv_email.setText("Not available");
                    }
                    if (user.getFirstNamePublic().equals("1")){
                        tv_firstname.setText(user.getFirstName());
                    }else{
                        tv_firstname.setText("Not available");
                    }
                    if(user.getLastNamePublic().equals("1")){
                        tv_lastname.setText(user.getLastName());
                    }else{
                        tv_lastname.setText("Not available");
                    }
                    if(user.getPhoneNumberPublic().equals("1")){
                        tv_phone.setText(user.getPhoneNumber());
                    }else{
                        tv_phone.setText("Not available");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });

        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[] {  Manifest.permission.CALL_PHONE  },
                    1);
        }

        tv_phone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                String phone_no = tv_phone.getText().toString().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phone_no));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    startActivity(callIntent);
                } catch(SecurityException se) {

                }
            }
        });

        /*
        * The structure should be:
        * key{
        *   email
        *   emailpublic
        *   firstname
        *   ...
        *   comments{
        *       key{
        *           comment 1
        *       }
        *       key {
        *           comment 2
        *       }
        *   }
        * }
        *
        * */
        Query queryComments = myFirebase.child(CentralData.uid).child("comments").orderByChild("comment");
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null) {
                    Log.d("SNAPSHOT NULL:", "ERROR SNAPSHOT DOES NOT EXIST");
                } else {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });


    }

}
