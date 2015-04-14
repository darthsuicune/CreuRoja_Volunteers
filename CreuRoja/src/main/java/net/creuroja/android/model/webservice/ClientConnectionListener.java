package net.creuroja.android.model.webservice;

public interface ClientConnectionListener {
	void onValidResponse(Response response);
	void onErrorResponse(int code, int errorResId);
}
