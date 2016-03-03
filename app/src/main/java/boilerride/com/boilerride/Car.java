package boilerride.com.boilerride;

import com.firebase.client.Firebase;

/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class Car {
    private String model;
    private int year;
    private String license;
    private String color;
    private int numOfSeats;
    private Firebase myFb;

    public Car(String model, int year, String license,
               String color, int numOfSeats) {
        this.model = model;
        this.year = year;
        this.license = license;
        this.color = color;
        this.numOfSeats = numOfSeats;
        myFb  = new Firebase("https://luminous-torch-1510.firebaseio.com/");
    }
}
