package de.gregoryseibert.wetter.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by gs71756 on 14.10.2016.
 */

public class ForecastSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static ForecastSyncAdapter forecastSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("ForecastSyncService", "onCreate - ForecastSyncService");
        synchronized (syncAdapterLock) {
            if (forecastSyncAdapter == null) {
                forecastSyncAdapter = new ForecastSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return forecastSyncAdapter.getSyncAdapterBinder();
    }
}
