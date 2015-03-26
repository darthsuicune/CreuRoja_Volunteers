package net.creuroja.android.model.webservice.responses;

/**
 * Created by denis on 26.03.15.
 */
public class RailsLocationsResponse extends Response {
    final String locations;

    public RailsLocationsResponse(String locations) {
        this.locations = locations;
    }

    @Override public boolean isValid() {
        return true;
    }

    @Override public String content() {
        return locations;
    }

    @Override public int errorMessageResId() {
        return 0;
    }

    @Override public int responseCode() {
        return 200;
    }
}
