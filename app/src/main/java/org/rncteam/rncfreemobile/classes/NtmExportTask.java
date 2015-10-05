package org.rncteam.rncfreemobile.classes;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class NtmExportTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "NtmExportTask";
    private static final String S_URL = "http://rfm.dataremix.fr/export.php";

    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:42.0) Gecko/20100101 Firefox/42.0";

    private Context context;
    private String ntmFileName;

    // Parameters to pass
    private String nickname;

    private ProgressDialog dialog;

    public NtmExportTask(Context contex, String ntmFileName, String nickName) {
        this.context = contex;
        this.ntmFileName = ntmFileName;
        this.nickname = nickName;
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

            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            dos = new DataOutputStream(conn.getOutputStream());

            // Nickname
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"nickname\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(this.nickname);
            dos.writeBytes(lineEnd);

            // Ids
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"ids\""+ lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(sb.toString());
            dos.writeBytes(lineEnd);

            // File
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + ntmFile.getName() + "\"" + lineEnd);
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
                String chaine = "";
                while((inputLine = rd.readLine()) != null) {
                    chaine += inputLine;
                }

                Log.d(TAG, "Response : " + chaine);
            }
            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

            return "";

        } catch (Exception e) {
            Log.d(TAG, "Error send file: " + e.toString());
            Toast.makeText(rncmobile.getAppContext(), "Une erreur s'est produite. Vérifier la connexion", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //Log.d(TAG, "Result: " + result);
        //dialog.dismiss();

        String deviceName = android.os.Build.MODEL;
    }

}
