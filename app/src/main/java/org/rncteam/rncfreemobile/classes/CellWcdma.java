package org.rncteam.rncfreemobile.classes;

import android.telephony.CellIdentityWcdma;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cedricf_25 on 20/07/2015.
 */
public class CellWcdma {
    private static final String TAG = "CellWcdma";

    private int mcc;
    private int mnc;
    private int lac;
    private int psc;
    private int lcid;
    private String txt;
    private int cellSignalStrength;

    private Rnc rncDB;

    private int signalStrength;

    private boolean isRegistered;
    CellIdentityWcdma cellIdentity;


    public CellWcdma() {
        isRegistered = false;
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

    // LAC
    public int getLac() {
        return this.lac;
    }
    public void setLac(int lac) {
        this.lac = lac;
    }

    // PSC
    public int getPsc() {
        return this.psc;
    }
    public void setPsc(int psc) {
        this.psc = psc;
    }

    // LCid
    public int getLCid() {
        return this.lcid;
    }
    public void setLcid(int lcid) {
        this.lcid = lcid;
    }

    // CID
    public int getCid() {
        // Extended Cid
        // check cid 141226603
        if (getStdRnc() >= 256 && getStdRnc() <= 1000) {
            return getExtCid();
        } else {
            return getStdCid();
        }
    }
    public int getStdCid() {
        return getLCid() & 0xffff;
    }

    public int getExtCid() {
        return getLCid() % 4096;
    }

    // RNC
    public int getRnc() {
        // Extended RNC
        if (getStdRnc() >= 256 && getStdRnc() <= 1000) {
            return getExtRnc();
        } else {
            return getStdRnc();
        }
    }
    private int getStdRnc() {
        return (getLCid() >> 16) & 0xffff;
    }
    private int getExtRnc() {
        return getLCid() / 4096;
    }

    // TEXT
    public void setText(String txt) {
        this.txt = txt;
    }
    public String getText() {
        return this.txt;
    }

    // RNC DB
    public void setRncDB(Rnc rnc) {
        this.rncDB = rnc;
    }
    public Rnc getRncDB() {
        return this.rncDB;
    }

    // Signal Strength
    public void setCellSignalStrength (int cellSignalStrength) {
        this.cellSignalStrength = cellSignalStrength;
    }
    public int getCellSignalStrength () {
        return this.cellSignalStrength;
    }
    public int getCellSignalStrengthDbm () { return (2 * this.cellSignalStrength) - 113; }

    public void insertRncInLogs() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);

        // Is this new RNC exist in Logs database ?
        DatabaseLogs dbl = new DatabaseLogs(rncmobile.getAppContext());
        dbl.open();

        RncLogs rncLog = dbl.findRncLogsByRncCid(String.valueOf(getRnc()), String.valueOf(getCid()));
        if(rncLog == null) {
            RncLogs rncLogs = new RncLogs();

            rncLogs.set_tech("3G");
            rncLogs.set_mcc(String.valueOf(getMcc()));
            rncLogs.set_mnc(String.valueOf(getMnc()));
            rncLogs.set_cid(String.valueOf(getCid()));
            rncLogs.set_lac(String.valueOf(getLac()));
            rncLogs.set_rnc(String.valueOf(getRnc()));
            rncLogs.set_psc(String.valueOf(getPsc()));
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
