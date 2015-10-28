package org.rncteam.rncfreemobile.tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.rncteam.rncfreemobile.classes.CsvRncReader;
import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseInfo;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
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

    //String csvFileUrl = "http://rncmobile.free.fr/20815.csv";
    private final String csvFileUrl = "http://rfm.dataremix.fr/20815.csv";

    private Activity activity;
    private ProgressDialog mProgressDialog;

    private List<Rnc> lRnc = null;

    public CsvRncDownloader() {
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

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
    protected String doInBackground(String... sUrl) {
        HttpURLConnection conn = null;

        try {
            URL url = new URL(csvFileUrl);
            conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Reader isr = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader rd = new BufferedReader(isr);

                // Parse Csv file
                mProgressDialog.setMessage("Importation en masse dans la base...");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();

                CsvRncReader crr = new CsvRncReader();
                lRnc = crr.run(rd);
                rd.close();

                return "ok";
            }
            return "nok";

        } catch (SocketTimeoutException e) {
            String msg = "Timeout";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_LONG).show();
            return null;

        } catch (Exception e) {
            String msg = "Erreur lors de la récupération de 20815.csv";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_LONG).show();
            return e.toString();
        } finally {
            assert conn != null;
            conn.disconnect();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            if (result.equals("ok")) {

                // Add CVS to database
                if (lRnc != null && lRnc.size() > 0) {
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
                    for (int i = 0; i < lRncLogs.size(); i++) {
                        RncLogs rncLogs = lRncLogs.get(i);
                        Rnc rnc = dbr.findRncByRncCid(
                                String.valueOf(rncLogs.get_rnc()),
                                String.valueOf(rncLogs.get_cid()));

                        if (rnc != null) {
                            rncLogs.set_rnc_id(rnc.get_id());
                        }
                        dbl.addLog(rncLogs);
                    }

                    // Mark info
                    DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
                    dbi.open();

                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    dbi.updateInfo("rncBaseUpdate", sdf.format(new Date()), "-");

                    dbi.close();
                    dbr.close();
                    dbl.close();

                    rncmobile.notifyListLogsHasChanged = true;
                    Telephony tel = rncmobile.getTelephony();
                    tel.setCellChange(true);

                    mProgressDialog.dismiss();
                }
            } else {
                mProgressDialog.dismiss();
                Log.d(TAG, "Erreur lors du chargement 20815.csv:" + result);
                Toast.makeText(rncmobile.getAppContext(), "Erreur s'est produite", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            String msg = "Une erreur s'est produite lors de l'importation en masse";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_SHORT).show();
        } finally {
            activity.finish();
        }
    }
}
