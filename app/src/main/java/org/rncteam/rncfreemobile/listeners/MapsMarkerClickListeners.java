package org.rncteam.rncfreemobile.listeners;

import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by cedricf_25 on 01/08/2015.
 */
public class MapsMarkerClickListeners implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapsMarkerClickListener";

    @Override
    public boolean onMarkerClick(final Marker marker) {
        marker.showInfoWindow();

        return true;
    }
}
