package net.creuroja.android.model.webservice.util;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

public class RestWebServiceClientTest extends TestCase {
    static final String RESOURCE_SESSIONS = "sessions.json";
    static final String RESOURCE_LOCATION = "locations.json";
    static final String RESOURCE_VEHICLES = "vehicles.json";
    static final String ARG_EMAIL = "email";
    static final String ARG_PASSWORD = "password";
    static final String ARG_AUTHORIZATION = "Authorization";
    static final String ARG_LAST_UPDATE = "updated_at";
    static final String USERNAME = "user@user.us";
    static final String PASSWORD = "password";
    static final String TOKEN = "eA8fd5v-g59ru9E-VQzfvg";
    static final String UPDATE_TIME = "2015-04-08 09:11:16";

    RestWebServiceClient client;
    ResponseFactory factory;
    Response response;

    public void setUp() throws Exception {
        super.setUp();
        client = new RestWebServiceClient("http", "localhost:3000");
    }

    public void testGetWithLocations() throws Exception {
        setUpFactoryFor(RESOURCE_LOCATION);
        response = client.get(RESOURCE_LOCATION, authOptions(), lastUpdate());
    }


    private List<WebServiceOption> loginOptions() {
        List<WebServiceOption> list = new ArrayList<>();
        list.add(new WebServiceOption(ARG_EMAIL, USERNAME));
        list.add(new WebServiceOption(ARG_PASSWORD, PASSWORD));
        return list;
    }

    private List<WebServiceOption> authOptions() {
        List<WebServiceOption> list = new ArrayList<>();
        list.add(new WebServiceOption(ARG_AUTHORIZATION, TOKEN));
        return list;
    }

    private List<WebServiceOption> lastUpdate() {
        List<WebServiceOption> list = new ArrayList<>();
        list.add(new WebServiceOption(ARG_LAST_UPDATE, UPDATE_TIME));
        return list;
    }

    private void setUpFactoryFor(String resourceLocation) {
        factory = mock(ResponseFactory.class);
        switch (resourceLocation) {
            case RESOURCE_LOCATION:

                break;
            case RESOURCE_SESSIONS:

                break;
            case RESOURCE_VEHICLES:

                break;
        }
    }

    public void testPost() throws Exception {
        setUpFactoryFor(RESOURCE_SESSIONS);
        response = client.post(RESOURCE_SESSIONS, WebServiceOption.noOptions(),
                WebServiceOption.noOptions(), loginOptions());
    }
}