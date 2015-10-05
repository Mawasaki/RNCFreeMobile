package org.rncteam.rncfreemobile;

/**
 * Created by cedric on 14/07/2015.
 */
import org.rncteam.rncfreemobile.classes.Gps;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.classes.Telephony;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;

import java.util.List;

public class rncmobile extends Application {

    private static Context context;

    // Class Objects
    private static Telephony tel;

    private static Gps gps;
    private static Maps maps;

    public static Activity activity;
    public static View vMaps;
    public static boolean isAppStart;

    public static boolean onTransaction;
    public static boolean markerClicked;

    public static List<RncLogs> listRncLogs;

    public static SharedPreferences preferences;

    public void onCreate(){
        super.onCreate();

        rncmobile.context = getApplicationContext();

        // Init main classes
        tel = new Telephony(getAppContext());

        // Initialize specific class
        gps = new Gps(context);
        maps = new Maps();

        // Signal listener
        //tel.setSignalListener();

        // Enable GPS
        gps.enableGps();

        // Get preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // Application start
        isAppStart = true;

        // Transaction with http server
        onTransaction = false;
    }

    public static Context getAppContext() {
        return context;
    }

    public static Telephony getTelephony() {
        return tel;
    }

    public static Gps getGps() {
        return rncmobile.gps;
    }

    public static Maps getMaps() {
        return rncmobile.maps;
    }
}