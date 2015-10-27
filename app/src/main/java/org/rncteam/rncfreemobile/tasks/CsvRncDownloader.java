package org.rncteam.rncfreemobile.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.rncteam.rncfreemobile.classes.CsvRncReader;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.Database;
import org.rncteam.rncfreemobile.database.DatabaseInfo;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by @cedricf_25 on 15/07/2015.
 */
public class CsvRncDownloader extends AsyncTask<String, String, String> {

    private static final String TAG = "Downloader";

    String csvFileUrl = "http://rncmobile.free.fr/20815.csv";

    private Context context;
    private ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mWakeLock;
    private HttpURLConnection conn;

    public CsvRncDownloader(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        conn = null;

        try {

            URL url = new URL(csvFileUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setConnectTimeout(10000);
            conn.setReadTimeout(20000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + conn.getResponseCode()
                        + " " + conn.getResponseMessage();
            }

            int fileLength = conn.getContentLength();

            input = conn.getInputStream();
            output = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/20815.csv");

            byte data[] = new byte[4096];
            long total = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0)
                    publishProgress(("" + total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "TimeOut: " + e.toString());
            return null;

        } catch (Exception e) {
            return e.toString();

        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (conn != null)
                conn.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();

        mProgressDialog.setMessage("Download 20815.csv file...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        mWakeLock.release();
        mProgressDialog.dismiss();

        if (result != null) {
            // Parse Csv file
            mProgressDialog.setMessage("Importing in database...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.show();

            final Handler handler = new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    CsvRncReader crr = new CsvRncReader();
                    List<Rnc> lRnc = crr.run();

                    // Add CVS to database
                    DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                    DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                    dbr.open();
                    dbl.open();

                    // Save Logs
                    ArrayList<RncLogs> lRncLogs = dbl.findAllRncLogs();

                    // Delete and fill RNCs
                    dbr.deleteAllRnc();
                    dbl.deleteRncLogs();
                    dbr.addMassiveRnc(lRnc);

                    // Reaffects logs
                    for(int i=0;i<lRncLogs.size();i++) {
                        RncLogs rncLogs = lRncLogs.get(i);
                        Rnc rnc = dbr.findRncByRncCid(
                                String.valueOf(rncLogs.get_rnc()),
                                String.valueOf(rncLogs.get_cid()));

                        if(rnc != null) {
                                rncLogs.set_rnc_id(rnc.get_id());
                        }
                        dbl.addLog(rncLogs);
                    }

                    // Mark info
                    DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
                    dbi.open();

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    dbi.updateInfo("rncBaseUpdate", sdf.format(new Date()), "-");

                    dbi.close();
                    dbr.close();
                    dbl.close();

                    rncmobile.notifyListLogsHasChanged = true;
                    Telephony tel = rncmobile.getTelephony();
                    tel.setCellChange(true);

                    mProgressDialog.dismiss();
                }
            }, 1000);
        }
    }
}
