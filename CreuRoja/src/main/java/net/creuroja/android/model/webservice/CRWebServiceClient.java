package net.creuroja.android.model.webservice;

/**
 * Created by lapuente on 18.06.14.
 */
public interface CRWebServiceClient {
	String PROTOCOL = "https";
	String URL = "creuroja.net";
	String ARG_EMAIL = "email";
	String ARG_PASSWORD = "password";
	String ARG_AUTHORIZATION = "Authorization";
	String ARG_LAST_UPDATE = "updated_at";
	String RESOURCE_SESSIONS = "sessions.json";
	String RESOURCE_LOCATIONS = "locations.json";
	void signInUser(String username, String password);
	void getLocations(String accessToken, String lastUpdateTime);
}