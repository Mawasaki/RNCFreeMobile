package org.rncteam.rncfreemobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


/**
 * Created by cedricf_25 on 15/02/2015.
 */

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static final String TAG = "MainActivity";

    // Declaring Your View and Variables
    private Toolbar mToolbar;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        sp = rncmobile.getPreferences();
        if(sp.getBoolean("screen", true)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        stopMonitorService();
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        // Service

        launchMonitorService();
    }

    public void launchMonitorService() {
        if(sp.getBoolean("background_service", true)) {
            Intent i = new Intent(this, MonitorService.class);
            i.putExtra("foo", "bar");
            startService(i);
        }
    }

    public void stopMonitorService() {
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
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
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

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }
}
