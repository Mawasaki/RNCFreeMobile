package org.rncteam.rncfreemobile.classes;

import android.telephony.SignalStrength;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cedricf_25 on 20/07/2015.
 */
public class CellLte {
    private static final String TAG = "CellLte";

    Method[] methods;

    private int mcc;
    private int mnc;
    private int pci;
    private int tac;
    private int lcid;
    private String txt;
    private int cellSignalStrength;

    private int lteSignalStrength;
    private int lteAsu;
    private int lteRsrp;
    private int lteRsrq;
    private int lteRssnr;
    private int lteCqi;

    SignalStrength signalStrength;

    private Rnc rncDB;

    private boolean isRegistered;

    public CellLte() {
        isRegistered = false;

        methods = android.telephony.SignalStrength.class
                .getMethods();
    }

    public String getNetworkName() {
        return rncmobile.getTelephony().getNetworkName();
    }

    // Registered
    public void setIsRegistred(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
    public boolean getIsRegistred() {
        return this.isRegistered;
    }

    // MCC
    public int getMcc() {
        return this.mcc;
    }
    public void setMcc(int mcc) {
        this.mcc = mcc;
    }

    // MNC
    public int getMnc() {
        return this.mnc;
    }
    public void setMnc(int mnc) {
        this.mnc = mnc;
    }

    // TAC
    public int getTac() {
        return this.tac;
    }
    public void setTac(int tac) {
        this.tac = tac;
    }

    // PCI
    public int getPci() {
        return this.pci;
    }
    public void setPci(int pci) {
        this.pci = pci;
    }

    // RNC
    public int getRnc() {
        return this.lcid >> 8;
    }
    public String getFormatedCid() {
        return getRnc() + ":" + getCid();
    }

    // LCid
    public int getLCid() {
        return this.lcid;
    }
    public void setLCid(int lcid) {
        this.lcid = lcid;
    }

    // CID
    public int getCid() {
        return this.lcid & 0xff;
    }

    // CID Neigh
    public int getNeighCI() {return this.lcid & 0xff; }

    // TEXT
    public String getText() {
        return this.txt;
    }
    public void setText(String txt) {
        this.txt = txt;
    }

    // RNC DB
    public void setRncDB(Rnc rnc) {
        this.rncDB = rnc;
    }
    public Rnc getRncDB() {
        return this.rncDB;
    }

    // Signal Strength
    public int getLteSignalStrengthAsu() {
        return cellSignalStrength;
    }

    public int getLteSignalStrengthInDbm() {
        return (17 + getLteRsrp() + getLteRsrq());
    }

    public void setCellSignalStrength (int cellSignalStrength) {
        this.cellSignalStrength = cellSignalStrength;
    }

    public void setLteSignals(SignalStrength signalStrength) {
        setLteSignalStrength(getLteSignalByType(signalStrength, "getLteSignalStrength"));
        setLteAsu(getLteSignalByType(signalStrength, "getLteAsuLevel"));
        setLteRsrp(getLteSignalByType(signalStrength, "getLteRsrp"));
        setLteRsrq(getLteSignalByType(signalStrength, "getLteRsrq"));
        setLteRssnr(getLteSignalByType(signalStrength, "getLteRssnr"));
        setLteCqi(getLteSignalByType(signalStrength, "getLteCqi"));
    }

    private int getLteSignalByType(SignalStrength signalStrength, String type) {
        for (Method mthd : methods)
            try {
                if (mthd.getName().equals(type)) {
                    return (int)mthd.invoke(signalStrength);
                }
                //else return -1;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return -1;
    }

    public void setLteSignalStrength(int lteSignalStrength) {
        this.lteSignalStrength = lteSignalStrength;
    }

    public int getLteSignalStrength() {
        return this.lteSignalStrength;
    }

    public void setLteAsu(int lteAsu) {
        this.lteAsu = lteAsu;
    }

    public int getLteAsu() {
        return this.lteAsu;
    }

    public void setLteRsrp(int lteRsrp) {
        this.lteRsrp = lteRsrp;
    }

    public int getLteRsrp() {
        return this.lteRsrp;
    }

    public void setLteRsrq(int lteRsrq) {
        this.lteRsrq = lteRsrq;
    }

    public int getLteRsrq() {
        return this.lteRsrq;
    }

    public void setLteRssnr(int lteRssnr) {
        this.lteRssnr = lteRssnr;
    }

    public int getLteRssnr() { return this.lteRssnr/10; }

    public void setLteCqi(int lteCqi) { this.lteCqi = lteCqi; }

    public int getLteCqi() { return this.lteCqi; }

    public void insertRncInLogs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        // Is this new RNC exist in Logs database ?
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        RncLogs rncLog = dbl.findRncLogsByRncCid(String.valueOf(getRnc()), String.valueOf(getCid()));
        if(rncLog == null) {
            RncLogs rncLogs = new RncLogs();

            rncLogs.set_tech("4G");
            rncLogs.set_mcc(String.valueOf(getMcc()));
            rncLogs.set_mnc(String.valueOf(getMnc()));
            rncLogs.set_cid(String.valueOf(getCid()));
            rncLogs.set_lac(String.valueOf(getTac()));
            rncLogs.set_rnc(String.valueOf(getRnc()));
            rncLogs.set_psc(String.valueOf(getPci()));
            rncLogs.set_lat((rncDB.NOTHING) ? "0" : rncDB.get_lat());
            rncLogs.set_lon((rncDB.NOTHING) ? "0" : rncDB.get_lon());
            rncLogs.set_date(sdf.format(new Date()));
            rncLogs.set_txt((rncDB.NOTHING) ? "-" : rncDB.get_txt());

            dbl.addLog(rncLogs);

            // Update UI dataset
            if(rncmobile.listRncLogs != null) rncmobile.listRncLogs.add(0, rncLogs);
        } else { //update
            rncLog.set_date(sdf.format(new Date()));
            dbl.updateLogs(rncLog);

            Telephony tel = rncmobile.getTelephony();
            if(tel != null) tel.getAllRncLogs();
        }
        dbl.close();
    }

}
