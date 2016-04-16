package org.rncteam.rncfreemobile;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.rncteam.rncfreemobile.classes.Maps;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.classes.Utils;


/**
 * Created by cedricf_25 on 15/02/2015.
 */

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static final String TAG = "MainActivity";

    private SharedPreferences sp;

    private ViewPager mPager;

    private static final int NUM_PAGES = 4;

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         if(checkPermissions()){
             setContentView(R.layout.activity_main);
             rncmobile.setMainActivity(this);
             // Instantiate a ViewPager and a PagerAdapter.
             mPager = (ViewPager) findViewById(R.id.pager);
             PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
             mPager.setAdapter(mPagerAdapter);

             Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

             // Init main classes
             rncmobile.setTel(new Telephony());
             // Initialize specific class
             rncmobile.setMaps(new Maps());

             setSupportActionBar(mToolbar);
             getSupportActionBar().setDisplayShowHomeEnabled(true);

             rncmobile.fragmentDrawer = (FragmentDrawer)
                     getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
             rncmobile.fragmentDrawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
             rncmobile.fragmentDrawer.setDrawerListener(this);

             sp = rncmobile.getPreferences();
             if(sp.getBoolean("screen", true)) {
                 getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
             }

             displayView(0);
             stopMonitorService();
         }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Telephony tel = rncmobile.getTelephony();
        tel.stopListenManager();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        launchMonitorService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_tabs, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onPause() {
        super.onPause();

        // Store pos in prefs
        Maps maps = rncmobile.getMaps();
        Utils.storeLastPos(String.valueOf(maps.getLastPosLat()),
                String.valueOf(maps.getLastPosLon()),
                String.valueOf(maps.getLastZoom()));
    }

    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sp = rncmobile.getPreferences();
        if(sp.getBoolean("screen", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        stopMonitorService();
        rncmobile.setMainActivity(this);
        displayView(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_settings) {
            Intent intentPA = new Intent(rncmobile.getAppContext(), SettingsActivity.class);
            startActivity(intentPA);
            return true;
        }

        if(id == R.id.action_quit) {
            finish();
            stopMonitorService();
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchMonitorService() {
        if(sp.getBoolean("background_service", true)) {
            Intent i = new Intent(this, MonitorService.class);
            i.putExtra("foo", "bar");
            startService(i);
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        // Service
        launchMonitorService();
    }

    private void stopMonitorService() {
        Intent intent = new Intent();
        intent.setAction(MonitorService.ACTION);
        intent.putExtra(MonitorService.STOP_SERVICE_BROADCAST_KEY,
                MonitorService.RQS_STOP_SERVICE);
        sendBroadcast(intent);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public void displayView(int position) {

        switch (position) {
            /*
            case 0:
                fragment = new MonitorFragment();
                title = getString(R.string.title_monitor);
                break;
            case 1:
                fragment = new LogsFragment();
                title = getString(R.string.title_logs);
                break;
            case 2:
                fragment = new InfosFragment();
                title = getString(R.string.title_infos);
                break;
            case 3:
                fragment = new MapsFragment();
                title = getString(R.string.title_maps);
                break;
                */
            case 4:
                Intent intentDA = new Intent(rncmobile.getAppContext(), DataActivity.class);
                startActivity(intentDA);
                break;
            case 5:
                Intent intentPA = new Intent(rncmobile.getAppContext(), SettingsActivity.class);
                startActivity(intentPA);
            default:
                break;
        }

        mPager.setCurrentItem(position);
    }

    public boolean checkPermissions(){
        if(((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
        return true;
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new MonitorFragment();
                    break;
                case 1:
                    fragment = new LogsFragment();
                    break;
                case 2:
                    fragment = new InfosFragment();
                    break;
                case 3:
                    fragment = new MapsFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
