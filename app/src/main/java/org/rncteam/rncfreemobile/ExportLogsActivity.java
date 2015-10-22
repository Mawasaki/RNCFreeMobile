package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.adapters.ListExportHistoryAdapter;
import org.rncteam.rncfreemobile.database.DatabaseExport;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.tasks.NtmExportTask;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class ExportLogsActivity extends Activity {
    private static final String TAG = "ExportLogsActivity";

    private ArrayList<Export> lExport;

    // UI
    private TextView txtExportCountTotal;
    private TextView txtResponse;
    private TextView txtImportNickname;
    private Button btnExportLog;
    private ListView listViewExportLogs;

    private SharedPreferences sp;

    ListExportHistoryAdapter adapter;

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export_logs);

        final Context context = this.getApplicationContext();
        sp = rncmobile.getPreferences();
        activity = this;

        txtExportCountTotal = (TextView )findViewById(R.id.txt_export_count);

        txtResponse = (TextView )findViewById(R.id.txt_export_text_result);
        txtResponse.setText("");

        txtImportNickname = (TextView) findViewById(R.id.txt_import_text_nickname);
        btnExportLog = (Button) findViewById(R.id.btn_export_logs);

        listViewExportLogs = (ListView) findViewById(R.id.list_export_logs);

        // Initialise list export history
        lExport = new ArrayList<>();
        getAllExports();
        adapter = new ListExportHistoryAdapter(this,rncmobile.getAppContext(), lExport);
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

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
}
