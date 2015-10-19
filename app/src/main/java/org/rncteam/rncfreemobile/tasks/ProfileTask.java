package org.rncteam.rncfreemobile.tasks;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.classes.Elevation;
import org.rncteam.rncfreemobile.classes.JSONParser;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class ProfileTask extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = "ProfileTask";

    private Double myLat;
    private Double myLon;
    private Double rncLat;
    private Double rncLon;

    private String url = "http://rfm.dataremix.fr/elevation.php";

    ArrayList<NameValuePair> postParams;

    JSONArray jData;

    Elevation elevation;

    public ProfileTask(Double myLat, Double myLon, Double rncLat, Double rncLon, Elevation elevation) {
        this.myLat = myLat;
        this.myLon = myLon;
        this.rncLat = rncLat;
        this.rncLon = rncLon;
        this.elevation = elevation;
    }

    public JSONArray getResult() {
        return this.jData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        postParams = new ArrayList<NameValuePair>(8);

        postParams.add(new BasicNameValuePair("my_lat", Double.toString(myLat)));
        postParams.add(new BasicNameValuePair("my_lon", Double.toString(myLon)));
        postParams.add(new BasicNameValuePair("rnc_lat", Double.toString(rncLat)));
        postParams.add(new BasicNameValuePair("rnc_lon", Double.toString(rncLon)));
        postParams.add(new BasicNameValuePair("samples", "100"));

        Log.d(TAG, "" + myLat + "-" + myLon + " / " + rncLat + "-" + rncLon);

    }
    @Override
    protected JSONObject doInBackground(String... args) {
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(url, postParams);
        return json;
    }

    @Override
    protected void onPostExecute(JSONObject jArray) {
        if(jArray != null) {
            try {
                Log.d(TAG, jArray.getString("results").toString());

                this.elevation.jData = jArray.getJSONArray("results");
                this.elevation.dataOk = true;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
