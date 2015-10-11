package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.tasks.CsvRncDownloader;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        // Get UI
        lytDataDownload = (LinearLayout)findViewById(R.id.lyt_data_download);
        lytDataImport = (LinearLayout)findViewById(R.id.lyt_data_import);
        lytDataExport = (LinearLayout)findViewById(R.id.lyt_data_export);
        lytDataRncDelete = (LinearLayout)findViewById(R.id.lyt_data_rnc_delete);

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
                dbr.open();
                dbr.deleteAllRnc();
                dbr.close();

                // Update UI Monitor
                Telephony tel = rncmobile.getTelephony();
                tel.setCellChange(true);

                finish();
            }
        });
    }
}
