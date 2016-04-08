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
    /*LINK OF THE RIDE REQUESTED*/
    public static String rideKey;
    public static String rideCreatorUid;
    public static ArrayList<Ride> RideList;
    public static ArrayList<String> peopleInRides = new ArrayList<>();
    public static boolean notifications = true;
    public static String gravatarURL;
    public static String passengerKey;
    public static String distance;
    public static String duration;
    public static ArrayList<String> userRides = new ArrayList<>();

}
