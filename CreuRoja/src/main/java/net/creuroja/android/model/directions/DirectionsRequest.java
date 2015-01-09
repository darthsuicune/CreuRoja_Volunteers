package net.creuroja.android.model.directions;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by lapuente on 02.01.15.
 */
public class DirectionsRequest {
    public static final String DIRECTIONS_API_BASE_URL =
            "https://maps.googleapis.com/maps/api/directions/json?region=es&";
    public static final String ORIGIN_URL = "origin=";
    public static final String DESTINATION_URL = "destination=";
    public static final String SENSOR_URL = "sensor=";

    public String url;
    public DirectionsResponse response;
    public LatLng origin;
    public LatLng destination;

    public DirectionsRequest(LatLng origin, LatLng destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public List<DirectionsRoute> makeRequest() {
        getUrl();
        connect();
        return response.routes();
    }

    private DirectionsResponse connect() {
        //TODO: implement
        return null;
    }

    private void getUrl() {
        url = DIRECTIONS_API_BASE_URL + ORIGIN_URL + Double.toString(origin.latitude) + "," +
                Double.toString(origin.longitude) + DESTINATION_URL +
                Double.toString(destination.latitude) + "," +
                Double.toString(destination.longitude) + "sensor=true";
    }

}
