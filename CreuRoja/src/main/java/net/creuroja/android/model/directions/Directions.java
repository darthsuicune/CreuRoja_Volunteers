package net.creuroja.android.model.directions;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.09.14.
 */
public class Directions {
    LatLng origin;
    LatLng destination;
	private List<LatLng> points;

	public Directions(LatLng origin, LatLng destination) {
        points = new ArrayList<>();
        this.origin = origin;
        this.destination = destination;
        get();
    }

    public void get() {
        try {
            List<DirectionsRoute> routes = getRoutes();
            parseRoutes(routes);
        } catch (IOException e) {
            cantRetrieve(e);
        } catch (JSONException e) {
            cantRetrieve(e);
        }
    }

	public List<LatLng> points() {
		return this.points;
	}

	public int pointCount() {
		return points.size();
	}

	private List<DirectionsRoute> getRoutes() {
		DirectionsRequest request = new DirectionsRequest(origin, destination);
		return request.makeRequest();
	}

	private void parseRoutes(List<DirectionsRoute> routes) throws IOException, JSONException {
		for (DirectionsRoute route : routes) {
			points.addAll(route.path());
		}
	}

	private void cantRetrieve(IOException e) {
        e.printStackTrace();
        throw new DirectionsException(e);
    }

    private void cantRetrieve(JSONException e) {
        e.printStackTrace();
        throw new DirectionsException(e);
    }

    private class DirectionsException extends RuntimeException {
        Exception e;
        public DirectionsException(IOException e) {
            this.e = e;
        }

        public DirectionsException(JSONException e) {
            this.e = e;
        }
    }
}
