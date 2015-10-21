package org.rncteam.rncfreemobile.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.JSONParser;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by cedricf_25 on 19/10/2015.
 */
public class StatsTask extends AsyncTask<String, String, JSONObject> {
    private static final String TAG = "ProfileTask";

    Context context;
    View v;
    JSONObject jData;

    HashMap<String, String> postParams;

    private String url = "http://rfm.dataremix.fr/stats.php";

    public StatsTask(Context context, View view) {
        this.context = context;
        this.v = view;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        postParams = new HashMap<>();
        postParams.put("action", "Future action");
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
                if(jArray.getString("return").equals("STATS")) {
                    Log.d(TAG, jArray.getString("DATA").toString());

                    jData = jArray.getJSONObject("DATA");

                    // Get number of RNC
                    DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                    dbr.open();
                    double nb_rnc_umts = dbr.countUMTSRnc();
                    double nb_rnc_lte = dbr.countLTERnc();

                    // Get Total anfr Antennas
                    Double nbActAnt = jData.getDouble("nb_service");

                    double percentUtms = (nb_rnc_umts * 100) / nbActAnt;
                    double percentLte = (nb_rnc_lte * 100) / nbActAnt;

                    //  Build progress bar
                    ProgressBar pbRncCollector = (ProgressBar) v.findViewById(R.id.nav_progress_bar);
                    TextView txtNavStats = (TextView) v.findViewById(R.id.txt_nav_stats);
                    TextView txtNavStats2 = (TextView) v.findViewById(R.id.txt_nav_stats2);
                    Drawable drawable = rncmobile.getAppContext().getResources().getDrawable(R.drawable.progressbar);

                    pbRncCollector.setProgressDrawable(drawable);
                    pbRncCollector.setProgress((int) percentLte);   // Main Progress
                    pbRncCollector.setSecondaryProgress((int) percentUtms); // Secondary Progress
                    pbRncCollector.setMax(100); // Maximum Progress

                    txtNavStats.setText("Identifiées 3G/4G: " + String.format("%.1f",percentUtms)
                                            + "% / 4G: " + String.format("%.1f",percentLte) + "%");
                    txtNavStats2.setText("Reste: " + String.format("%.2f",(100 - percentUtms)) + "%");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
