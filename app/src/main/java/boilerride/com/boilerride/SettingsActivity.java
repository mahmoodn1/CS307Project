package boilerride.com.boilerride;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Firebase.ResultHandler;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private EditText tv_firstname;
    private EditText tv_lastname;
    private EditText tv_phone;

    private String old_email;
    private TextView tv_email;
    //private EditText tv_password_mail;


    private EditText tv_oldpassword;
    private EditText tv_newpassword1;
    private EditText tv_newpassword2;

    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/users");

    private String Key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        tv_firstname=(EditText)findViewById(R.id.firstname_field);
        tv_lastname=(EditText)findViewById(R.id.lastname_field);
        tv_email=(TextView)findViewById(R.id.email_field);
        tv_phone=(EditText)findViewById(R.id.phone_field);
        //tv_password_mail = (EditText)findViewById(R.id.password_field_mail);

        tv_oldpassword= (EditText)findViewById(R.id.oldPassword);
        tv_newpassword1 = (EditText)findViewById(R.id.newPassword1);
        tv_newpassword2 = (EditText)findViewById(R.id.newPassword2);


        Query queryRef = myFirebase.orderByChild("email").equalTo(CentralData.email);

        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot == null) {
                    Log.d("SNAPSHOT NULL:", "FUCK YOU");
                } else {
                    Log.d("LETS SEE:", "SI");

                    String a = snapshot.getValue().toString();
                    System.out.println("TOTAL" + a);
                    //String email = (String) snapshot.child("email").getValue();
                    //User logged =snapshot.getValue(User.class);
                    // System.out.println("SUBSTRING "+a.substring(a.indexOf("phoneNumber=") + "phoneNumber=".length() ,
                    //       a.indexOf(", firstName")));
                    System.out.println("Phone number: " + getPhoneNumber(a));
                    System.out.println("Firstname : " + getFirstName(a));
                    System.out.println("Lastname : " + getLastname(a));
                    System.out.println("Email : " + getEmail(a));
                    System.out.println("Key: " + getKey(a));
                    old_email = getEmail(a);
                    tv_firstname.setText(getFirstName(a));
                    tv_lastname.setText(getLastname(a));
                    tv_email.setText(getEmail(a));
                    tv_phone.setText(getPhoneNumber(a));
                    Key = getKey(a);

                    Button submitChanges = (Button) findViewById(R.id.submit_profile_changes_button);
                    submitChanges.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptSubmitChanges();
                        }
                    });
                    /*
                    Button submitEmail = (Button) findViewById(R.id.changeEmailButton);
                    submitEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptChangeEmail();
                        }
                    });
                    */
                    Button submitPassword = (Button) findViewById(R.id.changePwdButton);
                    submitPassword.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptchangePassword();
                        }
                    });
                }
            }
            //{-KByr1nljsb6SOCvUYmo={phoneNumber=jskskd, firstName=jsksks, lastName=hfksks, email=mliuzhan@purdue.edu}}

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

    private void attemptSubmitChanges(){

        String firstname= tv_firstname.getText().toString();
        String lastname = tv_lastname.getText().toString();
        String phone = tv_phone.getText().toString();
        //String email = tv_email.getText().toString();

        Firebase node = myFirebase.child(Key);
        Map<String, Object> newFirstname = new HashMap<String, Object>();
        newFirstname.put("firstName", firstname);
        node.updateChildren(newFirstname);

        Map<String, Object> newLastname = new HashMap<String, Object>();
        newFirstname.put("lastName", lastname);
        node.updateChildren(newLastname);

        Map<String, Object> newPhone = new HashMap<String, Object>();
        newFirstname.put("phoneNumber", phone);
        node.updateChildren(newPhone);

        Toast.makeText(getApplicationContext(), "Information saved", Toast.LENGTH_LONG).show();
        /*
        Map<String, Object> newEmail = new HashMap<String, Object>();
        newFirstname.put("email", email);
        node.updateChildren(newEmail);
        */
        //Firebase changeEmail = new Firebase("https://luminous-torch-1510.firebaseio.com/");
    }

    /*
    private void attemptChangeEmail(){

        String password= tv_password_mail.getText().toString();
        final String email = tv_email.getText().toString();

        if(isEmailValid(email)) {


            Firebase changeEmail = new Firebase("https://luminous-torch-1510.firebaseio.com/");

            changeEmail.changeEmail(old_email, password, email, new ResultHandler() {
                @Override
                public void onSuccess() {
                    // email changed
                    Firebase node = myFirebase.child(Key);
                    Map<String, Object> newEmail = new HashMap<String, Object>();
                    newEmail.put("email", email);
                    node.updateChildren(newEmail);
                    Toast.makeText(getApplicationContext(), "Email changed", Toast.LENGTH_LONG).show();
                    startSettingsActivity();
                }

                @Override
                public void onError(FirebaseError Error) {
                    Toast.makeText(getApplicationContext(), "Error in changing email", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Email is not valid", Toast.LENGTH_LONG).show();
        }
    }
    */
    private void attemptchangePassword(){
        String oldpassword = tv_oldpassword.getText().toString();
        String newpassword1 = tv_newpassword1.getText().toString();
        String newpassword2 = tv_newpassword2.getText().toString();
        System.out.println("OLD PASSWORD: " + oldpassword);
        System.out.println("NEW PASSWORD1: " + newpassword1);
        System.out.println("NEW PASSWORD2: " + newpassword2);
        if(newpassword1.equals(newpassword2)) {
            System.out.println("ESTOY DENTRO DEL IF");
            Firebase changePassword = new Firebase("https://luminous-torch-1510.firebaseio.com/");
            changePassword.changePassword(old_email, oldpassword, newpassword1, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    // password changed
                    Toast.makeText(getApplicationContext(), "Password changed", Toast.LENGTH_LONG).show();
                    System.out.println("SUCCESS IN CHANGING PASSWORD");
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    // error encountered
                    Toast.makeText(getApplicationContext(), "Error in changing password", Toast.LENGTH_LONG).show();
                    System.out.println("ERROR IN CHANGING PASSWORD");
                }


            });
        }else{
            System.out.println("NO ESTOY DENTRO DEL IF");
            Toast.makeText(getApplicationContext(), "Incorrect Password", Toast.LENGTH_LONG).show();
        }

    }

    private void startSettingsActivity()
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }

    private String getKey(String text){
        return text.substring(text.indexOf("{") + "{".length() , text.indexOf("={"));
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains("@purdue.edu");
    }
    private String getPhoneNumber(String text){
        return text.substring(text.indexOf("phoneNumber=") + "phoneNumber=".length() , text.indexOf(", firstName"));
    }

    private String getFirstName(String text){
        return text.substring(text.indexOf("firstName=") + "firstName=".length() , text.indexOf(", lastName"));
    }

    private String getLastname(String text){
        return text.substring(text.indexOf("lastName=") + "lastName=".length() , text.indexOf(", email"));
    }

    private String getEmail(String text){
        return text.substring(text.indexOf("email=") + "email=".length() , text.indexOf("}}"));
    }

}
