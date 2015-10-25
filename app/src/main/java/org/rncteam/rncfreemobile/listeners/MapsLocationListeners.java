package org.rncteam.rncfreemobile.listeners;

import android.app.Activity;
import android.location.Location;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.classes.Utils;
import org.rncteam.rncfreemobile.rncmobile;

import java.text.DecimalFormat;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class MapsLocationListeners implements GoogleMap.OnMyLocationChangeListener {

    private Maps maps;


    public MapsLocationListeners() {
        this.maps = rncmobile.getMaps();
    }

    @Override
    public void onMyLocationChange(Location location) {
        if(location != null) {
            maps.setLastPosLat(location.getLatitude());
            maps.setLastPosLon(location.getLongitude());
            maps.setLastAlt(location.getAltitude());
            maps.setLastAccu(location.getAccuracy());

            maps.setExtInfoBox();
        } else {
            maps.setLastPosLat(0.0);
            maps.setLastPosLon(0.0);
            maps.setLastAlt(0.0);
            maps.setLastAccu(0);
        }
    }

}
