package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.List;

/**
 * Created by cedricf_25 on 16/07/2015.
 */

public class ListMonitorMainLteAdapter extends BaseAdapter {

    private static final String TAG = "ListMonitorMainLteAdapter";

    private List<Rnc> lCell;

    public ListMonitorMainLteAdapter(List<Rnc> listCell) {
        this.lCell = listCell;
    }

    public int getCount() {
        return lCell.size();
    }

    public Rnc getItem(int position) {
        return lCell.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater li = (LayoutInflater) rncmobile.getAppContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.listview_monitor_main_lte,
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
            holder.txtAsu = (TextView) convertView.findViewById(R.id.txt_asu);
            holder.txtCqi = (TextView) convertView.findViewById(R.id.txt_cqi);
            holder.txtTA = (TextView) convertView.findViewById(R.id.txt_ta);
            holder.txtData = (TextView) convertView.findViewById(R.id.txt_data);
            holder.txrFreq = (TextView) convertView.findViewById(R.id.txt_freq_sect);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rnc rnc = lCell.get(position);

        holder.txtOpe.setText(String.valueOf(rnc.getNetworkName()));
        holder.txtCi.setText(String.valueOf(rnc.get_lcid()));
        holder.txtCid.setText(String.valueOf(rnc.getRnc() + ":" +rnc.getCid()));
        holder.txtTac.setText(String.valueOf(rnc.get_lac()));
        holder.txtPci.setText(String.valueOf(rnc.get_psc()));
        holder.txtRssi.setText(String.valueOf(rnc.computeRssi()) + " dBm");
        holder.txtRsrp.setText(String.valueOf(rnc.getLteRsrp()) + " dBm");
        holder.txtRsrq.setText(String.valueOf(rnc.getLteRsrq()) + " dB");
        holder.txtSnr.setText(String.valueOf((rnc.getLteRssnr() / 10) + " dB"));
        holder.txtAsu.setText(String.valueOf(rnc.getLteAsu()));
        holder.txtCqi.setText(String.valueOf(rnc.getLteCqi()));
        holder.txtTA.setText(String.valueOf(rnc.getLteTA()));
        holder.txtData.setText(rnc.get_txt());

        holder.txrFreq.setText(rnc.getFreqTxt() + ((rnc.getSectText().equals("-")) ? "" : " / " + rnc.getSectText()));

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
        TextView txtAsu;
        TextView txtCqi;
        TextView txtTA;
        TextView txtData;
        TextView txrFreq;
    }

}
