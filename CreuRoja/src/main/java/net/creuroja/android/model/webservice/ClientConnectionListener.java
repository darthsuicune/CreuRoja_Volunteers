package net.creuroja.android.model.webservice;

import net.creuroja.android.model.webservice.responses.Response;

public interface ClientConnectionListener {
	void onValidResponse(Response response);
	void onErrorResponse(int code, int errorResId);
}
