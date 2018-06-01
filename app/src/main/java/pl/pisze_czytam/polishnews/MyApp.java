/* Class created to get custom color from resources in static QueryUtils
 * - not one of the android colors. I cannot find better way so far. */

package pl.pisze_czytam.polishnews;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
