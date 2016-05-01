package org.rncteam.rncfreemobile.classes;

import org.rncteam.rncfreemobile.tasks.CrashReportTask;

/**
 * Created by cedricf_25 on 28/10/2015.
 */
public class HttpLog {

    public HttpLog() {

    }

    static public void send(final String TAG, final Throwable exception, final String message) {
        CrashReportTask crt = new CrashReportTask(TAG, exception, message);
        crt.execute();
    }
}
