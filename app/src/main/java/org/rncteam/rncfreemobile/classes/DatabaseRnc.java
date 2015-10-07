package org.rncteam.rncfreemobile.classes;

import android.content.ContentValues;
import android.database.Cursor;

import android.content.Context;
import android.database.sqlite.SQLiteStatement;

import org.rncteam.rncfreemobile.models.Rnc;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by cedricf_25 on 15/07/2015.
 */
public class DatabaseRnc extends Database {

    private static final String TAG = "DatabaseRnc";

    private static final String TABLE_RNCS = "rncs";

    public DatabaseRnc(Context context) {
        super(context);
    }

    // RNC Table Management
    public void addRnc(Rnc rnc) {
        ContentValues v = new ContentValues();

        v.put(COL_RNCS_TECH, rnc.get_tech());
        v.put(COL_RNCS_MCC, rnc.get_mcc());
        v.put(COL_RNCS_MNC, rnc.get_mnc());
        v.put(COL_RNCS_CID, rnc.get_cid());
        v.put(COL_RNCS_LAC, rnc.get_lac());
        v.put(COL_RNCS_RNC, rnc.get_rnc());
        v.put(COL_RNCS_PSC, rnc.get_psc());
        v.put(COL_RNCS_LAT, rnc.get_lat());
        v.put(COL_RNCS_LON, rnc.get_lon());
        v.put(COL_RNCS_TXT, rnc.get_txt());

        mdb.insert(TABLE_RNCS, null, v);
    }

    public void addMassiveRnc(List<Rnc> lRnc) {
        String sql = "INSERT INTO "+ TABLE_RNCS +" VALUES (?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mdb.compileStatement(sql);
        mdb.beginTransaction();

        for (int i=0;i< lRnc.size();i++) {
            statement.clearBindings();

            statement.bindString(1, String.valueOf(i));
            statement.bindString(2, lRnc.get(i).get_tech());
            statement.bindString(3, lRnc.get(i).get_mcc());
            statement.bindString(4, lRnc.get(i).get_mnc());
            statement.bindString(5, lRnc.get(i).get_cid());
            statement.bindString(6, lRnc.get(i).get_lac());
            statement.bindString(7, lRnc.get(i).get_rnc());
            statement.bindString(8, lRnc.get(i).get_psc());
            statement.bindDouble(9, lRnc.get(i).get_lat());
            statement.bindDouble(10, lRnc.get(i).get_lon());
            statement.bindString(11, lRnc.get(i).get_txt());

            statement.execute();
        }

        mdb.setTransactionSuccessful();
        mdb.endTransaction();
    }

    public void deleteRnc() {
        mdb.delete(TABLE_RNCS, null, null);
    }

    public List<Rnc> findAllRnc() {
        List<Rnc> lRnc = new ArrayList<Rnc>();

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

    public Rnc findRncByName(String rncName, String cid) {
        Rnc rnc = new Rnc();

        String query = "SELECT * FROM " + TABLE_RNCS + " WHERE "
                + COL_RNCS_RNC + " = ? AND "
                + COL_RNCS_CID + " = ?; ";

        Cursor c = mdb.rawQuery(query, new String[]{rncName,cid});

        if (c.getCount() > 0) {
            c.moveToFirst();
            rnc = cToRnc(c);
            rnc.NOTHING = false;
        }
        else rnc.NOTHING = true;

        c.close();

        return rnc;
    }

    public List<Rnc> findRncByPsc(String psc) {
        List<Rnc> lRnc = new ArrayList<Rnc>();

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
        ArrayList<Rnc> lRnc = new ArrayList<Rnc>();

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
/*
    public Rnc findRncByCoo(String lat, String lon) {
        Rnc rnc = new Rnc();

        String query = "SELECT * FROM " + TABLE_RNCS + " "
                + "WHERE " + COL_RNCS_LAT + " between ? AND ?"
                + "AND " + COL_RNCS_LON + " between ? AND ? "
                + "AND " + COL_RNCS_TECH + " = '3G' "
                + "GROUP BY " + COL_RNCS_RNC;

        String slat_1 = String.valueOf(lat - 0.01);
        String slat_2 = String.valueOf(lat + 0.01);
        String slon_1 = String.valueOf(lon - 0.01);
        String slon_2 = String.valueOf(lon + 0.01);

        Cursor c = mdb.rawQuery(query, new String[]{slat_1, slat_2, slon_1, slon_2});

        if (c.getCount() > 0) {
            c.moveToFirst();
            rnc = cToRnc(c);
            rnc.NOTHING = false;
        }
        else rnc.NOTHING = true;

        c.close();

        return rnc;
    }
*/
    public Rnc findRncById(String rncId) {
        String query = "SELECT * FROM " + TABLE_RNCS + " WHERE " + COL_ID + " = " + rncId;

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        Rnc rnc = cToRnc(c);
        c.close();

        return rnc;
    }

    private Rnc cToRnc(Cursor c) {
        Rnc rnc = new Rnc();

        //cell.set_id(c.getInt(0));
        rnc.set_tech(c.getString(1));
        rnc.set_mcc(c.getString(2));
        rnc.set_mnc(c.getString(3));
        rnc.set_cid(c.getString(4));
        rnc.set_lac(c.getString(5));
        rnc.set_rnc(c.getString(6));
        rnc.set_psc(c.getString(7));
        rnc.set_lat(c.getDouble(8));
        rnc.set_lon(c.getDouble(9));
        rnc.set_txt(c.getString(10));

        return rnc;
    }


}
