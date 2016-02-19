package com.blackbox.cvrt.core.rest.model;

/**
 * @author Jerrick Pua
 */
public class APIResponse {
    public APIResponse() {

    }

    private String message;

    private int statusCode;

    public APIResponse( String message, int statusCode ) {
        this.message = message;
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode( int statusCode ) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "APIResponse{" +
                "message='" + message + '\'' +
                ", statusCode=" + statusCode +
                '}';
    }
}
