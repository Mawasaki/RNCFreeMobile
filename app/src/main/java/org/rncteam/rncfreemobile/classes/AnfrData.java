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

        LatLng sw = cs.southwest;
        LatLng ne = cs.northeast;

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
        Log.d(TAG, "PostExecute1");
        rncmobile.getMaps().removeMarkers();
        if(jArray != null) {
            try {
                // Gettings errors
                if(jArray.getString("return").equals("SUPPORTS")) {

                    JSONArray jData = jArray.getJSONArray("DATA");

                    Log.d(TAG,"Nb antennes : " + jData.length());
                    for(int i=0;i<jData.length();i++) {

                        Rnc rnc = new Rnc();
                        rnc.NOTHING = true;

                        double anfr_lat = Double.valueOf(jData.getJSONObject(i).getString("lat"));
                        double anfr_lon = Double.valueOf(jData.getJSONObject(i).getString("lon"));

                        int icon = 0;

                        if (jData.getJSONObject(i).getString("Dte_En_Service") != "null") {
                            // Check if in RNC Mobile database

                            if (lRnc.size() > 0) {
                                for (int j = 0; j < lRnc.size(); j++) {
                                    double rnc_lat1 = Double.valueOf(lRnc.get(j).get_lat()) + 0.01;
                                    double rnc_lon1 = Double.valueOf(lRnc.get(j).get_lon()) + 0.01;
                                    double rnc_lat2 = Double.valueOf(lRnc.get(j).get_lat()) - 0.01;
                                    double rnc_lon2 = Double.valueOf(lRnc.get(j).get_lon()) - 0.01;

                                    if (rnc_lat1 >= anfr_lat && rnc_lat2 <= anfr_lat
                                            && rnc_lon1 >= anfr_lon && rnc_lon2 <= anfr_lon) {
                                        rnc = lRnc.get(j);
                                        rnc.NOTHING = false;
                                        break;
                                    }
                                }
                            }

                            if (!rnc.NOTHING)
                                icon = R.drawable.circle_green;
                            else
                                icon = R.drawable.circle_grey;

                        } else
                            icon = R.drawable.circle_red;

                        String markerTitle = "Rnc Free Mobile";

                        AnfrInfos anfrInfos = new AnfrInfos();

                        anfrInfos.setLieu(jData.getJSONObject(i).getString("ADR_LB_LIEU"));
                        anfrInfos.setAdd1(jData.getJSONObject(i).getString("ADR_LB_ADD1"));
                        anfrInfos.setAdd2(jData.getJSONObject(i).getString("ADR_LB_ADD2"));
                        anfrInfos.setAdd3(jData.getJSONObject(i).getString("ADR_LB_ADD3"));
                        anfrInfos.setCp(jData.getJSONObject(i).getString("ADR_NM_CP"));
                        anfrInfos.setCommune(jData.getJSONObject(i).getString("commune"));

                        anfrInfos.setHauteur("30m");
                        anfrInfos.setImplantation("10/10/13");
                        anfrInfos.setActivation("12/04/14");
                        anfrInfos.setTypeSupport("Pylone Autostable");

                        maps.setAnfrAntennasMarkers(rnc,
                                Double.parseDouble(jData.getJSONObject(i).getString("lat")),
                                Double.parseDouble(jData.getJSONObject(i).getString("lon")),
                                markerTitle,
                                anfrInfos,
                                icon);

                    }
                }

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
