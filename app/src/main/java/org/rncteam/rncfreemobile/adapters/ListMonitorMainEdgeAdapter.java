package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.activity.rncmobile;
import org.rncteam.rncfreemobile.models.Rnc;

import java.util.List;

/**
 * Created by cedricf_25 on 16/07/2015.
 */

public class ListMonitorMainEdgeAdapter extends BaseAdapter {
    private static final String TAG = "ListMonitorMainEdgeAdapter";

    private final List<Rnc> lCell;

    public ListMonitorMainEdgeAdapter(List<Rnc> listCell) {
        this.lCell = listCell;
    }

    public int getCount() {
        return lCell.size();
    }

    public Rnc getItem(int position) {
        return lCell.get(position);
    }

    public long getItemId(int position) {
        return lCell.get(position).getCid();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater li = (LayoutInflater) rncmobile.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.listview_monitor_main_edge,
                    parent, false);

            holder.txtOpe = (TextView) convertView.findViewById(R.id.txt_operator);
            holder.txtCid = (TextView) convertView.findViewById(R.id.txt_cid_e);
            holder.txtLac = (TextView) convertView.findViewById(R.id.txt_lac);
            holder.txtRxl = (TextView) convertView.findViewById(R.id.txt_rxl);

            holder.fl_background = (FrameLayout) convertView.findViewById(R.id.fl_monitor_edge_general);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rnc rnc = lCell.get(position);

        holder.txtOpe.setText(rnc.getNetworkName());
        holder.txtCid.setText(String.valueOf(rnc.getCid()));
        holder.txtLac.setText(String.valueOf(rnc.get_lac()));
        holder.txtRxl.setText(String.valueOf(rnc.getUmtsRscp()) + " dBm");

        // Roaming
        if(rnc.get_mnc() != 15)
            holder.fl_background.setBackgroundColor(Color.parseColor("#DDDDDD"));
        else {
            holder.fl_background.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }


        return convertView;
    }

    private class ViewHolder {
        TextView txtOpe;
        TextView txtCid;
        TextView txtLac;
        TextView txtRxl;

        FrameLayout fl_background;
    }

}
