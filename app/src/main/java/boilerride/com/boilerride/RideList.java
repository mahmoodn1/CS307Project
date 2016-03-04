package boilerride.com.boilerride;

import java.util.ArrayList;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class RideList {
    private ArrayList<Ride> listofRides;
    Firebase ref;

    public RideList() {
        this.listofRides = new ArrayList <Ride>();
        ref = new Firebase("https://luminous-torch-1510.firebaseio.com/rides");
    }

    public ArrayList<Ride> getRides() {
        // Attach an listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("There are " + snapshot.getChildrenCount() + " blog posts");
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String rideString = postSnapshot.getValue().toString();
                    String[] rideA = rideString.split(" ");
                    System.out.println(rideString);


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
