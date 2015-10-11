package org.rncteam.rncfreemobile.models;

import android.telephony.SignalStrength;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cedric_f25 on 15/07/2015.
 */
public class Rnc {
    private static final String TAG = "RNC";

    public boolean NOT_IDENTIFIED;
    public boolean NOT_IN_DB;

    // Database attributes
    private int _id;
    private String _tech;
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

    private String networkName;
    private SignalStrength signalStrength;

    public Rnc() {
        _lon = -1.0;
        _lat = -1.0;
        _lcid = -1;
        NOT_IDENTIFIED = false;
        NOT_IN_DB = false;

        umtsRscp = -1;
        lteRssi = -1;
        lteAsu = -1;
        lteRsrp = -1;
        lteRsrq = -1;
        lteRssnr = -1;
        lteCqi = -1;
        signalStrength = null;
        networkName = "err";
    }

    public void calc() {
        // Signal Strength
        if(signalStrength != null) {
            if(get_tech().equals("3G"))
                umtsRscp = signalStrength.getGsmSignalStrength();

            if(get_tech().equals("4G")) {
                setLteSignals();
                lteRssi = 17 + getLteRsrp() + getLteRsrq();
            }
        }

        // CID & RNC
        _cid = getCid();
        _rnc = getRnc();
    }

    // UMTS Management
    public int getCid() {
        if(get_tech().equals("4G")) return get_lcid() & 0xff;
        if (getStdRnc() >= 256 && getStdRnc() <= 1000 && get_mnc() == 15) {
            return getExtCid();
        } else {
            return getStdCid();
        }
    }

    public int getRnc() {
        // In UTMS, we can have extend RNC, alright
        if(get_tech().equals("4G")) return get_lcid() >> 8;
        if (getStdRnc() >= 256 && getStdRnc() <= 1000 && get_mnc() == 15) {
            return getExtRnc();
        } else {
            return getStdRnc();
        }
    }

    public int getStdCid() {
        return get_lcid() & 0xffff;
    }

    public int getExtCid() {
        return get_lcid() & 0xfff;
    }

    private int getStdRnc() {
        return (get_lcid() >> 16) & 0xffff;
    }
    private int getExtRnc() {
        return (get_lcid() >> 12) & 0xffff;
    }

    // LTE Signals management
    private void setLteSignals() {
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
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return -1;
    }

    public String get_real_rnc() {
        String rnc = String.valueOf(_rnc);
        if(rnc.substring(0,2).equals("40")) return rnc.substring(2, rnc.length());
        else return rnc;
    }

    // Getter & Setter Database
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_tech() {
        return _tech;
    }

    public void set_tech(String _tech) {
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

    public void setLteAsu(int lteAsu) {
        this.lteAsu = lteAsu;
    }

    public int getLteRsrp() {
        return lteRsrp;
    }

    public void setLteRsrp(int lteRsrp) {
        this.lteRsrp = lteRsrp;
    }

    public int getLteRsrq() {
        return lteRsrq;
    }

    public void setLteRsrq(int lteRsrq) {
        this.lteRsrq = lteRsrq;
    }

    public int getLteRssnr() {
        return lteRssnr;
    }

    public void setLteRssnr(int lteRssnr) {
        this.lteRssnr = lteRssnr;
    }

    public int getLteCqi() {
        return lteCqi;
    }

    public void setLteCqi(int lteCqi) {
        this.lteCqi = lteCqi;
    }

    public SignalStrength getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(SignalStrength signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = networkName;
    }
}

