package org.rncteam.rncfreemobile.models;

import android.telephony.SignalStrength;

import org.rncteam.rncfreemobile.database.Database;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.rncmobile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

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
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return -1;
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
        if(get_cid() > 80 && get_cid() < 85) return "2600 Mhz";
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
                if(rRnc.get_lat() == 0.0 && rRnc.get_lon() == 0.0 && rRnc.get_txt().equals("-")) {
                    rRnc.NOT_IDENTIFIED = true;
                } else rRnc.NOT_IDENTIFIED = false;
                return rRnc;
            }
        }
        return null;
    }

    public Rnc getAnIdentifiedRnc(ArrayList<Rnc> lRnc) {
        for(int i=0;i<lRnc.size();i++) {
            if(!lRnc.get(i).get_txt().equals("-")
                    && lRnc.get(i).get_lat() != 0.0 && lRnc.get(i).get_lon() != 0.0) {
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

    public void updateFamilyUnknowRnc(ArrayList<Rnc> lRnc, Rnc rnc) {

        for(int i=0;i<lRnc.size();i++) {
            if(lRnc.get(i).get_txt().equals("-")
                    && lRnc.get(i).get_lat() == 0.0 && lRnc.get(i).get_lon() == 0.0) {
                Rnc rncToUpdate = lRnc.get(i);
                rncToUpdate.set_lat(rnc.get_lat());
                rncToUpdate.set_lon(rnc.get_lon());
                rncToUpdate.set_txt(rnc.get_txt());

                DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());
                dbr.open();
                dbr.updateRnc(rncToUpdate);
                dbr.close();
            }
        }

    }

    // Redifine txt of a new RNC from 20815.csv
    private String getFormattedString(String rncName) {
        // Pos of [
        int pos = rncName.indexOf("[");
        if(pos == -1) return rncName;
        else return rncName.substring(0, pos - 1);
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

