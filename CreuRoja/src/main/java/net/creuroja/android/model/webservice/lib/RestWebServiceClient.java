package net.creuroja.android.model.webservice.lib;

import android.util.Log;

import net.creuroja.android.model.webservice.RailsWebServiceClient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class RestWebServiceClient {
    public static final String TAG = "ERROR!!!!";
    String protocol;
    String host;
    HttpsURLConnection connection;
    InputStream caInput;

    public RestWebServiceClient(InputStream cert, String protocol, String serverUrl) {
        this.caInput = cert;
        this.protocol = protocol;
        this.host = RailsWebServiceClient.TEST_URL;
    }

    public String get(String resource, List<WebServiceOption> headerOptions,
                      List<WebServiceOption> getOptions)
            throws IOException {
        String response = null;
        try {
            setUpConnection(resource, headerOptions, getOptions);
            response = asString(connection.getInputStream());
        } catch (FileNotFoundException e) {
            String error = asString(connection.getErrorStream());
            Log.e(TAG, error);
        } finally {
            connection.disconnect();
        }
        return response;
    }

    private void setUpConnection(String resource, List<WebServiceOption> headerOptions,
                                 List<WebServiceOption> urlOptions)
            throws IOException {
        try {
            URL url = new URL(protocol + "://" + host + "/" +
                    resourceWithOptions(resource, urlOptions));
            setUpAuthority(url);
            setHeaders(headerOptions);
        } catch (KeyManagementException | NoSuchAlgorithmException | CertificateException | KeyStoreException e) {
            e.printStackTrace();
        }
    }

    private void setUpAuthority(URL url) throws IOException, KeyManagementException,
            NoSuchAlgorithmException, KeyStoreException, CertificateException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("suicune-pc", session);
            }
        };

        connection = (HttpsURLConnection) url.openConnection();
        connection.setHostnameVerifier(hostnameVerifier);
        connection.setSSLSocketFactory(context.getSocketFactory());
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
        return toString(builder, options);
    }

    private String toString(StringBuilder builder, List<WebServiceOption> options)
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
                       List<WebServiceOption> urlOptions,
                       List<WebServiceOption> postOptions)
            throws IOException {
        setUpConnection(resource, headerOptions, urlOptions);
        String response = null;
        try {
            writePostOptions(postOptions);
            response = asString(connection.getInputStream());
        } catch (FileNotFoundException e) {
            String error = asString(connection.getErrorStream());
            Log.e(TAG + "in catch", error);
        } finally {
            connection.disconnect();
        }
        return response;
    }

    private void writePostOptions(List<WebServiceOption> options) throws IOException {
        connection.setDoOutput(true);
        connection.setChunkedStreamingMode(0);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        String toWrite = toString(new StringBuilder(), options);
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
