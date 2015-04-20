package net.creuroja.android.model.webservice.util;

import net.creuroja.android.model.webservice.ErrorResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public abstract class ResponseFactory {
    Response response;

    public Response build(HttpURLConnection connection) throws IOException {
        try {
            response = readData(connection);
        } catch (IOException e) {
            response = new ErrorResponse(asString(connection.getErrorStream()),
                    connection.getResponseCode());
        }
        return response;
    }

    private Response readData(HttpURLConnection connection) throws IOException {
        InputStream result = connection.getInputStream();
        if (connection.getResponseCode() == 200) {
            response = fillResponseData(asString(result));
        } else {
            response = new ErrorResponse(asString(connection.getErrorStream()),
                    connection.getResponseCode());
        }
        return response;
    }

    protected abstract Response fillResponseData(String input);

    private String asString(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = reader.readLine();
        while (line != null) {
            builder.append(line);
            line = reader.readLine();
        }
        return builder.toString();
    }
}
