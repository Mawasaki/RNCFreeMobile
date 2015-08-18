package org.rncteam.rncfreemobile.classes;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 24/07/2015.
 */

public class AnfrData extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = "AnfrData";

    private Telephony tel;
    private Gps gps;
    private Maps maps;

    ArrayList<Rnc> lRnc;

    ArrayList<NameValuePair> postParams;

    private String url = "http://rfm.dataremix.fr/supports.php";

    public AnfrData() {
        //tel = rncmobile.getTelephony();
        //tUmts = rncmobile.getTelephonyUmts();
        //tLte = rncmobile.getTelephonyLte();
        gps = rncmobile.getGps();
        maps = rncmobile.getMaps();

        rncmobile.onTransaction = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        postParams = new ArrayList<NameValuePair>(8);

        DatabaseRnc rncDB = new DatabaseRnc(rncmobile.getAppContext());
        rncDB.open();

        LatLngBounds cs = maps.getProjection();
        LatLng sw = maps.getProjection().southwest;
        LatLng ne = maps.getProjection().northeast;

        lRnc = rncDB.findListRncByCoo(sw.latitude, ne.latitude, sw.longitude, ne.longitude);

        rncDB.close();

        postParams.add(new BasicNameValuePair("lat_sw", Double.toString(sw.latitude)));
        postParams.add(new BasicNameValuePair("lon_sw", Double.toString(sw.longitude)));
        postParams.add(new BasicNameValuePair("lat_ne", Double.toString(ne.latitude)));
        postParams.add(new BasicNameValuePair("lon_ne", Double.toString(ne.longitude)));

        rncmobile.onTransaction = true;

        Log.d(TAG,"OnPreExecute");
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        Log.d(TAG,"DoInBackground 1");
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(url, postParams);
        Log.d(TAG,"DoInBackground 2");

        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jArray) {
        Log.d(TAG,"PostExecute1");
        rncmobile.getMaps().removeMarkers();
        if(jArray != null) {
            try {
                // Gettings errors
                if(jArray.getString("return").equals("SUPPORTS")) {

                    JSONArray jData = jArray.getJSONArray("DATA");

                    for(int i=0;i<jData.length();i++) {

                        Rnc rnc = new Rnc();
                        rnc.NOTHING = true;

                        double anfr_lat = Double.valueOf(jData.getJSONObject(i).getString("lat"));
                        double anfr_lon = Double.valueOf(jData.getJSONObject(i).getString("lon"));

                        int icon = 0;
                        if(jData.getJSONObject(i).getString("Dte_En_Service") != "null") {
                            // Check if in RNC Mobile database

                            if(lRnc.size() > 0) {
                                for(int j=0; j < lRnc.size();j++) {
                                    double rnc_lat1 = Double.valueOf(lRnc.get(j).get_lat())+0.01;
                                    double rnc_lon1 = Double.valueOf(lRnc.get(j).get_lon())+0.01;
                                    double rnc_lat2 = Double.valueOf(lRnc.get(j).get_lat())-0.01;
                                    double rnc_lon2 = Double.valueOf(lRnc.get(j).get_lon())-0.01;

                                    if(rnc_lat1 >= anfr_lat && rnc_lat2 <= anfr_lat
                                            && rnc_lon1 >= anfr_lon && rnc_lon2 <= anfr_lon) {
                                        rnc = lRnc.get(j);
                                        rnc.NOTHING = false;
                                        break;
                                    }
                                }
                            }

                            if(!rnc.NOTHING)
                                icon = R.drawable.circle_green;
                            else
                                icon = R.drawable.circle_grey;

                        }

                        else
                            icon = R.drawable.circle_red;

                        String markerTitle = "";
                        markerTitle = jData.getJSONObject(i).getString("ADR_LB_LIEU") + " ";

                        if(jData.getJSONObject(i).getString("ADR_LB_ADD1") != "")
                            markerTitle += jData.getJSONObject(i).getString("ADR_LB_ADD1") + " ";

                        if(jData.getJSONObject(i).getString("ADR_LB_ADD2") != "")
                            markerTitle += jData.getJSONObject(i).getString("ADR_LB_ADD2") + " ";

                        if(jData.getJSONObject(i).getString("ADR_LB_ADD3") != "")
                            markerTitle += jData.getJSONObject(i).getString("ADR_LB_ADD3") + " ";

                        markerTitle += jData.getJSONObject(i).getString("ADR_NM_CP");

                        maps.setAnfrAntennasMarkers(rnc,
                                Double.parseDouble(jData.getJSONObject(i).getString("lat")),
                                Double.parseDouble(jData.getJSONObject(i).getString("lon")),
                                markerTitle,
                                icon);

                    }


                    Log.d(TAG,"PostExecute2");
                    /*
                    for(int i=0;i<jArray.length();i++) {

                        maps.setAnfrAntennasMarkers(
                                Double.parseDouble(jArray.getJSONObject(i).getString("latitude")),
                                Double.parseDouble(jArray.getJSONObject(i).getString("longitude")),
                                jArray.getJSONObject(i).getString("commune"),
                                jArray.getJSONObject(i).getString("exploitant"));

                    /*
                    if(jArray.getJSONObject(0).getString("ERROR").equals("NOT_ANTENNAS")) {
                        Toast.makeText(Wimma.getAppContext(), "Aucune antenne trouvée.", Toast.LENGTH_SHORT).show();
                        maps.removeMarkers();
                        maps.removeLineMeToAntennas();
                        map_info_1.setText("Aucune antenne trouvée");
                        map_info_2.setText("");
                        map_info_3.setText("");
                        return;
                    }
                    if(jArray.getJSONObject(0).getString("ERROR").equals("NOT_ENOUGH_MESURES")) {
                        Toast.makeText(Wimma.getAppContext(), "Pas assez de mesures OpencellID sur cette cellule: "+tel.getCid(), Toast.LENGTH_SHORT).show();
                        maps.removeLineMeToAntennas();
                        maps.removeMaxMarkers();
                        map_info_1.setText("Pas assez de mesures OpencellID sur cette cellule: "+tel.getCid());
                        map_info_2.setText("");
                        map_info_3.setText("");
                        return;
                    }
                }

                double latClosestAntennas = 0;
                double lonClosestAntennas = 0;
                double latMaxPoint = 0;
                double lonMaxPoint = 0;

                // Delete All markers before redraw
                maps.removeMarkers();
                maps.removeMaxMarkers();

                for(int i=0;i<jArray.length();i++) {

                    maps.setAnfrAntennasMarkers(
                            Double.parseDouble(jArray.getJSONObject(i).getString("latitude")),
                            Double.parseDouble(jArray.getJSONObject(i).getString("longitude")),
                            jArray.getJSONObject(i).getString("commune"),
                            jArray.getJSONObject(i).getString("exploitant"));

                    if(jArray.getJSONObject(i).getString("closest").equals("1")) {
                        latClosestAntennas = Double.parseDouble(jArray.getJSONObject(i).getString("latitude"));
                        lonClosestAntennas = Double.parseDouble(jArray.getJSONObject(i).getString("longitude"));
                        latMaxPoint = Double.parseDouble(jArray.getJSONObject(i).getString("maxmidpoint_lat"));
                        lonMaxPoint = Double.parseDouble(jArray.getJSONObject(i).getString("maxmidpoint_lon"));

                        String exploitant = jArray.getJSONObject(i).getString("exploitant");
                        String commune = jArray.getJSONObject(i).getString("commune");
                        String code_postal = jArray.getJSONObject(i).getString("code_postal");
                        String nb_mesures = jArray.getJSONObject(i).getString("nb_mesures");
                        String adresse = "";
                        if (!jArray.getJSONObject(i).getString("adresse").equals("null"))
                            adresse = jArray.getJSONObject(i).getString("adresse")+ " ";
                        String lieu_dit = "";
                        if (!jArray.getJSONObject(i).getString("lieu_dit").equals("null"))
                            lieu_dit = jArray.getJSONObject(i).getString("lieu_dit");
                        String nature_support = "";
                        if (!jArray.getJSONObject(i).getString("nature_support").equals("null"))
                            nature_support = "-"+jArray.getJSONObject(i).getString("nature_support");
                        String hauteur = "";
                        if (!jArray.getJSONObject(i).getString("hauteur_m").equals("null"))
                            hauteur = jArray.getJSONObject(i).getString("hauteur_m");

                        map_info_1.setText("Connecté à " + exploitant + " - " + code_postal +" " + commune);
                        map_info_2.setText(adresse.trim() + lieu_dit.trim() + nature_support.trim()+": "+hauteur+"m");
                        map_info_3.setText("Nombre de mesures : "+nb_mesures);
                    }
                    */
                }
/*
                maps.removeLineMeToAntennas();

                maps.setLineMeToAntenna(gps.getLatitude(), gps.getLongitude(),
                        latClosestAntennas, lonClosestAntennas);

                maps.setMaxAntennas(latMaxPoint,lonMaxPoint);
                */

            } catch (JSONException e) {
                e.printStackTrace();
                rncmobile.onTransaction = false;
            }
        }
        else {
            // Implement error getting cells
        }
        rncmobile.onTransaction = false;
    }

}
