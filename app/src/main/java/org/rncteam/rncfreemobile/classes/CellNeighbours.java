package org.rncteam.rncfreemobile.classes;

import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.util.Log;
import android.widget.Toast;

import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.activity.rncmobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 31/07/2015.
 */
public class CellNeighbours {
    private static final String TAG = "CellNeighbours";

    private ArrayList<Rnc> lRnc;

    public CellNeighbours() {
        lRnc = new ArrayList<>();
    }

    public void startManager() {
        Telephony tel = rncmobile.getTelephony();

        if(tel != null) {
            lRnc = new ArrayList<>();

            try {

                List<CellInfo> lAci = tel.getTelephonyManager().getAllCellInfo();
                if (lAci != null && lAci.size() > 0) { // If device supports new API
                    for (CellInfo cellInfo : lAci) {
                        Rnc rnc = new Rnc();

                        // Some base init infos
                        rnc.NOT_IDENTIFIED = true;
                        rnc.set_txt("-");

                        if (cellInfo != null && cellInfo instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;

                            rnc.setIsRegistered(cellInfoWcdma.isRegistered());

                            rnc.set_mcc(cellInfoWcdma.getCellIdentity().getMcc());
                            rnc.set_mnc(cellInfoWcdma.getCellIdentity().getMnc());
                            rnc.set_lac(cellInfoWcdma.getCellIdentity().getLac());
                            rnc.set_psc(cellInfoWcdma.getCellIdentity().getPsc());
                            rnc.set_lcid(cellInfoWcdma.getCellIdentity().getCid());

                            rnc.setLteRssi(cellInfoWcdma.getCellSignalStrength().getDbm());

                            if (!cellInfoWcdma.isRegistered() && tel.getNetworkClass() == 3) {
                                lRnc.add(rnc);
                            }

                        }
                        if (cellInfo != null && cellInfo instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

                            rnc.setIsRegistered(cellInfoLte.isRegistered());

                            rnc.set_mcc(cellInfoLte.getCellIdentity().getMcc());
                            rnc.set_mcc(cellInfoLte.getCellIdentity().getMnc());
                            rnc.set_lac(cellInfoLte.getCellIdentity().getTac());
                            rnc.set_psc(cellInfoLte.getCellIdentity().getPci());
                            rnc.set_lcid(cellInfoLte.getCellIdentity().getCi());

                            rnc.setLteRssi(cellInfoLte.getCellSignalStrength().getDbm());

                            // How the new API is tunneling LTE Neigh ?
                            if (!cellInfoLte.isRegistered() && tel.getNetworkClass() == 4) {
                                lRnc.add(rnc);
                            }
                        }
                    }
                } else {
                    List<NeighboringCellInfo> lNci = tel.getTelephonyManager().getNeighboringCellInfo();

                    for (int i = 0; i < lNci.size(); i++) {
                        Rnc rnc = new Rnc();

                        // Some base init infos
                        rnc.NOT_IDENTIFIED = true;
                        rnc.set_txt("-");

                        // Infos from API
                        rnc.set_lac(lNci.get(i).getLac());
                        rnc.set_psc(lNci.get(i).getPsc());
                        rnc.set_lcid(lNci.get(i).getCid());
                        rnc.setLteRssi(lNci.get(i).getRssi());

                        lRnc.add(rnc);
                    }
                }
            } catch(Exception e) {
                String msg = "Erreur manager CellNeighbours";
                HttpLog.send(TAG, e, msg);
                Log.d(TAG, msg + e.toString());
                Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Implement the neigbourgh cell list in RNC Object? ... mhhhh :/
    public ArrayList<Rnc> getNearestNeighboringInRnc(Rnc rnc) {

        try {
            DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
            dbr.open();

            Rnc overlapRnc = new Rnc();

            if (lRnc.size() > 0) {
                for (int i = 0; i < lRnc.size(); i++) {
                    Rnc nearestRnc = lRnc.get(i);

                    if (lRnc.get(i).get_psc() > 0) {
                        // List of all PSC
                        List<Rnc> lRncPsc = dbr.findRncByPsc(String.valueOf(lRnc.get(i).get_psc()));
                        double nearestPoint = -1;
                        double tempPoint;

                        // If Psc > 0 && Know RNC && Find Pscs in database
                        if (!rnc.NOT_IDENTIFIED && lRncPsc.size() > 0) {

                            for (int j = 0; j < lRncPsc.size(); j++) {
                                tempPoint = distance(lRncPsc.get(j).get_lat(),
                                        lRncPsc.get(j).get_lon(),
                                        rnc.get_lat(),
                                        rnc.get_lon());

                                if ((nearestPoint < 0 || tempPoint < nearestPoint) && !lRncPsc.get(j).equals(overlapRnc)) {
                                    nearestPoint = tempPoint;
                                    nearestRnc = lRncPsc.get(j);
                                }
                            }
                        }
                        if (nearestRnc != null) {
                            nearestRnc.setLteRssi(lRnc.get(i).getLteRssi());
                            if (!rnc.NOT_IDENTIFIED) nearestRnc.NOT_IDENTIFIED = false;
                            lRnc.set(i, nearestRnc);
                            overlapRnc = nearestRnc;
                        }
                    } else lRnc.remove(i);
                }
            }
            dbr.close();

        } catch(Exception e) {
            String msg = "Erreur lors du calcul distance cellNeighbours";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_SHORT).show();
        }
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
