package boilerride.com.boilerride;

/**
 * Created by nadeemmahmood on 3/2/16.
 */
public class Passenger extends User {
    private boolean privacy;


    public Passenger(String firstName, String lastName, String email,
                     String password, String phoneNumber) {
        super(firstName, lastName,email,password, phoneNumber);
        privacy = false;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

}
