package net.creuroja.android.model.webservice.lib;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestWebServiceClient {
	String protocol;
	String host;
	HttpURLConnection connection;

	public RestWebServiceClient(String protocol, String serverUrl) {
		this.protocol = protocol;
		this.host = serverUrl;
	}

	public String get(String resource, List<WebServiceOption> options)
			throws IOException {
		connection = getConnection(resource, urlOptions(options));
		String response = null;
		try {
			response = asString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			String error = asString(connection.getErrorStream());
			Log.e("ERROR!!!!", error);
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private HttpURLConnection getConnection(String resource, List<WebServiceOption> options)
			throws IOException {
		URL url = new URL(protocol, host, resourceWithOptions(resource, options));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		setHeaders(connection, options);
		return connection;
	}

	private void setHeaders(HttpURLConnection connection, List<WebServiceOption> options) {
		for(WebServiceOption option : options) {
			if(option.optionType == WebServiceOption.OptionType.HEADER) {
				addHeader(connection, option);
			}
		}
	}

	private void addHeader(HttpURLConnection connection, WebServiceOption option) {
		connection.setRequestProperty(option.key, option.value);
	}

	private List<WebServiceOption> urlOptions(List<WebServiceOption> options) {
		List<WebServiceOption> urlOptions = new ArrayList<>();
		for(WebServiceOption option : options) {
			if(option.optionType == WebServiceOption.OptionType.GET) {
				urlOptions.add(option);
			}
		}
		return urlOptions;
	}

	private String resourceWithOptions(String resource, List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		StringBuilder builder = new StringBuilder(resource + ".json");
		if(options.size() > 0) {
			builder.append("?");
		}
		toString(builder, options);
		return builder.toString();
	}

	private String toString(StringBuilder builder, List<WebServiceOption> options) throws UnsupportedEncodingException {
		for(int i  = 0; i < options.size(); i++) {
			WebServiceOption option = options.get(i);
			builder.append(URLEncoder.encode(option.key, "UTF-8"));
			builder.append("=");
			builder.append(URLEncoder.encode(option.value, "UTF-8"));
			if(i < options.size() - 1) {
				builder.append("&");
			}
		}
		return builder.toString();
	}

	public String post(String resource, List<WebServiceOption> options)
			throws IOException {
		connection = getConnection(resource, urlOptions(options));
		String response = null;
		try {
			printHeaders();
			writePostOptions(options);
			response = asString(connection.getInputStream());
		} catch (FileNotFoundException e) {
			String error = asString(connection.getErrorStream());
			Log.e("ERROR!!!!", error);
		} finally {
			connection.disconnect();
		}
		return response;
	}

	private void printHeaders() {
		Map<String, List<String>> headers = connection.getHeaderFields();
		for(String key : headers.keySet())
		{
			List<String> strings = headers.get(key);
			for (String string : strings) {
				Log.w(key, string);
			}
		}
	}

	private void writePostOptions(List<WebServiceOption> options) throws IOException {
		connection.setDoOutput(true);
		connection.setChunkedStreamingMode(0);
		OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
		String toWrite = toString(new StringBuilder(), options);
		writer.write(toWrite);
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
