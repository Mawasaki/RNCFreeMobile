package org.rncteam.rncfreemobile.classes;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
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
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.rncmobile;
import org.rncteam.rncfreemobile.tasks.ProfileTask;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by cedricf_25 on 12/10/2015.
 */

public class Elevation implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "Elevation";

    private Activity activity;
    private Utils utils;
    private Telephony tel;

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView txtTitleChart;
    private RelativeLayout loadingChart;

    private Typeface tf;

    public boolean dataOk;
    public JSONArray jData;

    private Handler handler;

    private Double myLat;
    private Double myLon;
    private Double rncLat;
    private Double rncLon;

    public Elevation(Activity activity) {
        handler = new Handler();
        this.activity = activity;
        this.utils = new Utils();
        this.dataOk = false;
        this.tel = rncmobile.getTelephony();
    }

    public void initChart() {

        txtTitleChart = (TextView) activity.findViewById(R.id.txtTitleChart);
        loadingChart = (RelativeLayout) activity.findViewById(R.id.loadingPanelChart);
/*
        mSeekBarX = (SeekBar) activity.findViewById(R.id.seekBar1);
        mSeekBarY = (SeekBar) activity.findViewById(R.id.seekBar2);

        mSeekBarX.setProgress(45);
        mSeekBarY.setProgress(100);

        mSeekBarY.setOnSeekBarChangeListener(this);
        mSeekBarX.setOnSeekBarChangeListener(this);
*/
        mChart = (LineChart) activity.findViewById(R.id.chart_profile);
        mChart.setViewPortOffsets(10, 20, 10, 10);
        //mChart.setBackgroundColor(Color.rgb(104, 241, 175));

        // no description text
        mChart.setDescription("");

        // enable value highlighting
        mChart.setHighlightEnabled(true);

        mChart.setAutoScaleMinMaxEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        mChart.setPadding(10,10,10,10);

        XAxis x = mChart.getXAxis();
        x.setTypeface(tf);
        x.setTextColor(Color.BLUE);
        x.setPosition(XAxis.XAxisPosition.TOP_INSIDE);

        YAxis y = mChart.getAxisLeft();
        y.setTypeface(tf);
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

        // Start timer
        waitResult.run();
    }

    private void setData() {
        try {
            ArrayList<String> xVals = new ArrayList<String>();

            LatLng myLoc = new LatLng(myLat,myLon);
            LatLng btsLoc = new LatLng(rncLat, rncLon);

            Double distance = utils.calculationByDistance(myLoc, btsLoc);
            DecimalFormat mFormat = new DecimalFormat("##");
            double meter = distance * 1000;

            Float x = new Float(Math.round(meter));
            int increment = x.intValue()/100;

            int firstX = 0;
            int lastX = 0;
            for (int i = 0; i <= meter; i+=increment) {
                xVals.add((i) + "");
                if(i==0) firstX = i;
                lastX = i;
            }

            ArrayList<Entry> vals1 = new ArrayList<Entry>();

            float firstPoint = 0;
            float lastPoint = 0;
            int firtI = 0;
            int lastI = 0;
            for (int i = 0; i < jData.length(); i++) {
                // Gets poitns
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
            AnfrInfos ai = tel.getAnfrInfos();
            vals1.add(new Entry(lastPoint + Float.valueOf(ai.getHauteur()), lastI));

            LineDataSet set1 = new LineDataSet(vals1, "Profile Dataset");
            set1.setLineWidth(1.8f);
            set1.setColor(Color.GREEN);

            // create a data object with the datasets
            LineData data = new LineData(xVals, set1);
            //data.setValueTypeface(tf);
            //data.setValueTextSize(9f);
            data.setDrawValues(false);

            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(0,set1);

            //
            // Draw a line
            //
            // For X
            ArrayList<String> linexVals = new ArrayList<String>();

            linexVals.add((firstX) + "");
            linexVals.add((lastX) + "");

            // For Y
            ArrayList<Entry> lineVals1 = new ArrayList<Entry>();
            lineVals1.add(new Entry(firstPoint, 0));
            lineVals1.add(new Entry(lastPoint + Float.valueOf(ai.getHauteur()), lastI));

            LineDataSet set2 = new LineDataSet(lineVals1, "Line Dataset");
            set2.setLineWidth(2f);
            set2.setColor(Color.RED);

            dataSets.add(1, set2);

            // create a data object with the datasets
            data = new LineData(xVals, dataSets);
            /*
            data2.setValueTypeface(tf);
            data2.setValueTextSize(9f);
            data2.setDrawValues(false);
            */

            // set data
            mChart.setData(data);

            // Title chart
            txtTitleChart.setText("Elevation profile - Me -> RNC " + tel.getLoggedRnc().get_real_rnc());

            loadingChart.setVisibility(View.GONE);

        } catch (Exception e) {

        }
    }

/*
    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((1990 +i) + "");
        }

        ArrayList<Map.Entry> vals1 = new ArrayList<Map.Entry>();

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 20;// + (float)
            // ((mult *
            // 0.1) / 10);
            vals1.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(vals1, "DataSet 1");
        set1.setLineWidth(1.8f);
        set1.setColor(Color.BLACK);
        /*
        set1.setDrawHorizontalHighlightIndicator(false);
        set1.setFillFormatter(new FillFormatter() {
            @Override
            public float getFillLinePosition(LineDataSet dataSet, LineDataProvider dataProvider) {
                return -10;
            }
        });

        // create a data object with the datasets
        LineData data = new LineData(xVals, set1);
        data.setValueTypeface(tf);
        data.setValueTextSize(9f);
        data.setDrawValues(false);

        // set data
        mChart.setData(data);
    }
*/
    private Runnable waitResult = new Runnable() {
        public void run() {
            if(dataOk) {
                // Set resultset
                setData();
                mChart.invalidate();
                dataOk = false;
                handler.removeCallbacks(waitResult);
            }

            handler.postDelayed(this, 1000);
        }
    };
}
