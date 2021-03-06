package org.rncteam.rncfreemobile.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import android.widget.ListView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.adapters.ListLogsMainAdapter;

import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.util.ArrayList;


/**
 * Created by cedricf_25 on 14/07/2015.
 */
public class LogsFragment extends Fragment {

    private static final String TAG = "LogsFragment";

    private ListLogsMainAdapter adapterLogs;

    private Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_logs,container,false);

        setHasOptionsMenu(true);

        rncmobile.listRncLogs = new ArrayList<>();

        getAllRncLogs();

        // Init listview
        ListView listViewLogsMain = (ListView) v.findViewById(R.id.list_logs);
        listViewLogsMain.setDivider(null);

        adapterLogs = new ListLogsMainAdapter(rncmobile.listRncLogs);
        listViewLogsMain.setAdapter(adapterLogs);

        handler = new Handler();
        displayLogs.run();

        return v;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

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
        rncmobile.displayView = 1;
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        getAllRncLogs();
    }

    private void getAllRncLogs() {

        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        ArrayList<RncLogs> lRncLogs = dbl.findAllRncLogs();

        rncmobile.listRncLogs.clear();
        for(int i=0;i<lRncLogs.size();i++)
            rncmobile.listRncLogs.add(i,lRncLogs.get(i));

        dbl.close();
    }

    private void removeList() {
        rncmobile.listRncLogs.clear();
        adapterLogs.notifyDataSetChanged();
    }

    private final Runnable displayLogs = new Runnable() {
        public void run() {
            if(rncmobile.notifyListLogsHasChanged) {
                getAllRncLogs();
            }
            adapterLogs.notifyDataSetChanged();
            rncmobile.notifyListLogsHasChanged = false;
            handler.postDelayed(this, 3000);
        }
    };
}
