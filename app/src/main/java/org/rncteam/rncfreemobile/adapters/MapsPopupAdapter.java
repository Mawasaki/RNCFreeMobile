package org.rncteam.rncfreemobile.adapters;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.AnfrInfos;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Rnc;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 01/08/2015.
 */
public class MapsPopupAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "MapsPopupAdapter";

    private View popup=null;
    private LayoutInflater inflater=null;

    private Marker marker;

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

        this.marker = marker;

        Maps maps = rncmobile.getMaps();
        AnfrInfos anfrInfos = maps.getAnfrInfoMarkers(marker);

        TextView title = (TextView)popup.findViewById(R.id.title);
        TextView rnc = (TextView)popup.findViewById(R.id.rnc);
        TextView txt1 = (TextView) popup.findViewById(R.id.txt1);
        TextView txt2 = (TextView) popup.findViewById(R.id.txt2);
        TextView txt3 = (TextView) popup.findViewById(R.id.txt3);
        TextView txt31 = (TextView) popup.findViewById(R.id.txt31);
        TextView txt4 = (TextView) popup.findViewById(R.id.txt4);
        TextView txt41 = (TextView) popup.findViewById(R.id.txt41);
        TextView txt5 = (TextView) popup.findViewById(R.id.txt5);
        TextView txt51 = (TextView) popup.findViewById(R.id.txt51);
        TextView txt6 = (TextView) popup.findViewById(R.id.txt6);

        TextView umts = (TextView) popup.findViewById(R.id.umts);
        TextView lte = (TextView) popup.findViewById(R.id.lte);

        Button bt1 = (Button) popup.findViewById(R.id.bt1);

        /* Title */
        title.setText(marker.getTitle());

        rnc.setText(" / RNC : " + ((anfrInfos.getRnc().NOTHING == true) ? "-" : anfrInfos.getRnc().get_rnc()));

        txt1.setText(anfrInfos.getLieu() + " " + anfrInfos.getAdd1() + " " + anfrInfos.getAdd2() + " " +
                     anfrInfos.getAdd3());

        txt2.setText(anfrInfos.getCp() + " " + anfrInfos.getCommune());

        txt3.setText("Implantation : " + anfrInfos.getImplantation());
        txt31.setText(" / Modification : " + ((anfrInfos.getModification() != "") ? anfrInfos.getModification() : "-"));

        txt4.setText("Activation : " + ((anfrInfos.getActivation() != "") ? anfrInfos.getActivation() : "-"));
        txt41.setText(" / Hauteur : " + ((anfrInfos.getHauteur() != "") ? anfrInfos.getHauteur() + "m" : "-"));

        txt5.setText("Secteurs : " + anfrInfos.getAzimuts().length());
        txt51.setText(" / Propriétaire : " + ((anfrInfos.getProprietaire() != "") ? anfrInfos.getProprietaire() : "-"));

        txt6.setText("Support : " + anfrInfos.getTypeSupport());

        // Tech
        umts.setTextColor((anfrInfos.getRnc().get_tech() == "3G") ? Color.parseColor("#000000") : Color.parseColor("#DDDDDD"));
        lte.setTextColor((anfrInfos.getRnc().get_tech() == "4G") ? Color.parseColor("#000000") : Color.parseColor("#DDDDDD"));

        // Antenna Image
        JSONArray secteurs = anfrInfos.getAzimuts();
        RelativeLayout rl = (RelativeLayout) popup.findViewById(R.id.lyt_ant);

        // Remove old antennas images
        rl.removeAllViewsInLayout();

        for(int i=0;i<secteurs.length();i++) {

            if(secteurs != null) {
                try {
                    ImageView ant = new ImageView(rncmobile.getAppContext());
                    ant.setImageResource(R.drawable.blines);

                    ant.setPivotX(1);
                    ant.setPivotY(1);

                    ant.setRotation(Float.parseFloat(secteurs.getJSONObject(i).getString("AER_NB_AZIMUT")) - 180);

                    rl.addView(ant);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

        // Write potential futur RNC
        Telephony tel = rncmobile.getTelephony();

        Rnc newRnc = new Rnc();
        newRnc = tel.getLoggedRnc();

        // Text
        String text = (anfrInfos.getLieu() != "") ? anfrInfos.getLieu() + " " : "";
        text += (anfrInfos.getAdd1() != "") ? anfrInfos.getAdd1() + " " : "";
        text += (anfrInfos.getAdd2() != "") ? anfrInfos.getAdd2() + " " : "";
        text += (anfrInfos.getAdd3() != "") ? anfrInfos.getAdd3() + " " : "";
        text += (anfrInfos.getCp() != "") ? anfrInfos.getCp() + " " : "";
        text += (anfrInfos.getCommune() != "") ? anfrInfos.getCommune() + " " : "";
        text.toUpperCase();
        newRnc.set_txt(text);

        // Coo
        newRnc.set_lat(anfrInfos.getLat());
        newRnc.set_lon(anfrInfos.getLon());

        tel.setTempNewRnc(newRnc);

        return(popup);
    }

}