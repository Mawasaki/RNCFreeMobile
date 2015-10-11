package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.classes.Gps;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 02/10/2015.
 */
public class LogsSetCooActivity extends Activity {
    private static final String TAG = "LogsSetCooActivity";

    private RncLogs rnclogs;
    private Maps maps;
    private Gps gps;

    private GoogleMap mMap;
    private Marker marker;

    private Button btnSavePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_logs_set_coo);

        this.rnclogs = (RncLogs) getIntent().getSerializableExtra("logsInfosObject");

        maps = rncmobile.getMaps();
        gps = rncmobile.getGps();
        gps.enableGps();

        setUpMapIfNeeded();
        mMap.setMyLocationEnabled(true);

        CameraPosition cameraPosition;
        if(rnclogs.get_lat() == -1.0 || rnclogs.get_lon() == -1.0) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(gps.getLatitude(), gps.getLongitude()))
                    .zoom(12)
                    .bearing(0)
                    .build();
        } else {

            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.valueOf(rnclogs.get_lat()), Double.valueOf(rnclogs.get_lon())))
                    .zoom(12)
                    .bearing(0)
                    .build();
        }
        this.mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.valueOf(rnclogs.get_lat()), Double.valueOf(rnclogs.get_lon())))
                .title("RNC : " + rnclogs.get_rnc()));

        this.mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                LatLng centerOfMap = mMap.getCameraPosition().target;

                marker.setPosition(centerOfMap);
            }
        });

        btnSavePos = (Button) findViewById(R.id.btn_save_pos);
        btnSavePos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCoo();
            }
        });
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map3)).getMap();
        }
    }

    private void updateCoo() {
        LatLng newPos = marker.getPosition();

        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbl.open();
        dbr.open();

        ArrayList<RncLogs> lRnc = dbl.findRncLogsByRnc(String.valueOf(rnclogs.get_rnc()));
        for(int i=0;i<lRnc.size();i++) {
            RncLogs rncLog = lRnc.get(i);
            rncLog.set_lat(newPos.latitude);
            rncLog.set_lon(newPos.longitude);
            dbl.updateEditedLogs(rncLog);

            // Update already rncs
            Rnc rnc =  dbr.findRncByNameCid(String.valueOf(rncLog.get_rnc()), String.valueOf(rncLog.get_cid()));
            rnc.set_lat(newPos.latitude);
            rnc.set_lon(newPos.longitude);
            dbr.updateRnc(rnc);
        }

        dbl.close();
        dbr.close();
        this.finish();
    }
}
