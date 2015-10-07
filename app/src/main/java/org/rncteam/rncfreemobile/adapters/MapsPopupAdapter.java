package org.rncteam.rncfreemobile.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.AnfrInfos;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.models.Rnc;
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
        title.setText("");

        rnc.setText("RNC : " + ((anfrInfos.getRnc().NOTHING == true) ? "-" : anfrInfos.getRnc().get_real_rnc()));

        String fullAddress = (!anfrInfos.getAdd1().equals("")) ? anfrInfos.getAdd1() + " " : "";
        fullAddress += (!anfrInfos.getAdd2().equals("")) ? anfrInfos.getAdd2() + " " : "";
        fullAddress += (!anfrInfos.getAdd3().equals("")) ? anfrInfos.getAdd3() + " " : "";
        fullAddress += (!anfrInfos.getLieu().equals("")) ? "(" + anfrInfos.getLieu() + ") " : "";
        fullAddress += (!anfrInfos.getCp().equals("")) ? anfrInfos.getCp() + " " : "";
        fullAddress += (!anfrInfos.getCommune().equals("")) ? anfrInfos.getCommune() + " " : "";

        txt1.setText(fullAddress);

        //txt2.setText(anfrInfos.getCp() + " " + anfrInfos.getCommune());
        txt2.setVisibility(View.GONE);

        txt3.setText("Implantation : " + (anfrInfos.getImplantation() != "null" ? anfrInfos.getImplantation() : ""));
        txt31.setText(" / Modification : " + ((anfrInfos.getModification() != "null") ? anfrInfos.getModification() : "-"));

        txt4.setText("Activation : " + ((anfrInfos.getActivation() != "null") ? anfrInfos.getActivation() : "-"));
        txt41.setText(" / Hauteur : " + ((anfrInfos.getHauteur() != "null") ? anfrInfos.getHauteur() + "m" : "-"));

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

        Rnc newRnc = tel.getLoggedRnc();

        // Button
        if(newRnc != null) {
            if (anfrInfos.getRnc().NOTHING == true) {
                bt1.setText("Attribuer le RNC " + newRnc.get_real_rnc() + " à cette addresse");

                newRnc.set_lat(Double.valueOf(anfrInfos.getLat()));
                newRnc.set_lon(Double.valueOf(anfrInfos.getLon()));
                newRnc.set_txt(fullAddress.toUpperCase());
                newRnc.NOTHING = false;

                tel.setTempNewRnc(newRnc);
            } else {
                bt1.setText("Antenne déja identifiée");
            }
        } else {
            bt1.setText("Non connecté");
        }

        return(popup);
    }

}