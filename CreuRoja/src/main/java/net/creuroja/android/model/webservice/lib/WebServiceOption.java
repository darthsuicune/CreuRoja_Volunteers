package net.creuroja.android.model.webservice.lib;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.07.14.
 */
public class WebServiceOption {
	public String key;
	public String value;

	public WebServiceOption(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public static List<WebServiceOption> noOptions() {
		return new ArrayList<>();
	}
}
