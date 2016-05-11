package org.rncteam.rncfreemobile.activity;

/**
 * Created by cedric on 14/07/2015.
 */
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.classes.Telephony;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import java.util.ArrayList;

public class rncmobile extends Application {
    private static final String TAG = "rncmobile";

    private static Context context;
    private static Context baseContext;

    // Class Objects
    private static Telephony tel;
    private static Maps maps;

    public static Activity mainActivity;

    public static boolean markerClicked;

    public static ArrayList<RncLogs> listRncLogs;
    public static boolean notifyListLogsHasChanged;

    public static SharedPreferences preferences;
    public static FragmentDrawer fragmentDrawer;

    public static boolean rncDataCharged;

    // Permission
    public static boolean accessCoarseLocation;
    public static boolean accessFineLocation;
    public static boolean writeExternalStorage;
    public static boolean readPhoneState;
    public static int REQUEST_CODE_ASK_PERMISSIONS = 123;

    public static int displayView;

    public static int debugBadCI;
    public static int debugIntTechno;
    public static int debugMncMcc;
    public static int debugLast;
    public static int debugUnknownTechno;
    public static int debugCountDispatch;
    public static int techno;

    public void onCreate(){
        super.onCreate();

        rncmobile.context = getApplicationContext();

        // Get preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Data of 20815.csv charged
        rncDataCharged = false;

        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }


    public static Context getAppContext() {
        return context;
    }

    public static Telephony getTelephony() {
        return tel;
    }

    public static void setTel(Telephony tel) {
        rncmobile.tel = tel;
    }

    public static Maps getMaps() {
        return rncmobile.maps;
    }

    public static void setMaps(Maps maps) {
        rncmobile.maps = maps;
    }

    public static void setMainActivity(Activity activity) {
        mainActivity = activity;
    }
    public static Activity getMainActivity() {
        return mainActivity;
    }

    public static Context getAppBaseContext() {
        return mainActivity.getBaseContext();
    }

    public static void setBaseContext(Context baseContext) {
        rncmobile.baseContext = baseContext;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static String appVersion() {
        try {
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            String[] v_parts = version.split("-");

            return v_parts[0];
        } catch (PackageManager.NameNotFoundException e) {
            return e.toString();
        }
    }

    public static String appBuild() {
        try {
            //return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            String[] v_parts = version.split("-");

            return v_parts[1];
        } catch (PackageManager.NameNotFoundException e) {
            return e.toString();
        }
    }

}