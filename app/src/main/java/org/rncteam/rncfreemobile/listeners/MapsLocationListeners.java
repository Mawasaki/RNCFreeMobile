package org.rncteam.rncfreemobile.listeners;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.classes.Utils;
import org.rncteam.rncfreemobile.rncmobile;

import java.text.DecimalFormat;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class MapsLocationListeners implements GoogleMap.OnMyLocationChangeListener {

    private Telephony tel;
    private Activity activity;
    private Utils utils;

    // UI Attributes
    private TextView txtMapExtInfosRnc;
    private TextView txtMapExtInfosCid;
    private TextView txtMapExtInfosDistance;
    private TextView txtMapExtInfosTxt;

    public MapsLocationListeners(Activity activity) {
        tel = rncmobile.getTelephony();
        this.activity = activity;
        utils = new Utils();

        // Init UI elements
        txtMapExtInfosRnc = (TextView) this.activity.findViewById(R.id.map_ext_infos_rnc);
        txtMapExtInfosCid = (TextView) this.activity.findViewById(R.id.map_ext_infos_cid);
        txtMapExtInfosDistance = (TextView) this.activity.findViewById(R.id.map_ext_infos_distance);
        txtMapExtInfosTxt = (TextView) this.activity.findViewById(R.id.map_ext_infos_txt);
    }

    @Override
    public void onMyLocationChange(Location location) {
        LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

        txtMapExtInfosRnc.setText("RNC: " + tel.getLoggedRnc().get_rnc());
        txtMapExtInfosCid.setText("CID: " + tel.getLoggedRnc().get_cid());
        txtMapExtInfosTxt.setText(tel.getLoggedRnc().get_txt());

        LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng btsLoc = new LatLng(tel.getLoggedRnc().get_lat(), tel.getLoggedRnc().get_lon());

        Double distance = utils.calculationByDistance(myLoc, btsLoc);
        DecimalFormat kmFormat = new DecimalFormat("#.##");
        DecimalFormat mFormat = new DecimalFormat("##");

        double km = distance / 1;
        double meter = distance * 1000;

        if(km > 1)
            txtMapExtInfosDistance.setText("BTS: " + kmFormat.format(km) + "km");
        else
            txtMapExtInfosDistance.setText("BTS: " + mFormat.format(meter) + "m");

    }

}
