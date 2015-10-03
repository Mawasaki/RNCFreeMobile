package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.CellLte;

import java.util.List;

/**
 * Created by cedricf_25 on 16/07/2015.
 */

public class ListMonitorMainLteAdapter extends BaseAdapter {

    private static final String TAG = "ListMonitorMainLteAdapter";

    Context context;

    protected List<CellLte> lCell;
    LayoutInflater inflater;

    public ListMonitorMainLteAdapter(Context context, List<CellLte> listCell) {
        this.lCell = listCell;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public int getCount() {
        return lCell.size();
    }

    public CellLte getItem(int position) {
        return lCell.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.listview_monitor_main_lte,
                    parent, false);

            holder.txtOpe = (TextView) convertView.findViewById(R.id.txt_operator);
            holder.txtCi = (TextView) convertView.findViewById(R.id.txt_ci);
            holder.txtCid = (TextView) convertView.findViewById(R.id.txt_cid);
            holder.txtTac = (TextView) convertView.findViewById(R.id.txt_tac);
            holder.txtPci = (TextView) convertView.findViewById(R.id.txt_pci);
            holder.txtRssi = (TextView) convertView.findViewById(R.id.txt_rssi);
            holder.txtRsrp = (TextView) convertView.findViewById(R.id.txt_rsrp);
            holder.txtRsrq = (TextView) convertView.findViewById(R.id.txt_rsrq);
            holder.txtSnr = (TextView) convertView.findViewById(R.id.txt_snr);
            holder.txtData = (TextView) convertView.findViewById(R.id.txt_data);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CellLte tLte = lCell.get(position);


        holder.txtOpe.setText(String.valueOf(tLte.getNetworkName()));
        holder.txtCi.setText(String.valueOf(tLte.getLCid()));
        holder.txtCid.setText(String.valueOf(tLte.getFormatedCid()));
        holder.txtTac.setText(String.valueOf(tLte.getTac()));
        holder.txtPci.setText(String.valueOf(tLte.getPci()));
        holder.txtRssi.setText(String.valueOf("-" + tLte.getLteSignalStrength()) + " dBm");
        holder.txtRsrp.setText(String.valueOf(tLte.getLteRsrp()) + " dBm");
        holder.txtRsrq.setText(String.valueOf(tLte.getLteRsrq()) + " dB");
        holder.txtSnr.setText(String.valueOf(tLte.getLteRssnr()) + " dB");
        holder.txtData.setText(tLte.getText());


        return convertView;
    }

    private class ViewHolder {
        TextView txtOpe;
        TextView txtCi;
        TextView txtCid;
        TextView txtTac;
        TextView txtPci;
        TextView txtRssi;
        TextView txtRsrp;
        TextView txtRsrq;
        TextView txtSnr;
        TextView txtData;
    }

}
