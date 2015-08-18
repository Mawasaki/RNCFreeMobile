package org.rncteam.rncfreemobile;

import org.rncteam.rncfreemobile.adapters.MapsPopupAdapter;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.listeners.MapsChangeListeners;
import org.rncteam.rncfreemobile.listeners.MapsMarkerClickListeners;
import org.rncteam.rncfreemobile.classes.Telephony;

import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class MapsFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private Maps maps;
    private Telephony tel;
    private View view;

    // UI Objects
    private TextView t_info_1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_maps,container,false);

        // Retrive main classes
        maps = rncmobile.getMaps();
        mMap = maps.getMap();

        tel = rncmobile.getTelephony();

        setUpMapIfNeeded();

        // Maps listeners
        MapsChangeListeners mapListener = new MapsChangeListeners(maps);
        maps.setCameraListener(mapListener);

        MapsMarkerClickListeners mapMarkerListener = new MapsMarkerClickListeners();
        maps.setMarkerClickListener(mapMarkerListener);

        mMap.setInfoWindowAdapter(new MapsPopupAdapter(getActivity().getLayoutInflater()));
        mMap.setOnInfoWindowClickListener(this);

        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(rncmobile.getAppContext(), marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                maps.setMap(mMap);
                maps.initializeMap();
            }
        }
    }

}
