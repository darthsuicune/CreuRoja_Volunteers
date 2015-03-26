package net.creuroja.android.model.webservice.responses;

public class RailsLoginResponse extends Response {

	final String authToken;

	public RailsLoginResponse(String authToken) {
		this.authToken = authToken;
	}

	@Override public boolean isValid() {
		return true;
	}

	@Override public String content() {
		return authToken;
	}

	@Override public int errorMessageResId() {
		return 0;
	}

	@Override public int responseCode() {
		return 200;
	}
}
