package org.rncteam.rncfreemobile;

import org.rncteam.rncfreemobile.tasks.CsvRncDownloader;
import org.rncteam.rncfreemobile.classes.Gps;
import org.rncteam.rncfreemobile.classes.ViewPagerAdapter;
import org.rncteam.rncfreemobile.view.SlidingTabLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Created by cedricf_25 on 15/02/2015.
 */

public class MainActivity extends ActionBarActivity {
    private static final String TAG = "MainActivity";

    // Declaring Your View and Variables

    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Monitor","Logs","Infos","Carte"};
    int Numboftabs  = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        SharedPreferences sp = rncmobile.getPreferences();
        if(sp.getBoolean("screen", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Gps gps = rncmobile.getGps();
        if(gps != null) {
            gps.disableGps();
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_tabs, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onPause() {
        super.onPause();
        //if(sp.getBoolean("screen", true)) rncmobile.powerRelease();
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem logToggle = menu.findItem(R.id.action_delete);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = rncmobile.getPreferences();
        if(sp.getBoolean("screen", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        Gps gps = rncmobile.getGps();

        if(!gps.gpsStatus()) rncmobile.setGps(new Gps(rncmobile.getAppContext()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intentPA = new Intent(rncmobile.getAppContext(), SettingsActivity.class);
            startActivity(intentPA);
            return true;
        }

        if (id == R.id.action_quit) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
