package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserSignUpTask mAuthTask = null;

    private Firebase myFirebase;

    private boolean feedback = false;
    /*Random Password at the begining*/
    private String mPassword;
    private SecureRandom mRandom = new SecureRandom();

    // UI references.
    private AutoCompleteTextView mEmailView;
    /*
    private EditText mPasswordView1;
    private EditText mPasswordView2;
    */
    private EditText mFirstName;
    private EditText mLastName;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Firebase.setAndroidContext(this);
        myFirebase  = new Firebase("https://sweltering-fire-447.firebaseio.com/");

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.sign_up_email);
        populateAutoComplete();
        /*
        mPasswordView1 = (EditText) findViewById(R.id.sign_up_password);
        mPasswordView2 = (EditText) findViewById(R.id.repeat_password);
        */
        mFirstName = (EditText) findViewById(R.id.sign_up_firstname);
        mLastName = (EditText) findViewById(R.id.sign_up_lastname);


        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        /*
        mPasswordView1.setError(null);
        mPasswordView2.setError(null);
        */
        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        /*
        String password1 = mPasswordView1.getText().toString();
        String password2 = mPasswordView2.getText().toString();
        */
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        /*
        if (!TextUtils.isEmpty(password1) && !isPasswordValid(password1)) {
            mPasswordView1.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView1;
            cancel = true;
        }
        // Check if passwords are equal
        if (!password1.equals(password2)) {
            mPasswordView2.setError(getString(R.string.error_password_not_equal));
            focusView = mPasswordView2;
            cancel = true;
        }
        */
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

        // Check for a valid first name
        if (TextUtils.isEmpty(firstName)) {
            mFirstName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        }

        // Check for a valid last name
        if (TextUtils.isEmpty(lastName)) {
            mLastName.setError(getString(R.string.error_field_required));
            focusView = mFirstName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            focusView = null;
            mPassword = new BigInteger(130, mRandom).toString(32);
            showProgress(true);
            mAuthTask = new UserSignUpTask(email, mPassword, firstName, lastName);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.contains("@purdue.edu");
    }

    /*
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    */

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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(SignUpActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserSignUpTask extends AsyncTask<Void, Void, Boolean> {
        private final User user;
        private final String mEmail;
        private final String mPassword;
        private final String mFirstname;
        private final String mLastname;

        UserSignUpTask(String email, String password, String Firstname, String Lastname) {
            this.user = new User(email, Firstname, Lastname);
            mEmail = email;
            mPassword = password;
            mFirstname = Firstname;
            mLastname = Lastname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                myFirebase.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        Log.d("CREATE USER SUCCESS:", mEmail + " " + mPassword);

                        Firebase fireUser = myFirebase.child("users");
                        User user = new User(mEmail, mFirstname, mLastname);
                        fireUser.push().setValue(user);

                        myFirebase.resetPassword(mEmail, new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {
                                Log.d("PASSWORD SENT TO USER MAIL :", mEmail);
                                mAuthTask = null;
                                showProgress(false);

                                Toast.makeText(getApplicationContext(),
                                        "Account created succesfully. We sent the password to your email. Please change it before 24h", Toast.LENGTH_LONG).show();
                                SignUpActivity();
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {
                                mAuthTask = null;
                                showProgress(false);
                                Log.d("CREATE PASSWORD ERROR:", firebaseError.getMessage());

                                Toast.makeText(getApplicationContext(),
                                        "Unable to create user account", Toast.LENGTH_LONG).show();
                                SignUpActivity();

                            }
                        });
                        //Query queryref = fireUser.child("email").equalTo(mEmail); check how it works
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Log.d("CREATE USER ERROR:", firebaseError.getMessage());


                        Toast.makeText(getApplicationContext(),
                                "Unable to create user account", Toast.LENGTH_LONG).show();
                        SignUpActivity();
                    }

                });
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }


            // TODO: register the new account here.
            return false;
        }
        /*
        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            if (success) {
                Toast.makeText(getApplicationContext(),
                        "Account created succesfully. We sent the password to your email. Please change it before 24h", Toast.LENGTH_LONG).show();
            } else {
               // Toast.makeText(getApplicationContext(),
                 //       "Unable to create user account3", Toast.LENGTH_LONG).show();
            }
        }
        */
        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void LoginActivity()
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void SignUpActivity()
    {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    public class User{

        private String email;
        private String Firstname;
        private String Lastname;
        public User() {}
        public User(String email, String Firstname, String Lastname) {
            this.email = email;
            this.Firstname = Firstname;
            this.Lastname = Lastname;
        }
        public String getFirstname() {
            return this.Firstname;
        }
        public String getLastname() {
            return this.Lastname;
        }

        public String getEmail(){
            return this.email;
        }
    }


}