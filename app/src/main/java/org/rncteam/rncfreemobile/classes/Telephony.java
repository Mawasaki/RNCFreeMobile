package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
import android.content.Context;
import android.os.Handler;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Telephony {
    private static final String TAG = "Telephony";

    // Constants
    private final String UNIDENTIFIED_CELL_TEXT = "-";
    private final static String TECH_UTMS_TXT = "3G";
    private final static String TECH_LTE_TXT = "4G";

    // Telephony attibutes
    private final TelephonyManager telephonyManager;
    private CellLocation cellLocation;
    private GsmCellLocation gsmCellLocation;
    private SignalStrength signalStrength = null;

    // Specials
    private final Handler handler;
    private boolean signalChange = false;
    private boolean cellChange = false;
    private Rnc loggedRnc;
    private Rnc markedRnc;

    private ArrayList<Rnc> lNeigh;

    public Telephony(Context context) {
        // Initialize Telephony attributes
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        cellLocation = telephonyManager.getCellLocation();
        gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        // Initialize Listeners
        TelephonyStateListener tsl = new TelephonyStateListener();
        telephonyManager.listen(tsl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_LOCATION);

        // Initialize vars
        lNeigh = new ArrayList<>();

        // Initialize timer
        handler = new Handler();

        signalChange = true;
        cellChange = true;

        dispatchCI.run();
    }

    private void dispatchCellInfo() {
        // Start new cell identification
        Rnc rnc = new Rnc();

        rnc.setIsRegistered(true);
        rnc.set_tech(getNetworkClassTxt());
        rnc.set_mcc(getMcc());
        rnc.set_mnc(getMnc());
        rnc.set_lac(gsmCellLocation.getLac());
        rnc.set_rnc(rnc.getRnc());
        rnc.set_psc(gsmCellLocation.getPsc());
        rnc.set_lcid(gsmCellLocation.getCid());
        rnc.set_cid(rnc.getCid());
        rnc.setSignalStrength((signalStrength != null) ? signalStrength : null);
        rnc.setNetworkName(getNetworkName());

        // Init different Cell calculation (signals, rnc, ...)
        rnc.calc();

        // Start RNC Identification
        DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
        dbr.open();
        Rnc rncDB = dbr.findRncByNameCid(String.valueOf(rnc.getRnc()), String.valueOf(rnc.getCid()));

        // RNC is not identified, insert in RNC database
        if(rncDB.NOT_IDENTIFIED) rnc.NOT_IDENTIFIED = true;
        if (rncDB.NOT_IN_DB) {
            rnc.NOT_IDENTIFIED = true;
            rnc.set_txt(UNIDENTIFIED_CELL_TEXT);
            dbr.addRnc(rnc);
        } else {
            // Update infos of current Object rnc
            rnc.set_lon(rncDB.get_lon());
            rnc.set_lat(rncDB.get_lat());
            rnc.set_txt(rncDB.get_txt());
        }

        // Pas this rnc to UI
        setLoggedRnc(rnc);

        if(cellChange) {
            // Is this RNC in log ?
            DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
            dbl.open();
            RncLogs rncLog = dbl.findRncLogsByRncCid(String.valueOf(rnc.getRnc()), String.valueOf(rnc.getCid()));

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

            if (rncLog != null) {
                // Update log
                rncLog.set_date(sdf.format(new Date()));
                dbl.updateLogs(rncLog);
            } else {
                // Insert in log
                if(rnc.get_cid() > 0 && rnc.get_lac() != 0/* && rnc.get_mnc() == 15*/) {

                    rncLog = new RncLogs();
                    rncLog.set_tech(getNetworkClassTxt());
                    rncLog.set_mcc(String.valueOf(rnc.get_mcc()));
                    rncLog.set_mnc(String.valueOf(rnc.get_mnc()));
                    rncLog.set_cid(String.valueOf(rnc.getCid()));
                    rncLog.set_lac(String.valueOf(rnc.get_lac()));
                    rncLog.set_rnc(String.valueOf(rnc.getRnc()));
                    rncLog.set_lat(rnc.get_lat());
                    rncLog.set_lon(rnc.get_lon());
                    rncLog.set_txt(String.valueOf(rnc.get_txt()));
                    rncLog.set_psc(String.valueOf(rnc.get_psc()));
                    rncLog.set_date(sdf.format(new Date()));

                    // Check if a RNC is already identified
                    ArrayList<RncLogs> lLogsRnc = dbl.findRncLogsByRnc(String.valueOf(rnc.getRnc()));

                    if(lLogsRnc.size() > 0) {
                        rncLog.set_lat(lLogsRnc.get(0).get_lat());
                        rncLog.set_lon(lLogsRnc.get(0).get_lon());
                        rncLog.set_txt(lLogsRnc.get(0).get_txt());

                        // Update RNC already RNC
                        rnc.set_txt(lLogsRnc.get(0).get_txt());
                        rnc.set_lat(lLogsRnc.get(0).get_lat());
                        rnc.set_lon(lLogsRnc.get(0).get_lon());
                        dbr.updateRnc(rnc);
                    }

                    // Insert in database a main list
                    dbl.addLog(rncLog);
                }
            }
            dbl.close();
            dbr.close();
            rncmobile.notifyListLogsHasChanged = true;
        }

        // Start PSC/PCI management if cell is know
        lNeigh.clear();
        CellNeighbours cellNeighbours = new CellNeighbours();
        cellNeighbours.startManager();
        lNeigh = cellNeighbours.getNearestNeighboringInRnc(rnc);

        Maps maps = rncmobile.getMaps();

        if(maps != null && maps.getMap() != null && !loggedRnc.NOT_IDENTIFIED && cellChange) {
            maps.setLastZoom(12.0f);
            maps.setCenterCamera(loggedRnc.get_lat(),
                    loggedRnc.get_lon());
        }
    }


        /* TODO : Special feature : Export LOG on rncmobile if RNC not defined
        if(getLoggedRnc().NOTHING) {
            NtmExportTask net = new NtmExportTask(rncmobile.getAppContext(), NtmFileName,
                    inpImportNickname.getText().toString(), inpImportName.getText().toString());
            net.NtmExportSetData(lRncLogs.size(), nbUmtsLogs, nbLteLogs);
            net.execute();
        }
        */


    Runnable dispatchCI = new Runnable() {
        public void run() {
            if (signalChange || cellChange)
                dispatchCellInfo();
            signalChange = false;
            cellChange = false;

            handler.postDelayed(this, 1000);
        }
    };

    // Manage listeners telephony
    private class TelephonyStateListener extends PhoneStateListener {

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            setSignalStrength(signalStrength);
            signalChange = true;
        }

        public void onCellLocationChanged(final CellLocation location) {
            super.onCellLocationChanged(location);

            setCellLocation(location);
            setGsmCellLocation();
            cellChange = true;
        }
    }

    // Getters & Setters
    public Rnc getLoggedRnc() {
        return loggedRnc;
    }

    public void setLoggedRnc(Rnc loggedRnc) {
        this.loggedRnc = loggedRnc;
    }

    public Rnc getMarkedRnc() {
        return markedRnc;
    }

    public void setMarkedRnc(Rnc markedRnc) {
        this.markedRnc = markedRnc;
    }

    public ArrayList<Rnc> getlNeigh() {
        return lNeigh;
    }

    public void setlNeigh(ArrayList<Rnc> lNeigh) {
        this.lNeigh = lNeigh;
    }

    // SIGNALS
    private void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }

    // Cell location
    private void setCellLocation(CellLocation cellLocation) {
        this.cellLocation = cellLocation;
    }

    private void setGsmCellLocation() {
        gsmCellLocation = (GsmCellLocation) this.cellLocation;
    }

    // Telephony manager
    public TelephonyManager getTelephonyManager() {
        return this.telephonyManager;
    }

    //
    // Getters from telephony
    //
    private String getNetworkOperator() {
        return telephonyManager.getNetworkOperator();
    }

    public int getDataActivity() {
        int dataState = telephonyManager.getDataState();

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
        int networkType = telephonyManager.getNetworkType();

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

    public String getNetworkClassTxt() {
        if (getNetworkClass() == 3) return TECH_UTMS_TXT;
        else if (getNetworkClass() == 4) return TECH_LTE_TXT;
        else return TECH_UTMS_TXT;
    }

    private int getMcc() {
        if (!getNetworkOperator().isEmpty())
            return Integer.parseInt(getNetworkOperator().substring(0, 3));
        return 0;
    }

    private int getMnc() {
        if (!getNetworkOperator().isEmpty())
            return Integer.parseInt(getNetworkOperator().substring(3));
        return 0;
    }

    public String getNetworkName() {
        return telephonyManager.getNetworkOperatorName();
    }

    public String getDeviceId() { return telephonyManager.getDeviceId(); }

    public boolean isCellChange() {
        return cellChange;
    }

    public void setCellChange(boolean cellChange) {
        this.cellChange = cellChange;
    }
}
