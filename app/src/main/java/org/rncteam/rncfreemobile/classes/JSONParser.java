package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 24/07/2015.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.rncmobile;

import android.util.Log;

public class JSONParser {
    private static final String TAG = "JSONParser";

    static InputStream is = null;
    static JSONObject jArray = null;
    static String json = "";

    public JSONParser() {
    }

    public JSONObject getJSONFromUrl(String url, ArrayList<NameValuePair> nameValuePairs) {
        // Making HTTP request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse httpResponse = httpClient.execute(httpPost);

            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            Log.d("Error","UEE : " + e.getMessage());
        } catch (ClientProtocolException e) {
            Log.d("Error","CPE : " + e.getMessage());
        } catch (IOException e) {
            Log.d("Error","IOE : " + e.getMessage());
            return null;
        }

        // Set response
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, "iso-8859-1"), 8);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }

            is.close();
            json = sb.toString();

        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
            return null;
        }

        // try parse the string to a JSON object
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