package org.rncteam.rncfreemobile.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.telephony.PhoneStateListener;

import org.rncteam.rncfreemobile.R;
import org.rncteam.rncfreemobile.classes.HttpLog;
import org.rncteam.rncfreemobile.classes.Telephony;
import org.rncteam.rncfreemobile.models.Rnc;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by cedricf_25 on 15/10/2015.
 */
public class MonitorService extends Service {
    private static final String TAG = "MonitorService";

    final static String ACTION = "MonitorServiceAction";
    final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;

    private Handler handler;

    private Telephony tel;
    private boolean tempPass;

    private MonitorServiceReceiver monitorServiceReceiver;

    private final Service thisService = this;

    private Rnc oldRnc;

    private Bitmap logoBitmap = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");

        this.monitorServiceReceiver = new MonitorServiceReceiver();

        tel = rncmobile.getTelephony();
        tempPass = true;

        handler = new Handler();
        taskMonitor.run();

        //logoBitmap = getBitmapFromURL("http://rfm.dataremix.fr/logo.png");
    }

    /**
     * Display a notification in the notification bar.
     */
    private final Runnable taskMonitor = new Runnable() {
        public void run() {
            try {
            if (tel != null && tel.getLoggedRnc() != null &&
                    (tempPass || oldRnc.get_id() != tel.getLoggedRnc().get_id())) {

                // Main intent
                PendingIntent contentIntent = PendingIntent.getActivity(thisService, 0, new Intent(thisService, MainActivity.class), 0);

                // Cancel intent
                Intent cancelReceive = new Intent();
                cancelReceive.setAction(ACTION);
                PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(thisService, 12345, cancelReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                if (tel.getLoggedRnc() != null) {

                    Notification notification;
                    if (tel.getNetworkClass() == 2) {
                        notification = new Notification.Builder(thisService)
                                .setContentTitle(String.valueOf("RNC Free mobile"))
                                .setContentText("Edge is not monitored")
                                .setSmallIcon(R.drawable.ic_notif_small_icon)
                                .setLargeIcon(logoBitmap)
                                .setAutoCancel(true)
                                .setContentIntent(contentIntent).build();
                    } else if (tel.getNetworkClass() == 3 || tel.getNetworkClass() == 4) {
                        notification = new Notification.Builder(thisService)
                                .setContentTitle(String.valueOf(tel.getLoggedRnc().getNetworkName()
                                        + " (" + ((tel.getLoggedRnc().get_tech() == 3) ? "3G" : "4G") + ") ")
                                        + (tel.getLoggedRnc().get_rnc() + ":")
                                        + (tel.getLoggedRnc().get_cid() + " | ")
                                        + ((tel.getLoggedRnc().get_tech() == 3 ? tel.getLoggedRnc().getUmtsRscp() + " dBm" : ""))
                                        + ((tel.getLoggedRnc().get_tech() == 4 ? tel.getLoggedRnc().getLteRsrp() + " dBm" : "")))
                                .setContentText(tel.getLoggedRnc().get_txt())
                                .setSmallIcon(R.drawable.ic_notif_small_icon)
                                .setLargeIcon(logoBitmap)
                                .setAutoCancel(true)
                                .addAction(R.drawable.ic_clear_black, "Cancel Monitoring", pendingIntentCancel)
                                .setContentIntent(contentIntent).build();
                    } else {
                        notification = new Notification.Builder(thisService)
                                .setContentTitle(String.valueOf("RNC Free mobile"))
                                .setContentText("No connection")
                                .setSmallIcon(R.drawable.ic_notif_small_icon)
                                .setLargeIcon(logoBitmap)
                                .setContentIntent(contentIntent).build();
                    }
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notification.flags = notification.flags
                            | Notification.FLAG_ONGOING_EVENT;
                    notification.flags |= Notification.FLAG_AUTO_CANCEL;

                    mNotificationManager.notify(0, notification);

                    handler.postDelayed(this, 3000);
                }
                oldRnc = tel.getLoggedRnc();
                tempPass = false;
            }
        } catch (Exception e) {
                String msg = "Erreur dans monitorService";
                HttpLog.send(TAG, e, msg);
                Log.d(TAG, msg + e.toString());
                Intent intent = new Intent();
                intent.setAction(MonitorService.ACTION);
                intent.putExtra(MonitorService.STOP_SERVICE_BROADCAST_KEY,
                        MonitorService.RQS_STOP_SERVICE);
                sendBroadcast(intent);
            }
        }

    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Received start id " + startId + ": " + intent);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(this.monitorServiceReceiver, intentFilter);

        return START_STICKY; // Run until explicitly stopped.
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(taskMonitor);
        this.unregisterReceiver(monitorServiceReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public class MonitorServiceReceiver extends BroadcastReceiver {
        final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
        final static int RQS_STOP_SERVICE = 1;

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            int rqs = arg1.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);
            String action = arg1.getAction();

            if (rqs == RQS_STOP_SERVICE) {
                stopSelf();
                ((NotificationManager) rncmobile.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancelAll();
            }

            if(ACTION.equals(action)) {
                stopSelf();
                ((NotificationManager) rncmobile.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE))
                        .cancelAll();
            }
        }
    }
}
