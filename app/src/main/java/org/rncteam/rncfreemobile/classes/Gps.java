package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import org.rncteam.rncfreemobile.rncmobile;

public class Gps {

    private Context mContext;

    private LocationManager lm;
    private Location l;
    private GpsStatus gs;
    private Maps maps;

    private boolean isActive;

    LocationListener locationListener;
    GpsStatus.Listener gpsListener;

    public Gps(Context context) {
        mContext = context;
        maps = rncmobile.getMaps();

        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        gs = lm.getGpsStatus(null);

        l = null;

        setGpsStatus(true);
    }

    public LocationManager getLocationManager() {
        return lm;
    }

    private void setLocationListener() {
        locationListener = new LocationStateListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, (float)5.0, locationListener);
    }

    private void setGpsListener() {
        gpsListener = new gpsStateListener();
        lm.addGpsStatusListener(gpsListener);
    }

    private void removeGpsStatusUpdate() {
        lm.removeGpsStatusListener(gpsListener);
    }

    private void removeLocationUpdate() {
        lm.removeUpdates(locationListener);
    }

    public void updateGpsStatus(GpsStatus gpsStatus) {
        gs = gpsStatus;
    }

    public boolean getGpsStatus(){
        return isActive;
    }

    public void setGpsStatus(boolean etat) {
        isActive = etat;
    }

    public void disableGps() {
        setGpsStatus(false);
        removeGpsStatusUpdate();
        removeLocationUpdate();
    }

    public void enableGps() {
        setGpsStatus(true);
        setLocationListener();
        setGpsListener();
    }

    public void setLocation(Location location) {
        l = location;
    }

    public double getLatitude() {
        if(l != null) return l.getLatitude();
        else return 0.0;
    }

    public double getLongitude() {
        if(l != null) return l.getLongitude();
        else return 0.0;
    }

    public int getMaxSatellite() {
        if(gs != null)
            return gs.getMaxSatellites();
        else
            return -1;
    }

    public int getMaxFixSatellite() {
        if(gs != null) {
            Iterable<GpsSatellite> itSat = gs.getSatellites();
            int i = 0;
            for (GpsSatellite gsi : itSat) i++;
            return i;
        }
        else return -1;
    }

    public int getNumSatellite() {
        if(gs != null) {
            Iterable<GpsSatellite> itSat = gs.getSatellites();
            int i = 0;
            for (GpsSatellite gsi : itSat)
                if(gsi.usedInFix()) i++;
            return i;
        }
        else return -1;
    }

    // Gestion des listeners Location
    private class LocationStateListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }
    }

    // Gestion des listeners GPS
    private class gpsStateListener implements GpsStatus.Listener {
        public void onGpsStatusChanged(int event) {
            switch(event){
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    updateGpsStatus(lm.getGpsStatus(null));

                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    updateGpsStatus(lm.getGpsStatus(null));
            }
        }
    }
}

