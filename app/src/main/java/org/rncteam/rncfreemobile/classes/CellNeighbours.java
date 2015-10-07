package org.rncteam.rncfreemobile.classes;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 31/07/2015.
 */
public class CellNeighbours {
    private static final String TAG = "CellNeighbours";

    private ArrayList<Rnc> lEmpltyNeigh;

    public CellNeighbours() {
        lEmpltyNeigh = new ArrayList<>();
    }

    public void add(int lac, int psc, int lcid, int signalStrength) {
        Rnc rnc = new Rnc();
        rnc.set_lac(String.valueOf(lac));
        rnc.set_psc(String.valueOf(psc));
        rnc.set_cid(String.valueOf(lcid));
        rnc.set_rssi(signalStrength);
        lEmpltyNeigh.add(rnc);
    }

    // Implement the neigbourgh cell list in RNC Object? ... mhhhh :/
    public ArrayList<Rnc> getNearestNeighboringInRnc(Rnc rnc) {
        ArrayList<Rnc> lRnc = new ArrayList<Rnc>();

        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();

        Rnc overlapRnc = new Rnc();

        if(lEmpltyNeigh != null && lEmpltyNeigh.size() > 0) {
            for (int i = 0; i < lEmpltyNeigh.size(); i++) {
                Rnc nearestRnc = null;

                if(Integer.valueOf(lEmpltyNeigh.get(i).get_psc()) > 0) {
                    List<Rnc> lRncPsc = dbr.findRncByPsc(String.valueOf(lEmpltyNeigh.get(i).get_psc()));
                    double nearestPoint = -1;
                    double tempPoint;

                    // If Psc > 0 && Know RNC && Find Pscs in database
                    if (!rnc.NOTHING && lRncPsc.size() > 0) {

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
                                String.valueOf(lEmpltyNeigh.get(i).get_cid()), "",
                                String.valueOf(lEmpltyNeigh.get(i).get_lac()),
                                String.valueOf(lEmpltyNeigh.get(i).get_psc()), 0.0, 0.0, "-");
                    }

                } else {
                    nearestRnc = new Rnc("", "", "",
                            String.valueOf(lEmpltyNeigh.get(i).get_cid()), "",
                            String.valueOf(lEmpltyNeigh.get(i).get_lac()),
                            String.valueOf(lEmpltyNeigh.get(i).get_psc()), 0.0, 0.0, "-");
                }

                if (nearestRnc != null) {
                    nearestRnc.set_rssi(lEmpltyNeigh.get(i).get_rssi());
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
