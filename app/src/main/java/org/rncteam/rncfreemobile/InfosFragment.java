package org.rncteam.rncfreemobile;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rncteam.rncfreemobile.database.DatabaseInfo;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class InfosFragment extends Fragment {
    private static final String TAG = "InfosFragment";

    // UI
    private TextView txtInfoVersion1;
    private TextView txtInfoVersion2;
    private TextView txtRncUpdate;
    private TextView txtRncUpdate2;
    private TextView txtRncUpdate3;
    private TextView txtLogsNbTotal;
    private TextView txtLogsNbUmts;
    private TextView txtLogsNbLte;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_info,container,false);

        // Version
        txtInfoVersion1 = (TextView) v.findViewById(R.id.txt_info_version1);
        txtInfoVersion2 = (TextView) v.findViewById(R.id.txt_info_version2);
        // Rnc update
        txtRncUpdate = (TextView) v.findViewById(R.id.txt_rnc_database);
        txtRncUpdate2 = (TextView) v.findViewById(R.id.txt_rnc_database2);
        txtRncUpdate3 = (TextView) v.findViewById(R.id.txt_rnc_database3);
        // Info logs
        txtLogsNbTotal = (TextView) v.findViewById(R.id.txt_logs_nb_total);
        txtLogsNbUmts = (TextView) v.findViewById(R.id.txt_logs_nb_umts);
        txtLogsNbLte = (TextView) v.findViewById(R.id.txt_logs_nb_lte);

        setInfoRncMobile();
        setInfoVersion();
        setInfoLog();

        return v;
    }

    public void onResume() {
        super.onResume();

        setInfoRncMobile();
        setInfoVersion();
    }

    private void setInfoVersion() {
        txtInfoVersion1.setText("Version: " + rncmobile.appVersion());
        txtInfoVersion2.setText("Build: " + rncmobile.appBuild());
    }

    private void setInfoRncMobile() {
        DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
        dbi.open();

        ArrayList lInfo = dbi.getInfo("rncBaseUpdate");
        dbi.close();

        if (lInfo.size() > 0)
            txtRncUpdate.setText((String) lInfo.get(1));

        if(txtRncUpdate.getText().equals("0")) {
            txtRncUpdate.setText("Please update");
            rncmobile.rncDataCharged = false;
        } else {
            rncmobile.rncDataCharged = true;
        }

        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();
        Integer nb_cid = dbr.countAllCid();
        Integer nb_rnc_umts = dbr.countUMTSRnc();
        Integer nb_rnc_lte = dbr.countLTERnc();
        dbr.close();

        txtRncUpdate2.setText("Nb of CIDs = " + String.valueOf(nb_cid));
        txtRncUpdate3.setText("Nb of RNCs UMTS/LTE = " + String.valueOf(nb_rnc_umts) + "/" + String.valueOf(nb_rnc_lte));
    }

    private void setInfoLog() {
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        List<RncLogs> lRncLogs = dbl.findAllRncLogs();

        int nbUmtsLogs = 0;
        int nbLteLogs = 0;

        for(int i=0;i<lRncLogs.size();i++) {
            if(lRncLogs.get(i).get_tech() == 3) nbUmtsLogs++;
            if(lRncLogs.get(i).get_tech() == 4) nbLteLogs++;
        }

        txtLogsNbTotal.setText("Total: " + lRncLogs.size());
        txtLogsNbUmts.setText("Total umts: " + nbUmtsLogs);
        txtLogsNbLte.setText("Total lte: " + nbLteLogs);

        dbl.close();
    }
}
