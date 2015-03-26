package net.creuroja.android.model.webservice.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class RestWebServiceClient {
	public static final String TAG = "ERROR!!!!";
	String protocol;
	String host;
	HttpsURLConnection connection;

	public RestWebServiceClient(String protocol, String serverUrl) {
		this.protocol = protocol;
		this.host = serverUrl;
	}

	public String get(String resource, List<WebServiceOption> headerOptions,
					  List<WebServiceOption> getOptions) throws IOException {
		String response = null;
		try {
			setUpConnection(resource, headerOptions, getOptions);
			response = asString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			readError();
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private void setUpConnection(String resource, List<WebServiceOption> headerOptions,
								 List<WebServiceOption> urlOptions) throws IOException {
		URL url = new URL(protocol + "://" + host + "/" +
						  resourceWithOptions(resource, urlOptions));
		connection = (HttpsURLConnection) url.openConnection();
		setHeaders(headerOptions);
	}

	private void setHeaders(List<WebServiceOption> options) {
		for (WebServiceOption option : options) {
			connection.setRequestProperty(option.key, option.value);
		}
	}

	private String resourceWithOptions(String resource, List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder(resource);
		if (options.size() > 0) {
			builder.append("?");
		}
		return addOptionsToBuilder(builder, options);
	}

	private String addOptionsToBuilder(StringBuilder builder, List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		for (int i = 0; i < options.size(); i++) {
			WebServiceOption option = options.get(i);
			builder.append(URLEncoder.encode(option.key, "UTF-8"));
			builder.append("=");
			builder.append(URLEncoder.encode(option.value, "UTF-8"));
			if (i < options.size() - 1) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public String post(String resource, List<WebServiceOption> headerOptions,
					   List<WebServiceOption> urlOptions, List<WebServiceOption> postOptions)
			throws IOException {
		setUpConnection(resource, headerOptions, urlOptions);
		String response = null;
		try {
			writePostOptions(postOptions);
			response = asString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			readError();
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private void readError() throws IOException {
		readHeaders();
		String error = asString(connection.getErrorStream());
		Log.e(TAG, error);
	}

	private void readHeaders() {
		for(String key : connection.getHeaderFields().keySet()) {
			List<String> values = connection.getHeaderFields().get(key);
			for(String value : values) {
				Log.e(TAG, key + ": " + value);
			}
		}
	}

	private void writePostOptions(List<WebServiceOption> options) throws IOException {
		String toWrite = addOptionsToBuilder(new StringBuilder(), options);
		connection.setDoOutput(true);
		connection.setFixedLengthStreamingMode(toWrite.length());
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		writer.write(toWrite);
		writer.flush();
		writer.close();
	}

	private String asString(InputStream response) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(response));
		String line = reader.readLine();
		while (line != null) {
			builder.append(line);
			line = reader.readLine();
		}
		return builder.toString();
	}
}
