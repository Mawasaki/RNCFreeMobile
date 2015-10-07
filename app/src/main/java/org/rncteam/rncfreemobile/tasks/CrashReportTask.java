package org.rncteam.rncfreemobile.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cedricf_25 on 06/10/2015.
 */
public class CrashReportTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "CrashReportTask";

    private static final String S_URL = "http://rfm.dataremix.fr/crash.php";

    private Activity activity;
    private Context context;
    private String err;

    public CrashReportTask(Activity activity, Context contex, String err) {
        this.activity = activity;
        this.context = contex;
        this.err = err;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {
            URL url = new URL(S_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") ;

            OutputStreamWriter request = new OutputStreamWriter(conn.getOutputStream());

            String parameters = "phone=" + android.os.Build.MODEL +
                                "&v_android="+ android.os.Build.VERSION.RELEASE +
                                "&crash=" + err;

            request.write(parameters);

            request.flush();
            request.close();

            String line = "";
            String response = null;
            InputStreamReader isr = new InputStreamReader(conn.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }

            response = sb.toString();
            Log.d("d", "Crash response " + response);
            isr.close();
            reader.close();

            return "";

        } catch (Exception e) {
            Log.d(TAG, "Error send file: " + e.toString());
            Toast.makeText(rncmobile.getAppContext(), "Erreur. Vérifier la connexion", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(rncmobile.getAppContext(), "Report envoyé", Toast.LENGTH_LONG).show();

        activity.finish();
    }

}
