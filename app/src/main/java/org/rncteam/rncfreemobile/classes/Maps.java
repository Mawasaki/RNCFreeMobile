package org.rncteam.rncfreemobile.classes;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
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
import com.google.android.gms.maps.model.Polyline;

import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.listeners.MapsChangeListeners;
import org.rncteam.rncfreemobile.listeners.MapsMarkerClickListeners;
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
    private double antLat;
    private double antLon;

    private ArrayList<Polyline> polyline;
    private Map<Marker, AnfrInfos> markers;
    private ArrayList<Marker> markersMax;

    public Maps() {
        lastZoom = 0;
        lastPosLat = 0.0;
        lastPosLon = 0.0;

        polyline = new ArrayList<Polyline>();
        markers = new HashMap<Marker, AnfrInfos>();
        markersMax = new ArrayList<Marker>();

        antLat = 0.0;
        antLon = 0.0;

        this.mMap = null;

    }

    public void initializeMap() {
        if(lastZoom == 0 && lastPosLat == 0.0 && lastPosLon == 0.0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(46.71109, 1.7191036), 5.0f));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastPosLat, lastPosLon), lastZoom));

        mMap.setMyLocationEnabled(true);

        setCameraListener();
        setMarkerClickListener();
    }

    public void setCameraListener() {
        MapsChangeListeners mapListener = new MapsChangeListeners(this);
        mMap.setOnCameraChangeListener(mapListener);
    }

    public void setMarkerClickListener() {
        MapsMarkerClickListeners mapMarkerListener = new MapsMarkerClickListeners();
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

    public Map<Marker, AnfrInfos> getMarkers() {
        return markers;
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

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setAnfrAntennasMarkers(Rnc rnc, double lat, double lng, String title, AnfrInfos anfrInfos, int icon) {

        markers.put(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon))),anfrInfos);

    }

}
