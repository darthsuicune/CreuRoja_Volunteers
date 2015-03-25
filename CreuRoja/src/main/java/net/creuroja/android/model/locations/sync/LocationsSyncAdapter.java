package net.creuroja.android.model.locations.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import net.creuroja.android.R;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.locations.LocationFactory;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.model.webservice.CRWebServiceClient;
import net.creuroja.android.model.webservice.ClientConnectionListener;
import net.creuroja.android.model.webservice.RailsWebServiceClient;
import net.creuroja.android.model.webservice.auth.AccountUtils;
import net.creuroja.android.model.webservice.lib.RestWebServiceClient;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

public class LocationsSyncAdapter extends AbstractThreadedSyncAdapter
        implements ClientConnectionListener {
    private static final String SYNC_ADAPTER_TAG = "CreuRoja SyncAdapter";
    private final AccountManager accountManager;
    Context context;
    SharedPreferences prefs;

    public LocationsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context = context;
        accountManager = AccountManager.get(context);
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @param account               The account that triggers the sync
     * @param extras                Bundle with flags sent by the event
     * @param authority             Authority of the content provider
     * @param contentProviderClient Used for connection with the ContentProvider
     * @param syncResult            Object to send information back to the sync framework
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient contentProviderClient,
                              SyncResult syncResult) {
        if (Settings.isConnected(context)) {
            try {
                RestWebServiceClient restClient =
                        new RestWebServiceClient(getContext().getAssets().open("server.crt"),
                                RailsWebServiceClient.PROTOCOL,
                                RailsWebServiceClient.URL);
                CRWebServiceClient client = new RailsWebServiceClient(restClient, this);

                String accessToken = accountManager
                        .blockingGetAuthToken(account, AccountUtils.AUTH_TOKEN_TYPE, true);

                String lastUpdateTime = prefs.getString(Settings.LAST_UPDATE_TIME, "0");
                client.getLocations(accessToken, lastUpdateTime);
            } catch (OperationCanceledException e) {
                Log.i(SYNC_ADAPTER_TAG, "Synchronization cancelled by the user");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(SYNC_ADAPTER_TAG, "There was an error while getting the Auth token");
                e.printStackTrace();
            } catch (AuthenticatorException e) {
                Log.e(SYNC_ADAPTER_TAG, "Error authenticating");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onValidResponse(String response) {
        Locations locations;
        try {
            locations = LocationFactory.fromWebResponse(response, prefs);
            locations.save(context.getContentResolver());

            prefs.edit().putString(Settings.LAST_UPDATE_TIME, locations.getLastUpdateTime())
                    .apply();
        } catch (IOException | JSONException | ParseException e) {
            onErrorResponse(500, R.string.error_invalid_response);
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(int code, int errorResId) {
        Log.d(SYNC_ADAPTER_TAG, getContext().getString(errorResId));
        if (code == 401) {
            Settings.clean(prefs, context.getContentResolver());
        }
    }
}
