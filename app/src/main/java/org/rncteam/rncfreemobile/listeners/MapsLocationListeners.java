package org.rncteam.rncfreemobile.listeners;

import android.location.Location;

import com.google.android.gms.maps.GoogleMap;

import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class MapsLocationListeners implements GoogleMap.OnMyLocationChangeListener {

    private final Maps maps;

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
        }
    }

}
