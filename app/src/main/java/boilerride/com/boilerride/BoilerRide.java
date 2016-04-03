package boilerride.com.boilerride;

import android.app.Application;

import com.firebase.client.Firebase;

import java.util.ArrayList;

/**
 * Created by nadeemmahmood on 2/27/16.
 */
public class BoilerRide extends android.app.Application {

    public static LocationService locationService;

    @Override
    public void onCreate()
    {
        super.onCreate();
        /* Initialize Firebase */
        Firebase.setAndroidContext(this);
        /* Enable disk persistence  */
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }

}
