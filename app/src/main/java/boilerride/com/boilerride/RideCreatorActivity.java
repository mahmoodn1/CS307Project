package boilerride.com.boilerride;

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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

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

        tv_firstname=(TextView)findViewById(R.id.rideCreator_firstnameF);
        tv_lastname=(TextView)findViewById(R.id.rideCreator_lastnameF);
        tv_email=(TextView)findViewById(R.id.rideCreator_emailF);
        tv_phone=(TextView)findViewById(R.id.rideCreator_phoneF);

        /*CHANGE TO RIDE LINK*/
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
    }

}
