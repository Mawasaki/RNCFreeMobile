package org.rncteam.rncfreemobile;

import org.rncteam.rncfreemobile.adapters.MapsPopupAdapter;
import org.rncteam.rncfreemobile.classes.Elevation;
import org.rncteam.rncfreemobile.classes.Gps;
import org.rncteam.rncfreemobile.classes.Utils;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Rnc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapsFragment";

    private GoogleMap mMap;
    private Maps maps;
    private Gps gps;
    private Telephony tel;
    private View view;

    private ImageButton btnActionProfile;
    private RelativeLayout lytMapExtProfile;
    private boolean btnOpen = false;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_maps,container,false);
        view = v;

        // Retrive main classes
        maps = rncmobile.getMaps();
        tel = rncmobile.getTelephony();
        gps = rncmobile.getGps();

        // Retrieve UI
        btnActionProfile = (ImageButton) v.findViewById(R.id.btn_action_profile);
        lytMapExtProfile = (RelativeLayout) v.findViewById(R.id.map_ext_profile);

        setUpMapIfNeeded();

        mMap.setInfoWindowAdapter(new MapsPopupAdapter(getActivity().getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);

        handler = new Handler();
        displayLoading.run();

        btnActionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnOpen) {
                    lytMapExtProfile.setVisibility(View.GONE);
                    btnOpen = false;
                }
                else {
                    lytMapExtProfile.setVisibility(View.VISIBLE);
                    btnOpen = true;

                    // draw profile chart
                    if(!tel.getLoggedRnc().NOT_IDENTIFIED
                            && tel.getLoggedRnc().get_lat() != -1.0
                            && tel.getLoggedRnc().get_lon() != -1) {
                        Elevation elevation = new Elevation(getActivity());
                        elevation.initChart();
                        elevation.getData(gps.getLatitude(), gps.getLongitude(),
                                tel.getLoggedRnc().get_lat(), tel.getLoggedRnc().get_lon());
                    } else Toast.makeText(rncmobile.getAppContext(),
                            "Pas de profil: antenne non identifiée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.getTitle().equals("grey") || marker.getTitle().equals("red")) {
            String msg = "Etes-vous sur de vouloir attribuer le RNC " + tel.getLoggedRnc().get_real_rnc() + " à cette addresse ?";
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(msg).setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        rncmobile.markerClicked = false;
        rncmobile.onTransaction = false;

        handler.removeCallbacksAndMessages(displayLoading);
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        displayLoading.run();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {

            }
        }
        maps.setMap(mMap);
        maps.initializeMap(getActivity());
        rncmobile.setMaps(maps);
        rncmobile.onTransaction = false;
    }

    private Runnable displayLoading = new Runnable() {
        public void run() {
            if(rncmobile.onTransaction == true) view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            else view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            handler.postDelayed(this, 1000);
        }
    };



    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Telephony tel = rncmobile.getTelephony();

                    // Update RNC Logs : Adresse, CP, Commune, gps
                    DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                    dbl.open();
                    dbl.updateLogsNewRnc(tel.getMarkedRnc());
                    dbl.close();

                    // Redirect to Logs
                    MainActivity activity = (MainActivity) getActivity();
                    activity.displayView(1);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

}
