package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.List;

public class Telephony {
    private static final String TAG = "Telephony";

    protected TelephonyManager tm;
    protected CellLocation cellLocation;
    protected GsmCellLocation gsmCellLocation;

    private TelephonyStateListener tsl;
    private SignalStrength signalStrength;

    private List<CellInfo> lNci;
    private CellWcdma cWcdma;
    private CellLte cLte;
    private CellNeighbours cNeigh;

    private ArrayList<CellWcdma> lcWcdma;
    private ArrayList<CellLte> lcLte;
    ArrayList<Rnc> lcNeigh;

    private Rnc loggedRnc;
    private Rnc loggedKnowRnc;

    private Context mContext;

    public Telephony(Context context) {
        mContext = context;
        tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        cellLocation = tm.getCellLocation();

        gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
        setSignalListener();

        signalStrength = null;

        lcWcdma = new ArrayList<CellWcdma>();
        lcLte = new ArrayList<CellLte>();

        lNci = tm.getAllCellInfo();
        dispatchCellInfo();
    }

    private String getNetworkOperator() {
        return tm.getNetworkOperator();
    }

    public int getMcc() {
        if (!getNetworkOperator().isEmpty())
            return Integer.parseInt(getNetworkOperator().substring(0, 3));
        return 0;
    }

    public int getMnc() {
        if (!getNetworkOperator().isEmpty())
            return Integer.parseInt(getNetworkOperator().substring(3));
        return 0;
    }

    public String getNetworkName() {
        return tm.getNetworkOperatorName();
    }

    public String getCellNetwork() {
        return tm.getNetworkOperator();
    }

    public int getDataActivity() {
        return tm.getDataState();
    }

    public Rnc getLoggedRnc() {
        return this.loggedRnc;
    }

    public void setLoggedRnc(Rnc rnc) {
        this.loggedRnc = rnc;
    }

    public void setCellLocation(CellLocation cellLocation) {
        this.cellLocation = cellLocation;
    }

    public void setGsmCellLocation() {
        gsmCellLocation = (GsmCellLocation) this.cellLocation;
    }

    public int getNetworkClass() {
        int networkType = tm.getNetworkType();

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 2;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 3;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 4;
            default:
                return 0;
        }
    }

    private void dispatchCellInfo() {
        lcWcdma.clear();
        lcLte.clear();

        // Dep API
        cWcdma = new CellWcdma();
        cLte = new CellLte();

        if (getNetworkClass() == 2) {
            // Not implemented
        }

        if (getNetworkClass() == 3) {
            cWcdma.setIsRegistred(true);

            cWcdma.setMcc(this.getMcc());
            cWcdma.setMnc(this.getMnc());
            cWcdma.setLac(gsmCellLocation.getLac());
            cWcdma.setPsc(gsmCellLocation.getPsc());
            cWcdma.setLcid(gsmCellLocation.getCid());

            if (signalStrength != null) {
                cWcdma.setCellSignalStrength(signalStrength.getGsmSignalStrength());
            } else {
                cWcdma.setCellSignalStrength(-1);
            }

            cWcdma.setRncDB(getRncDB(cWcdma.getRnc(), cWcdma.getCid()));
            cWcdma.setText(cWcdma.getRncDB().get_txt());
            cWcdma.insertRncInLogs();
            setLoggedRnc(cWcdma.getRncDB());

            lcWcdma.add(cWcdma);
        }
        if (getNetworkClass() == 4) {
            cLte.setIsRegistred(true);

            cLte.setMcc(this.getMcc());
            cLte.setMnc(this.getMnc());
            cLte.setTac(gsmCellLocation.getLac());
            cLte.setPci(gsmCellLocation.getPsc());
            cLte.setLCid(gsmCellLocation.getCid());

            if (signalStrength != null) {
                cLte.setCellSignalStrength(signalStrength.getGsmSignalStrength());
                cLte.setLteSignals(signalStrength);
            } else {
                cLte.setCellSignalStrength(-1);
            }

            cLte.setRncDB(getRncDB(cLte.getRnc(), cLte.getCid()));
            cLte.setText(cLte.getRncDB().get_txt());
            cLte.insertRncInLogs();
            setLoggedRnc(cLte.getRncDB());

            lcLte.add(cLte);
        }

        // Last NeighboringCell Infos
        cNeigh = new CellNeighbours();


        List<NeighboringCellInfo> lNci = tm.getNeighboringCellInfo();

        for(int i=0;i < lNci.size();i++) {
            cNeigh.add(lNci.get(i).getLac(), lNci.get(i).getPsc(), lNci.get(i).getCid(), lNci.get(i).getRssi());
        }

        lcNeigh = new ArrayList<>();

        // PSC Management
        if(getNetworkClass() == 3) {
            CellWcdma rncNeigh = getRegisteredWcdmaCell();
            if(rncNeigh != null)
                lcNeigh = cNeigh.getNearestNeighboringInRnc(rncNeigh.getRncDB());
        }
        if(getNetworkClass() == 4) {
            CellLte rncNeigh = getRegisteredLteCell();
            if(rncNeigh != null)
                lcNeigh = cNeigh.getNearestNeighboringInRnc(rncNeigh.getRncDB());
        }
    }

    public CellWcdma getRegisteredWcdmaCell() {
        for(int i=0;i<lcWcdma.size();i++) {
            if(lcWcdma.get(i).getIsRegistred())
                return lcWcdma.get(i);
        }
        return null;
    }

    public CellLte getRegisteredLteCell() {
        for(int i=0;i<lcLte.size();i++) {
            if(lcLte.get(i).getIsRegistred())
                return lcLte.get(i);
        }
        return null;
    }
    public ArrayList<Rnc> getNeighbourCell() {
        return lcNeigh;
    }

    // RNC TEXT
    private Rnc getRncDB(int rnc, int cid) {
        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();

        Rnc rncDB = dbr.findRncByName(String.valueOf(rnc), String.valueOf(cid));

        dbr.close();

        return rncDB;
    }

    // SIGNALS
    private void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void setSignalListener() {
        tsl = new TelephonyStateListener();
        tm.listen(tsl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_LOCATION);
    }

    // Gestion des listeners telephony
    private class TelephonyStateListener extends PhoneStateListener {

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            setSignalStrength(signalStrength);
            dispatchCellInfo();
        }

        public void onCellLocationChanged(final CellLocation location) {
            super.onCellLocationChanged(location);

            setCellLocation(location);
            setGsmCellLocation();
            dispatchCellInfo();
        }
    }
}
