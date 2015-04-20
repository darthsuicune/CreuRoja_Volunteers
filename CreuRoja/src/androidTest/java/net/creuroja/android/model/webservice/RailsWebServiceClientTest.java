package net.creuroja.android.model.webservice;

import android.content.ContentResolver;

import junit.framework.TestCase;

import net.creuroja.android.R;
import net.creuroja.android.model.locations.RailsLocationsResponse;
import net.creuroja.android.model.locations.RailsLocationsResponseFactory;
import net.creuroja.android.model.webservice.auth.RailsLoginResponse;
import net.creuroja.android.model.webservice.auth.RailsLoginResponseFactory;
import net.creuroja.android.model.webservice.util.Response;
import net.creuroja.android.model.webservice.util.ResponseFactory;
import net.creuroja.android.model.webservice.util.RestWebServiceClient;
import net.creuroja.android.model.webservice.util.WebServiceOption;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.util.List;

import static net.creuroja.android.model.webservice.RailsWebServiceClient.ARG_AUTHORIZATION;
import static net.creuroja.android.model.webservice.RailsWebServiceClient.ARG_EMAIL;
import static net.creuroja.android.model.webservice.RailsWebServiceClient.ARG_LAST_UPDATE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RailsWebServiceClientTest extends TestCase {
    RailsWebServiceClient client;
    RestWebServiceClient rest;
    ClientConnectionListener listener;

    Response loginResponse = new RailsLoginResponse("token");
    Response locationsResponse = new RailsLocationsResponse("locations");
    Response error401 = new ErrorResponse("invalid stuff", 401);

    public void setUp() throws Exception {
        super.setUp();
        rest = mock(RestWebServiceClient.class);
        listener = mock(ClientConnectionListener.class);
        client = new RailsWebServiceClient(mock(ContentResolver.class), rest, listener);
    }

    private Matcher<List<WebServiceOption>> matchesLogin(final String email) {
        return new BaseMatcher<List<WebServiceOption>>() {
            @Override public boolean matches(Object o) {
                if (o == null) {
                    return false;
                }
                List<WebServiceOption> list = (List<WebServiceOption>) o;
                boolean match = true;
                for (WebServiceOption option : list) {
                    switch (option.key) {
                        case ARG_EMAIL:
                            match = option.value.equals(email);
                            break;
                    }
                }
                return match;
            }

            @Override public void describeTo(Description description) {

            }
        };
    }

    private Matcher<List<WebServiceOption>> matchesToken(final String token) {
        return new BaseMatcher<List<WebServiceOption>>() {
            @Override public boolean matches(Object o) {
                if (o == null) {
                    return false;
                }
                List<WebServiceOption> list = (List<WebServiceOption>) o;
                return list.get(0).key.equals(ARG_AUTHORIZATION) &&
                        list.get(0).value.equals("Token token=" + token);
            }

            @Override public void describeTo(Description description) {

            }
        };
    }

    private Matcher<List<WebServiceOption>> matchesUpdate(final String update) {
        return new BaseMatcher<List<WebServiceOption>>() {
            @Override public boolean matches(Object o) {
                if (o == null) {
                    return false;
                }
                List<WebServiceOption> list = (List<WebServiceOption>) o;
                return list.get(0).key.equals(ARG_LAST_UPDATE) &&
                        list.get(0).value.equals(update);
            }

            @Override public void describeTo(Description description) {

            }
        };
    }

    public void testSignInUserAddsAResponseFactory() throws Exception {
        stubMethodsForLogin();
        client.signInUser("random", "password");
        verify(rest).setResponseFactory(any(RailsLoginResponseFactory.class));
    }

    public void testSignInUserWithValidData() throws Exception {
        givenTheRestServerRespondsToLoginWith(RailsLoginResponseFactory.class);
        client.signInUser("random", "password");
        verify(listener).onValidResponse(loginResponse);
    }

    private void givenTheRestServerRespondsToLoginWith(Class<? extends ResponseFactory> factory)
            throws IOException {
        rest.setResponseFactory(mock(factory));
        stubMethodsForLogin();
    }

    private void stubMethodsForLogin() throws IOException {
        when(rest.post(anyString(), anyListOf(WebServiceOption.class),
                anyListOf(WebServiceOption.class), argThat(matchesLogin("random"))))
                .thenReturn(loginResponse);
        when(rest.post(anyString(), anyListOf(WebServiceOption.class),
                anyListOf(WebServiceOption.class), argThat(matchesLogin("invalid"))))
                .thenReturn(error401);
        when(rest.post(anyString(), anyListOf(WebServiceOption.class),
                anyListOf(WebServiceOption.class), argThat(matchesLogin("error"))))
                .thenThrow(new IOException());
    }

    public void testSignInUserWithInvalidData() throws Exception {
        givenTheRestServerRespondsToLoginWith(RailsLoginResponseFactory.class);
        client.signInUser("invalid", "password");
        verify(listener).onErrorResponse(401, error401.errorMessageResId());
    }

    public void testSignInUserAndGetAnErrorDuringTheConnection() throws Exception {
        givenTheRestServerRespondsToLoginWith(RailsLoginResponseFactory.class);
        client.signInUser("error", "connection");
        verify(listener).onErrorResponse(500, R.string.error_connecting);
    }

    public void testGetLocationsAddsAResponseFactory() throws Exception {
        stubMethodsForLocations();
        client.getLocations("someValidToken", "someValidUpdate");
        verify(rest).setResponseFactory(any(RailsLocationsResponseFactory.class));
    }

    public void testGetLocationsWithValidTokenAndUpdate() throws Exception {
        givenTheRestServerRespondsToLocationsWith(RailsLocationsResponseFactory.class);
        client.getLocations("someValidToken", "someValidUpdate");
        verify(listener).onValidResponse(locationsResponse);
    }

    private void givenTheRestServerRespondsToLocationsWith(
            Class<? extends ResponseFactory> factory) throws IOException {
        rest.setResponseFactory(mock(factory));
        stubMethodsForLocations();
    }

    private void stubMethodsForLocations() throws IOException {
        when(rest.get(anyString(), argThat(matchesToken("someValidToken")),
                argThat(matchesUpdate("someValidUpdate"))))
                .thenReturn(locationsResponse);
        when(rest.get(anyString(), argThat(matchesToken("someValidToken")),
                argThat(matchesUpdate("someInvalidUpdate"))))
                .thenReturn(error401);
        when(rest.get(anyString(), argThat(matchesToken("someInvalidToken")),
                argThat(matchesUpdate("someValidUpdate"))))
                .thenReturn(error401);
        when(rest.get(anyString(), argThat(matchesToken("someWeirdShit")),
                argThat(matchesUpdate("someValidUpdate"))))
                .thenThrow(new IOException());
    }

    public void testGetLocationsWithValidTokenInvalidUpdate() throws Exception {
        givenTheRestServerRespondsToLocationsWith(RailsLocationsResponseFactory.class);
        client.getLocations("someInvalidToken", "someValidUpdate");
        verify(listener).onErrorResponse(error401.responseCode(), error401.errorMessageResId());
    }

    public void testGetLocationsWithInvalidTokenButValidUpdate() throws Exception {
        givenTheRestServerRespondsToLocationsWith(RailsLocationsResponseFactory.class);
        client.getLocations("someValidToken", "someInvalidUpdate");
        verify(listener).onErrorResponse(error401.responseCode(), error401.errorMessageResId());
    }

    public void testGetLocationsWithErrorInConnection() throws Exception {
        givenTheRestServerRespondsToLocationsWith(RailsLocationsResponseFactory.class);
        client.getLocations("someWeirdShit", "someValidUpdate");
        verify(listener).onErrorResponse(500, R.string.error_connecting);
    }
}