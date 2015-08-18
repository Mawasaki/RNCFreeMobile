package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.Rnc;
import org.rncteam.rncfreemobile.classes.RncLogs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class ListLogsMainAdapter extends BaseAdapter {

    private static final String TAG = "ListLogsMainAdapter";

    Context context;

    protected List<RncLogs> lCell;
    LayoutInflater inflater;

    public ListLogsMainAdapter(Context context, List<RncLogs> listCell) {
        this.lCell = listCell;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RncLogs rncLog = lCell.get(position);

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

        return convertView;
    }

    private class ViewHolder {
        TextView txtMainInfo;
        TextView txtDate;
        TextView txtTxt;
    }
}
