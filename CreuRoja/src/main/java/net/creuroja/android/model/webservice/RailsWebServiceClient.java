package net.creuroja.android.model.webservice;

import android.text.TextUtils;

import net.creuroja.android.R;
import net.creuroja.android.model.webservice.lib.RestWebServiceClient;
import net.creuroja.android.model.webservice.lib.WebServiceOption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RailsWebServiceClient implements CRWebServiceClient {
    public static final String PROTOCOL = "https";
    public static final String URL = "creuroja.net";
    public static final String TEST_URL = "suicune-pc:3000";
    private static final String WS_CR_TAG = "CreuRoja Rails webservice";
    private static final String ARG_EMAIL = "email";
    private static final String ARG_PASSWORD = "password";
    private static final String ARG_ACCESS_TOKEN = "Authorization: Token ";
    private static final String ARG_LAST_UPDATE = "updated_at";
    private static final String RESOURCE_SESSIONS = "sessions.json";
    private static final String RESOURCE_LOCATIONS = "locations.json";

    private RestWebServiceClient client;
    private ClientConnectionListener listener;

    public RailsWebServiceClient(RestWebServiceClient client, ClientConnectionListener listener) {
        this.client = client;
        this.listener = listener;
    }

    @Override
    public void signInUser(String email, String password) {
        try {
            List<WebServiceOption> options = loginAsOptions(email, password);
            String response = client.post(RESOURCE_SESSIONS, acceptAsOptions(),
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
            String response = client.get(RESOURCE_LOCATIONS, authAsOptions(accessToken),
                    lastUpdateAsOptions(lastUpdateTime));
            sendResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onErrorResponse(500, R.string.error_connecting);
        }
    }

    private void sendResponse(String response) {
        if (TextUtils.isEmpty(response)) {
            listener.onErrorResponse(500, R.string.error_connecting);
        } else {
            listener.onValidResponse(response);
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
        options.add(new WebServiceOption(ARG_ACCESS_TOKEN, "token=\"" + accessToken + "\""));
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
