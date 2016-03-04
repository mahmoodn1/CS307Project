package boilerride.com.boilerride;

import android.content.Context;
import android.content.Intent;
import android.net.Credentials;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.Map;

public class ResetPwdActivity extends AppCompatActivity {

    private Firebase myFirebase;
    private SignUpActivity.UserSignUpTask mAuthTask = null;
    //UI references
    private AutoCompleteTextView mEmailView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.reset_pwd_email);

        Button continueButton = (Button) findViewById(R.id.reset_pwd_continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptResetPwd();
            }
        });
    }

    //User enters email, if correct and exists reset pwd, if not nothing
    private void attemptResetPwd(){

        // Reset errors.
        mEmailView.setError(null);
        final String email = mEmailView.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(cancel){
            //There was an error
        }else{
            myFirebase  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
            myFirebase.resetPassword(email, new Firebase.ResultHandler() {
                @Override
                public void onSuccess() {
                    Log.d("RESET PASSWORD SUCCESS:", email);
                    onPostExecute(true, email);
                }
                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.d("RESET PASSWORD ERROR:", firebaseError.getMessage());
                    onPostExecute(false, email);
                }
            });
        }
    }
    protected void onPostExecute(final Boolean success, final String email){
        if (success) {
            Toast.makeText(getApplicationContext(),
                    "We have sent your new password. You have 24h to change it.", Toast.LENGTH_LONG).show();
        }
        // For security we say that we sent the password to the email either if the email exists or not.
        else {
            Toast.makeText(getApplicationContext(),
                    "This email is invalid.", Toast.LENGTH_LONG).show();
        }

    }
    private void startShowRidesActivity()
    {
        Intent intent = new Intent(this, ShowRidesActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains("@purdue.edu");
    }


}
