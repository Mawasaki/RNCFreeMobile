package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.RncLogs;

/**
 * Created by cedricf_25 on 01/10/2015.
 */
public class LogsMapsActivity extends Activity {
    private static final String TAG = "LogsMapsActivity";

    private RncLogs rnclogs;
    private Maps maps;

    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logs_maps);

        this.rnclogs = (RncLogs) getIntent().getSerializableExtra("logsInfosObject");

        maps = rncmobile.getMaps();

        setUpMapIfNeeded();

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(Double.valueOf(rnclogs.get_lat()), Double.valueOf(rnclogs.get_lon())))
                .zoom(14)
                .bearing(0)
                .build();

        this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(rnclogs.get_lat()), Double.valueOf(rnclogs.get_lon())))
                .title("RNC : " + rnclogs.get_rnc()));
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map2)).getMap();
        }
    }
}
