package net.creuroja.android.model.directions;

import java.util.List;

/**
 * Created by denis on 07.01.15.
 */
public class DirectionsResponse {
    public static final int STATUS_OK = 0;
    public static final int STATUS_NOT_OK = 1;
    public static final int STATUS_LIMIT_REACHED = 2;
    public static final String STATUS = "status";
    public static final String ROUTES = "routes";
    public static final String LEGS = "legs";
    public static final String STEPS = "steps";
    public static final String START_LOCATION = "start_location";
    public static final String END_LOCATION = "end_location";
    public static final String POLYLINE = "polyline";
    public static final String POINTS = "points";

    public String status;
    public List<DirectionsRoute> routes;

    public List<DirectionsRoute> routes() {
        return routes;
    }
}
