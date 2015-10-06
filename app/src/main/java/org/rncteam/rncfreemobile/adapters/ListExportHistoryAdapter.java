package org.rncteam.rncfreemobile.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.models.Export;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.Date;
import java.util.List;

/**
 * Created by cedricf_25 on 05/10/2015.
 */
public class ListExportHistoryAdapter extends BaseAdapter {
    private static final String TAG = "ListLogsMainAdapter";

    private Context context;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Export> lExportHistory;

    public ListExportHistoryAdapter(Activity activity, Context context, List<Export> lExportHistory) {
        this.activity = activity;
        this.lExportHistory = lExportHistory;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return lExportHistory.size();
    }

    public Export getItem(int position) {
        return lExportHistory.get(position);
    }

    public long getItemId(int position) {
        return lExportHistory.get(position).get_id();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            // Get UI Elements
            convertView = this.inflater.inflate(R.layout.listview_export_history,
                    parent, false);

            // TextView
            holder.txtMainInfo = (TextView) convertView.findViewById(R.id.txt_export_history_infos);
            holder.txtDate = (TextView) convertView.findViewById(R.id.txt_export_history_date);
            holder.txtNbTotal = (TextView) convertView.findViewById(R.id.txt_export_history_text_nb_total);
            holder.txtNbDetailed = (TextView) convertView.findViewById(R.id.txt_export_history_text_nb_detailed);
            holder.txtType = (TextView) convertView.findViewById(R.id.txt_export_history_text_type);
            holder.txtTel = (TextView) convertView.findViewById(R.id.txt_export_history_text_tel);
            holder.txtVersion = (TextView) convertView.findViewById(R.id.txt_export_history_text_version);

            holder.btnMenu = (ImageButton) convertView.findViewById(R.id.logs_btn_logs_history_menu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Export export = lExportHistory.get(position);

        // Set data to UI
        String mainInfo = "#" + String.valueOf(export.get_id()) + " "
                + ((export.get_name().equals("")) ? "" : export.get_name() + " ") + "by "
                + export.get_user_nick();

        String nbDetailed = "Number exported: " + export.get_nb()
                + " / 3G: " + export.get_nb_umts() + " / 4G: " + export.get_nb_lte();

        // Date
        String fDate = "";
        Date date = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
        if(export.get_date_obj().after(date)) fDate = export.get_time();
        else fDate = export.get_fr_datetime();

        holder.txtMainInfo.setText(mainInfo);
        holder.txtDate.setText(String.valueOf(fDate));
        holder.txtNbTotal.setText("Number Total Exported: " + export.get_nb());
        holder.txtNbDetailed.setText(nbDetailed);
        holder.txtType.setText("Type of export: " + export.get_type());
        holder.txtTel.setText("Phone: " + export.get_user_tel());
        holder.txtVersion.setText("App version: " + export.get_app_version());

        final View f_view = convertView;

        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(rncmobile.getAppContext(), view);
                popup.getMenuInflater().inflate(R.menu.menu_logs_history_listview, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.action_logs_history_listview_remove:
                                Toast.makeText(rncmobile.getAppContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.action_logs_history_listview_resend:
                                Toast.makeText(rncmobile.getAppContext(), "Not implemented yet", Toast.LENGTH_SHORT).show();
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
        TextView txtNbTotal;
        TextView txtNbDetailed;
        TextView txtType;
        TextView txtTel;
        TextView txtVersion;
        ImageButton btnMenu;
    }
}
