package net.creuroja.android.model.webservice;

/**
 * Created by lapuente on 18.06.14.
 */
public interface CRWebServiceClient {
	void signInUser(String username, String password);
	void getLocations(String accessToken, String lastUpdateTime);
}