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
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by cedricf_25 on 06/10/2015.
 */
public class CrashReportTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "CrashReportTask";

    private static final String S_URL = "http://rfm.dataremix.fr/crash.php";

    private Context context;
    Activity activity;
    private Throwable err;
    private String thread;
    private  HttpURLConnection conn;

    public CrashReportTask(Context contex, Throwable err, String thread) {
        this.context = contex;
        this.err = err;
        this.thread = thread;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... unsued) {
        try {

            URL url = new URL(S_URL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(10000);

            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") ;

            StackTraceElement[] arr = err.getStackTrace();


            String report = err.toString() + "\n";

            report += "--------- Stack trace ---------\n" + this.thread;

            for(int i=0;i<arr.length;i++) {
                report += "    " + arr[i].toString()+"\n";
            }

            report += "-------------------------------\n";

            report += "--------- Cause ---------\n\n";
            Throwable cause = err.getCause();

            if(cause != null) {
                report += cause.toString() + "\n\n";
                arr = cause.getStackTrace();
                for (int i=0; i<arr.length; i++)
                {
                    report += "    "+arr[i].toString()+"\n";
                }
            }
            report += "-------------------------------\n\n";

            OutputStreamWriter request = new OutputStreamWriter(conn.getOutputStream());

            String parameters = "phone=" + android.os.Build.MODEL +
                                "&v_android=" + android.os.Build.VERSION.RELEASE +
                                "&class=" + err.getStackTrace().getClass().getName() +
                                "&crash=" + report;
                                //" |3 " + err.get;


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

            conn.disconnect();

            return "ok";

        } catch (SocketTimeoutException e) {
            Log.d(TAG, "TimeOut: " + e.toString());
            return null;

        } catch (Exception e) {
            Log.d(TAG, "Error send file: " + e.toString());
            Toast.makeText(rncmobile.getAppContext(), "Erreur. Vérifier la connexion", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(activity, "Report RNC mobile envoyé", Toast.LENGTH_LONG).show();

        System.exit(1); // kill off the crashed app
    }

}
