package org.rncteam.rncfreemobile.models;

/**
 * Created by cedric_f25 on 15/07/2015.
 */
public class Rnc {
    private static final String TAG = "RNC";

    public boolean NOTHING;

    private int _id;
    protected String _tech;
    protected String _mcc;
    protected String _mnc;
    protected String _cid;
    protected String _rnc;
    protected String _lac;
    protected String _psc;
    protected Double _lon;
    protected Double _lat;
    protected String _txt;
    private int _rssi;

    private String ci;

    public Rnc() {
        NOTHING = false;
    }

    public Rnc(String _tech, String _mcc, String _mnc, String _cid, String _rnc, String _lac, String _psc, Double _lat, Double _lon, String _txt) {
        this._tech = _tech;
        this._mcc = _mcc;
        this._mnc = _mnc;
        this._cid = _cid;
        this._rnc = _rnc;
        this._lac = _lac;
        this._psc = _psc;
        this._lat = _lat;
        this._lon = _lon;
        this._txt = _txt;
    }

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

    public String get_mcc() {
        return _mcc;
    }

    public void set_mcc(String _mcc) {
        this._mcc = _mcc;
    }

    public String get_mnc() {
        return _mnc;
    }

    public void set_mnc(String _mnc) {
        this._mnc = _mnc;
    }

    public String get_cid() {
        return _cid;
    }

    public void set_cid(String _cid) {
        this._cid = _cid;
    }

    public String get_rnc() {
        return _rnc;
    }

    public void set_rnc(String _rnc) {
        this._rnc = _rnc;
    }

    public String get_lac() {
        return _lac;
    }

    public void set_lac(String _lac) {
        this._lac = _lac;
    }

    public String get_psc() {
        return _psc;
    }

    public void set_psc(String _psc) {
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
        if (this.NOTHING) return "-";
        else return _txt;
    }

    public String get_real_rnc() {
        if(_rnc.substring(0,2).equals("40")) return _rnc.substring(2,_rnc.length());
        else return _rnc;
    }

    public void set_txt(String _txt) {
        this._txt = _txt;
    }

    public String get_ci() {
        return ci;
    }

    public void set_ci(String ci) { this.ci = ci; }

    public int get_rssi() {
        return _rssi;
    }

    public void set_rssi(int rssi) { this._rssi = rssi; }
}

