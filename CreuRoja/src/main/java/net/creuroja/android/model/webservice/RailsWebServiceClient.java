package net.creuroja.android.model.webservice;

import android.content.ContentResolver;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.RailsLocationsResponseFactory;
import net.creuroja.android.model.webservice.auth.RailsLoginResponseFactory;
import net.creuroja.android.model.webservice.util.Response;
import net.creuroja.android.model.webservice.util.RestWebServiceClient;
import net.creuroja.android.model.webservice.util.WebServiceOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RailsWebServiceClient implements CRWebServiceClient {
    ContentResolver cr;
    RestWebServiceClient client;
    ClientConnectionListener listener;

    public RailsWebServiceClient(ContentResolver cr, RestWebServiceClient client,
                                 ClientConnectionListener listener) {
        this.cr = cr;
        this.client = client;
        this.listener = listener;
    }

    @Override public void signInUser(String email, String password) {
        try {
            client.setResponseFactory(new RailsLoginResponseFactory(cr));
            Response response = client.post(RESOURCE_SESSIONS, acceptAsOptions(),
                    WebServiceOption.noOptions(), loginAsOptions(email, password));
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onErrorResponse(500, R.string.error_connecting);
        }
    }

    private List<WebServiceOption> acceptAsOptions() {
        return new ArrayList<>();
    }

    @Override public void getLocations(String accessToken, String lastUpdateTime) {
        try {
            client.setResponseFactory(new RailsLocationsResponseFactory());
            Response response = client.get(RESOURCE_LOCATIONS, authAsOptions(accessToken),
                    lastUpdateAsOptions(lastUpdateTime));
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onErrorResponse(500, R.string.error_connecting);
        }
    }

    private void sendResponse(Response response) {
        if (response.isValid()) {
            listener.onValidResponse(response);
        } else {
            listener.onErrorResponse(response.responseCode(), response.errorMessageResId());
        }
    }

    private List<WebServiceOption> loginAsOptions(String email, String password) {
        List<WebServiceOption> options = new ArrayList<>();
        options.add(new WebServiceOption(ARG_EMAIL, email));
        options.add(new WebServiceOption(ARG_PASSWORD, password));
        return options;
    }

    private List<WebServiceOption> authAsOptions(String accessToken) {
        List<WebServiceOption> options = new ArrayList<>();
        options.add(new WebServiceOption(ARG_AUTHORIZATION, "Token token=" + accessToken));
        return options;
    }

    private List<WebServiceOption> lastUpdateAsOptions(String lastUpdateTime) {
        List<WebServiceOption> options = new ArrayList<>();
        if (!lastUpdateTime.equals("0")) {
            options.add(new WebServiceOption(ARG_LAST_UPDATE, lastUpdateTime));
        }
        return options;
    }
}
