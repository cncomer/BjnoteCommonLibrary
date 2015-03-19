package com.shwy.bestjoy.exception;

import java.io.IOException;

/**
 * Created by bestjoy on 15/3/2.
 */
public class StatusException extends IOException {

    private String mStatusCode;
    public StatusException(String detailMessage, String statusCode) {
        super(detailMessage);
        mStatusCode = statusCode;
    }

    public String getStatusCode() {
        return mStatusCode;
    }
}
