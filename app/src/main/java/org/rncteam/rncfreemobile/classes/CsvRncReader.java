package org.rncteam.rncfreemobile.classes;

import android.os.Environment;

import org.rncteam.rncfreemobile.models.Rnc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cedric on 15/07/2015.
 */
public class CsvRncReader {

    public List<Rnc> run() {

        String csvFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/20815.csv";
        BufferedReader reader = null;

        List<Rnc> lCell = new ArrayList<Rnc>();

        try {
            String line;
            reader = new BufferedReader(new FileReader(csvFile));

            while ((line = reader.readLine()) != null) {
                String[] RowData = line.split(";");

                lCell.add(lToRnc(RowData));
            }
        }
        catch (IOException ex) {
            // handle exception
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                // handle exception
            }
        }
        return lCell;
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
