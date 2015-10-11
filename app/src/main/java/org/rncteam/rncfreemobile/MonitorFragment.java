package org.rncteam.rncfreemobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextMenu;
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
public class MonitorFragment extends Fragment {
    private static final String TAG = "MonitorFragment";

    private FrameLayout fl;
    private FrameLayout fl_2g;
    ListView listViewRncMain;
    ListView listViewRncPsc;
    View v;

    private Telephony tel;

    private ArrayList<Rnc> lRncs;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.v =inflater.inflate(R.layout.fragment_monitor,container,false);

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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

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

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
        tel.setCellChange(true);
    }

    private Runnable displayMonitor = new Runnable() {
        public void run() {

            listViewRncMain.setVisibility(View.VISIBLE);
            listViewRncPsc.setVisibility(View.VISIBLE);
            fl.setVisibility(View.GONE);
            fl_2g.setVisibility(View.GONE);

            if(tel.getLoggedRnc() != null) {
                lRncs.clear();
                lRncs.add(tel.getLoggedRnc());

                if (tel.getNetworkClass() == 2) {
                    fl.setVisibility(View.GONE);
                    fl_2g.setVisibility(View.VISIBLE);
                    listViewRncMain.setVisibility(View.GONE);
                    listViewRncPsc.setVisibility(View.GONE);
                } else {
                    if (lRncs.get(0).get_tech().equals("3G")) {
                        ListMonitorMainUmtsAdapter adapter = new ListMonitorMainUmtsAdapter(rncmobile.getAppContext(), lRncs);
                        listViewRncMain.setAdapter(adapter);
                    } else if (lRncs.get(0).get_tech().equals("4G")) {
                        ListMonitorMainLteAdapter adapter = new ListMonitorMainLteAdapter(rncmobile.getAppContext(), lRncs);
                        listViewRncMain.setAdapter(adapter);
                    } else {
                        listViewRncMain.setVisibility(View.GONE);
                        fl.setVisibility(View.VISIBLE);
                    }
                }
                // Neighbours cell
                ArrayList<Rnc> arrayNeighCell = tel.getlNeigh();
                if(arrayNeighCell != null && arrayNeighCell.size() > 0) {
                    ListMonitorPscAdapter adapterPsc = new ListMonitorPscAdapter(rncmobile.getAppContext(), arrayNeighCell);
                    listViewRncPsc.setAdapter(adapterPsc);
                } else listViewRncPsc.setVisibility(View.GONE);

            }

            handler.postDelayed(this, 1000);
        }
    };
}
