package org.rncteam.rncfreemobile.classes;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.rncmobile;
import org.rncteam.rncfreemobile.tasks.ProfileTask;

import java.util.ArrayList;

/**
 * Created by cedricf_25 on 12/10/2015.
 */

public class Elevation implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "Elevation";

    private final Activity activity;
    private final Utils utils;

    private LineChart mChart;
    private TextView txtTitleChart;
    private RelativeLayout loadingChart;

    private Double myLat;
    private Double myLon;
    private Double rncLat;
    private Double rncLon;

    public Elevation(Activity activity) {
        this.activity = activity;
        this.utils = new Utils();
    }

    public void initChart() {

        txtTitleChart = (TextView) activity.findViewById(R.id.txtTitleChart);
        loadingChart = (RelativeLayout) activity.findViewById(R.id.loadingPanelChart);

        mChart = (LineChart) activity.findViewById(R.id.chart_profile);
        mChart.setViewPortOffsets(10, 20, 10, 10);
        //mChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        mChart.setDescription("");

        // enable value highlighting
        //mChart.setHighlightEnabled(true);

        mChart.setAutoScaleMinMaxEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        mChart.setPadding(10, 10, 10, 10);

        XAxis x = mChart.getXAxis();
        x.setTextColor(Color.BLUE);
        x.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        YAxis y = mChart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setStartAtZero(false);
        y.setTextColor(Color.RED);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.GRAY);

        //mChart.getAxisRight().setEnabled(false);

        mChart.getLegend().setEnabled(true);

        mChart.animateXY(2000, 2000);

        // dont forget to refresh the drawing
        mChart.invalidate();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
/*
        tvX.setText("" + (mSeekBarX.getProgress() + 1));
        tvY.setText("" + (mSeekBarY.getProgress()));

        setData(mSeekBarX.getProgress() + 1, mSeekBarY.getProgress());
*/
        // redraw
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    public void getData(Double myLat, Double myLon, Double rncLat, Double rncLon) {
        this.myLat = myLat;
        this.myLon = myLon;
        this.rncLat = rncLat;
        this.rncLon = rncLon;

        ProfileTask profileTask = new ProfileTask(myLat, myLon, rncLat, rncLon, this);
        profileTask.execute();

        loadingChart.setVisibility(View.VISIBLE);
    }

    public void setData(JSONArray jData) {
        try {
            ArrayList<String> xVals = new ArrayList<>();

            LatLng myLoc = new LatLng(myLat,myLon);
            LatLng btsLoc = new LatLng(rncLat, rncLon);

            Double distance = utils.calculationByDistance(myLoc, btsLoc);
            double meter = distance * 1000;

            Float x = (float) Math.round(meter);
            int increment = x.intValue()/100;

            for (int i = 0; i <= meter; i+=increment) {
                xVals.add((i) + "");
            }

            ArrayList<Entry> vals1 = new ArrayList<>();

            float firstPoint = 0;
            float lastPoint = 0;
            int lastI = 0;
            for (int i = 0; i < jData.length(); i++) {
                // Gets points
                JSONObject elevation_points = jData.getJSONObject(i);

                // First value is higer
                Float h = Float.parseFloat(elevation_points.getString("elevation"));
                if(i==0) h+=1;

                vals1.add(new Entry(h, i));

                if(i==0) firstPoint = h;
                lastPoint = h;
                lastI = i;
            }

            // Add h of antenna to las point
            Telephony tel = rncmobile.getTelephony();
            AnfrInfos ai = tel.getAnfrInfos();
            vals1.add(new Entry(lastPoint + Float.valueOf(ai.getHauteur()), lastI));

            LineDataSet set1 = new LineDataSet(vals1, "Profile Dataset");
            set1.setLineWidth(1.8f);
            set1.setColor(Color.GREEN);

            // create a data object with the datasets
            LineData data = new LineData(xVals, set1);
            data.setDrawValues(false);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(0,set1);

            //
            // Draw a line
            //
            ArrayList<Entry> lineVals1 = new ArrayList<>();
            lineVals1.add(new Entry(firstPoint, 0));
            lineVals1.add(new Entry(lastPoint + Float.valueOf(ai.getHauteur()), lastI));

            LineDataSet set2 = new LineDataSet(lineVals1, "Line Dataset");
            set2.setLineWidth(2f);
            set2.setColor(Color.RED);

            dataSets.add(1, set2);

            // create a data object with the datasets
            data = new LineData(xVals, dataSets);

            // set data
            mChart.setData(data);

            // Title chart
            txtTitleChart.setText("Elevation profile - Me -> RNC " + tel.getLoggedRnc().get_real_rnc());


        } catch (Exception e) {
            String msg = "Erreur lors de la récupération des données profile";
            HttpLog.send(TAG, e, msg);
            Log.d(TAG, msg + e.toString());
        } finally {
            loadingChart.setVisibility(View.GONE);
        }
    }

    public void updateGraph() {
        mChart.invalidate();
    }
}
