package boilerride.com.boilerride;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView;
import android.widget.ExpandableListAdapter;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.tkeunebr.gravatar.Gravatar;

public class SettingsActivity extends AppCompatActivity {

    private EditText tv_firstname;
    private EditText tv_lastname;
    private EditText tv_phone;

    private String old_email;
    private TextView tv_email;

    private EditText tv_oldpassword;
    private EditText tv_newpassword1;
    private EditText tv_newpassword2;

    private CheckBox emailChecked;
    private CheckBox nameChecked;
    private CheckBox phoneChecked;

    private Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);

        tv_firstname = (EditText)findViewById(R.id.firstname_field);
        tv_lastname = (EditText)findViewById(R.id.lastname_field);

        tv_email = (TextView)findViewById(R.id.email_field);
        tv_phone = (EditText)findViewById(R.id.phone_field);
        emailChecked = (CheckBox)findViewById(R.id.checkBoxEmail);
        nameChecked = (CheckBox)findViewById(R.id.checkBoxName);
        //firstNameChecked = (CheckBox)findViewById(R.id.checkBoxFirstName);
        //lastNameChecked = (CheckBox)findViewById(R.id.checkBoxLastName);
        phoneChecked = (CheckBox)findViewById(R.id.checkBoxPhoneNumber);

        tv_oldpassword = (EditText)findViewById(R.id.oldPassword);
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
                    old_email = user.getEmail();
                    String fullname = user.getFirstName() +" " +user.getLastName();
                    tv_firstname.setText(user.getFirstName());
                    tv_lastname.setText(user.getLastName());

                    tv_email.setText(user.getEmail());
                    tv_phone.setText(user.getPhoneNumber());

                    if(user.getEmailPublic().equals("1")){
                        emailChecked.setChecked(true);
                    }else{
                        emailChecked.setChecked(false);
                    }
                    if(user.getFirstNamePublic().equals("1")){
                        nameChecked.setChecked(true);
                    }else{
                        nameChecked.setChecked(false);
                    }
                    ImageView gravatarImage = (ImageView)findViewById(R.id.img_gravatar);
                    String userGravLink = Gravatar.init().with(user.getEmail()).size(400).build();
                    Picasso.with(getApplicationContext()).load(userGravLink).into(gravatarImage);
                    if (userGravLink.equals("http://www.gravatar.com/avatar/67f9aac2bf854f0624076502a264dc44?&size=400")) {
                        TextView tv = (TextView)findViewById(R.id.gravatarLink);
                        tv.setText("Click here to create profile and add image.");
                        tv.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                intent.setData(Uri.parse("https://signup.wordpress.com/signup/?ref" +
                                        "=oauth2&oauth2_redirect=c54a14df6102768de438a353709921c1%40https%3A%2F%" +
                                        "2Fpublic-api.wordpress.com%2Foauth2%2Fauthorize%2F%3Fclient_id%3D1854%26response_type%" +
                                        "3Dcode%26blog_id%3D0%26state%3Df2737a23f225bfff6b9ca503643d4cac4a2835bd6a65fe26e5c2fde249347eeb" +
                                        "%26redirect_uri%3Dhttps%253A%252F%252Fen.gravatar.com%252Fconnect%252F%253Faction%" +
                                        "253Drequest_access_token%26jetpack-code%26jetpack-user-id%3D0%26action%3Doauth2-login&wpcom_connect=1"));
                                startActivity(intent);
                            }
                        });
                    }
                    /*
                    if(user.getFirstNamePublic().equals("1")){
                        firstNameChecked.setChecked(true);
                    }else{
                        firstNameChecked.setChecked(false);
                    }
                    /*
                    if(user.getLastNamePublic().equals("1")){
                        lastNameChecked.setChecked(true);
                    }else{
                        lastNameChecked.setChecked(false);
                    }
                    */
                    if(user.getPhoneNumberPublic().equals("1")){
                        phoneChecked.setChecked(true);
                    }else{
                        phoneChecked.setChecked(false);
                    }

                    Button submitChanges = (Button) findViewById(R.id.submit_profile_changes_button);
                    submitChanges.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            attemptSubmitChanges();
                        }
                    });
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

        Firebase node = myFirebase.child(CentralData.uid);
        Map<String, Object> newFirstname = new HashMap<String, Object>();
        newFirstname.put("firstName", firstname);
        node.updateChildren(newFirstname);

        Map<String, Object> newLastname = new HashMap<String, Object>();
        newLastname.put("lastName", lastname);
        node.updateChildren(newLastname);

        Map<String, Object> newPhone = new HashMap<String, Object>();
        newPhone.put("phoneNumber", phone);
        node.updateChildren(newPhone);

        String trueValue = "1";
        String falseValue = "0";
        if (emailChecked.isChecked()){
            System.out.println("email checked " + emailChecked.isChecked());
            Map<String, Object> newEmailPublic = new HashMap<String, Object>();
            newEmailPublic.put("emailPublic", trueValue);
            node.updateChildren(newEmailPublic);
        }else{
            System.out.println("email checked " + emailChecked.isChecked());
            Map<String, Object> newEmailPublic = new HashMap<String, Object>();
            newEmailPublic.put("emailPublic", falseValue);
            node.updateChildren(newEmailPublic);
        }

        if (nameChecked.isChecked()){
            Map<String, Object> newFirstNamePublic = new HashMap<String, Object>();
            newFirstNamePublic.put("firstNamePublic", trueValue);
            node.updateChildren(newFirstNamePublic);
            Map<String, Object> newLastNamePublic = new HashMap<String, Object>();
            newLastNamePublic.put("lastNamePublic", trueValue);
            node.updateChildren(newLastNamePublic);
        }else{
            Map<String, Object> newFirstNamePublic = new HashMap<String, Object>();
            newFirstNamePublic.put("firstNamePublic", falseValue);
            node.updateChildren(newFirstNamePublic);
            Map<String, Object> newLastNamePublic = new HashMap<String, Object>();
            newLastNamePublic.put("lastNamePublic", falseValue);
            node.updateChildren(newLastNamePublic);
        }
        /*
        if (lastNameChecked.isChecked()){
            Map<String, Object> newLastNamePublic = new HashMap<String, Object>();
            newLastNamePublic.put("lastNamePublic", trueValue);
            node.updateChildren(newLastNamePublic);
        }else{
            Map<String, Object> newLastNamePublic = new HashMap<String, Object>();
            newLastNamePublic.put("lastNamePublic", falseValue);
            node.updateChildren(newLastNamePublic);
        }
        */
        if (phoneChecked.isChecked()){
            Map<String, Object> newPhonePublic = new HashMap<String, Object>();
            newPhonePublic.put("phoneNumberPublic", trueValue);
            node.updateChildren(newPhonePublic);
        }else{
            Map<String, Object> newEmailPublic = new HashMap<String, Object>();
            newEmailPublic.put("phoneNumberPublic", falseValue);
            node.updateChildren(newEmailPublic);
        }


        Toast.makeText(getApplicationContext(), "Information saved", Toast.LENGTH_LONG).show();
        /*
        Map<String, Object> newEmail = new HashMap<String, Object>();
        newFirstname.put("email", email);
        node.updateChildren(newEmail);
        */
        //Firebase changeEmail = new Firebase("https://luminous-torch-1510.firebaseio.com/");
    }

    private void attemptchangePassword(){
        String oldpassword = tv_oldpassword.getText().toString();
        String newpassword1 = tv_newpassword1.getText().toString();
        String newpassword2 = tv_newpassword2.getText().toString();
        System.out.println("OLD PASSWORD: " + oldpassword);
        System.out.println("NEW PASSWORD1: " + newpassword1);
        System.out.println("NEW PASSWORD2: " + newpassword2);
        if(newpassword1.equals(newpassword2)) {
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
}
