package org.rncteam.rncfreemobile.classes;

import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

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
    private ArrayList<Marker> markers;
    private ArrayList<Marker> markersMax;


    public Maps() {
        lastZoom = 0;
        lastPosLat = 0.0;
        lastPosLon = 0.0;

        polyline = new ArrayList<Polyline>();
        markers = new ArrayList<Marker>();
        markersMax = new ArrayList<Marker>();

        antLat = 0.0;
        antLon = 0.0;
    }

    public void initializeMap() {
        if(lastZoom == 0 && lastPosLat == 0.0 && lastPosLon == 0.0)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(46.71109, 1.7191036), 5.0f));
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastPosLat, lastPosLon), lastZoom));

        mMap.setMyLocationEnabled(true);

    }

    public void setCameraListener(MapsChangeListeners mapListener) {
        mMap.setOnCameraChangeListener(mapListener);
    }

    public void setMarkerClickListener(MapsMarkerClickListeners mapListener) {
        mMap.setOnMarkerClickListener(mapListener);
    }

    public void setLastPosLat(double latitude) {
        if(mMap.isMyLocationEnabled())
            this.lastPosLat = latitude;
    }

    public void setLastPosLon(double longitude) {
        if(mMap.isMyLocationEnabled())
            this.lastPosLon = longitude;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public Rnc getRncByMarker(Marker marker) {
        if(markers.size() > 0)
            for(int i=0;i<markers.size();i++)
                if(markers.get(i).equals(marker)) {
                    DatabaseRnc rncDB = new DatabaseRnc(rncmobile.getAppContext());
                    rncDB.open();

                    Rnc rnc = rncDB.findRncByCoo(marker.getPosition().latitude, marker.getPosition().longitude);

                    rncDB.close();

                    return rnc;
                }
        return null;
    }

    public void removeMarkers() {
        if(markers.size() > 0)
            for(int i=0;i<markers.size();i++)
                markers.get(i).remove();
    }

    public void removeMaxMarkers() {
        if(markersMax.size() > 0)
            for(int i=0;i<markersMax.size();i++)
                markersMax.get(i).remove();
    }

    public void removeLineMeToAntennas() {
        if(polyline.size() > 0)
            for(int i=0;i<polyline.size();i++)
                polyline.get(i).remove();
    }

    public LatLngBounds getProjection() {
        return this.mMap.getProjection().getVisibleRegion().latLngBounds;
    }

    public void setLastZoom(float zoom) {
        this.lastZoom = zoom;
    }

    public void setMap(GoogleMap map) {
        this.mMap = map;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public void setAntLat(double antLat) {
        this.antLat = antLat;
    }

    public void setAntLon(double antLon) {
        this.antLon = antLon;
    }

    public double getAntLat() {
        return this.antLat;
    }

    public double getAntLon() {
        return this.antLon;
    }

    public void setAnfrAntennasMarkers(Rnc rnc, double lat, double lng, String title, int icon) {

        markers.add(mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(icon))));

    }



}
