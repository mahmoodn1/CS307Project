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

    public ArrayList<Ride> getRides() {
        Firebase.setAndroidContext(context);
        // Attach an listener to read the data at our rides reference
            myFirebase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("There are " + snapshot.getChildrenCount() + " rides");
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        String rideString = postSnapshot.getValue().toString();
                        String[] rideA = rideString.split(" ");
                        String value;
                        for (int i = 0; i < rideA.length - 1; i++) {
                            rideA[i] = rideA[i].substring(rideA[i].indexOf("=") + 1);
                            rideA[i] = rideA[i].substring(0, rideA[i].indexOf(","));
                        }
                        rideA[rideA.length - 1] = rideA[rideA.length - 1].substring(rideA[rideA.length - 1].indexOf("=") + 1);
                        rideA[rideA.length - 1] = rideA[rideA.length - 1].substring(0, rideA[rideA.length - 1].indexOf("}"));


                        double numOfPassengers = Double.valueOf(rideA[6]);
                        double fare = Double.valueOf(rideA[4]);
                        double distance = Double.valueOf(rideA[3]);
                        String origin = rideA[7];
                        String destination = rideA[2];
                        double maxPassengers = Double.valueOf(rideA[5]);
                        String departTime = rideA[1];
                        String arrivalTime = rideA[0];
                        String timePosted = rideA[8];
                        String title = rideA[9];
                        String type1 = rideA[10];
                        boolean type;
                        if (type1.equals("offer"))
                            type = false;
                        else
                            type = true;


                        Ride ride = new Ride(numOfPassengers, fare, distance, origin, destination, maxPassengers, departTime, arrivalTime,
                                timePosted, title, type);
                        listofRides.add(ride);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    System.out.println("The read failed: " + firebaseError.getMessage());
                }
            });
        return listofRides;
    }

}
