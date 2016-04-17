package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.activity.rncmobile;

import java.util.List;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class ListMonitorPscAdapter extends BaseAdapter {
    private static final String TAG = "ListMonitorPscAdapter";

    protected final List<Rnc> lCell;

    public ListMonitorPscAdapter(List<Rnc> listCell) {
        this.lCell = listCell;
    }

    public int getCount() {
        return lCell.size();
    }

    public Rnc getItem(int position) {
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
            convertView = li.inflate(R.layout.listview_monitor_psc,
                    parent, false);

            holder.txtPsc = (TextView) convertView.findViewById(R.id.txt_psc_psc);
            holder.txtRssi = (TextView) convertView.findViewById(R.id.txt_psc_rssi);
            holder.txtTxt = (TextView) convertView.findViewById(R.id.txt_psc_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try{
            Rnc rnc = lCell.get(position);

            String psc_cid = String.valueOf(rnc.get_psc());

            if(!rnc.NOT_IDENTIFIED && !rnc.get_txt().equals("-")) {
                psc_cid += "  " + String.valueOf(rnc.get_rnc());
                psc_cid += ":" + String.valueOf(rnc.get_cid());
            }

            holder.txtPsc.setText(psc_cid);
            holder.txtRssi.setText(String.valueOf((rnc.getLteRssi()) + " dBm"));
            holder.txtTxt.setText(String.valueOf(rnc.get_txt()));
        }
        catch (IndexOutOfBoundsException e){
            lCell.clear();
        }

        return convertView;
    }

    private class ViewHolder {
        TextView txtPsc;
        TextView txtRssi;
        TextView txtTxt;
    }
}
