package org.rncteam.rncfreemobile.classes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import org.rncteam.rncfreemobile.InfosFragment;
import org.rncteam.rncfreemobile.LogsFragment;
import org.rncteam.rncfreemobile.MonitorFragment;
import org.rncteam.rncfreemobile.MapsFragment;

/**
 * Created by cedricf_25 on 15/02/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm,CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        if(position == 0) {
            MonitorFragment f_monitor = new MonitorFragment();
            return f_monitor;
        }
        else if(position == 1) {
            LogsFragment f_logs = new LogsFragment();
            return f_logs;
        }
        else if(position == 2) {
            InfosFragment f_infos = new InfosFragment();
            return f_infos;
        }
        else {
            MapsFragment f_maps = new MapsFragment();
            return f_maps;
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}
