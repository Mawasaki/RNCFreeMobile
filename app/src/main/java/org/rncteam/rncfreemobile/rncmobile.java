package org.rncteam.rncfreemobile;

/**
 * Created by cedric on 14/07/2015.
 */
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.classes.Telephony;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;

import java.util.ArrayList;

public class rncmobile extends Application {
    private static final String TAG = "rncmobile";

    private static Context context;

    // Class Objects
    private static Telephony tel;
    private static Maps maps;

    public static boolean isAppStart;

    public static boolean onTransaction;
    public static boolean markerClicked;

    public static ArrayList<RncLogs> listRncLogs;
    public static boolean notifyListLogsHasChanged;

    public static SharedPreferences preferences;
    public static FragmentDrawer fragmentDrawer;

    public static boolean rncDataCharged;

    public void onCreate(){
        super.onCreate();

        rncmobile.context = getApplicationContext();

        // Init main classes
        tel = new Telephony(getAppContext());

        // Initialize specific class
        maps = new Maps();

        // Get preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Application start
        isAppStart = true;

        // Transaction with http server
        onTransaction = false;

        // Data of 20815.csv charged
        rncDataCharged = false;

        // Crash report
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);

        preferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }


    public static Context getAppContext() {
        return context;
    }

    public static Telephony getTelephony() {
        return tel;
    }

    public static Maps getMaps() {
        return rncmobile.maps;
    }

    public static void setMaps(Maps maps) {
        rncmobile.maps = maps;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }

    public static String appVersion() {
        try {
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            String[] v_parts = version.split("-");

            return v_parts[1];
        } catch (PackageManager.NameNotFoundException e) {
            return e.toString();
        }
    }

    public static String appBuild() {
        try {
            //return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            String version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            String[] v_parts = version.split("-");

            return v_parts[2];
        } catch (PackageManager.NameNotFoundException e) {
            return e.toString();
        }
    }

    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e("errorRNC1", ex.toString());

                    LayoutInflater li = (LayoutInflater) rncmobile.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    Intent intentCA = new Intent(li.getContext(), CrashActivity.class);
                    intentCA.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK); // required when starting from Application
                    intentCA.putExtra("crashObject", ex);
                    startActivity(intentCA);

                    System.exit(0); // kill off the crashed app
                }
            };

}