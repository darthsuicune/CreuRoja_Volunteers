package net.creuroja.android.model.webservice.responses;

/**
 * Created by denis on 27.03.15.
 */
public class ErrorResponse extends Response {
    private final String content;
    private final int responseCode;

    public ErrorResponse(String content, int responseCode) {
        this.content = content;
        this.responseCode = responseCode;
    }

    @Override public boolean isValid() {
        return false;
    }

    @Override public String content() {
        return content;
    }

    @Override public int errorMessageResId() {
        return 0;
    }

    @Override public int responseCode() {
        return responseCode;
    }
}
