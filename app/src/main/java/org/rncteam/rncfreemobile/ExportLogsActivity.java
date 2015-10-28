package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.rncteam.rncfreemobile.adapters.ListExportHistoryAdapter;
import org.rncteam.rncfreemobile.database.DatabaseExport;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.tasks.NtmExportTask;
import org.rncteam.rncfreemobile.models.Export;

import java.util.ArrayList;

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class ExportLogsActivity extends Activity {
    private static final String TAG = "ExportLogsActivity";

    private ArrayList<Export> lExport;

    private ListExportHistoryAdapter adapter;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export_logs);

        SharedPreferences sp = rncmobile.getPreferences();
        activity = this;

        TextView txtExportCountTotal = (TextView) findViewById(R.id.txt_export_count);

        TextView txtResponse = (TextView) findViewById(R.id.txt_export_text_result);
        txtResponse.setText("");

        TextView txtImportNickname = (TextView) findViewById(R.id.txt_import_text_nickname);
        Button btnExportLog = (Button) findViewById(R.id.btn_export_logs);

        ListView listViewExportLogs = (ListView) findViewById(R.id.list_export_logs);

        // Initialise list export history
        lExport = new ArrayList<>();
        getAllExports();
        adapter = new ListExportHistoryAdapter(lExport);
        listViewExportLogs.setAdapter(adapter);

        // Set text to UI
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();
        txtExportCountTotal.setText(String.valueOf(dbl.countAllLogs()));
        dbl.close();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtImportNickname.setText("Nickname: " + sp.getString("nickname", "nonIdentified"));


        // Button export management
        btnExportLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NtmExportTask net = new NtmExportTask(ExportLogsActivity.this);
                net.execute();
            }
        });
    }

    private void getAllExports() {
        DatabaseExport dbe = new DatabaseExport(rncmobile.getAppContext());

        dbe.open();
        lExport = dbe.findAllExport(lExport);
        dbe.close();
    }

    public void updateHistoryList() {
        // Update list
        getAllExports();
        adapter.notifyDataSetChanged();

        if(activity != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        finish();
    }
}
