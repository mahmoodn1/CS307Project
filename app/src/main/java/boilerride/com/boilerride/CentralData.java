package boilerride.com.boilerride;

import java.util.ArrayList;

/**
 * Created by mlz on 4/03/16.
 */
public class CentralData {
    public static String email;
    public static String uid;
    public static String origin;
    public static String destination;
    public static String dist;
    public static String origin1;
    public static String destination1;
    /*LINK OF THE RIDE REQUESTED*/
    public static String rideKey;
    public static String rideCreatorUid;
    public static ArrayList<Ride> RideList;
    public static ArrayList<String> peopleInRides = new ArrayList<>();
    public static ArrayList<String> peopleNameInRides = new ArrayList<String>();
    public static boolean notifications = true;
    public static String gravatarURL;
    public static String passengerKey;
    public static String distance;
    public static String duration;
    public static ArrayList<String> userRides = new ArrayList<>();
    public static ArrayList<String> myRides = new ArrayList<String>();

}
