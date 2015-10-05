package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.adapters.ListExportHistoryAdapter;
import org.rncteam.rncfreemobile.classes.DatabaseExport;
import org.rncteam.rncfreemobile.classes.DatabaseLogs;
import org.rncteam.rncfreemobile.classes.NtmExportTask;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.w3c.dom.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class ExportLogsActivity extends Activity {
    private static final String TAG = "ExportLogsActivity";

    private static final String URL = "http://rfm.dataremix.fr/export.php";

    private List<RncLogs> lRncLogs;
    private List<Export> lExport;

    // UI
    private TextView txtExportCountTotal;
    private TextView txtExportCountUmts;
    private TextView txtExportCountLte;
    private TextView txtResponse;
    private EditText inpImportNickname;
    private EditText inpImportName;
    private Button btnExportLog;
    private ListView listViewExportLogs;

    Handler handler;
    Telephony tel;
    // Count
    private int nbUmtsLogs;
    private int nbLteLogs;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_export_logs);

        final Context context = this.getApplicationContext();

        txtExportCountTotal = (TextView )findViewById(R.id.txt_export_count);
        txtExportCountUmts = (TextView )findViewById(R.id.txt_export_count_umts);
        txtExportCountLte = (TextView )findViewById(R.id.txt_export_count_lte);

        txtResponse = (TextView )findViewById(R.id.txt_export_text_result);
        txtResponse.setText("");
        tel = rncmobile.getTelephony();
        tel.setHttpResponse("");

        inpImportNickname = (EditText) findViewById(R.id.inp_import_nickname);
        inpImportName = (EditText) findViewById(R.id.inp_import_name);
        btnExportLog = (Button) findViewById(R.id.btn_export_logs);

        listViewExportLogs = (ListView) findViewById(R.id.list_export_logs);

        // GetList of logs
        getAllRncLogs();

        // Set count logs
        countLogsByTech();

        txtExportCountTotal.setText(String.valueOf(lRncLogs.size()));
        txtExportCountUmts.setText(String.valueOf(nbUmtsLogs));
        txtExportCountLte.setText(String.valueOf(nbLteLogs));

        // Initialise list export history
        getAllExports();
        ListExportHistoryAdapter adapter = new ListExportHistoryAdapter(this,rncmobile.getAppContext(), lExport);
        listViewExportLogs.setAdapter(adapter);

        // Button export management
        btnExportLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Control of form
                    if (!inpImportNickname.getText().toString().equals("")) {
                        // Some formats of final file
                        String delimiter = ";";
                        String crlf = "\r\n";

                        // FileName
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                        Date now = new Date();
                        String NtmFileName = "NTM_" + formatter.format(now) + ".ntm";

                        // Directory
                        File ntmFile = new File(rncmobile.getAppContext().getExternalFilesDir(null), NtmFileName);
                        if (!ntmFile.exists())
                            ntmFile.createNewFile();

                        BufferedWriter writer = new BufferedWriter(new FileWriter(ntmFile, true));

                        // Write lines
                        for (int i = 0; i < lRncLogs.size(); i++) {
                            String finalLine = lRncLogs.get(i).get_tech() + delimiter +
                                    lRncLogs.get(i).get_mcc() + delimiter +
                                    lRncLogs.get(i).get_mnc() + delimiter +
                                    lRncLogs.get(i).get_cid() + delimiter +
                                    lRncLogs.get(i).get_lac() + delimiter +
                                    lRncLogs.get(i).get_rnc() + delimiter +
                                    lRncLogs.get(i).get_lat() + delimiter +
                                    lRncLogs.get(i).get_lon() + delimiter +
                                    lRncLogs.get(i).get_txt() + crlf;

                            writer.write(finalLine);
                        }

                        writer.close();

                        MediaScannerConnection.scanFile(rncmobile.getAppContext(),
                                new String[]{ntmFile.toString()},
                                null,
                                null);

                        // Now we send file to HTTP

                        NtmExportTask net = new NtmExportTask(rncmobile.getAppContext(), NtmFileName,
                                inpImportNickname.getText().toString(), inpImportName.getText().toString());
                        net.NtmExportSetData(lRncLogs.size(), nbUmtsLogs, nbLteLogs);
                        net.execute();

                        handler = new Handler();
                        displayResponse.run();

                    } else {
                        Toast.makeText(rncmobile.getAppContext(), "Saisir un pseudo", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
    }

    private void getAllRncLogs() {
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());

        dbl.open();
        lRncLogs = dbl.findAllRncLogs();
        dbl.close();
    }

    private void countLogsByTech() {
        this.nbUmtsLogs = 0;
        this.nbLteLogs = 0;
        for(int i=0;i<lRncLogs.size();i++) {
            if(lRncLogs.get(i).get_tech().equals("3G")) nbUmtsLogs++;
            if(lRncLogs.get(i).get_tech().equals("4G")) nbLteLogs++;
        }
    }

    private void getAllExports() {
        DatabaseExport dbe = new DatabaseExport(rncmobile.getAppContext());

        dbe.open();
        lExport = dbe.findAllExport();
        dbe.close();
    }

    private Runnable displayResponse = new Runnable() {
        public void run() {
            if(!tel.getHttpResponse().equals("")) {
                String html = TextUtils.htmlEncode(tel.getHttpResponse());
                txtResponse.setText(Html.fromHtml(tel.getHttpResponse()));
            }

            handler.postDelayed(this, 5000);
        }
    };
}
