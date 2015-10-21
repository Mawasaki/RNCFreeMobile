package org.rncteam.rncfreemobile.tasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.AnfrInfos;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.classes.JSONParser;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cedricf_25 on 24/07/2015.
 */

public class AnfrData extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = "AnfrData";

    private final Telephony tel;
    private final Maps maps;

    private ArrayList<Rnc> lRnc;

    private HashMap<String, String> postParams;

    public AnfrData() {
        tel = rncmobile.getTelephony();
        maps = rncmobile.getMaps();

        rncmobile.onTransaction = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        postParams = new HashMap<>();

        LatLngBounds cs = maps.getProjection();
        LatLng sw = cs.southwest;
        LatLng ne = cs.northeast;

        DatabaseRnc rncDB = new DatabaseRnc(rncmobile.getAppContext());
        rncDB.open();
        lRnc = rncDB.findListRncByCoo(sw.latitude, ne.latitude, sw.longitude, ne.longitude);
        rncDB.close();

        postParams.put("lat_sw", Double.toString(sw.latitude));
        postParams.put("lon_sw", Double.toString(sw.longitude));
        postParams.put("lat_ne", Double.toString(ne.latitude));
        postParams.put("lon_ne", Double.toString(ne.longitude));

        rncmobile.onTransaction = true;
    }

    @Override
    protected JSONObject doInBackground(String... args) {
        JSONParser jParser = new JSONParser();
        String url = "http://rfm.dataremix.fr/supports.php";
        return jParser.getJSONFromUrl(url, postParams);
    }

    @Override
    protected void onPostExecute(JSONObject jArray) {
        String markerTitle = "";
        int icon = 0;

        rncmobile.onTransaction = false;
        rncmobile.getMaps().removeMarkers();

        if(jArray != null) {
            try {
                if(jArray.getString("return").equals("SUPPORTS")) {

                    JSONArray jData = jArray.getJSONArray("DATA");

                    for(int i=0;i<jData.length();i++) {
                        AnfrInfos anfrInfos = new AnfrInfos();

                        Rnc rnc = new Rnc();

                        double anfr_lat = Double.valueOf(jData.getJSONObject(i).getString("lat"));
                        double anfr_lon = Double.valueOf(jData.getJSONObject(i).getString("lon"));

                        if (!jData.getJSONObject(i).getString("Dte_En_Service").equals("null")) {

                            if (lRnc.size() > 0) {
                                for (int j = 0; j < lRnc.size(); j++) {
                                    double rnc_lat1 = lRnc.get(j).get_lat() + 0.005;
                                    double rnc_lon1 = lRnc.get(j).get_lon() + 0.005;
                                    double rnc_lat2 = lRnc.get(j).get_lat() - 0.005;
                                    double rnc_lon2 = lRnc.get(j).get_lon() - 0.005;

                                    if (rnc_lat1 >= anfr_lat && rnc_lat2 <= anfr_lat
                                            && rnc_lon1 >= anfr_lon && rnc_lon2 <= anfr_lon) {
                                        rnc = lRnc.get(j);
                                        rnc.NOT_IDENTIFIED = false;
                                        break;
                                    }
                                }
                            }

                            Telephony tel = rncmobile.getTelephony();

                            if (tel != null && tel.getLoggedRnc() != null && !rnc.NOT_IDENTIFIED) {
                                if (!rnc.get_real_rnc().equals(tel.getLoggedRnc().get_real_rnc())) {
                                    icon = R.drawable.circle_green;
                                    markerTitle = "green";
                                } else {
                                    icon = R.drawable.circle_orange;
                                    markerTitle = "orange";
                                    anfrInfos.setHauteur(jData.getJSONObject(i).getString("AER_NB_ALT_BAS"));
                                    tel.setAnfrInfos(anfrInfos);
                                }
                            } else {
                                icon = R.drawable.circle_grey;
                                markerTitle = "grey";
                            }
                        } else {
                            if(tel != null) {
                                icon = R.drawable.circle_red;
                                markerTitle = "red";
                            }
                        }


                        anfrInfos.setLieu(jData.getJSONObject(i).getString("ADR_LB_LIEU"));
                        anfrInfos.setAdd1(jData.getJSONObject(i).getString("ADR_LB_ADD1"));
                        anfrInfos.setAdd2(jData.getJSONObject(i).getString("ADR_LB_ADD2"));
                        anfrInfos.setAdd3(jData.getJSONObject(i).getString("ADR_LB_ADD3"));
                        anfrInfos.setCp(jData.getJSONObject(i).getString("ADR_NM_CP"));
                        anfrInfos.setCommune(jData.getJSONObject(i).getString("commune_nom"));

                        anfrInfos.setHauteur(jData.getJSONObject(i).getString("AER_NB_ALT_BAS"));
                        anfrInfos.setImplantation(jData.getJSONObject(i).getString("Dte_Implantation"));
                        anfrInfos.setModification(jData.getJSONObject(i).getString("Dte_modif"));
                        anfrInfos.setActivation(jData.getJSONObject(i).getString("Dte_En_Service"));
                        anfrInfos.setTypeSupport(jData.getJSONObject(i).getString("NAT_LB_NOM"));

                        anfrInfos.setLat(jData.getJSONObject(i).getString("lat"));
                        anfrInfos.setLon(jData.getJSONObject(i).getString("lon"));

                        anfrInfos.setProprietaire(jData.getJSONObject(i).getString("TPO_LB"));

                        anfrInfos.setAzimuts(jData.getJSONObject(i).getJSONArray("AZIMUT"));

                        anfrInfos.setRnc(rnc);

                        maps.setAnfrAntennasMarkers(
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
    }
}
