package net.creuroja.android.model.webservice;

/**
 * Created by lapuente on 08.08.14.
 */

public interface ClientConnectionListener {
	void onValidResponse(String response);
	void onErrorResponse(int code, int errorResId);
}
