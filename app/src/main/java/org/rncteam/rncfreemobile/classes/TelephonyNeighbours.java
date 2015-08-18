package org.rncteam.rncfreemobile.classes;

import android.content.Context;

import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;
import android.util.Log;

import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 20/07/2015.
 */
public class TelephonyNeighbours extends Telephony {
    private static final String TAG = "TelephonyNeighbours";

    public TelephonyNeighbours(Context context) {
        super(context);
    }

    // Implement the neigbourgh cell list in RNC Object? ... mhhhh :/
    public ArrayList<Rnc> getNearestNeighboringInRnc(Rnc rnc) {
        ArrayList<Rnc> lRnc = new ArrayList<Rnc>();

        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();

        // Last NeighboringCell Infos
        List<NeighboringCellInfo> lNci = tm.getNeighboringCellInfo();

        Rnc overlapRnc = new Rnc();

        if(lNci != null && lNci.size() > 0) {
            for (int i = 0; i < lNci.size(); i++) {
                Rnc nearestRnc = null;

                List<Rnc> lRncPsc = dbr.findRncByPsc(String.valueOf(lNci.get(i).getPsc()));

                double nearestPoint = -1;
                double tempPoint;

                // If Psc > 0 && Know RNC && Find Pscs in database
                if (lNci.get(i).getPsc() > 0 && !rnc.NOTHING && lRncPsc.size() > 0) {

                    for (int j = 0; j < lRncPsc.size(); j++) {
                        tempPoint = distance(Double.valueOf(lRncPsc.get(j).get_lat()),
                                Double.valueOf(lRncPsc.get(j).get_lon()),
                                Double.valueOf(rnc.get_lat()),
                                Double.valueOf(rnc.get_lon()));

                        //Log.d(TAG, "Trace3: " + lRncPsc.get(j).get_rnc());

                        if ((nearestPoint < 0 || tempPoint < nearestPoint) && !lRncPsc.get(j).equals(overlapRnc)) {

                            nearestPoint = tempPoint;
                            nearestRnc = lRncPsc.get(j);
                        }
                    }

                } else {
                    nearestRnc = new Rnc("", "", "",
                            String.valueOf(lNci.get(i).getCid()), "",
                            String.valueOf(lNci.get(i).getLac()),
                            String.valueOf(lNci.get(i).getPsc()), "", "", "-");
                }

                if (nearestRnc != null) {
                    nearestRnc.set_rssi(lNci.get(i).getRssi());
                    lRnc.add(i, nearestRnc);
                    overlapRnc = nearestRnc;
                }
            }
        }
        dbr.close();

        return lRnc;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1.609344; // in KM

        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}
