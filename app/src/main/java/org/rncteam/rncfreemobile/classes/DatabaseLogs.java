package org.rncteam.rncfreemobile.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;
import org.rncteam.rncfreemobile.rncmobile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 21/07/2015.
 */
public class DatabaseLogs extends Database {
    private static final String TAG = "DatabaseLogs";

    private static final String TABLE_LOGS = "logs";

    public DatabaseLogs(Context context) {
        super(context);
    }

    // RNC Table Management
    public void addLog(RncLogs rncLog) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_TECH, rncLog.get_tech());
        v.put(COL_LOGS_MCC, rncLog.get_mcc());
        v.put(COL_LOGS_MNC, rncLog.get_mnc());
        v.put(COL_LOGS_CID, rncLog.get_cid());
        v.put(COL_LOGS_LAC, rncLog.get_lac());
        v.put(COL_LOGS_RNC, rncLog.get_rnc());
        v.put(COL_LOGS_PSC, rncLog.get_psc());
        v.put(COL_LOGS_LAT, rncLog.get_lat());
        v.put(COL_LOGS_LON, rncLog.get_lon());
        v.put(COL_LOGS_DATE, rncLog.get_date());
        v.put(COL_LOGS_TXT, rncLog.get_txt());

        mdb.insert(TABLE_LOGS, null, v);
    }

    public void updateLogs(RncLogs rncLog) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_DATE, rncLog.get_date());

        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC + " = ? AND "
                + COL_LOGS_CID + " = ?", new String[]{rncLog.get_rnc(), rncLog.get_cid()});
    }

    public void updateEditedLogs(RncLogs rncLog) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_TXT, rncLog.get_txt());
        v.put(COL_LOGS_LAT, rncLog.get_lat());
        v.put(COL_LOGS_LON, rncLog.get_lon());

        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC + " = ? AND "
                + COL_LOGS_CID + " = ?", new String[]{rncLog.get_rnc(), rncLog.get_cid()});
    }

    public void updateLogsNewRnc(Rnc rnc) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_TXT, rnc.get_txt());
        v.put(COL_LOGS_LAT, rnc.get_lat());
        v.put(COL_LOGS_LON, rnc.get_lon());

        // 3G Rnc
        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC + " = ?", new String[]{rnc.get_real_rnc()});
        // 4G Rnc
        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC + " = ?", new String[]{"40" + rnc.get_real_rnc()});
    }

    public void deleteRncLogs() {
        mdb.delete(TABLE_LOGS, null, null);
    }

    public void deleteOneLogs(RncLogs rncLog) {
        mdb.delete(TABLE_LOGS, COL_ID + " = ?", new String[]{String.valueOf(rncLog.get_id())});
    }

    public List<RncLogs> findAllRncLogs() {
        List<RncLogs> lRncLogs = new ArrayList<RncLogs>();

        String query = "SELECT * FROM " + TABLE_LOGS
                + " ORDER BY " + COL_LOGS_DATE + " DESC";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            RncLogs rncLog = cToRncLogs(c);
            lRncLogs.add(rncLog);
            c.moveToNext();
        }

        c.close();
        return lRncLogs;
    }

    public void findAllRncLogsMainList() {
        if(rncmobile.listRncLogs != null) {
            rncmobile.listRncLogs.clear();

            String query = "SELECT * FROM " + TABLE_LOGS
                    + " ORDER BY " + COL_LOGS_DATE + " DESC";

            Cursor c = mdb.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                RncLogs rncLog = cToRncLogs(c);
                rncmobile.listRncLogs.add(rncLog);
                c.moveToNext();
            }

            c.close();
        }
    }

    public RncLogs findRncLogsByRnc(String rnc) {
        String rncLte = "40" + rnc;

        String query = "SELECT * FROM " + TABLE_LOGS + " "
                + "WHERE " + COL_LOGS_RNC + " = ? "
                + "OR " + COL_LOGS_RNC + "=  ?;";

        Cursor c = mdb.rawQuery(query, new String[]{rnc,rncLte});
        if(c.getCount() > 0) {
            c.moveToFirst();

            RncLogs rncLog = cToRncLogs(c);
            c.close();
            return rncLog;
        }
        return null;
    }

    public RncLogs findRncLogsByRncCid(String rnc, String cid) {
        String query = "SELECT * FROM " + TABLE_LOGS + " "
                + "WHERE " + COL_LOGS_RNC + " = ? "
                + "AND " + COL_LOGS_CID + "=  ?;";

        Cursor c = mdb.rawQuery(query, new String[]{rnc,cid});
        if(c.getCount() > 0) {
            c.moveToFirst();

            RncLogs rncLog = cToRncLogs(c);
            c.close();
            return rncLog;
        }
        return null;
    }

    private RncLogs cToRncLogs(Cursor c) {
        RncLogs rncLog = new RncLogs();

        rncLog.set_id(c.getInt(0));
        rncLog.set_tech(c.getString(1));
        rncLog.set_mcc(c.getString(2));
        rncLog.set_mnc(c.getString(3));
        rncLog.set_cid(c.getString(4));
        rncLog.set_lac(c.getString(5));
        rncLog.set_rnc(c.getString(6));
        rncLog.set_psc(c.getString(7));
        rncLog.set_lat(Double.valueOf(c.getString(8)));
        rncLog.set_lon(Double.valueOf(c.getString(9)));
        rncLog.set_date(c.getString(10));
        rncLog.set_txt(c.getString(11));

        return rncLog;
    }
}
