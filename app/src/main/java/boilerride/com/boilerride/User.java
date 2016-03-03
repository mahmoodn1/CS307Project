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
    private String password;
    private String phoneNumber;
    private Firebase myFb;

    public User(String firstName, String lastName, String email,
                String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        myFb  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
    }

    public boolean changePassword(String newPassword) {
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

    public boolean changeEmail(String newEmail) {
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

    public boolean resetEmail() {
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
