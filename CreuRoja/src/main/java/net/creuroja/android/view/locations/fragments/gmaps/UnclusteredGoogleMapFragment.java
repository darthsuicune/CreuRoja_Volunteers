package net.creuroja.android.view.locations.fragments.gmaps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.creuroja.android.model.locations.Location;
import net.creuroja.android.model.locations.LocationType;
import net.creuroja.android.model.locations.Locations;
import net.creuroja.android.view.locations.fragments.LocationsHandlerFragment;
import net.creuroja.android.view.locations.fragments.MapFragmentHandler;

import java.util.HashMap;
import java.util.Map;

public class UnclusteredGoogleMapFragment extends SupportMapFragment implements MapFragmentHandler,
        LocationsHandlerFragment.OnLocationsListUpdated {
    GoogleMap map;
    MapInteractionListener listener;

    Locations locations;
    Map<Location, Marker> currentMarkers = new HashMap<>();
    SharedPreferences prefs;
    DirectionsDrawnListener directionsListener;

    int currentZoom;

    public UnclusteredGoogleMapFragment() {
        super();
    }

    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (MapInteractionListener) activity;
            directionsListener = (DirectionsDrawnListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString() + " must implement MapInteractionListener");
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);
        initMap(savedInstanceState);
        return v;
    }

    private void initMap(Bundle savedInstanceState) {
            map = getMap();
            setMapOptions();
        currentZoom = DEFAULT_ZOOM;
        moveCameraTo(new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE));
    }

    private void setMapOptions() {
        UiSettings settings = map.getUiSettings();
        settings.setCompassEnabled(false);
        settings.setRotateGesturesEnabled(true);
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(true);
        settings.setScrollGesturesEnabled(true);
        settings.setTiltGesturesEnabled(true);
    }

    private void moveCameraTo(LatLng location) {
        CameraPosition.Builder cameraBuilder = new CameraPosition.Builder();
        cameraBuilder.target(location).zoom(currentZoom);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraBuilder.build()));
    }

    @Override public void setMapType(MapType mapType) {

    }

    @Override public void getDirections(android.location.Location origin,
                                        Location destination) {

    }

    @Override public void activateLocationsOfType(LocationType type) {

    }

    @Override public void deactivateLocationsOfType(LocationType type) {

    }

    @Override public boolean locate(android.location.Location location) {
        return false;
    }

    @Override public Fragment fragment() {
        return this;
    }

    @Override public void removeDirections() {

    }

    @Override public boolean hasDirections() {
        return false;
    }

    @Override public void setMapInteractionListener(
            MapInteractionListener mapInteractionListener) {

    }

    @Override public void onLocationsListUpdated(Locations list) {
        this.locations = list;
        drawMarkers();
    }

    private void drawMarkers() {
        map.clear();
        for(Location location : this.locations) {
            MarkerOptions options = new MarkerOptions();
            if(location.type != LocationType.NONE) {
                options.icon(BitmapDescriptorFactory.fromResource(location.type.icon));
            }
            options.position(new LatLng(location.latitude, location.longitude));
            options.title(location.name);
            map.addMarker(options);
        }
    }
}
