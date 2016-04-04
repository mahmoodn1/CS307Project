package boilerride.com.boilerride;

import java.lang.reflect.Array;
import java.util.ArrayList;
/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class Ride {
    public boolean type; // 0 = offer 1 = request
    public double numOfPassengers;
    public double fare;
    private double distance;
    public String origin;
    public String destination;
    public double maxPassengers;
    public String departTime;
    private String arrivalTime;
    private String timePosted;
    public String title;
    private String createdByUser;
    public ArrayList<Passenger> listOfPassengers;

    public Ride(double numOfPassengers, double fare, double distance, String origin, String destination, double maxPassengers, String departTime, String arrivalTime, String timePosted,
                String title, boolean type, String createdByUser) {
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
        this.createdByUser = createdByUser;
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
