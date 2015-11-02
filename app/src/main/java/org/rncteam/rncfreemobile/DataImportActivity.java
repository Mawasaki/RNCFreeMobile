package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.database.DatabaseRnc;
import org.rncteam.rncfreemobile.models.Rnc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedricf_25 on 02/11/2015.
 */
public class DataImportActivity extends Activity {
    private static final String TAG = "DataImportActivity";

    // UI
    private RadioGroup rg_group;

    private String pathToExternalStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_import);

        TextView lytDataDownload = (TextView) findViewById(R.id.txt_data_import_info);
        rg_group = (RadioGroup) findViewById(R.id.rg_files);
        Button btnImportData = (Button) findViewById(R.id.btn_import_data);

        pathToExternalStorage = Environment.getExternalStorageDirectory().toString();

        lytDataDownload.setText("To import database place it into the folder: "
                + pathToExternalStorage + "/RNCMobile");

        File f = new File(pathToExternalStorage + "/RNCMobile");
        File[] files = f.listFiles();

        for (int i=0; i < files.length; i++)
        {
            RadioButton button = new RadioButton(this);
            button.setId(i);
            button.setText(files[i].getName());
            //button.setChecked(i == currentHours);
            //button.setBackgroundResource(R.drawable.item_selector); // This is a custom button drawable, defined in XML
            rg_group.addView(button);
        }

        btnImportData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if a button is clicked
                int selectedId = rg_group.getCheckedRadioButtonId();
                if(selectedId > -1) {
                    RadioButton rb_selected = (RadioButton) findViewById(selectedId);

                    String csvFile = pathToExternalStorage + "/RNCMobile/"
                            + rb_selected.getText().toString();

                    BufferedReader reader = null;

                    // Fill array with find lines
                    List<Rnc> lCell = new ArrayList<>();

                    try {

                        String line;
                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile),"iso-8859-1"));

                        while ((line = reader.readLine()) != null) {
                            String[] RowData = line.split(";");

                            lCell.add(lToRnc(RowData));
                        }

                        reader.close();
                    }
                    catch (IOException e) {
                        String msg = "Erreur lors de l'importation";
                        HttpLog.send(TAG, e, msg);
                        Log.d(TAG, msg + e.toString());
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    }

                    // Start import from array
                    // Reaffects logs
                    DatabaseRnc dbr = new DatabaseRnc(rncmobile.getAppContext());

                    dbr.open();

                    for (int i = 0; i < lCell.size(); i++) {
                        Rnc rncImport = lCell.get(i);
                        Rnc rnc = dbr.findRncByRncCid(
                                String.valueOf(rncImport.get_rnc()),
                                String.valueOf(rncImport.get_cid()));

                        if (rnc != null) {
                            // Disable import update rnc for now
                            dbr.updateRnc(rncImport);
                        } else {
                            dbr.addRnc(rncImport);
                        }
                    }

                    Telephony tel = rncmobile.getTelephony();
                    tel.setCellChange(true);
                    tel.dispatchCellInfo();

                    Toast.makeText(getApplicationContext(), "Database has been updated", Toast.LENGTH_SHORT).show();

                    finish();

                    dbr.close();
                } else {
                    Toast.makeText(getApplicationContext(), "Please click a database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private Rnc lToRnc(String[] l) {
        Rnc rnc = new Rnc();

        rnc.set_tech(((String.valueOf(l[0])).equals("3G") ? 3 : 4));
        rnc.set_mcc(Integer.valueOf(l[1]));
        rnc.set_mnc(Integer.valueOf(l[2]));
        rnc.set_cid(Integer.valueOf(l[3]));
        rnc.set_lac(Integer.valueOf(l[4]));
        rnc.set_rnc(Integer.valueOf(l[5]));
        rnc.set_psc(Integer.valueOf(l[6]));
        rnc.set_lat(Double.valueOf(l[7]));
        rnc.set_lon(Double.valueOf(l[8]));
        rnc.set_txt(l[9]);

        return rnc;
    }

}
