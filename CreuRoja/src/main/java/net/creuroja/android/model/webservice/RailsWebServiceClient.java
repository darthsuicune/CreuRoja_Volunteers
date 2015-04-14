package net.creuroja.android.model.webservice;

import net.creuroja.android.R;
import net.creuroja.android.model.webservice.util.RestWebServiceClient;
import net.creuroja.android.model.webservice.util.WebServiceOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RailsWebServiceClient implements CRWebServiceClient {
    public static final String PROTOCOL = "https";
    public static final String URL = "creuroja.net";
    private static final String WS_CR_TAG = "CreuRoja Rails webservice";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_AUTHORIZATION = "Authorization";
    private static final String ARG_LAST_UPDATE = "updated_at";
    private static final String RESOURCE_SESSIONS = "sessions.json";
    private static final String RESOURCE_LOCATIONS = "locations.json";

    RestWebServiceClient client;
    ClientConnectionListener listener;

    public RailsWebServiceClient(RestWebServiceClient client, ClientConnectionListener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void signInUser(String email, String password) {
        try {
            List<WebServiceOption> options = loginAsOptions(email, password);
            Response response = client.post(RESOURCE_SESSIONS, acceptAsOptions(),
                    WebServiceOption.noOptions(), options);
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onErrorResponse(500, R.string.error_connecting);
        }
    }

    private List<WebServiceOption> acceptAsOptions() {
        List<WebServiceOption> options = new ArrayList<>();
        //Include here headers as needed.
        return options;
    }

    @Override
    public void getLocations(String accessToken, String lastUpdateTime) {
        try {
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
