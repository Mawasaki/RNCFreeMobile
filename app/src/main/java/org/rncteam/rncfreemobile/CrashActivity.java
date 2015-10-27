package org.rncteam.rncfreemobile;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.rncteam.rncfreemobile.tasks.CrashReportTask;

/**
 * Created by cedricf_25 on 06/10/2015.
 */
public class CrashActivity extends Activity {
    private static final String TAG = "ExportLogsActivity";

    TextView txtPhone;
    TextView txtVersionAndroid;
    Button btn_crash;

    Throwable crash;
    String thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crash);

        // Get UI
        txtPhone = (TextView)findViewById(R.id.txt_crash_info1);
        txtVersionAndroid = (TextView)findViewById(R.id.txt_crash_info2);
        btn_crash = (Button)findViewById(R.id.btn_crash);

        crash = (Throwable) getIntent().getSerializableExtra("crashObject");
        thread = (String) getIntent().getSerializableExtra("crashThread");

        txtPhone.setText("Phone: " + android.os.Build.MODEL);
        txtVersionAndroid.setText("Android Version: " + android.os.Build.VERSION.RELEASE);

        final Activity f_activity = this;

        btn_crash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrashReportTask crt = new CrashReportTask(rncmobile.getAppContext(), crash, thread.toString());
                crt.setActivity(f_activity);
                crt.execute();
            }
        });
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();

        finish();
    }
}
