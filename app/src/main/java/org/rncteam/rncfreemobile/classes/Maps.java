package org.rncteam.rncfreemobile.classes;

import android.app.Activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.listeners.MapsChangeListeners;
import org.rncteam.rncfreemobile.listeners.MapsLocationListeners;
import org.rncteam.rncfreemobile.listeners.MapsMarkerClickListeners;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class Maps {
    private static final String TAG = "Maps";

    public GoogleMap mMap;

    private float lastZoom;
    private double lastPosLat;
    private double lastPosLon;

    private Map<Marker, AnfrInfos> markers;

    public Maps() {
        lastZoom = 0;
        lastPosLat = 0.0;
        lastPosLon = 0.0;

        markers = new HashMap<Marker, AnfrInfos>();

        this.mMap = null;
    }

    public void initializeMap(Activity activity) {
        if (lastZoom == 0 && lastPosLat == 0.0 && lastPosLon == 0.0) {
            Telephony tel = rncmobile.getTelephony();

            if (tel != null) {
                if (tel.getLoggedRnc() != null && !tel.getLoggedRnc().NOT_IDENTIFIED) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(tel.getLoggedRnc().get_lat(), tel.getLoggedRnc().get_lon()))
                            .zoom(12.0f)
                            .bearing(0)
                            .build();

                    this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(46.71109, 1.7191036), 5.0f));
                }
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(46.71109, 1.7191036), 5.0f));
            }

        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastPosLat, lastPosLon), lastZoom));
        }

        setMapMyLocationEnabled(true);

        setCameraListener();
        setLocationListener(activity);
        setMarkerClickListener(activity);
    }

    public void setMapMyLocationEnabled(boolean enabled) {
        mMap.setMyLocationEnabled(enabled);
    }

    public void setCameraListener() {
        MapsChangeListeners mapListener = new MapsChangeListeners(this);
        mMap.setOnCameraChangeListener(mapListener);
    }

    public void setLocationListener(Activity activity) {
        MapsLocationListeners mapListener = new MapsLocationListeners(activity);
        mMap.setOnMyLocationChangeListener(mapListener);
    }

    public void setMarkerClickListener(Activity activity) {
        MapsMarkerClickListeners mapMarkerListener = new MapsMarkerClickListeners(activity);
        mMap.setOnMarkerClickListener(mapMarkerListener);
    }

    public void setLastPosLat(double latitude) {
        if(mMap.isMyLocationEnabled())
            this.lastPosLat = latitude;
    }

    public void setLastPosLon(double longitude) {
        if(mMap.isMyLocationEnabled())
            this.lastPosLon = longitude;
    }

    public AnfrInfos getAnfrInfoMarkers(Marker marker) {
        return markers.get(marker);
    }

    public void removeMarkers() {
        Iterator it = markers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Marker m = (Marker) pair.getKey();
            m.remove();
        }
        markers.clear();

    }

    public LatLngBounds getProjection() {
        return this.mMap.getProjection().getVisibleRegion().latLngBounds;
    }

    public void setLastZoom(float zoom) {
        this.lastZoom = zoom;
    }

    public void setCenterCamera(double lat, double lon) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat, lon))
                .zoom(this.lastZoom)
                .bearing(0)
                .build();

        this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void switchMarkerIcon(Rnc newRnc) {
        Marker oldMarker = null;
        Marker newMarker = null;
        if(markers != null && markers.size() > 0) {
            Iterator it = markers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Marker m = (Marker) pair.getKey();
                AnfrInfos anfrInfos = (AnfrInfos) pair.getValue();

                // Find marker with icon Orange
                if (m.getTitle().equals("orange")) oldMarker = m;

                // Find marker to change
                if (anfrInfos.getRnc().get_real_rnc().equals(newRnc.get_real_rnc())) newMarker = m;

            }
            // Switch
            if (oldMarker != null && newMarker != null) {
                oldMarker.setTitle("green");
                oldMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.circle_green));
                newMarker.setTitle("orange");
                newMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.circle_orange));
            }
        }
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setAnfrAntennasMarkers(double lat, double lng, String title, AnfrInfos anfrInfos, int icon) {

        markers.put(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon))),anfrInfos);

    }

}
