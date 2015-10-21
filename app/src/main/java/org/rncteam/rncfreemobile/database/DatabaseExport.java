package org.rncteam.rncfreemobile.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.rncteam.rncfreemobile.models.Export;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 04/10/2015.
 */
public class DatabaseExport extends Database {
    private static final String TAG = "DatabaseExport";

    private static final String TABLE_EXPORT = "export";

    public DatabaseExport(Context context) {
        super(context);
    }

    // Export Table Management
    public void addExport(Export exp) {
        ContentValues v = new ContentValues();

        v.put(COL_EXPORT_USER_ID, exp.get_user_id());
        v.put(COL_EXPORT_USER_NICK, exp.get_user_nick());
        v.put(COL_EXPORT_USER_PWD, exp.get_user_pwd());
        v.put(COL_EXPORT_USER_TXT, exp.get_user_txt());
        v.put(COL_EXPORT_USER_TEL, exp.get_user_tel());
        v.put(COL_EXPORT_DATE, exp.get_date());
        v.put(COL_EXPORT_NB, exp.get_nb());
        v.put(COL_EXPORT_STATE, exp.get_state());
        v.put(COL_EXPORT_TYPE, exp.get_type());
        v.put(COL_EXPORT_APP_VS, exp.get_app_version());

        mdb.insert(TABLE_EXPORT, null, v);
    }

    public void deleteExport() {
        mdb.delete(TABLE_EXPORT, null, null);
    }

    public ArrayList<Export> findAllExport(ArrayList<Export> lExport) {
        lExport.clear();

        String query = "SELECT * FROM " + TABLE_EXPORT + " "
                + "ORDER BY " + COL_EXPORT_DATE + " DESC";

        Cursor c = mdb.rawQuery(query, null);
        c.moveToFirst();

        while(!c.isAfterLast()) {
            Export exp = cToExport(c);
            lExport.add(exp);
            c.moveToNext();
        }

        c.close();
        return lExport;
    }

    private Export cToExport(Cursor c) {
        Export exp = new Export();

        exp.set_id(c.getInt(0));
        exp.set_user_id(c.getString(1));
        exp.set_user_nick(c.getString(2));
        exp.set_user_pwd(c.getString(3));
        exp.set_user_txt(c.getString(4));
        exp.set_user_tel(c.getString(5));
        exp.set_date(c.getString(6));
        exp.set_nb(c.getString(7));
        exp.set_state(c.getString(8));
        exp.set_type(c.getString(9));
        exp.set_app_version(c.getString(10));

        return exp;
    }

}