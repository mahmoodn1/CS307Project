package boilerride.com.boilerride;

import android.content.Context;

import com.firebase.client.Firebase;

import java.util.ArrayList;
/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class RideList {
    public static ArrayList<Ride> listofRides;
    public Firebase myFirebase = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");
    Context context;
    public RideList(Context context) {
        this.context = context;
        listofRides = new ArrayList <Ride>();
    }


}
