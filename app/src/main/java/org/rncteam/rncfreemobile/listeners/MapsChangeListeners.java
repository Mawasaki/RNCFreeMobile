package org.rncteam.rncfreemobile.listeners;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;

import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.rncmobile;
import org.rncteam.rncfreemobile.tasks.AnfrData;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class MapsChangeListeners implements OnCameraChangeListener {
    private static final String TAG = "MapsChangeListeners";

    private Maps maps;
    boolean zoom_info;

    public MapsChangeListeners(Maps maps) {
        super();
        this.maps = maps;
        zoom_info = false;
    }

    @Override
    public void onCameraChange(CameraPosition position) {

        this.maps.setLastPosLat(position.target.latitude);
        this.maps.setLastPosLon(position.target.longitude);
        this.maps.setLastZoom(position.zoom);

        Telephony tel = rncmobile.getTelephony();

        int dataState = tel.getDataActivity();
        if(dataState != 0) {
            if (position.zoom > 9 && tel != null) {
                if (rncmobile.onTransaction == false && rncmobile.markerClicked == false) {
                    AnfrData anfrData = new AnfrData();
                    anfrData.execute();
                    zoom_info = true;
                } else rncmobile.markerClicked = false;
            } else {
                rncmobile.getMaps().removeMarkers();
                if (zoom_info)
                    Toast.makeText(rncmobile.getAppContext(), "Zoom trop élévé pour afficher les antennes", Toast.LENGTH_SHORT).show();
                zoom_info = false;
            }
        } else {
            Toast.makeText(rncmobile.getAppContext(), "Non connecté", Toast.LENGTH_SHORT).show();
        }
    }
}
