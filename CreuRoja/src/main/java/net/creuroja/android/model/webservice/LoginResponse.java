package net.creuroja.android.model.webservice;

import net.creuroja.android.model.users.User;

/**
 * Created by denis on 19.06.14.
 */
public interface LoginResponse {
	String IS_VALID = "isValid";
	String ERROR_CODE = "errorCode";
	String ERROR_MESSAGE = "errorMessage";

	boolean isValid();
	String authToken();
	int errorCode();
	String errorMessage();
	User user();
}
