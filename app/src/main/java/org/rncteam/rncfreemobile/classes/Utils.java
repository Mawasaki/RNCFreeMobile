package org.rncteam.rncfreemobile.classes;

import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.rncteam.rncfreemobile.database.DatabaseInfo;
import org.rncteam.rncfreemobile.activity.rncmobile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cedricf_25 on 09/10/2015.
 */
final public class Utils {
    private static final String TAG = "RNCLOGS";

    static public String get_fr_datetime(String sDate) {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(sDate);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        long when = date.getTime();
        int flags = 0;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
        flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

        String finalDateTime = android.text.format.DateUtils.formatDateTime(rncmobile.getAppContext(),
                when + TimeZone.getDefault().getOffset(when), flags);

        return finalDateTime;
    }

    static public String get_time(String sDate) {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(sDate);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    static public String get_formated_date_abbrev(String sDate) {
        Date d = get_date_obj(sDate);

        try {
            long prev = d.getTime();
            long now = System.currentTimeMillis();
            return String.valueOf(DateUtils.getRelativeTimeSpanString(prev, now, 0L, DateUtils.FORMAT_ABBREV_ALL));
        } catch (Exception e) {

        }
        return "";
    }

    static public Date get_date_obj(String sDate) {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(sDate);
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        return date;
    }

    public double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));

        return Radius * c;
    }

    public static void storeLastPos(String lastPosLat, String lastPosLon, String lastZoom) {
        DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
        dbi.open();

        dbi.updateInfo("lastPosLat", "0", lastPosLat);
        dbi.updateInfo("lastPosLon", "0", lastPosLon);
        dbi.updateInfo("lastZoom", "0", lastZoom);

        dbi.close();
    }

    public static ArrayList<String> getLastPos() {
        DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
        dbi.open();

        ArrayList<String> lastPos = new ArrayList<>();

        try {
            lastPos.add(0, dbi.getInfo("lastPosLat").get(2).toString());
            lastPos.add(1, dbi.getInfo("lastPosLon").get(2).toString());
            lastPos.add(2, dbi.getInfo("lastZoom").get(2).toString());
        } catch (IndexOutOfBoundsException e) {
            String msg = "Erreur, recréaton des valeurs par défauts";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());

            lastPos.add(0, "46.71109");
            lastPos.add(1, "1.7191036");
            lastPos.add(2, "5.0f");
        }

        dbi.close();

        return lastPos;
    }

}
