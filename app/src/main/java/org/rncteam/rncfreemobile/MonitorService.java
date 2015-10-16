package org.rncteam.rncfreemobile;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.rncteam.rncfreemobile.listeners.MonitorServiceReceiver;

/**
 * Created by cedricf_25 on 15/10/2015.
 */
public class MonitorService extends Service {
    private static final String TAG = "MonitorService";

    final static String ACTION = "MonitorServiceAction";
    final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;

    MonitorServiceReceiver monitorServiceReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        this.monitorServiceReceiver = new MonitorServiceReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(monitorServiceReceiver, intentFilter);

        // Send Notification
        String notificationTitle = "RNC Free Mobile";
        String notificationText = "Fort du salbert";

        /*
        Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(myBlog));
        PendingIntent pendingIntent
                = PendingIntent.getActivity(getBaseContext(),
                0, myIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);
*/


        Notification notification = new Notification.Builder(this)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText).setSmallIcon(R.mipmap.ic_launcher)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        this.unregisterReceiver(monitorServiceReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


}
