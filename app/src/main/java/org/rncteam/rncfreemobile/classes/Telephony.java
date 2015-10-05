package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
import android.content.Context;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import org.rncteam.rncfreemobile.models.Rnc;
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
    private List<CellInfo> lNci2;
    private CellWcdma cWcdma;
    private CellLte cLte;
    private CellNeighbours cNeigh;

    private ArrayList<CellWcdma> lcWcdma;
    private ArrayList<CellLte> lcLte;
    ArrayList<Rnc> lcNeigh;

    private Rnc loggedRnc;
    private Rnc loggedKnowRnc;
    private Rnc tempNewRnc;

    private Handler handler;

    private String httpResponse;

    private int tempTech;
    private boolean signalChange;
    private boolean cellChange;

    private Context mContext;

    public Telephony(Context context) {
        mContext = context;
        tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);

        cellLocation = tm.getCellLocation();

        gsmCellLocation = (GsmCellLocation) tm.getCellLocation();
        setSignalListener();

        signalStrength = null;
        tempTech = getNetworkClass();

        lcWcdma = new ArrayList<CellWcdma>();
        lcLte = new ArrayList<CellLte>();

        lNci = tm.getAllCellInfo();
        dispatchCellInfo();

        handler = new Handler();
        dispatchCI.run();
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

    public Rnc getLoggedRnc() {
        return this.loggedRnc;
    }

    public void setLoggedRnc(Rnc rnc) {
        this.loggedRnc = rnc;
    }

    public Rnc getTempNewRnc() {
        return this.tempNewRnc;
    }

    public void setTempNewRnc(Rnc tempNewRnc) {
        this.tempNewRnc = tempNewRnc;
    }

    public void setCellLocation(CellLocation cellLocation) {
        this.cellLocation = cellLocation;
    }

    public void setGsmCellLocation() {
        gsmCellLocation = (GsmCellLocation) this.cellLocation;
    }

    public String getDeviceId() { return tm.getDeviceId(); }

    public int getDataActivity() {
        int dataState = tm.getDataState();

        switch (dataState) {
            case TelephonyManager.DATA_CONNECTED:
            case TelephonyManager.DATA_SUSPENDED:
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
            case TelephonyManager.DATA_CONNECTING:
                return 1;
            default:
                return 0;
        }
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
        lcNeigh = new ArrayList<>();

        cWcdma = new CellWcdma();
        cLte = new CellLte();

        setLoggedRnc(null);

        getDataActivity();

        // Is telephony initilized
        if (getNetworkClass() != 0) {

            //if (getDataActivity() != 0 && gsmCellLocation.getCid() > 0) {
                // Mhhh.. Very ugly, I do this for moment
                //if (tempTech == getNetworkClass()) {
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
                        if(cellChange) cWcdma.insertRncInLogs();
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
                        if(cellChange) cLte.insertRncInLogs();
                        setLoggedRnc(cLte.getRncDB());

                        lcLte.add(cLte);
                    }

                    Maps maps = rncmobile.getMaps();

                    // Recenter map
                    if(maps != null && maps.getMap() != null && !loggedRnc.NOTHING && cellChange) {

                        maps.setLastZoom(12.0f);
                        maps.setCenterCamera(Double.valueOf(loggedRnc.get_lat()),
                                Double.valueOf(loggedRnc.get_lon()));
                    }

                    // Last NeighboringCell Infos
                    cNeigh = new CellNeighbours();

                    // PSC Management
                    lNci2 = tm.getAllCellInfo();
                    if (lNci2 != null && lNci2.size() > 0) { // If device supports new API
                        for (CellInfo cellInfo : lNci2) {
                            cWcdma = new CellWcdma();
                            cLte = new CellLte();

                            if (cellInfo != null && cellInfo instanceof CellInfoWcdma) {
                                CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;

                                cWcdma.setIsRegistred(cellInfoWcdma.isRegistered());

                                cWcdma.setMcc(cellInfoWcdma.getCellIdentity().getMcc());
                                cWcdma.setMnc(cellInfoWcdma.getCellIdentity().getMnc());
                                cWcdma.setLac(cellInfoWcdma.getCellIdentity().getLac());
                                cWcdma.setPsc(cellInfoWcdma.getCellIdentity().getPsc());
                                cWcdma.setLcid(cellInfoWcdma.getCellIdentity().getCid());

                                cWcdma.setCellSignalStrength(cellInfoWcdma.getCellSignalStrength().getDbm());

                                if (!cellInfoWcdma.isRegistered()) {
                                    cNeigh.add(cWcdma.getLac(), cWcdma.getPsc(), cWcdma.getLCid(), cWcdma.getCellSignalStrength());
                                }

                            }
                            if (cellInfo != null && cellInfo instanceof CellInfoLte) {
                                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;

                                cLte.setIsRegistred(cellInfoLte.isRegistered());

                                cLte.setMcc(cellInfoLte.getCellIdentity().getMcc());
                                cLte.setMnc(cellInfoLte.getCellIdentity().getMnc());
                                cLte.setTac(cellInfoLte.getCellIdentity().getTac());
                                cLte.setPci(cellInfoLte.getCellIdentity().getPci());
                                cLte.setLCid(cellInfoLte.getCellIdentity().getCi());

                                cLte.setCellSignalStrength(cellInfoLte.getCellSignalStrength().getDbm());

                                // How the new API is tunneling LTE Neigh ?
                                if (!cellInfoLte.isRegistered()) {
                                    cNeigh.add(cLte.getNeighCI(), cLte.getPci(), cLte.getNeighCI(), cLte.getLteSignalStrength());
                                } else {
                                    // Get PCI or other info of new API
                                    CellLte rncNeigh = getRegisteredLteCell();
                                    rncNeigh.setPci(cLte.getPci());
                                    setRegisteredLteCell(rncNeigh);
                                }
                            }
                        }
                    } else {
                        List<NeighboringCellInfo> lNci = tm.getNeighboringCellInfo();

                        for (int i = 0; i < lNci.size(); i++) {
                            cNeigh.add(lNci.get(i).getLac(), lNci.get(i).getPsc(), lNci.get(i).getCid(), lNci.get(i).getRssi());
                        }
                    }

                    if (getNetworkClass() == 3) {
                        CellWcdma rncNeigh = getRegisteredWcdmaCell();
                        if (rncNeigh != null)
                            lcNeigh = cNeigh.getNearestNeighboringInRnc(rncNeigh.getRncDB());
                    }
                    if (getNetworkClass() == 4) {
                        CellLte rncNeigh = getRegisteredLteCell();
                        if (rncNeigh != null)
                            lcNeigh = cNeigh.getNearestNeighboringInRnc(rncNeigh.getRncDB());
                    }
                    tempTech = getNetworkClass();
                //}
            //}
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
    public void setRegisteredLteCell(CellLte rncNeigh) {
        for(int i=0;i<lcLte.size();i++) {
            if(lcLte.get(i).getIsRegistred())
                lcLte.set(i,rncNeigh);
        }
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

        if(rncDB.NOTHING) {
            rncDB.set_rnc(String.valueOf(rnc));
            rncDB.set_cid(String.valueOf(cid));
        }

        return rncDB;
    }

    // ListLogs
    public void getAllRncLogs() {
        if(rncmobile.listRncLogs != null) {
            DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());

            dbl.open();
            dbl.findAllRncLogsMainList();
            dbl.close();
        }
    }

    // SIGNALS
    private void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }

    public void setSignalListener() {
        tsl = new TelephonyStateListener();
        tm.listen(tsl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_LOCATION);
    }

    public String getHttpResponse() {
        return httpResponse;
    }

    public void setHttpResponse(String httpResponse) {
        this.httpResponse = httpResponse;
    }

    // Gestion des listeners telephony
    private class TelephonyStateListener extends PhoneStateListener {

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            setSignalStrength(signalStrength);
            signalChange = true;
            //dispatchCellInfo();
        }

        public void onCellLocationChanged(final CellLocation location) {
            super.onCellLocationChanged(location);

            setCellLocation(location);
            setGsmCellLocation();
            signalChange = true;
            cellChange = true;
            //dispatchCellInfo();

            tempTech = getNetworkClass();
        }
    }

    private Runnable dispatchCI = new Runnable() {
        public void run() {
            if (signalChange)
                dispatchCellInfo();
            signalChange = false;
            cellChange = false;

            handler.postDelayed(this, 1000);
        }
    };
}
