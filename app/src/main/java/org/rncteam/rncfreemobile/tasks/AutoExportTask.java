package org.rncteam.rncfreemobile.tasks;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by cedricf_25 on 19/10/2015.
 */
public class AutoExportTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "AutoExportTask";
    private static final String S_URL = "http://rncmobile.fr/autolog.php";
    //private static final String S_URL = "http://rfm.dataremix.fr/exportauto.php";

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";

    // Parameters to pass
    private Telephony tel;
    private final Rnc rnc;

    public AutoExportTask(Rnc rnc) {
        this.rnc = rnc;
    }

    @Override
    protected void onPreExecute() {
        this.tel = rncmobile.getTelephony();

        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();
        dbl.updateSyncLogs(rnc, 1);
        dbl.close();
    }

    @Override
    protected String doInBackground(Void... nothing) {
        try {
            // Prepare some informations
            SharedPreferences sp = rncmobile.getPreferences();
            String nickname = "Unknown";
            if(sp != null) nickname = sp.getString("nickname", "Unknown");

            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------------------------";

            URL url = new URL(S_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(8000);

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            // xG
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"xg\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(((rnc.get_tech() == 3) ? "3G" : "4G"));
            dos.writeBytes(lineEnd);

            // MCC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mcc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_mcc()));
            dos.writeBytes(lineEnd);

            // MNC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mnc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_mnc()));
            dos.writeBytes(lineEnd);

            // RNC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"rnc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_rnc()));
            dos.writeBytes(lineEnd);

            // CID
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"cid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_cid()));
            dos.writeBytes(lineEnd);

            // LAC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"lac\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_lac()));
            dos.writeBytes(lineEnd);

            // PSC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"psc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc.get_psc()));
            dos.writeBytes(lineEnd);

            // Userid
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tel.getDeviceIdMD5().toString().substring(0, 10));
            dos.writeBytes(lineEnd);

            // Nickname
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_name\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(nickname);
            dos.writeBytes(lineEnd);

            // AppVersion
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"app_id\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("v" + rncmobile.appVersion());
            dos.writeBytes(lineEnd);

            // Phone
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mobi_id\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(android.os.Build.MODEL);
            dos.writeBytes(lineEnd);

            // Hidden
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"action\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("autolog");
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            String httpResponse;
            if (serverResponseCode == 200) {
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                String inputLine;
                String resp = "";
                while ((inputLine = rd.readLine()) != null) {
                    resp += inputLine;
                }

                httpResponse = resp;

                // What the good response ?
                DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                dbl.open();
                dbl.updateSyncLogs(rnc, 2);
                dbl.close();
                rncmobile.notifyListLogsHasChanged = true;
            } else {
                httpResponse = "error";
            }
            //close the streams //
            dos.flush();
            dos.close();
            conn.disconnect();

            return httpResponse;

        } catch (SocketTimeoutException e) {
            String msg = "Timeout AutoExportTask";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } catch (Exception e) {
            String msg = "Erreur AutoExportTask";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
    }
}