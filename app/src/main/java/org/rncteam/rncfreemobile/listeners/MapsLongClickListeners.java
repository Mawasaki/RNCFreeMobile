package org.rncteam.rncfreemobile.listeners;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.classes.AnfrInfos;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 02/11/2015.
 */
public class MapsLongClickListeners implements GoogleMap.OnMapLongClickListener {

    Marker marker;

    public MapsLongClickListeners() {
        this.marker = null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Maps maps = rncmobile.getMaps();
        Telephony tel = rncmobile.getTelephony();

        // Fill a anfrInfo object
        AnfrInfos anfrInfos = new AnfrInfos();
        anfrInfos.setRnc(tel.getLoggedRnc());
        anfrInfos.setLat(String.valueOf(latLng.latitude));
        anfrInfos.setLon(String.valueOf(latLng.longitude));
        anfrInfos.setAdd1(((tel.getLoggedRnc().get_txt().equals("-")) ? "-" : tel.getLoggedRnc().get_txt()));

        if(marker == null) {
            marker = maps.getMap().addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("grey"));

            maps.addMarker(marker,anfrInfos);
        } else {
            marker.setPosition(latLng);

            maps.updateMarker(marker,anfrInfos);
        }
    }
}
