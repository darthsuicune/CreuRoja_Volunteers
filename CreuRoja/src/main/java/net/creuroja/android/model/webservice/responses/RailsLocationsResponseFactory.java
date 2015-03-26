package net.creuroja.android.model.webservice.responses;

import android.content.ContentResolver;

public class RailsLocationsResponseFactory extends ResponseFactory {
    ContentResolver cr;

    public RailsLocationsResponseFactory(ContentResolver contentResolver) {
        this.cr = contentResolver;
    }

    @Override public Response fillResponseData(String response) {
        return new RailsLocationsResponse(response);
    }
}
