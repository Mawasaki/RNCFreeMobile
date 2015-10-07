package org.rncteam.rncfreemobile.adapters;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import org.rncteam.rncfreemobile.LogsDetailsActivity;
import org.rncteam.rncfreemobile.LogsMapsActivity;
import org.rncteam.rncfreemobile.LogsSetCooActivity;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.DatabaseLogs;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.Date;
import java.util.List;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class ListLogsMainAdapter extends BaseAdapter {
    private static final String TAG = "ListLogsMainAdapter";

    private Context context;
    private Activity activity;

    protected List<RncLogs> lCell;
    LayoutInflater inflater;

    public ListLogsMainAdapter(Activity activity, Context context, List<RncLogs> listCell) {
        this.lCell = listCell;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.activity = activity;
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

    public void remove() {

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.listview_logs_main,
                    parent, false);

            holder.txtMainInfo = (TextView) convertView.findViewById(R.id.txt_logs_main_infos);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_logs_date);
            holder.txtTxt = (TextView) convertView.findViewById(R.id.txt_logs_text);
            holder.txtOperator = (TextView) convertView.findViewById(R.id.txt_logs_operator);
            holder.fm_background = (FrameLayout) convertView.findViewById(R.id.fm_logs_general);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final RncLogs rncLog = lCell.get(position);

        String mainInfo = String.valueOf(rncLog.get_tech()) + " "
                + String.valueOf(rncLog.get_rnc()) + " "
                + String.valueOf(rncLog.get_cid()) + " "
                + String.valueOf(rncLog.get_lac()) + " "
                + String.valueOf(rncLog.get_psc()) + " ";

        // Date
        String fDate = "";
        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        if(rncLog.get_date_obj().after(date)) fDate = rncLog.get_time();
        else fDate = rncLog.get_fr_datetime();

        holder.txtMainInfo.setText(mainInfo);
        holder.txtDate.setText(String.valueOf(fDate));
        holder.txtTxt.setText(String.valueOf(rncLog.get_txt()));
        holder.txtOperator.setText(String.valueOf(rncLog.get_mcc()) + " " + String.valueOf(rncLog.get_mnc()));

        // Roaming operator
        if(!rncLog.get_mnc().equals("15"))
            holder.fm_background.setBackgroundColor(Color.parseColor("#DDDDDD"));
        else
            holder.fm_background.setBackgroundColor(Color.parseColor("#FFFFFF"));
        // Popup
        ImageButton btnMenu = (ImageButton) convertView.findViewById(R.id.logs_btn_menu);

        final View f_view = convertView;

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(rncmobile.getAppContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_logs_listview, popup.getMenu());
                if(rncLog.get_lat().equals("0")) popup.getMenu().findItem(R.id.action_logs_listview_maps).setEnabled(false);
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
                                activity.startActivity(intentLDA);
                                return true;

                            case R.id.action_logs_listview_maps:
                                Intent intentLMA = new Intent(li.getContext(), LogsMapsActivity.class);
                                intentLMA.putExtra("logsInfosObject", rncLog);
                                activity.startActivity(intentLMA);
                                return true;

                            case R.id.action_logs_listview_set_coo:
                                Intent intentLSCA = new Intent(li.getContext(), LogsSetCooActivity.class);
                                intentLSCA.putExtra("logsInfosObject", rncLog);
                                activity.startActivity(intentLSCA);
                                return true;

                            case R.id.action_logs_listview_edit:
                                View popSwitchView = li.inflate(R.layout.popup_logs_edit, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        activity);
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
                                dbl.findAllRncLogsMainList();
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
        FrameLayout fm_background;
    }
}
