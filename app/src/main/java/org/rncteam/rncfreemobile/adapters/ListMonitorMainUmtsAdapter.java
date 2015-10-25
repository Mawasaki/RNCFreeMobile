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
import org.rncteam.rncfreemobile.models.Rnc;

import java.util.List;

/**
 * Created by cedricf_25 on 16/07/2015.
 */

public class ListMonitorMainUmtsAdapter extends BaseAdapter {
    private static final String TAG = "ListMonitorMainUmtsAdapter";

    Context context;

    protected List<Rnc> lCell;
    LayoutInflater inflater;

    public ListMonitorMainUmtsAdapter(Context context, List<Rnc> listCell) {
        this.lCell = listCell;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
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
            convertView = this.inflater.inflate(R.layout.listview_monitor_main_umts,
                    parent, false);

            holder.txtOpe = (TextView) convertView.findViewById(R.id.txt_operator);
            holder.txtCi = (TextView) convertView.findViewById(R.id.txt_ci);
            holder.txtCid = (TextView) convertView.findViewById(R.id.txt_cid);
            holder.txtLac = (TextView) convertView.findViewById(R.id.txt_lac);
            holder.txtRnc = (TextView) convertView.findViewById(R.id.txt_rnc);
            holder.txtPsc = (TextView) convertView.findViewById(R.id.txt_psc);
            holder.txtRscp = (TextView) convertView.findViewById(R.id.txt_rscp);
            holder.txtData = (TextView) convertView.findViewById(R.id.txt_data);
            holder.txrFreq = (TextView) convertView.findViewById(R.id.txt_freq_sect);

            holder.fl_background = (FrameLayout) convertView.findViewById(R.id.fl_monitor_umts_general);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rnc rnc = lCell.get(position);

        holder.txtOpe.setText(rnc.getNetworkName());
        holder.txtCi.setText(String.valueOf(rnc.get_lcid()));
        holder.txtCid.setText(String.valueOf(rnc.getCid()));
        holder.txtLac.setText(String.valueOf(rnc.get_lac()));
        holder.txtRnc.setText(String.valueOf(rnc.getRnc()));
        holder.txtPsc.setText(String.valueOf(rnc.get_psc()));
        holder.txtRscp.setText(String.valueOf(rnc.getUmtsRscp()) +" dBm");
        holder.txtData.setText(rnc.get_txt());

        // Roaming
        if(rnc.get_mnc() != 15)
            holder.fl_background.setBackgroundColor(Color.parseColor("#DDDDDD"));
        else {
            holder.fl_background.setBackgroundColor(Color.parseColor("#FFFFFF"));
            holder.txrFreq.setText(rnc.getFreqTxt() + ((rnc.getSectText().equals("-")) ? "" : " / " + rnc.getSectText()));
        }


        return convertView;
    }

    private class ViewHolder {
        TextView txtOpe;
        TextView txtCi;
        TextView txtCid;
        TextView txtLac;
        TextView txtRnc;
        TextView txtPsc;
        TextView txtRscp;
        TextView txtData;
        TextView txrFreq;

        FrameLayout fl_background;
    }

}
