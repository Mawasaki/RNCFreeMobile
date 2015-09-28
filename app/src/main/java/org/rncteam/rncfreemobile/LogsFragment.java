package org.rncteam.rncfreemobile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.rncteam.rncfreemobile.adapters.ListLogsMainAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorMainLteAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorMainUmtsAdapter;
import org.rncteam.rncfreemobile.adapters.ListMonitorPscAdapter;
import org.rncteam.rncfreemobile.classes.DatabaseLogs;
import org.rncteam.rncfreemobile.classes.DatabaseRnc;
import org.rncteam.rncfreemobile.classes.Rnc;
import org.rncteam.rncfreemobile.classes.RncLogs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class LogsFragment extends Fragment {

    private static final String TAG = "LogsFragment";

    ListView listViewLogsMain;
    View v;

    int count_d = 4001;

    ListLogsMainAdapter adapterLogs;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_logs,container,false);
        this.v = v;

        setHasOptionsMenu(true);

        getAllRncLogs();

        // Init listview
        listViewLogsMain = (ListView) v.findViewById(R.id.list_logs);
        adapterLogs = new ListLogsMainAdapter(rncmobile.getAppContext(), rncmobile.listRncLogs);
        listViewLogsMain.setAdapter(adapterLogs);

        handler = new Handler();
        displayLogs.run();

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getActivity().getMenuInflater().inflate(R.menu.menu_tabs, menu);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_tabs, menu);

        MenuItem btn_LogsDelete = menu.findItem(R.id.action_logs_delete);
        btn_LogsDelete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_img_warning)
                        .setTitle("Delete all logs")
                        .setMessage("Are you sure you want to delete all logs?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeList();

                                DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                                dbl.open();
                                dbl.deleteRncLogs();
                                dbl.close();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
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

    @Override
    public void onResume() {
        super.onResume();
        getAllRncLogs();
        adapterLogs = new ListLogsMainAdapter(rncmobile.getAppContext(), rncmobile.listRncLogs);
        listViewLogsMain.setAdapter(adapterLogs);
    }

    public void getAllRncLogs() {
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());

        dbl.open();
        rncmobile.listRncLogs = dbl.findAllRncLogs();
        dbl.close();
    }

    public void removeList() {
        rncmobile.listRncLogs.clear();
        adapterLogs.notifyDataSetChanged();
    }

    private Runnable displayLogs = new Runnable() {
        public void run() {
            adapterLogs.notifyDataSetChanged();

            handler.postDelayed(this, 5000);
        }
    };

}
