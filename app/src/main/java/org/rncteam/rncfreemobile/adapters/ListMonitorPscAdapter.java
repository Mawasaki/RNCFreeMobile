package org.rncteam.rncfreemobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.models.Rnc;

import java.util.List;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class ListMonitorPscAdapter extends BaseAdapter {

    private static final String TAG = "ListMonitorPscAdapter";

    Context context;

    protected List<Rnc> lCell;
    LayoutInflater inflater;

    public ListMonitorPscAdapter(Context context, List<Rnc> listCell) {
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
        return lCell.get(position).get_id();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.listview_monitor_psc,
                    parent, false);

            holder.txtPsc = (TextView) convertView.findViewById(R.id.txt_psc_psc);
            holder.txtRssi = (TextView) convertView.findViewById(R.id.txt_psc_rssi);
            holder.txtTxt = (TextView) convertView.findViewById(R.id.txt_psc_text);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Rnc rnc = lCell.get(position);

        String psc_cid = String.valueOf(rnc.get_psc());

        if(!rnc.NOT_IDENTIFIED) {
            psc_cid += "  " + String.valueOf(rnc.get_rnc());
            psc_cid += ":" + String.valueOf(rnc.get_cid());
        }

        holder.txtPsc.setText(psc_cid);
        holder.txtRssi.setText(String.valueOf(rnc.getLteRssi()) + " dBm");
        holder.txtTxt.setText(String.valueOf(rnc.get_txt()));

        return convertView;
    }

    private class ViewHolder {
        TextView txtPsc;
        TextView txtRssi;
        TextView txtTxt;
    }
}
