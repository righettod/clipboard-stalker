package eu.righettod.clipboardstalker;

import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "ClipboardStalker";
    private ScheduledThreadPoolExecutor scheduler;
    private ScheduledFuture task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Access to the clipboard manager
        final ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //Get component
        final TextView capturedDataView = findViewById(R.id.capturedData);
        capturedDataView.setMovementMethod(new ScrollingMovementMethod());
        //Get delay
        final EditText watchDelay = findViewById(R.id.watchDelay);
        //Affect event listeners
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                capturedDataView.setText("");
            }
        });
        ToggleButton captureButton = findViewById(R.id.captureButton);
        captureButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Runnable stalkerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            new StalkingTask(clipboard, capturedDataView).execute();
                        }
                    };
                    scheduler = new ScheduledThreadPoolExecutor(1);
                    if(watchDelay.getText().length() == 0){
                        watchDelay.setText("5");
                    }
                    long delay = Long.parseLong(watchDelay.getText().toString());
                    task = scheduler.scheduleWithFixedDelay(stalkerRunnable, 1, delay, TimeUnit.SECONDS);
                } else {
                    if (scheduler != null && !scheduler.isShutdown()) {
                        scheduler.shutdown();
                    }
                    if (task != null && !task.isCancelled()) {
                        task.cancel(false);
                    }
                }
            }
        });

        AppCenter.start(getApplication(), "3031a4b4-8f17-4928-98ed-d25b33531238", Analytics.class, Crashes.class);
    }
}
