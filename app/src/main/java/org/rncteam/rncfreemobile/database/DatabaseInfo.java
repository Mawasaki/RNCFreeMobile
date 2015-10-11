package org.rncteam.rncfreemobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 17/07/2015.
 */
public class DatabaseInfo extends Database {
    private static final String TAG = "DatabaseInfo";

    public DatabaseInfo(Context context) {
        super(context);
    }

    // Infos Table Management
    public void addInfo(String type, String date, String text) {
        ContentValues v = new ContentValues();

        v.put(COL_INFO_TYPE, type);
        v.put(COL_INFO_DATE, date);
        v.put(COL_INFO_TEXT, text);

        mdb.insert(TABLE_INFOS, null, v);
    }

    public void updateInfo(String type, String date, String text) {
        ContentValues v = new ContentValues();

        v.put(COL_INFO_DATE, date);
        v.put(COL_INFO_TEXT, text);

        mdb.update(TABLE_INFOS, v, COL_INFO_TYPE + " = ?", new String[] { type });
    }

    public ArrayList getInfo(String type) {
        ArrayList lInfo = new ArrayList();

        String query = "SELECT * FROM " + TABLE_INFOS + " WHERE "
                + COL_INFO_TYPE + " = ?; ";

        Cursor c = mdb.rawQuery(query, new String[]{type});

        if (c.getCount() > 0) {
            c.moveToFirst();
            lInfo = cToType(c);
        }

        c.close();

        return lInfo;
    }

    public void deleteInfos() {
        mdb.delete(TABLE_INFOS, null, null);
    }

    public ArrayList findInfoByType(String type) {
        ArrayList info = null;

        String query = "SELECT * FROM " + TABLE_INFOS + " WHERE " + COL_INFO_TYPE + " = ?; ";
        Cursor c = mdb.rawQuery(query, new String[]{type});

        if (c.getCount() > 0) {
            c.moveToFirst();
            info = cToType(c);
        }
        c.close();

        return info;
    }

    private ArrayList cToType(Cursor c) {
        ArrayList<String> lInfo = new ArrayList();

        //cell.set_id(c.getInt(0));
        lInfo.add(0, c.getString(1));
        lInfo.add(1, c.getString(2));
        lInfo.add(2, c.getString(3));

        return lInfo;
    }
}
