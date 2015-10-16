package org.rncteam.rncfreemobile.listeners;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.rncteam.rncfreemobile.rncmobile;

/**
 * Created by cedricf_25 on 15/10/2015.
 */
public class MonitorServiceReceiver extends BroadcastReceiver {

    final static String ACTION = "MonitorServiceAction";
    final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        int rqs = arg1.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);

        if (rqs == RQS_STOP_SERVICE) {

            ((NotificationManager) rncmobile.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE))
                    .cancelAll();
        }
    }
}