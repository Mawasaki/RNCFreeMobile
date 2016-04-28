package org.rncteam.rncfreemobile.classes;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.rncteam.rncfreemobile.activity.MainActivity;
import org.rncteam.rncfreemobile.activity.MonitorService;
import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.activity.rncmobile;
import org.rncteam.rncfreemobile.tasks.AutoExportTask;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by cedricf_25 on 14/07/2015.
 */
@SuppressWarnings("DefaultFileTemplate")

public class Telephony {
    private static final String TAG = "Telephony";

    // Telephony attributes
    private final TelephonyManager telephonyManager;
    private final TelephonyStateListener tsl;
    private CellLocation cellLocation;
    private GsmCellLocation gsmCellLocation;
    private SignalStrength signalStrength = null;

    // Specials
    private final Handler handler;
    private boolean signalChange;
    private boolean cellChange;
    private Rnc loggedRnc;
    private Rnc markedRnc;
    private final SimpleDateFormat sdf;
    private AnfrInfos anfrInfos;

    private ArrayList<Rnc> lNeigh;

    public Telephony() {
        // Initialize Telephony attributes
        Context context = rncmobile.getAppContext();
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        cellLocation = telephonyManager.getCellLocation();
        gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        // Initialize Listeners
        tsl = new TelephonyStateListener();
        telephonyManager.listen(tsl, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS | PhoneStateListener.LISTEN_CELL_LOCATION);

        // Initialize vars
        lNeigh = new ArrayList<>();

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        signalChange = true;
        cellChange = true;

        // Initialize timer
        handler = new Handler();

        //dispatchCI.run();
    }

    public void dispatchCellInfo() {
        try {
            // Start new cell identification
            Rnc rnc = new Rnc();

            // fill telephony information
            rnc.setIsRegistered(true);
            rnc.set_tech(getNetworkClass());
            rnc.set_mcc(getMcc());
            rnc.set_mnc(getMnc());
            rnc.set_lac(gsmCellLocation.getLac());

            // Checking LCID in log

            rnc.set_lcid(gsmCellLocation.getCid());
            rnc.set_cid(rnc.getCid());
            rnc.set_rnc(rnc.getRnc());
            rnc.setNetworkName(getNetworkName());

            // Signal Strength
            if (signalStrength != null) {
                rnc.setSignalStrength(signalStrength);
                if (rnc.get_tech() == 2 || rnc.get_tech() == 3)
                    rnc.setUmtsRscp((2 * signalStrength.getGsmSignalStrength()) - 113);

                if (rnc.get_tech() == 4) {
                    rnc.setLteSignals();
                    rnc.setLteRssi(17 + rnc.getLteRsrp() + rnc.getLteRsrq());
                }
            }

            // Get some info of new API
            List<CellInfo> lAci = getTelephonyManager().getAllCellInfo();
            int psc = -1;
            if (lAci != null && lAci.size() > 0) {
                for (CellInfo cellInfo : lAci) {
                    if (cellInfo != null && cellInfo instanceof CellInfoWcdma) {
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
                        if (cellInfoWcdma.isRegistered()) {
                            psc = cellInfoWcdma.getCellIdentity().getPsc();
                            int signal = cellInfoWcdma.getCellSignalStrength().getDbm();
                            rnc.setUmtsRscp(signal);
                        }
                    }
                    if (cellInfo != null && cellInfo instanceof CellInfoLte) {
                        CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                        if (cellInfoLte.isRegistered()) {
                            psc = cellInfoLte.getCellIdentity().getPci();
                            int signal = cellInfoLte.getCellSignalStrength().getDbm();
                            rnc.setLteRssi(signal);
                            int timingAdvance = cellInfoLte.getCellSignalStrength().getTimingAdvance();
                            rnc.setLteTA(timingAdvance);
                        }
                    }
                }
                rnc.set_psc((psc == 0) ? -1 : psc);
            } else {
                rnc.set_psc((gsmCellLocation.getPsc() == 0) ? -1 : psc);
            }

            // Log in roaming
            SharedPreferences sp = rncmobile.getPreferences();
            boolean logRoaming = false;
            if (sp != null && sp.getBoolean("log_roaming", true)) {
                logRoaming = true;
            }

            // for roaming
            if (cellChange && logRoaming && rnc.get_mnc() != 15) {
                rnc = rnc.setRoamingCid(rnc);
                setLoggedRnc(rnc);
            }

            // Protect some bad infos from API
            // Manage 2G
            if(getNetworkClass() == 2) {
                setLoggedRnc(rnc);
            }
            // Manage 3G/4G
            else if (getNetworkClass() == 3 || getNetworkClass() == 4)
            {
                /* Chris Patch */
                /* 1) check CID */
                if(gsmCellLocation.getCid() <= 0 || gsmCellLocation.getCid() >= 268435455){
                    return;
                }

                /* 2) Check with logged cell and logged cell before */
                if(loggedRnc != null) {
                    if(rnc.get_lcid() == loggedRnc.get_lcid() &&
                    rnc.get_mcc() != loggedRnc.get_mcc()) {
                        return;
                    }
                }

                if (rnc.get_mnc() == 15 && rnc.get_cid() > 0) {
                    /*if ((Integer.valueOf(rnc.get_real_rnc()) > 999) &&
                            (Integer.valueOf(rnc.get_real_rnc()) < 8000)) {*/

                        // Start RNC management just if cell change
                        if (cellChange) {
                            DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                            dbr.open();
                            DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
                            dbl.open();

                            long lastInsertId = -1;

                            // Get all entries of this RNC
                            ArrayList<Rnc> lRncDb = dbr.findRncByRnc(rnc.get_real_rnc());
                            // Check if we know RNC and is identified or not
                            Rnc iRnc = rnc.getThisRnc(lRncDb, rnc);

                            // If RNC is know
                            if (iRnc != null) {
                                // if RNC is already identified
                                if (!iRnc.NOT_IDENTIFIED) {
                                    // Just set good info
                                    rnc.set_lat(iRnc.get_lat());
                                    rnc.set_lon(iRnc.get_lon());
                                    rnc.set_txt(iRnc.get_txt());
                                    rnc.NOT_IDENTIFIED = false;
                                } else {
                                    // Recopy the name of cell and gps if exists
                                    rnc = rnc.setInfosFromAnotherRnc(lRncDb, rnc);
                                    rnc.AUTOLOG = true;
                                    dbr.updateRnc(rnc);
                                }
                                rnc.set_id(iRnc.get_id());
                            } else {
                                // Recopy the name of cell and gps if exists
                                rnc = rnc.setInfosFromAnotherRnc(lRncDb, rnc);
                                rnc.AUTOLOG = true;
                                lastInsertId = dbr.addRnc(rnc);
                                rnc.set_id((int) lastInsertId);
                            }

                            // For log, we prepare infos
                            int logsSync = 0;
                            RncLogs rncLogs = new RncLogs();
                            rncLogs.set_date(sdf.format(new Date()));

                            // update LCID in database
                            dbr.updateLcid(rnc);

                            // if we detect an insertion, add in log
                            if (lastInsertId > 0) {
                                rncLogs.set_rnc_id((int) lastInsertId);
                                dbl.addLog(rncLogs);
                            } else {
                                // Check if present in rnc database and we have already set this cid
                                if (iRnc != null) {
                                    RncLogs iRncLogs = dbl.findOneRncLogs(iRnc.get_id());
                                    // If we find a log, just update it
                                    if (iRncLogs != null) {
                                        iRncLogs.set_date(sdf.format(new Date()));
                                        logsSync = iRncLogs.get_sync();
                                        dbl.updateLogs(iRncLogs);
                                    } else {
                                        // Else add log
                                        rncLogs.set_rnc_id(iRnc.get_id());
                                        logsSync = 0;
                                        dbl.addLog(rncLogs);
                                    }
                                }
                            }
                            rncmobile.notifyListLogsHasChanged = true;

                            dbl.close();
                            dbr.close();

                            // Switch icon map
                            Maps maps = rncmobile.getMaps();
                            if (maps != null) maps.switchMarkerIcon(rnc);
                            // Textbox on map
                            if (maps != null && maps.isMapInitilized() && loggedRnc != null) maps.setExtInfoBox();

                            // Autolog
                        /* Comm for tests
                        if(logsSync == 1 || (rnc.AUTOLOG && logsSync == 0 && rnc.get_mnc() == 15 && rncmobile.rncDataCharged)) {
                            AutoExportTask aet = new AutoExportTask(rnc);
                            aet.execute();
                        }*/
                            // FOR TESTS
                            if (logsSync < 2) {
                                AutoExportTask aet = new AutoExportTask(rnc);
                                aet.execute();
                            }

                        } else {
                            // No cell change
                            if(loggedRnc != null) {
                                Rnc iRnc = getLoggedRnc();
                                rnc.set_lat(iRnc.get_lat());
                                rnc.set_lon(iRnc.get_lon());
                                rnc.set_txt(iRnc.get_txt());
                                rnc.set_id(iRnc.get_id());
                                rnc.NOT_IDENTIFIED = iRnc.NOT_IDENTIFIED;
                            }
                        }
                        setLoggedRnc(rnc);
                    //}
                }
            }

            // Start PSC/PCI management if cell is know
            lNeigh.clear();
            CellNeighbours cellNeighbours = new CellNeighbours();
            cellNeighbours.startManager();
            lNeigh = cellNeighbours.getNearestNeighboringInRnc(rnc);

            // Center map
            if (sp != null && sp.getBoolean("map_center", true)) {
                Maps maps = rncmobile.getMaps();

                if (maps != null && maps.getMap() != null && !loggedRnc.NOT_IDENTIFIED && cellChange) {
                    maps.setLastZoom(12.0f);
                    maps.setCenterCamera(loggedRnc.get_lat(),
                            loggedRnc.get_lon());
                }
            }
        } catch (Exception e) {
            String msg = "Une erreur s'est produite dans la gestion de la telephonie";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
            Toast.makeText(rncmobile.getAppContext(), msg, Toast.LENGTH_SHORT).show();
        } finally {
            cellChange = false;
        }
    }

    // Manage listeners telephony
    private class TelephonyStateListener extends PhoneStateListener {

        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            setSignalStrength(signalStrength);
            signalChange = true;
            // Test to 0
            //handler.postDelayed(dispatchCI, 500);
            handler.postDelayed(dispatchCI, 0);
        }

        public void onCellLocationChanged(final CellLocation location) {
            super.onCellLocationChanged(location);

            setCellLocation(location);
            setGsmCellLocation();
            cellChange = true;
            // Test to 0
            //handler.postDelayed(dispatchCI, 500);
            handler.postDelayed(dispatchCI, 0);
        }
    }

    Runnable dispatchCI = new Runnable() {
        public void run() {
            if ((signalChange || cellChange) && gsmCellLocation != null){
                // Refresh notification bar
                Intent intent = new Intent(rncmobile.getAppContext(), MonitorService.class);
                intent.putExtra("foo", "bar");
                rncmobile.getAppContext().stopService(intent);
                rncmobile.getAppContext().startService(intent);

                dispatchCellInfo();
            }
            signalChange = false;
            cellChange = false;
        }
    };

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

    // SIGNALS
    private void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }

    public AnfrInfos getAnfrInfos() {
        return anfrInfos;
    }

    public void setAnfrInfos(AnfrInfos anfrInfos) {
        this.anfrInfos = anfrInfos;
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
        if (getNetworkClass() == 3) return "3G";
        else if (getNetworkClass() == 4) return "4G";
        else return "-";
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

    private String getNetworkName() {
        return telephonyManager.getNetworkOperatorName();
    }

    public String getDeviceId() { return telephonyManager.getDeviceId(); }

    public void setCellChange(boolean cellChange) {
        this.cellChange = cellChange;
    }

    public void stopListenManager() {
        telephonyManager.listen(tsl, PhoneStateListener.LISTEN_NONE);
    }

    public StringBuffer getDeviceIdMD5() {
        try {
            String deviceID = getDeviceId();
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(deviceID.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb;
        } catch(NoSuchAlgorithmException e) {
            return new StringBuffer("hash error");
        }
    }
}
