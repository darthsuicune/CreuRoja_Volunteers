package net.creuroja.android.model.webservice;

import net.creuroja.android.R;

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
        return R.string.error_unknown;
    }

    @Override public int responseCode() {
        return responseCode;
    }
}
