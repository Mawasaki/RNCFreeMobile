package org.rncteam.rncfreemobile.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseExport;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cedricf_25 on 19/10/2015.
 */
public class AutoExportTask extends AsyncTask<Rnc, Void, String> {
    private static final String TAG = "AutoExportTask";
    private static final String S_URL = "http://rncmobile.fr/autolog.php";
    //private static final String S_URL = "http://rfm.dataremix.fr/exportauto.php";

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";

    // Parameters to pass
    Telephony tel;
    TextView txtResponse;
    String httpResponse;

    public AutoExportTask() {
    }

    @Override
    protected void onPreExecute() {
        this.tel = rncmobile.getTelephony();
    }

    @Override
    protected String doInBackground(Rnc... rnc) {
        try {
            // Prepare some informations
            String nickname = rncmobile.getPreferences().getString("nickname", "nonIdentified");

            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------------------------";

            URL url = new URL(S_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            // Prepare some variables
            String delimiter = ";";
            String crlf = "\r\n";
            String exportData = "";

            // xG
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"xg\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(((rnc[0].get_tech() == 3) ? "3G" : "4G"));
            dos.writeBytes(lineEnd);

            // MCC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mcc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_mcc()));
            dos.writeBytes(lineEnd);

            // MNC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mnc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_mnc()));
            dos.writeBytes(lineEnd);

            // RNC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"rnc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_rnc()));
            dos.writeBytes(lineEnd);

            // CID
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"cid\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_cid()));
            dos.writeBytes(lineEnd);

            // LAC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"lac\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_lac()));
            dos.writeBytes(lineEnd);

            // PSC
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"psc\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(String.valueOf(rnc[0].get_psc()));
            dos.writeBytes(lineEnd);

            // Userid
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""+ lineEnd);
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
            dos.writeBytes("Content-Disposition: form-data; name=\"app_id\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("v" + rncmobile.appVersion());
            dos.writeBytes(lineEnd);

            // Phone
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"mobi_id\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(android.os.Build.MODEL);
            dos.writeBytes(lineEnd);

            // Hidden
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"action\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("autolog");
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if(serverResponseCode == 200){
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                String inputLine = "";
                String resp = "";
                while((inputLine = rd.readLine()) != null) {
                    resp += inputLine;
                }

                Log.d(TAG, "Response : " + resp);

                httpResponse = resp;

                // What the good response ?
                DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                dbl.open();
                dbl.updateSyncLogs(rnc[0], 1);
                dbl.close();
                rncmobile.notifyListLogsHasChanged = true;
            }
            else {
                httpResponse = "error";
            }
            //close the streams //
            dos.flush();
            dos.close();

            return httpResponse;

        } catch (Exception e) {
            Log.d(TAG, "Error send file: " + e.toString());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "Auto export OK");
    }
}