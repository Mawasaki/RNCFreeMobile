package org.rncteam.rncfreemobile.classes;

import android.view.View;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.MapsFragment;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.listeners.MapsChangeListeners;
import org.rncteam.rncfreemobile.listeners.MapsLocationListeners;
import org.rncteam.rncfreemobile.listeners.MapsLongClickListeners;
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
    private double lastAlt;
    private float lastAccu;
    private MapsFragment activity;

    private final Map<Marker, AnfrInfos> markers;

    public Maps() {
        ArrayList<String> lastPos = Utils.getLastPos();
        setLastPosLat(Double.valueOf(lastPos.get(0)));
        setLastPosLon(Double.valueOf(lastPos.get(1)));
        setLastZoom(Float.valueOf(lastPos.get(2)));

        lastAlt = 0.0;
        lastAccu = 0;

        markers = new HashMap<>();

        this.mMap = null;
    }

    public void initializeMap(MapsFragment activity) {

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
                            new LatLng(lastPosLat, lastPosLon), lastZoom));
                }
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(lastPosLat, lastPosLon), lastZoom));
            }

        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(lastPosLat, lastPosLon), lastZoom));
        }

        setMapMyLocationEnabled();

        setCameraListener();
        setLocationListener();
        setMarkerClickListener();

        this.activity = activity;
    }

    public void setMapMyLocationEnabled() {
        mMap.setMyLocationEnabled(true);
    }

    public void setCameraListener() {
        MapsChangeListeners mapListener = new MapsChangeListeners(this);
        mMap.setOnCameraChangeListener(mapListener);
    }

    public void setLocationListener() {
        MapsLocationListeners mapListener = new MapsLocationListeners();
        mMap.setOnMyLocationChangeListener(mapListener);
    }

    public void setMarkerClickListener() {
        MapsMarkerClickListeners mapMarkerListener = new MapsMarkerClickListeners();
        mMap.setOnMarkerClickListener(mapMarkerListener);
        MapsLongClickListeners mapsLongClickListeners = new MapsLongClickListeners();
        mMap.setOnMapLongClickListener(mapsLongClickListeners);
    }

    public double getLastPosLat() {
        return lastPosLat;
    }

    public double getLastPosLon() {
        return lastPosLon;
    }

    public double getLastZoom() {
        return lastZoom;
    }

    public double getLastAlt() {
        return lastAlt;
    }

    public float getLastAccu() {
        return lastAccu;
    }

    public void setLastPosLat(double latitude) {
        this.lastPosLat = latitude;
    }

    public void setLastPosLon(double longitude) {
        this.lastPosLon = longitude;
    }

    public void setLastAlt(double lastAlt) {
        if(mMap.isMyLocationEnabled())
            this.lastAlt = lastAlt;
    }

    public void setLastAccu(float lastAccu) {
        if(mMap.isMyLocationEnabled())
            this.lastAccu = lastAccu;
    }

    public boolean isMapInitilized() {
        return mMap != null;
    }

    public AnfrInfos getAnfrInfoMarkers(Marker marker) {
        return markers.get(marker);
    }

    public void removeMarkers() {
        for (Object o : markers.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            Marker m = (Marker) pair.getKey();
            m.remove();
        }
        markers.clear();

    }

    public void addMarker(Marker marker, AnfrInfos anfrInfos) {
        markers.put(marker, anfrInfos);
    }

    public void updateMarker(Marker marker, AnfrInfos anfrInfos) {
        AnfrInfos anfrInfos1 = getAnfrInfoMarkers(marker);
        if(anfrInfos1 != null) {
            markers.put(marker, anfrInfos);
        }
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
        if(markers.size() > 0) {
            for (Object o : markers.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
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

    public void setExtInfoBox() {
        Utils utils = new Utils();
        Telephony tel = rncmobile.getTelephony();

        LatLng myLoc = new LatLng(lastPosLat, lastPosLon);
        LatLng btsLoc = new LatLng(tel.getLoggedRnc().get_lat(), tel.getLoggedRnc().get_lon());

        Double distance = utils.calculationByDistance(myLoc, btsLoc);
        DecimalFormat kmFormat = new DecimalFormat("#.##");
        DecimalFormat mFormat = new DecimalFormat("##");

        double km = distance / 1;
        double meter = distance * 1000;

        String meToBts;

        if (tel.getLoggedRnc().NOT_IDENTIFIED) {
            meToBts = "-";
        } else {
            if (km > 1)
                meToBts = kmFormat.format(km) + "km";
            else
                meToBts = mFormat.format(meter) + "m";
        }

        Rnc rnc = tel.getLoggedRnc();

        activity.mapExtInfo.setVisibility(View.VISIBLE);
        activity.txtMapExtInfosRnc.setText(rnc.get_rnc() + ":"
                + rnc.get_cid() + " | "
                + meToBts + " | "
                + String.valueOf((rnc.get_tech() == 3) ? rnc.getUmtsRscp() : rnc.getLteRsrp()) + " dBm");
        activity.txtMapExtInfosTxt.setText(tel.getLoggedRnc().get_txt());
    }

}
