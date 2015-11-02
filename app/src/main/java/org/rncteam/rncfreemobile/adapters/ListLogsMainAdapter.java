package org.rncteam.rncfreemobile.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.rncteam.rncfreemobile.LogsDetailsActivity;
import org.rncteam.rncfreemobile.MainActivity;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.classes.Utils;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class ListLogsMainAdapter extends BaseAdapter {
    private static final String TAG = "ListLogsMainAdapter";

    private ArrayList<RncLogs> lCell;

    public ListLogsMainAdapter(ArrayList<RncLogs> listCell) {
        this.lCell = listCell;
    }

    public int getCount() {
        return lCell.size();
    }

    public RncLogs getItem(int position) {
        return lCell.get(position);
    }

    public long getItemId(int position) {
        return lCell.get(position).get_id();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();

            LayoutInflater li = (LayoutInflater) rncmobile.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.listview_logs_main,
                    parent, false);

            holder.txtMainInfo = (TextView) convertView.findViewById(R.id.txt_logs_main_infos);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_logs_date);
            holder.txtTxt = (TextView) convertView.findViewById(R.id.txt_logs_text);
            holder.txtOperator = (TextView) convertView.findViewById(R.id.txt_logs_operator);
            holder.fm_background = (FrameLayout) convertView.findViewById(R.id.fm_logs_general);
            holder.imvSync = (ImageView) convertView.findViewById(R.id.imv_logs_main_sync);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final RncLogs rncLog = lCell.get(position);

        String mainInfo = String.valueOf((rncLog.get_tech() == 3) ? "3G" : "4G" ) + " "
                + String.valueOf(rncLog.get_rnc()) + " "
                + String.valueOf(rncLog.get_cid()) + " "
                + String.valueOf(rncLog.get_lac()) + " "
                + String.valueOf(rncLog.get_psc()) + " ";

        // Date
        String lDate; String fDate;
        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);

        if(Utils.get_date_obj(rncLog.get_date()).after(date)) lDate = Utils.get_time(rncLog.get_date());
        else lDate = Utils.get_fr_datetime(rncLog.get_date());

        fDate = Utils.get_formated_date_abbrev(rncLog.get_date());

        holder.txtMainInfo.setText(mainInfo);
        holder.txtDate.setText(String.valueOf(fDate));
        holder.txtTxt.setText(String.valueOf(rncLog.get_txt()));
        holder.txtOperator.setText(lDate);

        // Popup
        ImageButton btnMenu = (ImageButton) convertView.findViewById(R.id.logs_btn_menu);

        // Roaming operator
        if(rncLog.get_mnc() != 15) {
            holder.fm_background.setBackgroundColor(Color.parseColor("#DDDDDD"));
            holder.imvSync.setVisibility(View.GONE);
        } else {
            holder.fm_background.setBackgroundColor(Color.parseColor("#FFFFFF"));

            // Sync
            // If rnc come from 20815, dismiss imageview
        /* FOR TESTS mettre !=
        if(rncLog.isRncIdentified() && rncLog.get_sync() == 0) {
            holder.imvSync.setVisibility(View.GONE);
        }
        else {
        */
            holder.imvSync.setVisibility(View.VISIBLE);

            // Set good sync img
            if (rncLog.get_sync() > 1) {
                holder.imvSync.setImageResource(R.drawable.ic_done_black);
            } else {
                holder.imvSync.setImageResource(R.drawable.ic_autorenew_black);
            }
            //}
        }
        final ViewGroup f_parent = parent;

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Maps maps = rncmobile.getMaps();
                Telephony tel = rncmobile.getTelephony();
                final Rnc loggedRnc = tel.getLoggedRnc();

                PopupMenu popup = new PopupMenu(rncmobile.getAppContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_logs_listview, popup.getMenu());

                if(rncLog.get_lat() == 0.0) popup.getMenu().findItem(R.id.action_logs_listview_maps).setEnabled(false);
                else popup.getMenu().findItem(R.id.action_logs_listview_maps).setEnabled(true);

                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        LayoutInflater li = (LayoutInflater) rncmobile.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        switch (menuItem.getItemId()) {
                            case R.id.action_logs_listview_details:
                                Intent intentLDA = new Intent(li.getContext(), LogsDetailsActivity.class);
                                intentLDA.putExtra("logsInfosObject", rncLog);
                                rncmobile.getMainActivity().startActivity(intentLDA);
                                return true;

                            case R.id.action_logs_listview_maps:
                                MainActivity mainActivity1 = (MainActivity) rncmobile.getMainActivity();
                                mainActivity1.displayView(3);

                                // Center on the point defined
                                if (maps != null && maps.getMap() != null && !loggedRnc.NOT_IDENTIFIED) {
                                    maps.setLastZoom(13.0f);
                                    maps.setCenterCamera(loggedRnc.get_lat(),
                                            loggedRnc.get_lon());
                                }

                                return true;

                            case R.id.action_logs_listview_set_coo:
                                MainActivity mainActivity2 = (MainActivity) rncmobile.getMainActivity();
                                mainActivity2.displayView(3);

                                // Center on the point defined
                                if (maps != null && maps.getMap() != null && !loggedRnc.NOT_IDENTIFIED) {
                                    maps.setLastZoom(13.0f);
                                    maps.setCenterCamera(loggedRnc.get_lat(),
                                            loggedRnc.get_lon());
                                }

                                return true;

                            case R.id.action_logs_listview_edit:
                                View popSwitchView = li.inflate(R.layout.popup_logs_edit, f_parent, false);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        rncmobile.getMainActivity());
                                alertDialogBuilder.setView(popSwitchView);

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,int id) {
                                                        // get user input and set it to result
                                                        // edit text
                                                        //result.setText(userInput.getText());
                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                return true;

                            case R.id.action_logs_listview_delete:
                                DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                                dbl.open();
                                dbl.deleteOneLogs(rncLog);
                                rncmobile.notifyListLogsHasChanged = true;
                                dbl.close();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

        return convertView;
    }

    private class ViewHolder {
        TextView txtMainInfo;
        TextView txtDate;
        TextView txtTxt;
        TextView txtOperator;
        ImageView imvSync;
        FrameLayout fm_background;
    }
}
