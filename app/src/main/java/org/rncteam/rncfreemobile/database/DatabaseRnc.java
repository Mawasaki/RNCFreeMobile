package org.rncteam.rncfreemobile.database;

import android.content.ContentValues;
import android.database.Cursor;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;

import org.rncteam.rncfreemobile.models.Rnc;
import org.rncteam.rncfreemobile.models.RncLogs;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cedricf_25 on 15/07/2015.
 */
public class DatabaseRnc extends Database {

    private static final String TAG = "DatabaseRnc";

    private static final String TABLE_RNCS = "rncs";

    private final String UNIDENTIFIED_CELL_TEXT = "-";

    public DatabaseRnc(Context context) {
        super(context);
    }

    public long addRnc(Rnc rnc) {
        ContentValues v = new ContentValues();

        v.put(COL_RNCS_TECH, rnc.get_tech());
        v.put(COL_RNCS_MCC, rnc.get_mcc());
        v.put(COL_RNCS_MNC, rnc.get_mnc());
        v.put(COL_RNCS_CID, rnc.getCid());
        v.put(COL_RNCS_LAC, rnc.get_lac());
        v.put(COL_RNCS_RNC, rnc.getRnc());
        v.put(COL_RNCS_PSC, rnc.get_psc());
        v.put(COL_RNCS_LAT, rnc.get_lat());
        v.put(COL_RNCS_LON, rnc.get_lon());
        v.put(COL_RNCS_TXT, rnc.get_txt());

        return mdb.insert(TABLE_RNCS, null, v);
    }

    public void addMassiveRnc(List<Rnc> lRnc) {
        String sql = "INSERT INTO "+ TABLE_RNCS +" VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mdb.compileStatement(sql);
        mdb.beginTransaction();

        for (int i=0;i< lRnc.size();i++) {
            statement.clearBindings();

            statement.bindLong(1, i);
            statement.bindLong(2, lRnc.get(i).get_tech());
            statement.bindLong(3, lRnc.get(i).get_mcc());
            statement.bindLong(4, lRnc.get(i).get_mnc());
            statement.bindLong(5, lRnc.get(i).get_cid());
            statement.bindLong(6, lRnc.get(i).get_lac());
            statement.bindLong(7, lRnc.get(i).get_rnc());
            statement.bindLong(8, lRnc.get(i).get_psc());
            statement.bindDouble(9, lRnc.get(i).get_lat());
            statement.bindDouble(10, lRnc.get(i).get_lon());
            statement.bindString(11, lRnc.get(i).get_txt());

            statement.execute();
        }

        mdb.setTransactionSuccessful();
        mdb.endTransaction();
    }

    public void deleteAllRnc() {
        mdb.delete(TABLE_RNCS, null, null);
    }


    public void updateRnc(Rnc rnc) {
        ContentValues v = new ContentValues();

        v.put(COL_RNCS_TECH, rnc.get_tech());
        v.put(COL_RNCS_LAT, rnc.get_lat());
        v.put(COL_RNCS_LON, rnc.get_lon());
        v.put(COL_RNCS_TXT, rnc.get_txt());

        mdb.update(TABLE_RNCS, v, COL_RNCS_RNC + " = ? AND "
                        + COL_RNCS_CID + " = ?",
                new String[]{String.valueOf(rnc.get_rnc()), String.valueOf(rnc.get_cid())});
    }

    public void updateAllRnc(Rnc rnc) {
        ContentValues v = new ContentValues();

        String rncUmts = String.valueOf(rnc.get_real_rnc());
        String rncLte = "40" + String.valueOf(rnc.get_real_rnc());

        v.put(COL_RNCS_LAT, rnc.get_lat());
        v.put(COL_RNCS_LON, rnc.get_lon());
        v.put(COL_RNCS_TXT, rnc.get_txt());

        mdb.update(TABLE_RNCS, v, COL_RNCS_RNC + " = ? OR "
                        + COL_RNCS_RNC + " = ?",
                new String[]{rncUmts, rncLte});
    }

    public List<Rnc> findAllRnc() {
        List<Rnc> lRnc = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RNCS;

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            Rnc rnc = cToRnc(c);
            lRnc.add(rnc);
            c.moveToNext();
        }

        c.close();
        return lRnc;
    }

    public Integer countAllCid() {
        List<Rnc> lRnc = new ArrayList<>();

        String query = "SELECT count( " + COL_RNC_ID + ") AS nb_rnc FROM " + TABLE_RNCS;

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        int nc_rnc = c.getInt(0);

        return nc_rnc;
    }

    public Integer countUMTSRnc() {
        String query = "SELECT "
                + "count(DISTINCT " + COL_RNCS_RNC + ") AS nb_rnc "
                + "FROM " + TABLE_RNCS + " "
                + "WHERE " + COL_RNCS_TECH + " = 3";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        int nc_rnc = c.getInt(0);

        return nc_rnc;
    }

    public Integer countLTERnc() {
        String query = "SELECT "
                + "count(DISTINCT " + COL_RNCS_RNC + ") AS nb_rnc "
                + "FROM " + TABLE_RNCS + " "
                + "WHERE " + COL_RNCS_TECH + " = 4";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        int nc_rnc = c.getInt(0);

        return nc_rnc;
    }

    public Rnc findRncByRncCid(String rncName, String cid) {
        Rnc rnc;

        String query = "SELECT * FROM " + TABLE_RNCS + " WHERE "
                + "(" + COL_RNCS_RNC + " = ? ) AND "
                + COL_RNCS_CID + " = ?; ";

        Cursor c = mdb.rawQuery(query, new String[]{rncName, cid});

        if (c.getCount() > 0) {
            c.moveToFirst();
            rnc = cToRnc(c);
        }
        else rnc = null;

        c.close();

        return rnc;
    }

    public ArrayList<Rnc> findRncByRnc(String rncName) {
        ArrayList<Rnc> lRnc = new ArrayList<>();

        String rncUmts = rncName;
        String rncLte = "40" + rncName;

        String query = "SELECT * FROM " + TABLE_RNCS + " WHERE "
                + COL_RNCS_RNC + " = ? OR " + COL_RNCS_RNC + " = ?;";

        Cursor c = mdb.rawQuery(query, new String[]{rncUmts,rncLte});
        c.moveToFirst();

        while(!c.isAfterLast()) {
            Rnc rnc = cToRnc(c);
            lRnc.add(rnc);
            c.moveToNext();
        }

        c.close();

        return lRnc;
    }

    public List<Rnc> findRncByPsc(String psc) {
        List<Rnc> lRnc = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RNCS + " WHERE "
                + COL_RNCS_PSC + " = ?;";

        Cursor c = mdb.rawQuery(query, new String[]{psc});
        c.moveToFirst();

        while(!c.isAfterLast()) {
            Rnc rnc = cToRnc(c);
            lRnc.add(rnc);
            c.moveToNext();
        }

        c.close();

        return lRnc;
    }

    public ArrayList<Rnc> findListRncByCoo(Double lat1, Double lat2, Double lon1, Double lon2) {
        ArrayList<Rnc> lRnc = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_RNCS + " "
                + "WHERE " + COL_RNCS_LAT + " between ? AND ? "
                + "AND " + COL_RNCS_LON + " between ? AND ? "
                + "GROUP BY " + COL_RNCS_RNC;

        String slat_1 = String.valueOf(lat1);
        String slat_2 = String.valueOf(lat2);
        String slon_1 = String.valueOf(lon1);
        String slon_2 = String.valueOf(lon2);

        Cursor c = mdb.rawQuery(query, new String[]{slat_1, slat_2, slon_1, slon_2});

        c.moveToFirst();

        while(!c.isAfterLast()) {
            Rnc rnc = cToRnc(c);
            lRnc.add(rnc);
            c.moveToNext();
        }

        c.close();

        return lRnc;
    }

    private Rnc cToRnc(Cursor c) {
        Rnc rnc = new Rnc();

        rnc.set_id(c.getInt(0));
        rnc.set_tech(c.getInt(1));
        rnc.set_mcc(c.getInt(2));
        rnc.set_mnc(c.getInt(3));
        rnc.set_cid(c.getInt(4));
        rnc.set_lac(c.getInt(5));
        rnc.set_rnc(c.getInt(6));
        rnc.set_psc(c.getInt(7));
        rnc.set_lat(c.getDouble(8));
        rnc.set_lon(c.getDouble(9));
        rnc.set_txt(c.getString(10));

        return rnc;
    }


}
