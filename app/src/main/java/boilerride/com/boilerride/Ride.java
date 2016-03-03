package boilerride.com.boilerride;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class Ride {
    private int numOfPassengers;
    private double fare;
    private double distance;
    private String origin;
    private String destination;
    private int maxPassengers;
    private String departTime;
    private String arrivalTime;
    private String timePosted;
    private String title;
    private ArrayList<Passenger> listOfPassengers;

    public Ride(int numOfPassengers, int fare, int distance, String origin, String destination, int maxPassengers, String departTime, String arrivalTime, String timePosted,
                String title) {
        this.numOfPassengers = numOfPassengers;
        this.fare = fare;
        this.distance = distance;
        this.origin = origin;
        this.origin = origin;
        this.destination = destination;
        this.maxPassengers = maxPassengers;
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.timePosted = timePosted;
        this.title = title;
        listOfPassengers = new ArrayList<Passenger>();
    }


}
