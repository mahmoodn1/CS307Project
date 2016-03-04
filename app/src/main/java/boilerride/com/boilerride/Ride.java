package boilerride.com.boilerride;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class Ride {
    private boolean type; // 0 = offer 1 = request
    private double numOfPassengers;
    private double fare;
    private double distance;
    private String origin;
    private String destination;
    private double maxPassengers;
    private String departTime;
    private String arrivalTime;
    private String timePosted;
    private String title;
    private ArrayList<Passenger> listOfPassengers;

    public Ride(double numOfPassengers, double fare, double distance, String origin, String destination, double maxPassengers, String departTime, String arrivalTime, String timePosted,
                String title, boolean type) {
        this.numOfPassengers = numOfPassengers;
        this.fare = fare;
        this.distance = distance;
        this.origin = origin;
        this.destination = destination;
        this.maxPassengers = maxPassengers;
        this.departTime = departTime;
        this.arrivalTime = arrivalTime;
        this.timePosted = timePosted;
        this.title = title;
        this.type = type;
        listOfPassengers = new ArrayList<Passenger>();
    }

    @Override
    public String toString()
    {
        if(type)
        return "(request) " + title;
        else return "(offer) " + title;
    }

}
