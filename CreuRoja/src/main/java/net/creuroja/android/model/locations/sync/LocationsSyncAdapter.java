package net.creuroja.android.model.locations.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import net.creuroja.android.R;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.locations.LocationFactory;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.model.webservice.CRWebServiceClient;
import net.creuroja.android.model.webservice.ClientConnectionListener;
import net.creuroja.android.model.locations.RailsLocationsResponseFactory;
import net.creuroja.android.model.webservice.RailsWebServiceClient;
import net.creuroja.android.model.webservice.auth.AccountUtils;
import net.creuroja.android.model.webservice.Response;
import net.creuroja.android.model.webservice.util.RestWebServiceClient;
import net.creuroja.android.view.users.activities.LoginActivity;

import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LocationsSyncAdapter extends AbstractThreadedSyncAdapter
        implements ClientConnectionListener {
    private static final String SYNC_ADAPTER_TAG = "CreuRoja SyncAdapter";
    private final AccountManager accountManager;
    Context context;
    SharedPreferences prefs;
    Account account;
    String accessToken;

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
        this.account = account;
        if (Settings.isConnected(context)) {
            try {
                RestWebServiceClient restClient =
                        new RestWebServiceClient(
                                new RailsLocationsResponseFactory(getContext().getContentResolver()),
                                RailsWebServiceClient.PROTOCOL,
                                RailsWebServiceClient.URL);
                CRWebServiceClient client = new RailsWebServiceClient(restClient, this);

                accessToken = accountManager
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
    public void onValidResponse(Response response) {
        Locations locations;
        try {
            String time = currentTime();
            locations = LocationFactory.fromWebResponse(response.content(), prefs);
            locations.save(context.getContentResolver());
            prefs.edit().putString(Settings.LAST_UPDATE_TIME, time).apply();
        } catch (IOException | JSONException | ParseException e) {
            onErrorResponse(500, R.string.error_invalid_response);
            e.printStackTrace();
        }
    }

    private String currentTime() {
        Calendar calendar = new GregorianCalendar();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd H:m:s");
        return format.format(calendar.getTime());
    }

    @Override
    public void onErrorResponse(int code, int errorResId) {
        Log.d(SYNC_ADAPTER_TAG, getContext().getString(errorResId));
        if (code == 401) {
            Settings.clean(prefs, context.getContentResolver());
            Settings.removeAccount(accountManager, accessToken);
        }
    }
}
