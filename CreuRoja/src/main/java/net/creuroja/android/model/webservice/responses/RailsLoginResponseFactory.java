package net.creuroja.android.model.webservice.responses;

import android.content.ContentResolver;

import net.creuroja.android.model.users.User;

import org.json.JSONException;
import org.json.JSONObject;

public class RailsLoginResponseFactory extends ResponseFactory {
    public static final String AUTH_TOKEN_HOLDER = "token";
    public static final String AUTH_USER_HOLDER = "user";

    ContentResolver cr;
    String authToken;

    public RailsLoginResponseFactory(ContentResolver cr) {
        this.cr = cr;
    }


    @Override public Response fillResponseData(String response) {
        String errorMessage = "";
        int errorCode = -1;
        try {
            JSONObject object = new JSONObject(response);
            if (object.has(AUTH_TOKEN_HOLDER)) {
                authToken = object.getString(AUTH_TOKEN_HOLDER);
            } else {
                errorCode = object.getInt(Response.ERROR_CODE);
                errorMessage = object.getString(Response.ERROR_MESSAGE);
            }
            if (object.has(AUTH_USER_HOLDER)) {
                User user = new User(object.getJSONObject(AUTH_USER_HOLDER));
                user.save(cr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new ErrorResponse("Malformed JSONObject", 500);
        }
        if(errorCode != -1) {
            return new ErrorResponse(errorMessage, errorCode);
        }
        return new RailsLoginResponse(authToken);
    }
}
