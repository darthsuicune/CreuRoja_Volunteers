package org.creuroja.android.model.ws;

/**
 * Created by lapuente on 18.06.14.
 */
public interface WebServiceClient {
	public LoginResponse signInUser(String username, String password);
	public LocationList getLocations(String accessToken);
	public LocationList getLocations(String lastUpdateTime, String accessToken);
}