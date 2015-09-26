package net.creuroja.android.dagger;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import net.creuroja.android.model.webservice.auth.AccountsHelper;

import dagger.Module;
import dagger.Provides;

@Module
public class LocationsActivityModule {
    Activity activity;
    GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener failedListener;

    public LocationsActivityModule(Activity activity, GoogleApiClient.ConnectionCallbacks connectionCallbacks,
                                   GoogleApiClient.OnConnectionFailedListener failedListener) {
        this.activity = activity;
        this.connectionCallbacks = connectionCallbacks;
        this.failedListener = failedListener;
    }

    @Provides AccountsHelper provideAccountsHelper() {
        return new AccountsHelper(activity);
    }

    @Provides GoogleApiClient provideClient() {
        return new GoogleApiClient.Builder(activity).addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks).addOnConnectionFailedListener(failedListener).build();
    }

    @Provides SharedPreferences providePrefs() {
        return PreferenceManager.getDefaultSharedPreferences(activity);
    }
}
