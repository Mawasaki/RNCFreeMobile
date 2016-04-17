package org.rncteam.rncfreemobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.tasks.CsvRncDownloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cedricf_25 on 11/10/2015.
 */
public class DataActivity extends Activity {
    private static final String TAG = "DataActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Get UI
        LinearLayout lytDataDownload = (LinearLayout) findViewById(R.id.lyt_data_download);
        LinearLayout lytDataImport = (LinearLayout) findViewById(R.id.lyt_data_import);
        LinearLayout lytDataExport = (LinearLayout) findViewById(R.id.lyt_data_export);
        LinearLayout lytDataExportFile = (LinearLayout) findViewById(R.id.lyt_data_export_file);
        LinearLayout lytDataRncDelete = (LinearLayout) findViewById(R.id.lyt_data_rnc_delete);

        // Actions on click
        lytDataDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CsvRncDownloader d = new CsvRncDownloader();
                d.setActivity(DataActivity.this);
                d.execute("");
            }
        });

        lytDataImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            LayoutInflater li = (LayoutInflater) rncmobile.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            Intent intentDIA = new Intent(li.getContext(), DataImportActivity.class);
            startActivity(intentDIA);
            }
        });

        lytDataExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = (LayoutInflater) rncmobile.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Intent intentELA = new Intent(li.getContext(), ExportLogsActivity.class);
                startActivity(intentELA);
            }
        });

        lytDataExportFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Some formats of final file
                    String delimiter = ";";
                    String crlf = "\r\n";

                    // FileName
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
                    Date now = new Date();

                    String DebugRncLogFileName = "RFM_" + formatter.format(now) + ".txt";

                    String pathToExternalStorage = Environment.getExternalStorageDirectory().toString();
                    File debugRncLogFile = new File(pathToExternalStorage
                            + "/" + "RNCMobile");

                    if (!debugRncLogFile.exists())
                        debugRncLogFile.mkdirs();

                    debugRncLogFile = new File(pathToExternalStorage
                            + "/" + "RNCMobile/" + DebugRncLogFileName);

                    BufferedWriter writerRncLog = new BufferedWriter
                            (new OutputStreamWriter(new FileOutputStream(debugRncLogFile),"iso-8859-1"));

                    // Debug for Rnc Log database
                    DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                    dbl.open();

                    List<RncLogs> lRncLogs = dbl.findAllRncLogs();
                    // Write lines
                    for (int i = 0; i < lRncLogs.size(); i++) {
                        String finalLine = ((lRncLogs.get(i).get_tech() == 3) ? "3G" : "4G") + delimiter +
                                lRncLogs.get(i).get_mcc() + delimiter +
                                lRncLogs.get(i).get_mnc() + delimiter +
                                lRncLogs.get(i).get_cid() + delimiter +
                                lRncLogs.get(i).get_lac() + delimiter +
                                lRncLogs.get(i).get_rnc() + delimiter +
                                ((lRncLogs.get(i).get_psc() == 0) ? -1 : lRncLogs.get(i).get_psc()) + delimiter +
                                lRncLogs.get(i).get_lat() + delimiter +
                                lRncLogs.get(i).get_lon() + delimiter +
                                lRncLogs.get(i).get_txt() + crlf;

                        writerRncLog.write(finalLine);
                    }

                    writerRncLog.close();

                    MediaScannerConnection.scanFile(rncmobile.getAppContext(),
                            new String[]{debugRncLogFile.toString()},
                            null,
                            null);

                    Toast.makeText(getApplicationContext(), "Exportation réussie : "
                            + DebugRncLogFileName, Toast.LENGTH_LONG).show();

                    finish();

                } catch(IOException e) {
                    String msg = "Erreur lors de l'exportation";
                    HttpLog.send(TAG, e, msg);
                    Log.d(TAG, msg + e.toString());
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    String msg = "Erreur lors de l'exportation";
                    HttpLog.send(TAG, e, msg);
                    Log.d(TAG, msg + e.toString());
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });


        lytDataRncDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                dbr.open();
                dbl.open();

                dbr.deleteAllRnc();
                dbl.deleteRncLogs();

                dbr.close();
                dbl.close();

                rncmobile.notifyListLogsHasChanged = true;
                Telephony tel = rncmobile.getTelephony();
                tel.setCellChange(true);
                tel.dispatchCellInfo();

                finish();
            }
        });


    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        finish();
    }
}
