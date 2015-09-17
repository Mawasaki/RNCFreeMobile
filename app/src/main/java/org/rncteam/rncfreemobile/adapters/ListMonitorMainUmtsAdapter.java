package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.CellWcdma;

import java.util.List;

/**
 * Created by cedricf_25 on 16/07/2015.
 */

public class ListMonitorMainUmtsAdapter extends BaseAdapter {
    private static final String TAG = "ListMonitorMainUmtsAdapter";

    Context context;

    protected List<CellWcdma> lCell;
    LayoutInflater inflater;

    public ListMonitorMainUmtsAdapter(Context context, List<CellWcdma> listCell) {
        this.lCell = listCell;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return lCell.size();
    }

    public CellWcdma getItem(int position) {
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

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CellWcdma tWcdma = lCell.get(position);

        holder.txtOpe.setText(tWcdma.getNetworkName());
        holder.txtCi.setText(String.valueOf(tWcdma.getLCid()));
        holder.txtCid.setText(String.valueOf(tWcdma.getCid()));
        holder.txtLac.setText(String.valueOf(tWcdma.getLac()));
        holder.txtRnc.setText(String.valueOf(tWcdma.getRnc()));
        holder.txtPsc.setText(String.valueOf(tWcdma.getPsc()));
        holder.txtRscp.setText(String.valueOf(tWcdma.getCellSignalStrengthDbm()) + " dBm");
        holder.txtData.setText(tWcdma.getText());

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
    }

}
