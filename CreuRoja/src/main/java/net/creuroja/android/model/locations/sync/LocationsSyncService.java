package net.creuroja.android.model.locations.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocationsSyncService extends Service {
	private static final Object sSyncAdapterLock = new Object();
	private static LocationsSyncAdapter sSyncAdapter = null;
    public LocationsSyncService() {
    }

	@Override public void onCreate() {
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new LocationsSyncAdapter(getApplicationContext(), true);
			}
		}
		super.onCreate();

	}

	@Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
