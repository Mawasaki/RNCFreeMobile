package org.rncteam.rncfreemobile.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.database.DatabaseExport;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
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

    private Activity activity;
    private String ntmFileName;

    // Parameters to pass
    private String nickname;
    private String expName;
    private int nbLogs;
    private int nbUmtsLogs;
    private int nbLteLogs;
    private String response;

    TextView txtResponse;
    String httpResponse;

    private ProgressDialog dialog;

    private int state;

    public NtmExportTask(Activity activity, String ntmFileName, String nickName, String expName) {
        this.activity = activity;
        this.ntmFileName = ntmFileName;
        this.nickname = nickName;
        this.expName = expName;

        txtResponse = (TextView)activity.findViewById(R.id.txt_export_text_result);
        txtResponse.setText("Sending your RNCs...");
    }

    public void NtmExportSetData(int nbLogs, int nbUmtsLogs, int nbLteLogs) {
        this.nbLogs = nbLogs;
        this.nbUmtsLogs = nbUmtsLogs;
        this.nbLteLogs = nbLteLogs;
    }

    @Override
    protected void onPreExecute() {
        //dialog = new ProgressDialog(this.context);
        //dialog.setMessage("Sending...");
        //dialog.show();
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {
            // Prepare some informations
            // UniqID
            Telephony tel = rncmobile.getTelephony();
            String deviceID = tel.getDeviceId();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(deviceID.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "---------------------------";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;

            File ntmFile = new File(rncmobile.getAppContext().getExternalFilesDir(null), ntmFileName);
            FileInputStream fileInputStream = new FileInputStream(ntmFile);

            URL url = new URL(S_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") ;

            conn.setRequestProperty("Content-Type", "multipart/form-data;charset=utf-8;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            // Nickname
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_nick\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(this.nickname);
            dos.writeBytes(lineEnd);

            // Nickname2
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(this.nickname);
            dos.writeBytes(lineEnd);

            // Ids
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user_id\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(sb.toString().substring(0,10));
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
            dos.writeBytes("Content-Disposition: form-data; name=\"fichier\";filename=\"" + ntmFile.getName() + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necesssary after file data...
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

                // Write this job into database
                Export export = new Export();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

                export.set_user_id(sb.toString());
                export.set_user_nick(this.nickname);
                export.set_user_pwd("");
                export.set_user_txt("");
                export.set_user_tel(android.os.Build.MODEL);
                export.set_name(expName);
                export.set_date(sdf.format(new Date()));
                export.set_nb(String.valueOf(nbLogs));
                export.set_nb_umts(String.valueOf(this.nbUmtsLogs));
                export.set_nb_lte(String.valueOf(this.nbLteLogs));
                export.set_state("");
                export.set_type("NetMonster");
                export.set_app_version(rncmobile.appVersion());

                DatabaseExport dbe = new DatabaseExport(rncmobile.getAppContext());
                dbe.open();
                dbe.addExport(export);
                dbe.close();

                state = 1;

                //Toast.makeText(rncmobile.getAppContext(), "Export réussi !", Toast.LENGTH_SHORT).show();
            }
            else {
                //Toast.makeText(rncmobile.getAppContext(), "Le serveur à retourné une erreur.", Toast.LENGTH_LONG).toString();
                state = 0;
            }
            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            return response;

        } catch (Exception e) {
            Log.d(TAG, "Error send file: " + e.toString());
            Toast.makeText(rncmobile.getAppContext(), "Erreur. Vérifier la connexion", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(state > 0 ) Toast.makeText(rncmobile.getAppContext(), "Export réussi", Toast.LENGTH_LONG).show();
        else Toast.makeText(rncmobile.getAppContext(), "Erreur s'est produite", Toast.LENGTH_LONG).show();

        txtResponse.setText(Html.fromHtml(httpResponse));
    }

}
