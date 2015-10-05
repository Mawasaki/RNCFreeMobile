package org.rncteam.rncfreemobile;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.rncteam.rncfreemobile.classes.DatabaseInfo;
import java.util.ArrayList;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class InfosFragment extends Fragment {
    private static final String TAG = "InfosFragment";

    Context context;

    TextView txtInfoVersion1;
    TextView txtInfoVersion2;
    TextView txtRncUpdate;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_info,container,false);
        this.v = v;

        context = rncmobile.getAppContext();

        // Version
        txtInfoVersion1 = (TextView) v.findViewById(R.id.txt_info_version1);
        txtInfoVersion2 = (TextView) v.findViewById(R.id.txt_info_version2);
        // Rnc update
        txtRncUpdate = (TextView) v.findViewById(R.id.txt_rnc_database);

        setInfoRncMobile();
        setInfoVersion();

        return v;
    }

    public void onResume() {
        super.onResume();
        setInfoRncMobile();
        setInfoVersion();
    }

    private void setInfoVersion() {
        txtInfoVersion1.setText(rncmobile.appVersion());
        txtInfoVersion2.setText(rncmobile.appBuild());
    }

    private void setInfoRncMobile() {
        DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());
        dbi.open();

        ArrayList lInfo = dbi.getInfo("rncBaseUpdate");

        if (lInfo.size() > 0)
            txtRncUpdate.setText((String) lInfo.get(1));

        if(txtRncUpdate.getText().equals("0")) txtRncUpdate.setText("Please update");

        dbi.close();
    }
}
