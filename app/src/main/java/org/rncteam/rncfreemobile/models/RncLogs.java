package org.rncteam.rncfreemobile.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.rncteam.rncfreemobile.rncmobile;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class RncLogs implements Serializable {
    private static final String TAG = "RNCLOGS";

    private int _id;
    private int _rnc_id;
    private String _date;
    private int _tech;
    private int _mcc;
    private int _mnc;
    private int _cid;
    private int _lac;
    private int _rnc;
    private int _psc;
    private Double _lat;
    private Double _lon;
    private String _txt;

    public RncLogs() {
    }

    public String get_fr_datetime() {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(get_date());
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        long when = date.getTime();
        int flags = 0;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_TIME;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_DATE;
        flags |= android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
        flags |= android.text.format.DateUtils.FORMAT_SHOW_YEAR;

        String finalDateTime = android.text.format.DateUtils.formatDateTime(rncmobile.getAppContext(),
                when + TimeZone.getDefault().getOffset(when), flags);

        return finalDateTime;
    }

    public String get_time() {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(get_date());
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public String get_formated_date_abbrev() {
        Date d = get_date_obj();

        try {
            long prev = d.getTime();
            long now = System.currentTimeMillis();
            return String.valueOf(DateUtils.getRelativeTimeSpanString(prev, now, 0L, DateUtils.FORMAT_ABBREV_ALL));
        } catch (Exception e) {

        }
        return "";
    }

    public Date get_date_obj() {
        Date date = new Date();
        DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = iso8601Format.parse(get_date());
        } catch (ParseException e) {
            Log.e(TAG, "Parsing ISO8601 datetime failed", e);
        }

        return date;
    }

    // Getter & setter
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int get_rnc_id() {
        return _rnc_id;
    }

    public void set_rnc_id(int _rnc_id) {
        this._rnc_id = _rnc_id;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public int get_tech() {
        return _tech;
    }

    public void set_tech(int _tech) {
        this._tech = _tech;
    }

    public int get_mcc() {
        return _mcc;
    }

    public void set_mcc(int _mcc) {
        this._mcc = _mcc;
    }

    public int get_mnc() {
        return _mnc;
    }

    public void set_mnc(int _mnc) {
        this._mnc = _mnc;
    }

    public int get_cid() {
        return _cid;
    }

    public void set_cid(int _cid) {
        this._cid = _cid;
    }

    public int get_lac() {
        return _lac;
    }

    public void set_lac(int _lac) {
        this._lac = _lac;
    }

    public int get_rnc() {
        return _rnc;
    }

    public void set_rnc(int _rnc) {
        this._rnc = _rnc;
    }

    public int get_psc() {
        return _psc;
    }

    public void set_psc(int _psc) {
        this._psc = _psc;
    }

    public Double get_lat() {
        return _lat;
    }

    public void set_lat(Double _lat) {
        this._lat = _lat;
    }

    public Double get_lon() {
        return _lon;
    }

    public void set_lon(Double _lon) {
        this._lon = _lon;
    }

    public String get_txt() {
        return _txt;
    }

    public void set_txt(String _txt) {
        this._txt = _txt;
    }
}
