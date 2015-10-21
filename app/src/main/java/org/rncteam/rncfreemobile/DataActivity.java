package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.tasks.CsvRncDownloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by cedricf_25 on 11/10/2015.
 */
public class DataActivity extends Activity {
    private static final String TAG = "DataActivity";

    // UI
    private LinearLayout lytDataDownload;
    private LinearLayout lytDataImport;
    private LinearLayout lytDataExport;
    private LinearLayout lytDataRncDelete;
    private LinearLayout lytDataRncDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Get UI
        lytDataDownload = (LinearLayout)findViewById(R.id.lyt_data_download);
        lytDataImport = (LinearLayout)findViewById(R.id.lyt_data_import);
        lytDataExport = (LinearLayout)findViewById(R.id.lyt_data_export);
        lytDataRncDelete = (LinearLayout)findViewById(R.id.lyt_data_rnc_delete);
        lytDataRncDebug = (LinearLayout)findViewById(R.id.lyt_data_rnc_debug);

        // Actions on click
        lytDataDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CsvRncDownloader d = new CsvRncDownloader(DataActivity.this);
                d.execute("");
            }
        });

        lytDataImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Nothing to do
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

                finish();
            }
        });

        lytDataRncDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Some formats of final file
                    String delimiter = ";";
                    String crlf = "\r\n";

                    // FileName
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                    Date now = new Date();
                    String DebugRncFileName = "DEBUG_RNC_" + formatter.format(now) + ".txt";
                    String DebugRncLogFileName = "DEBUG_RNC_LOG_" + formatter.format(now) + ".txt";

                    // Directory
                    File debugRncFile = new File(rncmobile.getAppContext().getExternalFilesDir(null), DebugRncFileName);
                    if (!debugRncFile.exists())
                        debugRncFile.createNewFile();

                    File debugRncLogFile = new File(rncmobile.getAppContext().getExternalFilesDir(null), DebugRncLogFileName);
                    if (!debugRncLogFile.exists())
                        debugRncLogFile.createNewFile();

                    BufferedWriter writerRnc = new BufferedWriter
                            (new OutputStreamWriter(new FileOutputStream(debugRncFile),"iso-8859-1"));
                    BufferedWriter writerRncLog = new BufferedWriter
                            (new OutputStreamWriter(new FileOutputStream(debugRncLogFile),"iso-8859-1"));


                    // Debug for RNC database
                    DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                    dbr.open();

                    List<Rnc> lRnc = dbr.findAllRnc();

                    // Write lines
                    for (int i = 0; i < lRnc.size(); i++) {
                        String finalLine = lRnc.get(i).get_tech() + delimiter +
                                lRnc.get(i).get_mcc() + delimiter +
                                lRnc.get(i).get_mnc() + delimiter +
                                lRnc.get(i).get_cid() + delimiter +
                                lRnc.get(i).get_lac() + delimiter +
                                lRnc.get(i).get_rnc() + delimiter +
                                (lRnc.get(i).get_psc().equals("0") ? "-1" : lRnc.get(i).get_psc()) + delimiter +
                                lRnc.get(i).get_lat() + delimiter +
                                lRnc.get(i).get_lon() + delimiter +
                                lRnc.get(i).get_txt() + crlf;

                        writerRnc.write(finalLine);
                    }
                    dbr.close();

                    // Debug for Rnc Log database
                    DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                    dbl.open();

                    List<RncLogs> lRncLogs = dbl.findAllRncLogs();
                    // Write lines
                    for (int i = 0; i < lRncLogs.size(); i++) {
                        String finalLine = lRncLogs.get(i).get_tech() + delimiter +
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

                    writerRnc.close();
                    writerRncLog.close();

                    MediaScannerConnection.scanFile(rncmobile.getAppContext(),
                            new String[]{debugRncFile.toString()},
                            null,
                            null);

                    MediaScannerConnection.scanFile(rncmobile.getAppContext(),
                            new String[]{debugRncLogFile.toString()},
                            null,
                            null);

                } catch (IOException e1) {

                }
            }
        });
    }
}
