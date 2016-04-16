package org.rncteam.rncfreemobile.models;

import android.telephony.SignalStrength;

import org.rncteam.rncfreemobile.database.DatabaseLogs;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cedric_f25 on 15/07/2015.
 */
public class Rnc {
    private static final String TAG = "RNC";

    // Constants
    private final String UNIDENTIFIED_CELL_TEXT = "-";
    private final static String TECH_UTMS_TXT = "3G";
    private final static String TECH_LTE_TXT = "4G";

    public boolean NOT_IDENTIFIED;
    public boolean AUTOLOG;

    private final int RSSI_CONSTANT = 17;

    // Database attributes
    private int _id;
    private Integer _tech;
    private Integer _mcc;
    private Integer _mnc;
    private Integer _lcid;
    private Integer _cid;
    private Integer _rnc;
    private Integer _lac;
    private Integer _psc;
    private Double _lon;
    private Double _lat;
    private String _txt;

    // Telephony attributes
    private boolean isRegistered;

    private int umtsRscp;
    private int lteRssi;
    private int lteAsu;
    private int lteRsrp;
    private int lteRsrq;
    private int lteRssnr;
    private int lteCqi;
    private int lteTA;

    private String networkName;
    private SignalStrength signalStrength;

    public Rnc() {
        _lon = 0.0;
        _lat = 0.0;
        _txt = "-";
        _lcid = -1;
        NOT_IDENTIFIED = true;
        AUTOLOG = false;

        umtsRscp = -1;
        lteRssi = -1;
        lteAsu = -1;
        lteRsrp = -1;
        lteRsrq = -1;
        lteRssnr = -1;
        lteCqi = -1;
        lteTA = -1;
        signalStrength = null;
        networkName = "err";
    }

    // UMTS Management
    public int getCid() {
        if(get_tech() == 4) return get_lcid() & 0xff;
        if (getStdRnc() >= 256 && getStdRnc() <= 1000 && get_mnc() == 15) {
            return getExtCid();
        } else {
            return getStdCid();
        }
    }

    public int getRnc() {
        // In UTMS, we can have extend RNC, alright
        if(get_tech() == 4) return get_lcid() >> 8;
        if (getStdRnc() >= 256 && getStdRnc() <= 1000 && get_mnc() == 15) {
            return getExtRnc();
        } else {
            return getStdRnc();
        }
    }

    private int getStdCid() {
        return get_lcid() & 0xffff;
    }

    private int getExtCid() {
        return get_lcid() & 0xfff;
    }

    private int getStdRnc() {
        return (get_lcid() >> 16) & 0xffff;
    }
    private int getExtRnc() {
        return (get_lcid() >> 12) & 0xffff;
    }

    // LTE Signals management
    public void setLteSignals() {
        setLteAsu(getLteSignalByType(signalStrength, "getLteAsuLevel"));
        setLteRsrp(getLteSignalByType(signalStrength, "getLteRsrp"));
        setLteRsrq(getLteSignalByType(signalStrength, "getLteRsrq"));
        setLteRssnr(getLteSignalByType(signalStrength, "getLteRssnr"));
        setLteCqi(getLteSignalByType(signalStrength, "getLteCqi"));
    }

    private int getLteSignalByType(SignalStrength signalStrength, String type) {
        final Method[] methods = android.telephony.SignalStrength.class.getMethods();
        for (Method mthd : methods)
            try {
                if (mthd.getName().equals(type)) {
                    return (int)mthd.invoke(signalStrength);
                }
                //else return -1;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        return -1;
    }

    public int computeRssi() {
        if(getLteRsrp() != -1 || getLteRsrq() != -1) return RSSI_CONSTANT + getLteRsrp() - getLteRsrq();
        else return -1;
    }

    public String get_real_rnc() {
        String rnc = String.valueOf(_rnc);
        if(rnc.length() > 4) {
            if (rnc.substring(0, 2).equals("40") && rnc.length() > 4) {
                return rnc.substring(2, rnc.length());
            }
        }
        return rnc;
    }

    public String getFreqTxt() {
        if(get_cid() > 100 && get_cid() < 32767) return "2100 Mhz";
        if(get_cid() > 32768 && get_cid() < 65435) return "900 Mhz";
        if(get_cid() > 90 && get_cid() < 95) return "900 Mhz";
        if(get_cid() > 20 && get_cid() < 25) return "2100 Mhz";
        if(get_cid() > 60 && get_cid() < 65) return "2600 Mhz";
        if(get_cid() > 80 && get_cid() < 85) return "1800 Mhz";
        return "-";
    }

    public String getSectText() {
        if(get_cid() > 100 && get_cid() < 32767) return "-";
        if(get_cid() > 32768 && get_cid() < 65435) return "-";
        if(get_cid() > 20 && get_cid() < 95) {
            if(String.valueOf(get_cid()).substring(1, 2).equals("1")) return "Secteur 1";
            if(String.valueOf(get_cid()).substring(1, 2).equals("2")) return "Secteur 2";
            if(String.valueOf(get_cid()).substring(1, 2).equals("3")) return "Secteur 3";
            if(String.valueOf(get_cid()).substring(1, 2).equals("4")) return "Secteur 4";
        }
        return "-";
    }

    public Rnc getThisRnc(ArrayList<Rnc> lRnc, Rnc rnc) {
        for(int i=0;i<lRnc.size();i++) {
            if(lRnc.get(i).get_cid() == rnc.getCid()) {
                Rnc rRnc = lRnc.get(i);
                if(!rRnc.get_txt().equals("-")) {
                    rRnc.NOT_IDENTIFIED = false;
                    return rRnc;
                }
                return rRnc;
            }
        }
        return null;
    }

    private Rnc getAnIdentifiedRnc(ArrayList<Rnc> lRnc) {
        for(int i=0;i<lRnc.size();i++) {
            if(!lRnc.get(i).get_txt().equals("-")) {
                return lRnc.get(i);
            }
        }
        return null;
    }

    public Rnc setInfosFromAnotherRnc(ArrayList<Rnc> lRnc, Rnc rnc) {
        // Check if this rnc is unknown
        if(rnc.NOT_IDENTIFIED) {
            Rnc iRnc = getAnIdentifiedRnc(lRnc);
            if(iRnc != null) {
                rnc.set_lon(iRnc.get_lon());
                rnc.set_lat(iRnc.get_lat());
                rnc.set_txt(getFormattedString(iRnc.get_txt()));
                rnc.NOT_IDENTIFIED = false;
            }
        }
        return rnc;
    }

    // Redifine txt of a new RNC from 20815.csv
    private String getFormattedString(String rncName) {
        // Pos of [
        int pos = rncName.indexOf("[");
        if(pos == -1) return rncName;
        else return rncName.substring(0, pos - 1);
    }

    public Rnc setRoamingCid(Rnc rnc) {
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
        if(iRnc != null) {
            // if RNC is already identified
            if(iRnc.NOT_IDENTIFIED) {
                dbr.updateRnc(rnc);
            }
            rnc.set_id(iRnc.get_id());
        } else {
            // Recopy the name of cell and gps if exists
            lastInsertId = dbr.addRnc(rnc);
            rnc.set_id((int) lastInsertId);
        }

        // For log, we prepare infos
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE);
        RncLogs rncLogs = new RncLogs();
        rncLogs.set_date(sdf.format(new Date()));

        // if we detect an insertion, add in log
        if(lastInsertId > 0) {
            rncLogs.set_rnc_id((int)lastInsertId);
            dbl.addLog(rncLogs);
        } else {
            // Check if present in rnc database and we have already set this cid
            if(iRnc != null) {
                RncLogs iRncLogs = dbl.findOneRncLogs(iRnc.get_id());
                // If we find a log, just update it
                if(iRncLogs != null) {
                    iRncLogs.set_date(sdf.format(new Date()));
                    dbl.updateLogs(iRncLogs);
                } else {
                    // Else add log
                    rncLogs.set_rnc_id(iRnc.get_id());
                    dbl.addLog(rncLogs);
                }
            }
        }
        rncmobile.notifyListLogsHasChanged = true;

        dbl.close();
        dbr.close();

        return rnc;
    }

    // Getter & Setter Database
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_tech() {
        return _tech;
    }

    public void set_tech(int _tech) {
        this._tech = _tech;
    }

    public Integer get_mcc() {
        return _mcc;
    }

    public void set_mcc(Integer _mcc) {
        this._mcc = _mcc;
    }

    public Integer get_mnc() {
        return _mnc;
    }

    public void set_mnc(Integer _mnc) {
        this._mnc = _mnc;
    }

    public Integer get_lcid() {
        return _lcid;
    }

    public void set_lcid(Integer _lcid) {
        this._lcid = _lcid;
    }

    public Integer get_cid() {
        return _cid;
    }

    public void set_cid(Integer _cid) {
        this._cid = _cid;
    }

    public Integer get_rnc() {
        return _rnc;
    }

    public void set_rnc(Integer _rnc) {
        this._rnc = _rnc;
    }

    public Integer get_lac() {
        return _lac;
    }

    public void set_lac(Integer _lac) {
        this._lac = _lac;
    }

    public Integer get_psc() {
        return _psc;
    }

    public void set_psc(Integer _psc) {
        this._psc = _psc;
    }

    public Double get_lon() {
        return _lon;
    }

    public void set_lon(Double _lon) {
        this._lon = _lon;
    }

    public Double get_lat() {
        return _lat;
    }

    public void set_lat(Double _lat) {
        this._lat = _lat;
    }

    public String get_txt() {
        return _txt;
    }

    public void set_txt(String _txt) {
        this._txt = _txt;
    }

    // Getters & setters Telephony

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public int getUmtsRscp() {
        return umtsRscp;
    }

    public void setUmtsRscp(int umtsRscp) {
        this.umtsRscp = umtsRscp;
    }

    public int getLteRssi() {
        return lteRssi;
    }

    public void setLteRssi(int lteRssi) {
        this.lteRssi = lteRssi;
    }

    public int getLteAsu() {
        return lteAsu;
    }

    private void setLteAsu(int lteAsu) {
        this.lteAsu = lteAsu;
    }

    public int getLteRsrp() {
        return lteRsrp;
    }

    private void setLteRsrp(int lteRsrp) {
        this.lteRsrp = lteRsrp;
    }

    public int getLteRsrq() {
        return lteRsrq;
    }

    private void setLteRsrq(int lteRsrq) {
        this.lteRsrq = lteRsrq;
    }

    public int getLteRssnr() {
        return lteRssnr;
    }

    private void setLteRssnr(int lteRssnr) {
        this.lteRssnr = lteRssnr;
    }

    public int getLteCqi() {
        return lteCqi;
    }

    private void setLteCqi(int lteCqi) {
        this.lteCqi = lteCqi;
    }

    public int getLteTA() {
        return (int) (lteTA);
    }

    public void setLteTA(int lteTA) {
        this.lteTA = lteTA;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }

    public SignalStrength getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }
}

