package org.rncteam.rncfreemobile.classes;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

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
    private AnfrInfos anfrInfos;

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
        // Get some info of new API
        List<CellInfo> lAci = getTelephonyManager().getAllCellInfo();
        int psc = -1;
        if (lAci != null && lAci.size() > 0) { // If device supports new API
            for (CellInfo cellInfo : lAci) {
                if (cellInfo != null && cellInfo instanceof CellInfoWcdma) {
                    CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                    if (cellInfoWcdma.isRegistered()) {
                        psc = cellInfoWcdma.getCellIdentity().getPsc();
                    }


                }
                if (cellInfo != null && cellInfo instanceof CellInfoLte) {
                    CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                    if (cellInfoLte.isRegistered()) {
                        psc = cellInfoLte.getCellIdentity().getPci();
                    }

                }
                rnc.set_psc(psc);
            }
        } else {
            rnc.set_psc(gsmCellLocation.getPsc());
        }

        rnc.setSignalStrength((signalStrength != null) ? signalStrength : null);
        rnc.set_lcid(gsmCellLocation.getCid());
        rnc.set_cid(rnc.getCid());
        rnc.setNetworkName(getNetworkName());

        // Init different Cell calculation (signals, rnc, ...)
        rnc.calc();

        // Doit on enregistrer intinerance
        SharedPreferences sp = rncmobile.getPreferences();
        boolean logRoaming = false;
        if(sp != null && sp.getBoolean("log_roaming", true)) {
            logRoaming = true;
        }
        // CAS 1 : Nouveau RNC, Nouveau LOG
        // Insertion dans la base de rnc
        // Insertion dans la base de log

        if((logRoaming || getMnc() == 15) && rnc.get_cid() > 0 && rnc.get_cid() < 1000000000 /* Huuuu */
                && (getNetworkClass() == 3 || getNetworkClass() == 4)) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

            DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
            dbr.open();
            Rnc rncDB = dbr.findRncByNameCid(String.valueOf(rnc.getRnc()), String.valueOf(rnc.getCid()));

            DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
            dbl.open();
            RncLogs rncLogDB = dbl.findRncLogsByRncCid(String.valueOf(rnc.getRnc()), String.valueOf(rnc.getCid()));

            // CAS 1 : Nouveau RNC, Nouveau LOG
            // Insertion dans la base de rnc sans txt ni lon lat
            // Insertion dans la base de log
            if(rncDB.NOT_IN_DB && rncLogDB == null) {
                // RNC
                rnc.set_lon(-1.0);
                rnc.set_lat(-1.0);
                rnc.set_txt("-");
                rnc.NOT_IDENTIFIED = true;

                dbr.addRnc(rnc);

                rncLogDB = new RncLogs();
                rncLogDB.set_tech(getNetworkClassTxt());
                rncLogDB.set_mcc(String.valueOf(rnc.get_mcc()));
                rncLogDB.set_mnc(String.valueOf(rnc.get_mnc()));
                rncLogDB.set_cid(String.valueOf(rnc.getCid()));
                rncLogDB.set_lac(String.valueOf(rnc.get_lac()));
                rncLogDB.set_rnc(String.valueOf(rnc.getRnc()));
                rncLogDB.set_lat(rnc.get_lat());
                rncLogDB.set_lon(rnc.get_lon());
                rncLogDB.set_txt(String.valueOf(rnc.get_txt()));
                rncLogDB.set_psc(String.valueOf(rnc.get_psc()));
                rncLogDB.set_date(sdf.format(new Date()));

                // Logs
                // Verifie que le RNC n'a pas déja connu de la base LOG
                ArrayList<RncLogs> rncExistLogDB = dbl.findRncLogsByRnc(String.valueOf(rnc.get_real_rnc()));
                if(rncExistLogDB.size() > 0) {
                    rncLogDB.set_lat(rncExistLogDB.get(0).get_lat());
                    rncLogDB.set_lon(rncExistLogDB.get(0).get_lon());
                    rncLogDB.set_txt(rncExistLogDB.get(0).get_txt());

                    rnc.set_lon(rncExistLogDB.get(0).get_lat());
                    rnc.set_lat(rncExistLogDB.get(0).get_lon());
                    rnc.set_txt(rncExistLogDB.get(0).get_txt());
                    if(!rncExistLogDB.get(0).get_txt().equals("-")) rnc.NOT_IDENTIFIED = true;
                }

                dbl.addLog(rncLogDB);
            }

            // CAS 2 : RNC connu, LOG inconnu
            // Mettre à jour l'objet courant
            // Inserer un LOG avec text connu du RNC
            if(!rncDB.NOT_IN_DB && rncLogDB == null) {
                // RNC
                rnc.set_lat(rncDB.get_lat());
                rnc.set_lon(rncDB.get_lon());
                rnc.set_txt(rncDB.get_txt());

                // Logs
                rncLogDB = new RncLogs();
                rncLogDB.set_tech(getNetworkClassTxt());
                rncLogDB.set_mcc(String.valueOf(rnc.get_mcc()));
                rncLogDB.set_mnc(String.valueOf(rnc.get_mnc()));
                rncLogDB.set_cid(String.valueOf(rnc.getCid()));
                rncLogDB.set_lac(String.valueOf(rnc.get_lac()));
                rncLogDB.set_rnc(String.valueOf(rnc.getRnc()));
                rncLogDB.set_lat(rnc.get_lat());
                rncLogDB.set_lon(rnc.get_lon());
                rncLogDB.set_txt(String.valueOf(rnc.get_txt()));
                rncLogDB.set_psc(String.valueOf(rnc.get_psc()));
                rncLogDB.set_date(sdf.format(new Date()));

                // Logs
                // Verifie que le RNC n'a pas déja connu de la base LOG
                ArrayList<RncLogs> rncExistLogDB = dbl.findRncLogsByRnc(String.valueOf(rnc.get_real_rnc()));
                if(rncExistLogDB.size() > 0) {
                    rncLogDB.set_lat(rncExistLogDB.get(0).get_lat());
                    rncLogDB.set_lon(rncExistLogDB.get(0).get_lon());
                    rncLogDB.set_txt(rncExistLogDB.get(0).get_txt());

                    rnc.set_lon(rncExistLogDB.get(0).get_lat());
                    rnc.set_lat(rncExistLogDB.get(0).get_lon());
                    rnc.set_txt(rncExistLogDB.get(0).get_txt());
                    if(!rncExistLogDB.get(0).get_txt().equals("-")) rnc.NOT_IDENTIFIED = true;
                }

                dbl.addLog(rncLogDB);
            }

            // CAS 3 : RNC inconnu, LOG connu
            // Ajout dans la table RNC avec les infos de LOG
            // LOG : mettre à jour la date
            if(rncDB.NOT_IN_DB && rncLogDB != null) {
                // RNC
                if(rncLogDB.get_txt().equals("-")) rnc.NOT_IDENTIFIED = true;
                rnc.set_lat(rncLogDB.get_lat());
                rnc.set_lon(rncLogDB.get_lon());
                rnc.set_txt(rncLogDB.get_txt());

                dbr.addRnc(rnc);

                // Logs
                rncLogDB.set_date(sdf.format(new Date()));
                dbl.updateLogs(rncLogDB);
            }

            // CAS 4 : RNC connu, LOG connu
            // Mettre à jour l'objet courant et la table rnc (LOG prioritaire)
            // Mettre à jour la date Log
            if(!rncDB.NOT_IN_DB && rncLogDB != null) {
                // RNC
                // In secu we update actual RNC in database if lat & lon diff
                // Verifie que le RNC n'a pas déja connu de la base LOG
                ArrayList<RncLogs> rncExistLogDB = dbl.findRncLogsByRnc(String.valueOf(rnc.get_real_rnc()));
                if(rncExistLogDB.size() > 0) {
                    // Pour chaque rnc, vérifie si cohérence du lat/lon, sinon on update toutes les infos du rnc trouvé
                    if (!rncDB.get_lat().equals(rncExistLogDB.get(0).get_lat())
                            || !rncDB.get_lon().equals(rncExistLogDB.get(0).get_lon())) {
                        dbr.updateOneRnc(rncDB.get_real_rnc(), rncExistLogDB.get(0));
                    }

                }

                if(rncLogDB.get_txt().equals("-")) rnc.NOT_IDENTIFIED = true;
                rnc.set_lat(rncLogDB.get_lat());
                rnc.set_lon(rncLogDB.get_lon());
                rnc.set_txt(rncLogDB.get_txt());

                dbr.updateRnc(rnc);

                // Logs
                rncLogDB.set_date(sdf.format(new Date()));
                if(cellChange) dbl.updateLogs(rncLogDB);
            }

            dbl.close();
            dbr.close();
            rncmobile.notifyListLogsHasChanged = true;
        } else {
            rnc.NOT_IDENTIFIED = true;
        }

        // Pas this rnc to UI
        setLoggedRnc(rnc);

        // Start PSC/PCI management if cell is know
        lNeigh.clear();
        CellNeighbours cellNeighbours = new CellNeighbours();
        cellNeighbours.startManager();
        lNeigh = cellNeighbours.getNearestNeighboringInRnc(rnc);

        // Doit on centrer la carte
        if(sp != null && sp.getBoolean("map_center", true)) {
            Maps maps = rncmobile.getMaps();

            if (maps != null && maps.getMap() != null && !loggedRnc.NOT_IDENTIFIED && cellChange) {
                maps.setLastZoom(12.0f);
                maps.setCenterCamera(loggedRnc.get_lat(),
                        loggedRnc.get_lon());
            }
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
            if ((signalChange || cellChange) && gsmCellLocation != null )
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
        else return UNIDENTIFIED_CELL_TEXT;
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

    public AnfrInfos getAnfrInfos() {
        return anfrInfos;
    }

    public void setAnfrInfos(AnfrInfos anfrInfos) {
        this.anfrInfos = anfrInfos;
    }
}
