package de.gregoryseibert.wetter.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Gregory Seibert on 14.10.2016.
 */

public class ForecastSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static ForecastSyncAdapter forecastSyncAdapter = null;

    @Override
    public void onCreate() {
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
