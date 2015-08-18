package org.rncteam.rncfreemobile.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Rnc;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 01/08/2015.
 */
public class MapsPopupAdapter implements GoogleMap.InfoWindowAdapter {
    private View popup=null;
    private LayoutInflater inflater=null;

    public MapsPopupAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            popup=inflater.inflate(R.layout.marker_popup, null);
        }


        TextView title = (TextView)popup.findViewById(R.id.title);
        TextView txt1 = (TextView) popup.findViewById(R.id.txt1);
        TextView txt2 = (TextView) popup.findViewById(R.id.txt2);
        TextView txt3 = (TextView) popup.findViewById(R.id.txt3);
        Button bt1 = (Button) popup.findViewById(R.id.bt1);


        /* Title */
        title.setText(marker.getTitle());

        /* Txt1 */
        txt1.setText("Implantation : 11/07/14");

        /* Txt2 */
        txt2.setText("Activation : 04/06/15");

        /* Txt3 */
        txt3.setText("Type support : Pylone autostable");


        Rnc rnc = rncmobile.getMaps().getRncByMarker(marker);

        Telephony tel = rncmobile.getTelephony();
        bt1.setText("Attribuer le RNC "+tel.getRegisteredWcdmaCell().getRnc()+ " à cette antenne");

        return(popup);
    }
}