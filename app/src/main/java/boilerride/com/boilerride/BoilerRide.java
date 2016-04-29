package boilerride.com.boilerride;

import com.firebase.client.Firebase;

/**
 * Created by nadeemmahmood on 2/27/16.
 */
public class BoilerRide extends android.app.Application {


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
