package org.rncteam.rncfreemobile;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.classes.DatabaseInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class InfosFragment extends Fragment {

    TextView txtRncUpdate;

    private Timer t;
    private TaskTimer taskTimer;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_info,container,false);
        this.v = v;

        setTimer();

        return v;
    }

    private void setTimer() {
        if(t != null) t.cancel();
        t = new Timer();

        taskTimer = new TaskTimer();

        t.scheduleAtFixedRate(taskTimer , 0 , 5000);
    }

    public void onPause() {
        super.onPause();
        cancelTimer();
    }

    public void onStop() {
        super.onStop();
        cancelTimer();
    }

    public void onResume() {
        super.onResume();
        setTimer();
    }

    private void cancelTimer() {
        t.cancel();
    }

    private Runnable displayInfos = new Runnable() {
        public void run() {
            // UI
            txtRncUpdate = (TextView) v.findViewById(R.id.txt_rnc_database);
            DatabaseInfo dbi = new DatabaseInfo(rncmobile.getAppContext());

            dbi.open();

            ArrayList lInfo = dbi.getInfo("rncBaseUpdate");

            if (lInfo.size() > 0) {
                txtRncUpdate.setText((String) lInfo.get(1));
            }
            dbi.close();
        }
    };

    class TaskTimer extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(displayInfos);
        }
    }
}
