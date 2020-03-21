package eu.righettod.clipboardstalker;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * In charge of monitoring the clipboard. Only support text content.
 */
public class StalkingTask extends AsyncTask<Void, Integer, String> {

    private ClipboardManager clipboardManager;
    private TextView capturedDataView;

    public StalkingTask(ClipboardManager clipboardManager, TextView capturedDataView) {
        this.clipboardManager = clipboardManager;
        this.capturedDataView = capturedDataView;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.trim().isEmpty()) {
            this.capturedDataView.append(s);
            this.capturedDataView.append("\n");
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        String data = null;
        try {
            if (this.clipboardManager.hasPrimaryClip() && this.clipboardManager.getPrimaryClipDescription() != null && (this.clipboardManager.getPrimaryClipDescription().hasMimeType("text/plain") || this.clipboardManager.getPrimaryClipDescription().hasMimeType("text/html"))) {
                ClipData clip = this.clipboardManager.getPrimaryClip();
                StringBuilder buffer = new StringBuilder();
                for (int i = 0; i < clip.getItemCount(); i++) {
                    buffer.append(clip.getItemAt(i).getText());
                }
                data = buffer.toString();
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "Error during catching!", e);
        }

        return data;
    }
}
