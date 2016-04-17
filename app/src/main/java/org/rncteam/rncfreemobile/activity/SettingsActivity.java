package org.rncteam.rncfreemobile.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import org.rncteam.rncfreemobile.R;

/**
 * Created by cedricf_25 on 12/10/2015.
 */
public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = "SettingsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        finish();
    }
}
