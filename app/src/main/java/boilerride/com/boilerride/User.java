package boilerride.com.boilerride;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String firstNamePublic;
    private String lastNamePublic;
    private String emailPublic;
    private String phoneNumberPublic;
    //private Firebase myFb;

    public User(String firstName, String lastName, String email, String phoneNumber, String a, String b, String c, String d) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstNamePublic = a;
        this.lastNamePublic = b;
        this.emailPublic = c;
        this.phoneNumberPublic = d;
        //myFb  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
    }
    public User(){}
    public String getFirstName(){
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public String getFirstNamePublic(){return this.firstNamePublic;}
    public String getLastNamePublic(){return this.lastNamePublic;}
    public String getEmailPublic(){return this.emailPublic;}
    public String getPhoneNumberPublic(){return this.phoneNumberPublic;}

    public boolean changePassword(String password, String newPassword, Firebase myFb) {
        myFb.changePassword(email, password, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // password changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
        return true;
    }

    public boolean changeEmail(String password, String newEmail, Firebase myFb) {
        myFb.changeEmail(email, password, newEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // email changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
        return true;
    }

    public boolean resetEmail(Firebase myFb) {
        myFb.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // password reset email sent
            }
            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
        return true;
    }


}
