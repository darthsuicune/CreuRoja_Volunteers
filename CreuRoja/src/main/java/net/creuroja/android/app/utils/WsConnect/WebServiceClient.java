package net.creuroja.android.app.utils.WsConnect;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lapuente on 30.07.14.
 */
public abstract class WebServiceClient {
	protected String mProtocol;
	protected String mServerUrl;

	public WebServiceClient(String protocol, String serverUrl) {
		this.mProtocol = protocol;
		this.mServerUrl = serverUrl;
	}

	public abstract HttpResponse get(String resource, WebServiceFormat format,
									 List<WebServiceOption> options) throws IOException;
	public abstract HttpResponse post(String resource, WebServiceFormat format,
									 List<WebServiceOption> options) throws IOException;

	public String getAsString(HttpResponse response) throws IOException {
		StringBuilder builder = new StringBuilder();
		BufferedReader reader =
				new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = reader.readLine();
		while (line != null) {
			builder.append(line);
			line = reader.readLine();
		}
		return builder.toString();
	}

	public String getStringResponse(String resource, WebServiceFormat format,
									   List<WebServiceOption> options) throws IOException {
		return getAsString(get(resource, format, options));
	}

	protected DefaultHttpClient getClient() {
		return new DefaultHttpClient();
	}

	protected HttpUriRequest getRequest(WebServiceRequestType requestType, String resource,
										WebServiceFormat format, List<WebServiceOption> options)
			throws UnsupportedEncodingException {
		List<NameValuePair> getOptions = new ArrayList();
		List<NameValuePair> postOptions = new ArrayList<>();
		List<NameValuePair> headerOptions = new ArrayList<>();
		parseOptions(getOptions, postOptions, headerOptions, options);
		return buildRequest(requestType, resource, format, getOptions, postOptions, headerOptions);
	}

	protected void parseOptions(List<NameValuePair> getOptions, List<NameValuePair> postOptions,
								List<NameValuePair> headerOptions, List<WebServiceOption> options) {
		if (options == null || options.isEmpty()) {
			return;
		}
		for (WebServiceOption option : options) {
			switch (option.optionType) {
				case GET:
					getOptions.add(new BasicNameValuePair(option.key, option.value));
					break;
				case POST:
					postOptions.add(new BasicNameValuePair(option.key, option.value));
					break;
				case HEADER:
					headerOptions.add(new BasicNameValuePair(option.key, option.value));
				default:
					break;
			}
		}
	}

	protected HttpUriRequest buildRequest(WebServiceRequestType requestType, String resource,
										  WebServiceFormat format, List<NameValuePair> getOptions,
										  List<NameValuePair> postOptions,
										  List<NameValuePair> headerOptions) {
		switch (requestType) {
			case GET:
				HttpGet request =
						new HttpGet(buildRequestUrl(resource, format, getOptions));
				addHeaders(request, headerOptions);
				return request;
			case POST:
				HttpPost postRequest =
						new HttpPost(buildRequestUrl(resource, format, getOptions));
				addHeaders(postRequest, headerOptions);
				addPostParameters(postRequest, postOptions);
				return postRequest;
			case PUT:
				break;
			case PATCH:
				break;
			case DELETE:
				break;
			default:
				assert false;
		}
		return null;
	}

	protected String buildRequestUrl(String resource, WebServiceFormat format,
									 List<NameValuePair> getOptions) {
		StringBuilder options = new StringBuilder();
		for (NameValuePair option : getOptions) {
			options.append(option.getName());
			options.append("=");
			options.append(option.getValue().replace(" ", "%20"));
			options.append("&");
		}
		if (options.toString().contains("&")) {
			options.deleteCharAt(options.length() - 1);
		}
		String result = mProtocol + "://" + mServerUrl + "/" + resource + format.toString();
		if(getOptions != null && !getOptions.isEmpty()) {
			result = result + "?" + options.toString();
		}
		return result;
	}

	public HttpUriRequest addHeaders(HttpUriRequest request, List<NameValuePair> headerOptions) {
		for(NameValuePair pair : headerOptions) {
			request.addHeader(pair.getName(), pair.getValue());
		}
		return request;
	}

	public HttpUriRequest addPostParameters(HttpPost request, List<NameValuePair> postOptions) {
		try {
			request.setEntity(new UrlEncodedFormEntity(postOptions));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return request;
	}
}