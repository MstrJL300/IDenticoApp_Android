package co.com.identico.appcarnet3.App;
import android.app.Application;
import android.os.SystemClock;

/**
 * Created by Miguel Forero on 11/01/2018.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SystemClock.sleep(2000);
    }
}
