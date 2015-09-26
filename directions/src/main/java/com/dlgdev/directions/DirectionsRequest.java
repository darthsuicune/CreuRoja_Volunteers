package com.dlgdev.directions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class DirectionsRequest {
    public static final String DIRECTIONS_API_BASE_URL =
            "https://maps.googleapis.com/maps/api/directions/json?region=es&";
    public static final String ORIGIN_URL = "origin=";
    public static final String DESTINATION_URL = "destination=";

    private List<DirectionsRoute> routes;

    HttpURLConnection connection;

    public DirectionsRequest() {

    }

    public List<DirectionsRoute> make(double originLat, double originLng, double destinationLat,
                                      double destinationLng) {
        try {
            buildConnectionUrl(originLat, originLng, destinationLat, destinationLng);
            DirectionsResponse response = connect();
            routes = response.routes();
        } catch (IOException e) {
            throw new DirectionsException(e);
        }
        return routes;
    }

    public List<DirectionsRoute> routes() {
        return routes;
    }

    private DirectionsResponse connect() throws IOException {
        InputStream stream = connection.getInputStream();
        String response = asString(stream);
        return new DirectionsResponse(response);
    }

    private String asString(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        do {
            line = reader.readLine();
            if(line != null) {
                builder.append(line);
            }
        } while(line != null);
        return builder.toString();
    }

    private void buildConnectionUrl(double originLat, double originLng, double destinationLat,
                                    double destinationLng) throws IOException {
        String url = DIRECTIONS_API_BASE_URL + ORIGIN_URL + originLat + "," + originLng + "&" +
                DESTINATION_URL + destinationLat + "," + destinationLng;
        connection = (HttpURLConnection) new URL(url).openConnection();
    }
}
