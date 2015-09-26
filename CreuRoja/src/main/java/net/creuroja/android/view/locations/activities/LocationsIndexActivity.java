package net.creuroja.android.view.locations.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.dlgdev.directions.Directions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

import net.creuroja.android.R;
import net.creuroja.android.dagger.DaggerLocationsActivityComponent;
import net.creuroja.android.dagger.LocationsActivityComponent;
import net.creuroja.android.dagger.LocationsActivityModule;
import net.creuroja.android.model.Settings;
import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.webservice.auth.AccountsHelper;
import net.creuroja.android.model.webservice.auth.AccountsHelper.LoginManager;
import net.creuroja.android.view.general.activities.SettingsActivity;
import net.creuroja.android.view.locations.OnDirectionsRequestedListener;
import net.creuroja.android.view.locations.ViewMode;
import net.creuroja.android.view.locations.fragments.LocationCardFragment;
import net.creuroja.android.view.locations.fragments.LocationDetailFragment;
import net.creuroja.android.view.locations.fragments.LocationListFragment;
import net.creuroja.android.view.locations.fragments.LocationListFragment.LocationsListListener;
import net.creuroja.android.view.locations.fragments.LocationsDrawerFragment;
import net.creuroja.android.view.locations.fragments.LocationsHandlerFragment;
import net.creuroja.android.view.locations.fragments.MapFragmentHandler;
import net.creuroja.android.view.locations.fragments.MapFragmentHandler.DirectionsDrawnListener;
import net.creuroja.android.view.locations.fragments.MapFragmentHandlerFactory;
import net.creuroja.android.view.users.activities.UserProfileActivity;

import javax.inject.Inject;

import static net.creuroja.android.view.locations.fragments.LocationCardFragment.OnLocationCardInteractionListener;
import static net.creuroja.android.view.locations.fragments.LocationDetailFragment.OnLocationDetailsListener;
import static net.creuroja.android.view.locations.fragments.LocationsDrawerFragment.MapNavigationDrawerCallbacks;
import static net.creuroja.android.view.locations.fragments.gmaps.ClusteredGoogleMapFragment.MapInteractionListener;

public class LocationsIndexActivity extends AppCompatActivity implements LoginManager,
        MapNavigationDrawerCallbacks, LocationsListListener, OnLocationCardInteractionListener,
        MapInteractionListener, OnDirectionsRequestedListener, OnLocationDetailsListener,
        DirectionsDrawnListener, ConnectionCallbacks, OnConnectionFailedListener {
    private static final String TAG_MAP = "CreuRojaMap";
    private static final String TAG_LIST = "CreuRojaLocationList";
    private static final String TAG_HANDLER = "CreuRojaLocationsHandler";
    private static final String TAG_CARD = "CreuRojaLocationCard";
    private static final String TAG_DETAILS = "CreuRojaDetail";
    private static final int REQUEST_RESOLVE_ERROR = 1234;
    private static final String DIALOG_ERROR = "dialog error";

    // This are the fragments that the activity handles
    LocationsDrawerFragment locationsDrawerFragment;
    MapFragmentHandler mapFragmentHandler;
    LocationListFragment listFragment;
    LocationCardFragment cardFragment;
    LocationsHandlerFragment locationsHandlerFragment;

    ViewMode currentViewMode;

    @Inject SharedPreferences prefs;
    @Inject GoogleApiClient client;
    @Inject AccountsHelper helper;
    boolean resolvingError;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationsActivityComponent component = DaggerLocationsActivityComponent.builder()
                .locationsActivityModule(new LocationsActivityModule(this, this, this))
                .build();
        component.inject(this);
        if (savedInstanceState != null && helper.getAccount() != null) {
            successfulLogin();
        } else {
            helper.requestLogin(this);
        }
    }

    // Callbacks for when the auth token is returned
    @Override public void successfulLogin() {
        client.connect();
        startUi();
    }

    @Override public void failedLogin() {
        this.finish();
    }

    private void startUi() {
        if (currentViewMode == null) {
            int preferredMode = prefs.getInt(Settings.VIEW_MODE, ViewMode.MAP.getValue());
            currentViewMode = ViewMode.getViewMode(preferredMode);
        }
        setContentView(R.layout.activity_locations);

        Toolbar toolbar = (Toolbar) findViewById(R.id.location_index_toolbar);
        setSupportActionBar(toolbar);

        FragmentManager manager = getSupportFragmentManager();
        setupDrawer(manager);
        setupLocationDataHandler(manager);
        setMainFragment();
    }

    private void setupDrawer(FragmentManager manager) {
        locationsDrawerFragment =
                (LocationsDrawerFragment) manager.findFragmentById(R.id.navigation_drawer);
        // Set up the drawer.
        locationsDrawerFragment
                .setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void setupLocationDataHandler(FragmentManager manager) {
        locationsHandlerFragment =
                (LocationsHandlerFragment) manager.findFragmentByTag(TAG_HANDLER);
        if (locationsHandlerFragment == null) {
            locationsHandlerFragment = LocationsHandlerFragment.newInstance();
            locationsHandlerFragment.setRetainInstance(true);
            manager.beginTransaction().add(locationsHandlerFragment, TAG_HANDLER).commit();
        }
        locationsHandlerFragment.registerListener(locationsDrawerFragment);
    }

    @Override public void onViewModeChanged(ViewMode newViewMode) {
        if (newViewMode == currentViewMode) {
            return;
        }
        currentViewMode = newViewMode;
        setMainFragment();
    }

    private void setMainFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (currentViewMode) {
            case LIST:
                loadListFragment(fragmentManager, transaction);
                locationsHandlerFragment.registerListener(listFragment);
                break;
            case MAP:
            default:
                loadMapFragment(fragmentManager, transaction);
                locationsHandlerFragment.registerListener(mapFragmentHandler);
                break;
        }
        transaction.commit();
        if (Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation) {
            //noinspection ResourceType
            findViewById(R.id.location_details_container)
                    .setVisibility(currentViewMode.getDetailsBlockVisibility());
            //noinspection ResourceType
            findViewById(R.id.locations_index_separator)
                    .setVisibility(currentViewMode.getDetailsBlockVisibility());
        }
    }

    private void loadListFragment(FragmentManager manager, FragmentTransaction transaction) {
        if (listFragment == null) {
            listFragment = (LocationListFragment) manager.findFragmentByTag(TAG_LIST);
            if (listFragment == null) {
                listFragment = LocationListFragment.newInstance();
            }
        }
        if (cardFragment != null && cardFragment.isVisible()) {
            transaction.remove(cardFragment);
        }
        transaction.replace(R.id.locations_container, listFragment, TAG_LIST);
    }

    private void loadMapFragment(FragmentManager manager, FragmentTransaction transaction) {
        if (mapFragmentHandler == null) {
            mapFragmentHandler = (MapFragmentHandler) manager.findFragmentByTag(TAG_MAP);
            if (mapFragmentHandler == null) {
                mapFragmentHandler = MapFragmentHandlerFactory.CLUSTERED.build();
            }
            mapFragmentHandler.setMapInteractionListener(this);
        }
        transaction.replace(R.id.locations_container, mapFragmentHandler.fragment(), TAG_MAP);
    }

    @Override public void onLocationListItemSelected(Location location) {
        openLocationDetails(location);
    }

    private void openLocationDetails(Location location) {
        //Viewing the list in portrait, or the map, a new activity must be launched
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                || currentViewMode == ViewMode.MAP) {
            openDetailsActivityFor(location);
        } else {
            openDetailsFragment(location);
        }
    }

    private void openDetailsActivityFor(Location location) {
        Intent intent = new Intent(this, LocationDetailsActivity.class);
        intent.putExtra(LocationDetailsActivity.EXTRA_LOCATION_ID, location.remoteId);
        startActivity(intent);

    }

    private void openDetailsFragment(Location location) {
        FragmentManager manager = getSupportFragmentManager();
        LocationDetailFragment fragment =
                (LocationDetailFragment) manager.findFragmentByTag(TAG_DETAILS);
        if (fragment == null) {
            fragment = LocationDetailFragment.newInstance(location.remoteId);
            manager.beginTransaction()
                    .add(R.id.location_details_container, fragment, TAG_DETAILS).commit();
        } else {
            fragment.setLocation(location);
        }

    }

    @Override public void onCardDetailsRequested(Location location) {
        openLocationDetails(location);
    }

    @Override public boolean onDirectionsRequested(Location destination) {
        if (ViewMode.LIST == currentViewMode) {
            onViewModeChanged(ViewMode.MAP);
        }
        android.location.Location origin = getCurrentLocation();
        if (origin != null) {
            mapFragmentHandler.getDirections(origin, destination);
            return true;
        }
        return false;
    }

    @Override public void onRemoveRouteRequested() {
        mapFragmentHandler.removeDirections();
    }

    @Override public boolean hasDirections() {
        return mapFragmentHandler.hasDirections();
    }

    @Override public void onCardCloseRequested() {
        getSupportFragmentManager().beginTransaction().remove(cardFragment).commit();
        cardFragment = null;
    }

    @Override public void onNavigationLegendItemActivated(LocationType type) {
        if (ViewMode.LIST == currentViewMode) {
            listFragment.activateLocations(type);
        } else {
            mapFragmentHandler.activateLocationsOfType(type);
        }

    }

    @Override public void onNavigationLegendItemDeactivated(LocationType type) {
        if (ViewMode.LIST == currentViewMode) {
            listFragment.deactivateLocations(type);
        } else {
            mapFragmentHandler.deactivateLocationsOfType(type);
        }
    }

    @Override public void onNavigationMapTypeSelected(MapFragmentHandler.MapType mapType) {
        if (ViewMode.MAP == currentViewMode) {
            mapFragmentHandler.setMapType(mapType);
        }
    }

    @Override public void onLocationClicked(Location location) {
        showLocationCard(location);
    }

    private void showLocationCard(Location location) {
        FragmentManager manager = getSupportFragmentManager();
        if (cardFragment == null) {
            cardFragment = (LocationCardFragment) manager.findFragmentByTag(TAG_CARD);
            if (cardFragment == null) {
                cardFragment = LocationCardFragment.newInstance(location);
            }
            manager.beginTransaction().replace(R.id.location_card_container, cardFragment, TAG_CARD)
                    .commit();
        }
        cardFragment.setLocation(location);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        if (locationsDrawerFragment != null && !locationsDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen if the drawer is not
            // showing. Otherwise, let the drawer decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.locate, menu);
            getMenuInflater().inflate(R.menu.global, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_view_profile:
                viewProfile();
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_locate:
                locate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewProfile() {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    private void locate() {
        android.location.Location location = getCurrentLocation();
        if (location != null && currentViewMode == ViewMode.MAP) {
            mapFragmentHandler.locate(location);
        }
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private android.location.Location getCurrentLocation() {
        android.location.Location location = null;
        if (areLocationServicesEnabled()) {
            location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (location == null) {
                Toast.makeText(getApplicationContext(), R.string.locating, Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            showLocationSettings();
        }
        return location;
    }

    private boolean areLocationServicesEnabled() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        return (lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showLocationSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.location_disabled_title);
        builder.setMessage(R.string.location_disabled_message);
        builder.setPositiveButton(R.string.open_location_settings,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        builder.setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }

    @Override public void onDirectionsDrawn(Directions directions) {
        cardFragment.onDirectionsDrawn(directions);
    }

    @Override public void onConnected(Bundle bundle) {
        //Nothing to do here
    }

    @Override public void onConnectionSuspended(int i) {
        //Nothing to do here
    }

    @Override public void onConnectionFailed(ConnectionResult result) {
        if (resolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                resolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                client.connect();
            }
        } else {
            resolvingError = true;
            showErrorDialog(result.getErrorCode());
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override public void onDismiss(DialogInterface dialog) {
            onDialogDismissed();
        }

        /* Called from ErrorDialogFragment when the dialog is dismissed. */
        public void onDialogDismissed() {
            ((LocationsIndexActivity)getActivity()).resolvingError = false;
        }
    }
}
