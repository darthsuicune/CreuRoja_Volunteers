package net.creuroja.android.model.locations;

import android.content.ContentResolver;

import net.creuroja.android.model.webservice.Response;
import net.creuroja.android.model.webservice.ResponseFactory;

public class RailsLocationsResponseFactory extends ResponseFactory {
    ContentResolver cr;

    public RailsLocationsResponseFactory(ContentResolver contentResolver) {
        this.cr = contentResolver;
    }

    @Override public Response fillResponseData(String response) {
        return new RailsLocationsResponse(response);
    }
}
