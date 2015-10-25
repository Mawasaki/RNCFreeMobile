package org.rncteam.rncfreemobile.listeners;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.rncteam.rncfreemobile.classes.Elevation;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 01/08/2015.
 */
public class MapsMarkerClickListeners implements GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapsMarkerClickListener";

    public MapsMarkerClickListeners() {

    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        rncmobile.markerClicked = true;

        rncmobile.getMaps().setCenterCamera(marker.getPosition().latitude, marker.getPosition().longitude);

        marker.showInfoWindow();

        return true;
    }
}
