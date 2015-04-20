package net.creuroja.android.model.locations;

import net.creuroja.android.model.webservice.util.Response;
import net.creuroja.android.model.webservice.util.ResponseFactory;

public class RailsLocationsResponseFactory extends ResponseFactory {

    @Override public Response fillResponseData(String response) {
        return new RailsLocationsResponse(response);
    }
}
