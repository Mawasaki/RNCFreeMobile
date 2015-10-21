package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 24/07/2015.
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.rncmobile;

import android.util.Log;
import android.widget.Toast;

public class JSONParser {
    private static final String TAG = "JSONParser";

    public JSONParser() {
    }

    public JSONObject getJSONFromUrl(String sUrl, HashMap<String, String> params) {
        // Making HTTP request
        HttpURLConnection conn;
        try {
            URL url = new URL(sUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            StringBuilder requestParams = new StringBuilder();

            if (params != null && params.size() > 0) {
                // creates the params string, encode them using URLEncoder
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    requestParams.append(URLEncoder.encode(key, "UTF-8"));
                    requestParams.append("=").append(
                            URLEncoder.encode(value, "UTF-8"));
                    requestParams.append("&");
                }

                // sends POST data
                OutputStreamWriter writer = new OutputStreamWriter(
                        conn.getOutputStream());
                writer.write(requestParams.toString());
                writer.flush();
            }

        } catch (Exception e) {
            Log.d(TAG, "HTTP Error: " + e.toString());
            //Toast.makeText(rncmobile.getAppContext(), "Erreur HTTP. Vérifier la connexion", Toast.LENGTH_LONG).show();
            rncmobile.onTransaction = false;
            return null;
        }

        // Get response
        String json = "";
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            conn.disconnect();
            json = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            Toast.makeText(rncmobile.getAppContext(), "Erreur de serveur", Toast.LENGTH_LONG).show();
            rncmobile.onTransaction = false;
            return null;
        }

        // try parse the string to a JSON object
        JSONObject jArray = null;
        try {
            jArray = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
            rncmobile.onTransaction = false;
            return null;
        }
        return jArray;
    }
}