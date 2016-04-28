package org.rncteam.rncfreemobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.util.ArrayList;

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
    public long addLog(RncLogs rncLog) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_RNC_ID, rncLog.get_rnc_id());
        v.put(COL_LOGS_DATE, rncLog.get_date());
        v.put(COL_LOGS_SYNC, 0);

        return mdb.insert(TABLE_LOGS, null, v);
    }

    public void updateLogs(RncLogs rncLog) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_DATE, rncLog.get_date());

        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC_ID + " = ?",
                new String[]{String.valueOf(rncLog.get_rnc_id())});
    }

    public void updateSyncLogs(Rnc rnc, int sync) {
        ContentValues v = new ContentValues();

        v.put(COL_LOGS_SYNC, sync);

        mdb.update(TABLE_LOGS, v, COL_LOGS_RNC_ID + " = ?",
                new String[]{String.valueOf(rnc.get_id())});
    }

    public void deleteRncLogs() {
        mdb.delete(TABLE_LOGS, null, null);
    }

    public void deleteOneLogs(RncLogs rncLog) {
        mdb.delete(TABLE_LOGS, COL_ID + " = ?", new String[]{String.valueOf(rncLog.get_id())});
    }

    public int countAllLogs() {
        String query = "SELECT count(" + COL_LOGS_ID + ") FROM " + TABLE_LOGS + ";";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        return c.getInt(0);
    }

    public ArrayList<RncLogs> findAllRncLogs() {
        ArrayList<RncLogs> lRncLogs = new ArrayList<>();

        String query = "SELECT r._id as rid, l._id as lid, * "
                + " FROM " + TABLE_RNCS + " AS r"
                + " INNER JOIN " + TABLE_LOGS + " AS l"
                + " ON l." + COL_LOGS_RNC_ID + " = r." + COL_RNC_ID
                + " ORDER BY " + COL_LOGS_DATE + " DESC";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            RncLogs rncLog = cToJoinRncLogs(c);
            lRncLogs.add(rncLog);
            c.moveToNext();
        }

        c.close();
        return lRncLogs;
    }

    public RncLogs findOneRncLogs(int rncId) {
        String query = "SELECT * FROM " + TABLE_LOGS + " WHERE "
                + COL_LOGS_RNC_ID + " = ?;";

        Cursor c = mdb.rawQuery(query, new String[]{String.valueOf(rncId)});

        if (c.getCount() > 0) {
            c.moveToFirst();
            return cToRncLogs(c);
        }
        return null;
    }

    private RncLogs cToRncLogs(Cursor c) {
        RncLogs rncLog = new RncLogs();

        rncLog.set_id(c.getInt(0));
        rncLog.set_rnc_id(c.getInt(1));
        rncLog.set_date(c.getString(2));
        rncLog.set_sync(c.getInt(3));

        return rncLog;
    }

    private RncLogs cToJoinRncLogs(Cursor c) {
        RncLogs rncLog = new RncLogs();

        rncLog.set_id(c.getInt(c.getColumnIndex("lid")));
        rncLog.set_rnc_id(c.getInt(c.getColumnIndex(COL_LOGS_RNC_ID)));
        rncLog.set_tech(c.getInt(c.getColumnIndex(COL_RNCS_TECH)));
        rncLog.set_mcc(c.getInt(c.getColumnIndex(COL_RNCS_MCC)));
        rncLog.set_mnc(c.getInt(c.getColumnIndex(COL_RNCS_MNC)));
        rncLog.set_lcid(c.getInt(c.getColumnIndex(COL_RNCS_LCID)));
        rncLog.set_cid(c.getInt(c.getColumnIndex(COL_RNCS_CID)));
        rncLog.set_lac(c.getInt(c.getColumnIndex(COL_RNCS_LAC)));
        rncLog.set_rnc(c.getInt(c.getColumnIndex(COL_RNCS_RNC)));
        rncLog.set_psc(c.getInt(c.getColumnIndex(COL_RNCS_PSC)));
        rncLog.set_lat(c.getDouble(c.getColumnIndex(COL_RNCS_LAT)));
        rncLog.set_lon(c.getDouble(c.getColumnIndex(COL_RNCS_LON)));
        rncLog.set_date(c.getString(c.getColumnIndex(COL_LOGS_DATE)));
        rncLog.set_txt(c.getString(c.getColumnIndex(COL_RNCS_TXT)));
        rncLog.set_sync(c.getInt(c.getColumnIndex(COL_LOGS_SYNC)));

        return rncLog;
    }
}
