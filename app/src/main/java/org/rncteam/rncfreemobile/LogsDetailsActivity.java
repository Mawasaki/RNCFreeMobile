package org.rncteam.rncfreemobile;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.rncteam.rncfreemobile.database.Database;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;

/**
 * Created by cedric_f25 on 29/09/2015.
 */
public class LogsDetailsActivity extends Activity {
    private static final String TAG = "LogsDetailsActivity";

    private RncLogs rnclogs;

    // UI
    Button btnDetailRestore;
    Button btnDetailDelete;
    EditText txtDetailText;
    EditText txtDetailLat;
    EditText txtDetailLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs_details);

        this.rnclogs = (RncLogs) getIntent().getSerializableExtra("logsInfosObject");

        setUIDetailsInfos();

        this.btnDetailRestore = (Button) findViewById(R.id.btn_detail_restore);
        btnDetailRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUIDetailsInfos();
            }
        });

        this.btnDetailDelete = (Button) findViewById(R.id.btn_detail_delete);
        btnDetailDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteThisCell();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Write new text and new coordinates
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        RncLogs rncLog = dbl.findRncLogsByRncCid(String.valueOf(rnclogs.get_rnc()), String.valueOf(rnclogs.get_cid()));
        if(rncLog != null) {
            rncLog.set_txt(this.txtDetailText.getText().toString());
            rncLog.set_lat(Double.valueOf(this.txtDetailLat.getText().toString()));
            rncLog.set_lon(Double.valueOf(this.txtDetailLon.getText().toString()));
            dbl.updateEditedLogs(rncLog);
        }
        rncmobile.notifyListLogsHasChanged = true;

        dbl.close();

        // Update RNC database
        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();

        Rnc rnc =  dbr.findRncByNameCid(String.valueOf(rnclogs.get_rnc()), String.valueOf(rnclogs.get_cid()));

        rnc.set_txt(this.txtDetailText.getText().toString());
        rnc.set_lat(Double.valueOf(this.txtDetailLat.getText().toString()));
        rnc.set_lon(Double.valueOf(this.txtDetailLon.getText().toString()));
        dbr.updateRnc(rnc);

        dbr.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Log.d(TAG, "On stop");
    }

    private void setUIDetailsInfos() {
        // Set information to UI
        TextView txtDetailTitle = (TextView) findViewById(R.id.txt_detail_title);
        TextView txtDetailProvider = (TextView) findViewById(R.id.txt_detail_provider);
        TextView txtDetailCid = (TextView) findViewById(R.id.txt_detail_cid);
        TextView txtDetailLac = (TextView) findViewById(R.id.txt_detail_lac);
        TextView txtDetailRnc = (TextView) findViewById(R.id.txt_detail_rnc);
        this.txtDetailText = (EditText) findViewById(R.id.inp_detail_text);
        this.txtDetailLat = (EditText) findViewById(R.id.inp_detail_lat);
        this.txtDetailLon = (EditText) findViewById(R.id.inp_detail_lon);


        txtDetailTitle.setText("Cell Information (" + rnclogs.get_tech() + ")");
        txtDetailProvider.setText(rnclogs.get_mcc() + "" + rnclogs.get_mnc());
        txtDetailCid.setText(rnclogs.get_cid());
        txtDetailLac.setText(rnclogs.get_lac());
        txtDetailRnc.setText(rnclogs.get_rnc());
        txtDetailText.setText(rnclogs.get_txt());
        txtDetailLat.setText(String.valueOf(rnclogs.get_lat()));
        txtDetailLon.setText(String.valueOf(rnclogs.get_lon()));
    }

    private void deleteThisCell() {
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        dbl.deleteOneLogs(rnclogs);

        dbl.close();

        this.finish();
    }
}
