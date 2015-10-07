package org.rncteam.rncfreemobile.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.rncteam.rncfreemobile.rncmobile;

import java.sql.SQLException;

/**
 * Created by cedricf_25 on 19/07/2015.
 */
public class Database  extends SQLiteOpenHelper {
    private static final String TAG = "Database";

    protected SQLiteDatabase mdb;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rfm.db";

    /* Table Rnc declaration */
    protected static final String TABLE_RNCS = "rncs";

    protected static final String COL_RNC_ID = "_id";
    protected static final String COL_RNCS_TECH   = "_tech";
    protected static final String COL_RNCS_MCC    = "_mcc";
    protected static final String COL_RNCS_MNC    = "_mnc";
    protected static final String COL_RNCS_CID    = "_cid";
    protected static final String COL_RNCS_LAC    = "_lac";
    protected static final String COL_RNCS_RNC    = "_rnc";
    protected static final String COL_RNCS_PSC    = "_psc";
    protected static final String COL_RNCS_LON    = "_lon";
    protected static final String COL_RNCS_LAT    = "_lat";
    protected static final String COL_RNCS_TXT    = "_txt";

    private static final String SQL_CREATE_TABLE_RNCS = "CREATE TABLE " + TABLE_RNCS + "("
            + COL_RNC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_RNCS_TECH + " TEXT NOT NULL, "
            + COL_RNCS_MCC + " TEXT NOT NULL, "
            + COL_RNCS_MNC + " TEXT NOT NULL, "
            + COL_RNCS_CID + " TEXT NOT NULL, "
            + COL_RNCS_LAC + " TEXT NOT NULL, "
            + COL_RNCS_RNC + " TEXT NOT NULL, "
            + COL_RNCS_PSC + " TEXT NOT NULL, "
            + COL_RNCS_LAT + " DOUBLE NOT NULL, "
            + COL_RNCS_LON + " DOUBLE NOT NULL, "
            + COL_RNCS_TXT + " TEXT NOT NULL "
            + ");";

    /* Table Info declaration */
    protected static final String TABLE_INFOS = "infos";

    protected static final String COL_ID = "_id";
    protected static final String COL_INFO_TYPE   = "_type";
    protected static final String COL_INFO_DATE    = "_date";
    protected static final String COL_INFO_TEXT   = "_text";

    private static final String SQL_CREATE_TABLE_INFOS = "CREATE TABLE " + TABLE_INFOS + "("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_INFO_TYPE + " TEXT NOT NULL, "
            + COL_INFO_DATE + " TEXT NOT NULL, "
            + COL_INFO_TEXT + " TEXT NOT NULL "
            + ");";

    /* Table Logs declaration */
    protected static final String TABLE_LOGS = "logs";

    protected static final String COL_LOGS_ID = "_id";
    protected static final String COL_LOGS_TECH   = "_tech";
    protected static final String COL_LOGS_MCC    = "_mcc";
    protected static final String COL_LOGS_MNC    = "_mnc";
    protected static final String COL_LOGS_CID    = "_cid";
    protected static final String COL_LOGS_LAC    = "_lac";
    protected static final String COL_LOGS_RNC    = "_rnc";
    protected static final String COL_LOGS_PSC    = "_psc";
    protected static final String COL_LOGS_LON     = "_lon";
    protected static final String COL_LOGS_LAT     = "_lat";
    protected static final String COL_LOGS_DATE    = "_date";
    protected static final String COL_LOGS_TXT    = "_txt";

    private static final String SQL_CREATE_TABLE_LOGS = "CREATE TABLE " + TABLE_LOGS + "("
            + COL_LOGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_LOGS_TECH + " TEXT NOT NULL, "
            + COL_LOGS_MCC + " TEXT NOT NULL, "
            + COL_LOGS_MNC + " TEXT NOT NULL, "
            + COL_LOGS_CID + " TEXT NOT NULL, "
            + COL_LOGS_LAC + " TEXT NOT NULL, "
            + COL_LOGS_RNC + " TEXT NOT NULL, "
            + COL_LOGS_PSC + " TEXT NOT NULL, "
            + COL_LOGS_LAT + " DOUBLE NOT NULL, "
            + COL_LOGS_LON + " DOUBLE NOT NULL, "
            + COL_LOGS_DATE + " TEXT NOT NULL, "
            + COL_LOGS_TXT + " TEXT NOT NULL "
            + ");";

    /* Table Export declaration */
    protected static final String TABLE_EXPORT = "export";

    protected static final String COL_EXPORT_ID        = "_id";
    protected static final String COL_EXPORT_USER_ID   = "_user_id";
    protected static final String COL_EXPORT_USER_NICK = "_user_nick";
    protected static final String COL_EXPORT_USER_PWD  = "_user_pwd";
    protected static final String COL_EXPORT_USER_TXT  = "_user_txt";
    protected static final String COL_EXPORT_USER_TEL  = "_user_tel";
    protected static final String COL_EXPORT_NAME      = "_name";
    protected static final String COL_EXPORT_DATE      = "_date";
    protected static final String COL_EXPORT_NB        = "_nb";
    protected static final String COL_EXPORT_NB_UMTS   = "_nb_umts";
    protected static final String COL_EXPORT_NB_LTE    = "_nb_lte";
    protected static final String COL_EXPORT_STATE     = "_state";
    protected static final String COL_EXPORT_TYPE      = "_type";
    protected static final String COL_EXPORT_APP_VS    = "_app_version";

    private static final String SQL_CREATE_TABLE_EXPORT = "CREATE TABLE " + TABLE_EXPORT + "("
            + COL_EXPORT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_EXPORT_USER_ID + " TEXT NOT NULL, "
            + COL_EXPORT_USER_NICK + " TEXT NOT NULL, "
            + COL_EXPORT_USER_PWD + " TEXT NOT NULL, "
            + COL_EXPORT_USER_TXT + " TEXT NOT NULL, "
            + COL_EXPORT_USER_TEL + " TEXT NOT NULL, "
            + COL_EXPORT_NAME + " TEXT NOT NULL, "
            + COL_EXPORT_DATE + " TEXT NOT NULL, "
            + COL_EXPORT_NB + " TEXT NOT NULL, "
            + COL_EXPORT_NB_UMTS + " TEXT NOT NULL, "
            + COL_EXPORT_NB_LTE + " TEXT NOT NULL, "
            + COL_EXPORT_STATE + " TEXT NOT NULL, "
            + COL_EXPORT_TYPE + " TEXT NOT NULL, "
            + COL_EXPORT_APP_VS + " TEXT NOT NULL "
            + ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create database rnc...");
        db.execSQL(SQL_CREATE_TABLE_RNCS);

        Log.d(TAG, "Create database info...");
        db.execSQL(SQL_CREATE_TABLE_INFOS);

        Log.d(TAG, "Create database logs...");
        db.execSQL(SQL_CREATE_TABLE_LOGS);

        Log.d(TAG, "Create database user...");
        db.execSQL(SQL_CREATE_TABLE_EXPORT);

        // Insert infos lines
        ContentValues values = new ContentValues();
        values.put(COL_INFO_TYPE, "rncBaseUpdate");
        values.put(COL_INFO_DATE, "0");
        values.put(COL_INFO_TEXT, "-");

        db.insert(TABLE_INFOS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RNCS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INFOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPORT);

        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void open() {
        mdb = this.getWritableDatabase();
    }

    public void close(){
        mdb.close();
    }
}
