package net.creuroja.android.model.directions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 12.01.15.
 */
public class DirectionsResponse {
	public static final String STATUS = "status";
	public static final String ROUTES = "routes";

	String status;
	List<DirectionsRoute> routes;
	JSONObject response;

	public DirectionsResponse(String response) {
		routes = new ArrayList<>();
		try {
			this.response = new JSONObject(response);
			parseObject();
		} catch (JSONException e) {
			throw new DirectionsException(e);
		}
	}

	private void parseObject() throws JSONException {
		getStatus();
		if(isValid()) {
			getRoutes();
		} else {
			throw new DirectionsException(status);
		}
	}

	private void getStatus() throws JSONException {
		status = response.getString(STATUS);
	}

	private boolean isValid() {
		return status.equals("OK");
	}

	private void getRoutes() throws JSONException {
		JSONArray array = response.getJSONArray(ROUTES);
		for(int i = 0; i < array.length(); i++) {
			routes.add(new DirectionsRoute(array.getJSONObject(i)));
		}
	}

	public List<DirectionsRoute> routes() {
		return routes;
	}
}
