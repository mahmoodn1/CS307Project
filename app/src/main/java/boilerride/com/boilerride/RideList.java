package boilerride.com.boilerride;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.client.Query;
/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class RideList {
    public static ArrayList<Ride> listofRides;
    public Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");
    Context context;
    public RideList(Context context) {
        this.context = context;
        this.listofRides = new ArrayList <Ride>();
    }


}
