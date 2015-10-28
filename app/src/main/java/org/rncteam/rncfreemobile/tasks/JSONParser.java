package org.rncteam.rncfreemobile.tasks;

/**
 * Created by cedricf_25 on 24/07/2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.classes.HttpLog;

import android.util.Log;

public class JSONParser {
    private static final String TAG = "JSONParser";

    public JSONParser() {
    }

    public JSONObject getJSONFromUrl(String sUrl, HashMap<String, String> params) {
        // Making HTTP request
        JSONObject jArray = null;
        try {
            URL url = new URL(sUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(10000);
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

                // Get response
                String json;

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "iso-8859-1"), 8);

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                conn.disconnect();
                json = sb.toString();
                jArray = new JSONObject(json);
            }
            return jArray;

        } catch (SocketTimeoutException e) {
            String msg = "Timeout JSONParser";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } catch (IOException e) {
            String msg = "IO Erreur";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } catch (JSONException e) {
            String msg = "JSON Exception";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } catch (Exception e) {
            String msg = "Exception";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        }
    }
}