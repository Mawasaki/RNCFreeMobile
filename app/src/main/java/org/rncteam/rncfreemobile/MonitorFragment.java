package org.rncteam.rncfreemobile;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.adapters.ListMonitorMainLteAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorMainUmtsAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorPscAdapter;
import org.rncteam.rncfreemobile.classes.CellLte;
import org.rncteam.rncfreemobile.classes.CellWcdma;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.classes.TelephonyNeighbours;

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
    private TelephonyNeighbours tNei;

    private Handler handler;

    boolean debug = true;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_monitor,container,false);
        this.v = v;

        debug = true;

        tel = rncmobile.getTelephony();

        // UI
        fl = (FrameLayout) v.findViewById(R.id.lyt_monitor_error);
        fl_2g = (FrameLayout) v.findViewById(R.id.lyt_monitor_2g);
        listViewRncMain = (ListView) v.findViewById(R.id.list_main);
        listViewRncPsc = (ListView) v.findViewById(R.id.list_psc);
        listViewRncPsc.setDivider(null);

        handler = new Handler();
        displayMonitor.run();

        return v;
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onResume() {
        super.onResume();
    }

    private Runnable displayMonitor = new Runnable() {
        public void run() {
            ArrayList<CellWcdma> arrayCellUmts = new ArrayList<CellWcdma>();
            ArrayList<CellLte> arrayCellLte = new ArrayList<CellLte>();
            ArrayList<Rnc> arrayNeighCell = new ArrayList<Rnc>();

            // Reseau Free
            //if (tel.getCellNetwork().equals("20815")) {
                listViewRncMain.setVisibility(View.VISIBLE);
                listViewRncPsc.setVisibility(View.VISIBLE);
                fl.setVisibility(View.GONE);
                fl_2g.setVisibility(View.GONE);

                if(tel.getDataActivity() != 0) {

                    if (tel.getNetworkClass() == 2) {
                        fl_2g.setVisibility(View.VISIBLE);
                    } else if (tel.getNetworkClass() == 3 && tel.getRegisteredWcdmaCell() != null) {

                        CellWcdma CWcdma = tel.getRegisteredWcdmaCell();
                        arrayCellUmts.add(CWcdma);

                        if (arrayCellUmts.size() > 0) {
                            ListMonitorMainUmtsAdapter adapter = new ListMonitorMainUmtsAdapter(rncmobile.getAppContext(), arrayCellUmts);
                            listViewRncMain.setAdapter(adapter);
                        } else {
                            Toast.makeText(rncmobile.getAppContext(), "(MonitorUI) Error: No valid RNC detected", Toast.LENGTH_LONG).show();
                        }
                    } else if (tel.getNetworkClass() == 4 && tel.getRegisteredLteCell() != null) {

                        arrayCellLte.add(tel.getRegisteredLteCell());

                        if (arrayCellLte.size() > 0) {
                            ListMonitorMainLteAdapter adapter = new ListMonitorMainLteAdapter(rncmobile.getAppContext(), arrayCellLte);
                            listViewRncMain.setAdapter(adapter);
                        } else {
                            Toast.makeText(rncmobile.getAppContext(), "(MonitorUI) Error: No valid RNC detected", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        listViewRncMain.setVisibility(View.GONE);
                        fl.setVisibility(View.VISIBLE);
                    }

                    // Display PSCs
                    arrayNeighCell = tel.getNeighbourCell();

                    if (arrayNeighCell != null && arrayNeighCell.size() > 0) {
                        ListMonitorPscAdapter adapterPsc = new ListMonitorPscAdapter(rncmobile.getAppContext(), arrayNeighCell);
                        listViewRncPsc.setAdapter(adapterPsc);
                    }
                    else listViewRncPsc.setVisibility(View.GONE);
                } else {
                    listViewRncMain.setVisibility(View.GONE);
                    listViewRncPsc.setVisibility(View.GONE);
                    fl.setVisibility(View.VISIBLE);
                }

           /* } else {
                listViewRncMain.setVisibility(View.GONE);
                listViewRncPsc.setVisibility(View.GONE);
            }*/

            handler.postDelayed(this, 1000);
        }
    };
}
