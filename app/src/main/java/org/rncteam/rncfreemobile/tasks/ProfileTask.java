package org.rncteam.rncfreemobile.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.classes.Elevation;
import org.rncteam.rncfreemobile.classes.HttpLog;

import java.util.HashMap;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class ProfileTask extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = "ProfileTask";

    private final Double myLat;
    private final Double myLon;
    private final Double rncLat;
    private final Double rncLon;

    private final String url = "http://rfm.dataremix.fr/elevation.php";

    private HashMap<String, String> postParams;

    private final Elevation elevation;

    public ProfileTask(Double myLat, Double myLon, Double rncLat, Double rncLon, Elevation elevation) {
        this.myLat = myLat;
        this.myLon = myLon;
        this.rncLat = rncLat;
        this.rncLon = rncLon;
        this.elevation = elevation;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        postParams = new HashMap<>();

        postParams.put("my_lat", Double.toString(myLat));
        postParams.put("my_lon", Double.toString(myLon));
        postParams.put("rnc_lat", Double.toString(rncLat));
        postParams.put("rnc_lon", Double.toString(rncLon));
        postParams.put("samples", "100");
    }
    @Override
    protected JSONObject doInBackground(String... args) {
        JSONParser jParser = new JSONParser();
        return jParser.getJSONFromUrl(url, postParams);
    }

    @Override
    protected void onPostExecute(JSONObject jArray) {
        if(jArray != null) {
            try {
                this.elevation.setData(jArray.getJSONArray("results"));
                this.elevation.updateGraph();

            } catch (JSONException e) {
                String msg = "Exception graph elevation";
                HttpLog.send(TAG, e, msg);
                Log.d(TAG, msg + e.toString());
            }
        }
    }

}
