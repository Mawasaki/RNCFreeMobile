package org.rncteam.rncfreemobile.tasks;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.ExportLogsActivity;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.database.DatabaseExport;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class NtmExportTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "NtmExportTask";
    private static final String S_URL = "http://rncmobile.fr/appimport.php";
    //private static final String S_URL = "http://rfm.dataremix.fr/export.php";

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";

    private final ExportLogsActivity activity;
    private final Telephony tel;
    private HttpURLConnection conn;

    // Parameters to pass
    private final String nickname;
    private final TextView txtResponse;
    private String httpResponse;

    private LinearLayout lytCelBtn;

    private ArrayList<RncLogs> lRncLogs;

    private int state;

    public NtmExportTask(Activity activity) {
        this.activity = (ExportLogsActivity)activity;

        SharedPreferences sp = rncmobile.getPreferences();
        this.nickname = sp.getString("nickname", "nonIdentified");

        this.tel = rncmobile.getTelephony();

        txtResponse = (TextView)activity.findViewById(R.id.txt_export_text_result);
        txtResponse.setText("Sending your RNCs...");

        lytCelBtn = (LinearLayout) activity.findViewById(R.id.lyt_cell_btn);
    }

    @Override
    protected void onPreExecute() {
        // Hide button
        lytCelBtn.setVisibility(View.GONE);

        // Prepare export
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();
        lRncLogs = dbl.findAllRncLogs();
        dbl.close();
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {
            // Prepare some informations
            DataOutputStream dos;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------------------------";

            URL url = new URL(S_URL);
            conn = (HttpURLConnection) url.openConnection();
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


            for (int i = 0; i < lRncLogs.size(); i++) {
                exportData += ((lRncLogs.get(i).get_tech() == 3 ? "3G" : "4G")) + delimiter +
                        lRncLogs.get(i).get_mcc() + delimiter +
                        lRncLogs.get(i).get_mnc() + delimiter +
                        lRncLogs.get(i).get_cid() + delimiter +
                        lRncLogs.get(i).get_lac() + delimiter +
                        lRncLogs.get(i).get_rnc() + delimiter +
                        ((lRncLogs.get(i).get_psc() == 0) ? "-1" : lRncLogs.get(i).get_psc()) + delimiter +
                        lRncLogs.get(i).get_lat() + delimiter +
                        lRncLogs.get(i).get_lon() + delimiter +
                        lRncLogs.get(i).get_txt() + crlf;
            }

            // Nickname
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(this.nickname);
            dos.writeBytes(lineEnd);

            // Ids
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(tel.getDeviceIdMD5().toString().substring(0,10));
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
            dos.writeBytes("Content-Disposition: form-data; name=\"MAX_FILE_SIZE\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("3000000");
            dos.writeBytes(lineEnd);

            // Hidden
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"action\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes("ajouter");
            dos.writeBytes(lineEnd);

            // File
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"fichier\";filename=export.ntm" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(exportData);
            dos.writeBytes(lineEnd);

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            int serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if(serverResponseCode == 200){
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));

                String inputLine;
                String resp = "";
                while((inputLine = rd.readLine()) != null) {
                    resp += inputLine;
                }

                Log.d(TAG, "Response : " + resp);

                httpResponse = resp;

                state = 1;
            }
            else {
                state = 0;
                httpResponse = "error";
            }
            //close the streams //
            dos.flush();
            dos.close();

            return httpResponse;

        } catch (SocketTimeoutException e) {
            String msg = "Timeout Export";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } catch (Exception e) {
            String msg = "Exception Export";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            return null;
        } finally {
            conn.disconnect();

        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Write this job into database
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        Export export = new Export();

        export.set_user_id(tel.getDeviceIdMD5().toString());
        export.set_user_nick(this.nickname);
        export.set_user_pwd("");
        export.set_user_txt("");
        export.set_user_tel(android.os.Build.MODEL);
        export.set_date(sdf.format(new Date()));
        export.set_nb(String.valueOf(lRncLogs.size()));
        export.set_type("NetMonster");
        export.set_app_version(rncmobile.appVersion());

        if(state > 0 ) {
            Toast.makeText(rncmobile.getAppContext(), "Export réussi", Toast.LENGTH_LONG).show();
            export.set_state("1");
        }
        else {
            Toast.makeText(rncmobile.getAppContext(), "Erreur s'est produite", Toast.LENGTH_LONG).show();
            export.set_state("0");
        }

        DatabaseExport dbe = new DatabaseExport(rncmobile.getAppContext());
        dbe.open();
        dbe.addExport(export);
        dbe.close();

        txtResponse.setText(Html.fromHtml(httpResponse));
        activity.updateHistoryList();

        lytCelBtn.setVisibility(View.VISIBLE);
    }
}
