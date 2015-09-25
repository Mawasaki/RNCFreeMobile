package org.rncteam.rncfreemobile;

import org.rncteam.rncfreemobile.adapters.MapsPopupAdapter;
import org.rncteam.rncfreemobile.classes.CellWcdma;
import org.rncteam.rncfreemobile.classes.DatabaseLogs;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.listeners.MapsChangeListeners;
import org.rncteam.rncfreemobile.listeners.MapsMarkerClickListeners;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.view.SlidingTabLayout;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {
    private static final String TAG = "MapsFragment";

    private GoogleMap mMap;
    private Maps maps;
    private Telephony tel;
    private View view;

    private Handler handler;

    // UI Objects
    private TextView t_info_1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_maps,container,false);
        view = v;

        // Retrive main classes
        maps = rncmobile.getMaps();
        //mMap = maps.getMap();

        tel = rncmobile.getTelephony();

        setUpMapIfNeeded();

        mMap.setInfoWindowAdapter(new MapsPopupAdapter(getActivity().getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);

        handler = new Handler();
        displayLoading.run();

        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        String msg = "Etes-vous sur de vouloir attribuer le RNC " + tel.getLoggedRnc().get_rnc() + " à cette addresse ?";
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(msg).setPositiveButton("Oui", dialogClickListener)
                .setNegativeButton("Non", dialogClickListener).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            Log.d(TAG, "nMap null");
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                maps.setMap(mMap);
                maps.initializeMap();
            }
        }
        rncmobile.onTransaction = false;
    }

    private Runnable displayLoading = new Runnable() {
        public void run() {
            if(rncmobile.onTransaction == true) view.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            else view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);

            handler.postDelayed(this, 500);
        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    // Insert address to Logs
                    DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                    dbl.open();

                    // Update RNC : Adresse, CP, Commune, gps

                    dbl.close();

                    ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
                    pager.setCurrentItem(1);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

}
