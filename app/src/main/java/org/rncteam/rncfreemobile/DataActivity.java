package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.tasks.CsvRncDownloader;

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
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        finish();
    }
}
