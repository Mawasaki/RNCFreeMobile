package org.rncteam.rncfreemobile;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import org.rncteam.rncfreemobile.adapters.ListMonitorMainLteAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorMainUmtsAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorPscAdapter;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.classes.Telephony;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
@SuppressWarnings("DefaultFileTemplate")

public class MonitorFragment extends Fragment {
    private static final String TAG = "MonitorFragment";

    private FrameLayout fl;
    private FrameLayout fl_2g;
    private ListView listViewRncMain;
    private ListView listViewRncPsc;

    private Telephony tel;

    private ArrayList<Rnc> lRncs;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_monitor,container,false);

        setHasOptionsMenu(true);

        // Get mains class
        tel = rncmobile.getTelephony();

        // Init var
        lRncs = new ArrayList<>();

        // Init UI
        fl = (FrameLayout) v.findViewById(R.id.lyt_monitor_error);
        fl_2g = (FrameLayout) v.findViewById(R.id.lyt_monitor_2g);
        listViewRncMain = (ListView) v.findViewById(R.id.list_main);
        listViewRncPsc = (ListView) v.findViewById(R.id.list_psc);
        listViewRncPsc.setDivider(null);

        handler = new Handler();
        displayMonitor.run();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_monitor, menu);

        MenuItem btn_LogsDelete = menu.findItem(R.id.action_monitor_database);
        btn_LogsDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                LayoutInflater li = (LayoutInflater) rncmobile.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                Intent intentDA = new Intent(li.getContext(), DataActivity.class);
                startActivity(intentDA);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    public void onResume() {
        super.onResume();
        //tel.setCellChange(true);
    }

    private final Runnable displayMonitor = new Runnable() {
        public void run() {

            if (rncmobile.accessCoarseLocation) {
                listViewRncMain.setVisibility(View.VISIBLE);
                listViewRncPsc.setVisibility(View.VISIBLE);
                fl.setVisibility(View.GONE);
                fl_2g.setVisibility(View.GONE);

                lRncs.clear();
                lRncs.add(tel.getLoggedRnc());

                if (lRncs.get(0) != null &&
                        (tel.getNetworkClass() == 3 || tel.getNetworkClass() == 4)) {

                    if (lRncs.get(0).get_tech() == 3) {
                        ListMonitorMainUmtsAdapter adapter = new ListMonitorMainUmtsAdapter(lRncs);
                        listViewRncMain.setAdapter(adapter);
                    } else if (lRncs.get(0).get_tech() == 4) {
                        ListMonitorMainLteAdapter adapter = new ListMonitorMainLteAdapter(lRncs);
                        listViewRncMain.setAdapter(adapter);
                    } else {
                        listViewRncMain.setVisibility(View.GONE);
                        fl.setVisibility(View.VISIBLE);
                    }
                    // Neighbours cell
                    ArrayList<Rnc> arrayNeighCell = tel.getlNeigh();
                    if (arrayNeighCell != null && arrayNeighCell.size() > 0) {
                        ListMonitorPscAdapter adapterPsc = new ListMonitorPscAdapter(arrayNeighCell);
                        listViewRncPsc.setAdapter(adapterPsc);
                    } else listViewRncPsc.setVisibility(View.GONE);

                } else {
                    listViewRncMain.setVisibility(View.GONE);
                    listViewRncPsc.setVisibility(View.GONE);
                    fl_2g.setVisibility(View.GONE); //// TODO: 21/10/2015
                    fl.setVisibility(View.VISIBLE);

                    if (tel.getNetworkClass() == 2) {
                        fl.setVisibility(View.GONE);
                        fl_2g.setVisibility(View.VISIBLE);
                        listViewRncMain.setVisibility(View.GONE);
                        listViewRncPsc.setVisibility(View.GONE);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        }
    };
}
