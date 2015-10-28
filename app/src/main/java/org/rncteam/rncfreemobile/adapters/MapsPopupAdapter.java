package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.AnfrInfos;
import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 01/08/2015.
 */
public class MapsPopupAdapter implements GoogleMap.InfoWindowAdapter {
    private static final String TAG = "MapsPopupAdapter";

    private View popup = null;
    private ViewGroup vg;

    public MapsPopupAdapter(ViewGroup container) {
        this.vg = container;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return(null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        if (popup == null) {
            LayoutInflater li = (LayoutInflater) rncmobile.getAppBaseContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popup=li.inflate( R.layout.marker_popup, vg, false);
        }

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
        TextView txt410 = (TextView) popup.findViewById(R.id.txt410);
        TextView txt411 = (TextView) popup.findViewById(R.id.txt411);
        TextView txt5 = (TextView) popup.findViewById(R.id.txt5);
        TextView txt51 = (TextView) popup.findViewById(R.id.txt51);
        TextView txt6 = (TextView) popup.findViewById(R.id.txt6);

        Button bt1 = (Button) popup.findViewById(R.id.bt1);

        /* Title */
        title.setText("");

        rnc.setText("RNC : " + ((anfrInfos.getRnc().NOT_IDENTIFIED) ? "-" : anfrInfos.getRnc().get_real_rnc()));

        String fullAddress = (!anfrInfos.getAdd1().equals("")) ? anfrInfos.getAdd1() + " " : "";
        fullAddress += (!anfrInfos.getAdd2().equals("")) ? anfrInfos.getAdd2() + " " : "";
        fullAddress += (!anfrInfos.getAdd3().equals("")) ? anfrInfos.getAdd3() + " " : "";
        fullAddress += (!anfrInfos.getLieu().equals("")) ? "(" + anfrInfos.getLieu() + ") " : "";
        fullAddress += (!anfrInfos.getCp().equals("")) ? anfrInfos.getCp() + " " : "";
        fullAddress += (!anfrInfos.getCommune().equals("")) ? anfrInfos.getCommune() + " " : "";

        txt1.setText(fullAddress);

        //txt2.setText(anfrInfos.getCp() + " " + anfrInfos.getCommune());
        txt2.setVisibility(View.GONE);

        txt3.setText("Implantation : " + (!anfrInfos.getImplantation().equals("null") ? anfrInfos.getImplantation() : ""));
        txt31.setText(" / Modification : " + (!anfrInfos.getModification().equals("null") ? anfrInfos.getModification() : "-"));

        txt4.setText("Activation : " + (!anfrInfos.getActivation().equals("null") ? anfrInfos.getActivation() : "-"));
        txt41.setText(" / Hauteur : " + (!anfrInfos.getHauteur().equals("null") ? anfrInfos.getHauteur() + "m" : "-"));

        txt51.setText(" / Propriétaire : " + (!anfrInfos.getProprietaire().equals("null") ? anfrInfos.getProprietaire() : "-"));

        txt6.setText("Support : " + anfrInfos.getTypeSupport());

        // Antenna Image
        JSONArray secteurs = anfrInfos.getAzimuts();
        RelativeLayout rl = (RelativeLayout) popup.findViewById(R.id.lyt_ant);

        // Remove old antennas images
        rl.removeAllViewsInLayout();

        int countAnt = 0;
        int countBlr = 0;
        int countFH = 0;

        for(int i=0;i<secteurs.length();i++) {
            try {
                // Azimut
                ImageView ant = new ImageView(rncmobile.getAppContext());
                ant.setImageResource(R.drawable.blines);

                // FH
                ImageView fh = new ImageView(rncmobile.getAppContext());
                fh.setImageResource(R.drawable.blines_b);

                // BLR
                ImageView blr = new ImageView(rncmobile.getAppContext());
                blr.setImageResource(R.drawable.blines_v);

                ant.setPivotX(0); fh.setPivotX(1); blr.setPivotX(1);
                ant.setPivotY(1); fh.setPivotY(1); blr.setPivotY(1);

                double d_azimut = (Double.parseDouble(secteurs.getJSONObject(i).getString("AER_NB_AZIMUT")));
                float azimut = ((float) d_azimut - 180);

                if(secteurs.getJSONObject(i).getString("EMR_LB_SYSTEME").equals("FH")) {
                    fh.setRotation(azimut);
                    rl.addView(fh);
                    countFH++;
                }
                else if(secteurs.getJSONObject(i).getString("EMR_LB_SYSTEME").equals("BLR 3 GHZ")) {
                    blr.setRotation(azimut);
                    rl.addView(blr);
                    countBlr++;
                }
                else {
                    ant.setRotation(azimut);
                    rl.addView(ant);
                    countAnt++;
                }
            } catch (JSONException e) {
                String msg = "Erreur lors de la génération de l'image des secteurs";
                HttpLog.send(TAG, e, msg);
                Log.d(TAG, msg + e.toString());
                Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }

        txt5.setText("Secteurs : " + countAnt);
        txt410.setText("BLR : " + countBlr);
        txt411.setText("FH : " + countFH);

        // Write potential futur RNC
        Telephony tel = rncmobile.getTelephony();

        Rnc logRnc = tel.getLoggedRnc();
        Rnc newRnc = new Rnc();
        newRnc.set_id(logRnc.get_id());
        newRnc.set_tech(logRnc.get_tech());
        newRnc.set_mcc(logRnc.get_mcc());
        newRnc.set_mnc(logRnc.get_mnc());
        newRnc.set_lcid(logRnc.get_lcid());
        newRnc.set_cid(logRnc.get_cid());
        newRnc.set_rnc(logRnc.get_rnc());
        newRnc.set_lac(logRnc.get_lac());
        newRnc.set_psc(logRnc.get_psc());

        // Button text
        if (anfrInfos.getRnc().NOT_IDENTIFIED && newRnc.get_mnc() == 15) {
            bt1.setText("Attribuer le RNC " + newRnc.get_real_rnc() + " à cette addresse");

            newRnc.set_lat(Double.valueOf(anfrInfos.getLat()));
            newRnc.set_lon(Double.valueOf(anfrInfos.getLon()));
            newRnc.set_txt(fullAddress.toUpperCase());

            tel.setMarkedRnc(newRnc);
        } else if(newRnc.get_mnc() == 1) {
            bt1.setText("Impossible d'attribuer un RNC Orange");
            marker.setTitle("green");
        } else {
            bt1.setText("Antenne déja identifiée");
            marker.setTitle("green");
        }

        return(popup);
    }

}