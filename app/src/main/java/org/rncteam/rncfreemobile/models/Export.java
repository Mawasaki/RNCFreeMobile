package org.rncteam.rncfreemobile.models;

/**
 * Created by cedric_f25 on 04/10/2015.
 */
public class Export {
    private static final String TAG = "DatabaseExport";

    private int _id;
    private String _user_id;
    private String _user_nick;
    private String _user_pwd;
    private String _user_txt;
    private String _user_tel;
    private String _name;
    private String _date;
    private String _nb;
    private String _nb_umts;
    private String _nb_lte;
    private String _state;
    private String _type;
    private String _app_version;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String get_user_id() {
        return _user_id;
    }

    public void set_user_id(String _user_id) {
        this._user_id = _user_id;
    }

    public String get_user_nick() {
        return _user_nick;
    }

    public void set_user_nick(String _user_nick) {
        this._user_nick = _user_nick;
    }

    public String get_user_pwd() {
        return _user_pwd;
    }

    public void set_user_pwd(String _user_pwd) {
        this._user_pwd = _user_pwd;
    }

    public String get_user_txt() {
        return _user_txt;
    }

    public void set_user_txt(String _user_txt) {
        this._user_txt = _user_txt;
    }

    public String get_user_tel() {
        return _user_tel;
    }

    public void set_user_tel(String _user_tel) {
        this._user_tel = _user_tel;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_nb() {
        return _nb;
    }

    public void set_nb(String _nb) {
        this._nb = _nb;
    }

    public String get_nb_umts() {
        return _nb_umts;
    }

    public void set_nb_umts(String _nb_umts) {
        this._nb_umts = _nb_umts;
    }

    public String get_nb_lte() {
        return _nb_lte;
    }

    public void set_nb_lte(String _nb_lte) {
        this._nb_lte = _nb_lte;
    }

    public String get_state() {
        return _state;
    }

    public void set_state(String _state) {
        this._state = _state;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_app_version() {
        return _app_version;
    }

    public void set_app_version(String _app_version) {
        this._app_version = _app_version;
    }
}
