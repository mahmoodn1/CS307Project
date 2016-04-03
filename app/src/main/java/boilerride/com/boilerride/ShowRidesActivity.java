package boilerride.com.boilerride;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Filter;

import android.widget.ArrayAdapter;
import android.view.ViewGroup;
import android.widget.Toast;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ShowRidesActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogFragmentListener {


    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private ListView list;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showrides_menu, menu);
        return true;

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.showrides_listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            menu.setHeaderTitle("Hello");
            String[] menuItems = new String[20];
            menuItems[0] = "Edit ride";
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

 //   @Override
  //  public boolean onContextItemSelected(MenuItem item) {
     //   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
      //  int listPosition = info.position;
      //  myList.get(listPosition).getTitle();//list item title
   // }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menustatus:
                menuStatus();
                return true;
            case R.id.menu_profile:
                menuSettings();
                return true;
            case R.id.menu_filter:
                menuFilter();
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

        list = (ListView) findViewById(R.id.showrides_listView);
        registerForContextMenu(list);
        ArrayList arrayList = new ArrayList<Ride>();
        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
        // and the array that contains the data
        ArrayAdapter adapter = new ArrayAdapter<Ride>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList){
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





        Ride ride1 = new Ride(1, 2, 200, "Purdue Union", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago airport ORD", false, false);
        Ride ride2  = new Ride(1, 3, 100, "Purdue Airport", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago downtown", false, true);
        Ride ride3  = new Ride(1, 2, 200, "Purdue Union", "Indy", 5, "12 am", "2 pm", "10 pm",
                "Ride to Indy", false, false);
        Ride ride4  = new Ride(1, 2, 200, "Purdue Union", "FortWayne", 5, "12 am", "2 pm", "10 pm",
                "Ride to FortWayne", false, false);
        // next thing you have to do is check if your adapter has changed



        arrayList.add(ride1);
        arrayList.add(ride2);
        arrayList.add(ride3);
        arrayList.add(ride4);

        adapter.notifyDataSetChanged();

        FloatingActionButton myFab = (FloatingActionButton)  findViewById(R.id.showrides_fab);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startCreateRideActivity();
            }
        });


    }


    private void startCreateRideActivity()
    {
        Intent intent = new Intent(this, CreateRideActivity.class);
        startActivity(intent);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
    private void menuStatus(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
    private void menuSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);

    }
    private void menuFilter() {


        showFilterDialog();


        //Intent intent = new Intent(this, SettingsActivity.class);
        //startActivity(intent);

    }



    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment editNameDialog = new FilterDialogFragment();

        editNameDialog.show(fm, "HiHiHifragment_edit_name");
    }

    @Override
    public void onFinishFilterDialogFragment(String inputText) {
        Toast.makeText(this, "Hi, " + inputText, Toast.LENGTH_SHORT).show();
    }

    private void filter(Boolean offer, Boolean search)
    {
        ArrayList arrayList = new ArrayList<Ride>();
        ArrayList NewArrayList = new ArrayList<Ride>();



        Ride ride1 = new Ride(1, 2, 200, "Purdue Union", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago airport ORD", false, false);
        Ride ride2  = new Ride(1, 3, 100, "Purdue Airport", "Chicago", 5, "12 am", "2 pm", "10 pm",
                "Ride to Chicago downtown", false, true);
        Ride ride3  = new Ride(1, 2, 200, "Purdue Union", "Indy", 5, "12 am", "2 pm", "10 pm",
                "Ride to Indy", false, false);
        Ride ride4  = new Ride(1, 2, 200, "Purdue Union", "FortWayne", 5, "12 am", "2 pm", "10 pm",
                "Ride to FortWayne", false, false);

        arrayList.add(ride1);
        arrayList.add(ride2);
        arrayList.add(ride3);
        arrayList.add(ride4);


        Iterator<Ride> iter = arrayList.iterator();
        while(iter.hasNext()){
            Ride item = iter.next();

            Boolean status = true;
            // Boolean status = item.status;

            if(((status == true) && (offer == true)) || ((status==false) && (search == true)))
                NewArrayList.add(item);

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

    }

}

