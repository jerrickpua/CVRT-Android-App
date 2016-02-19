package com.blackbox.cvrt.core.exception;

/**
 * @author Jerrick Pua
 */
public class LocationServiceUnavailableException extends Exception {
    public LocationServiceUnavailableException() {
        super( "Location service not enabled" );
    }

    public LocationServiceUnavailableException( String detailMessage ) {
        super( detailMessage );
    }

    public LocationServiceUnavailableException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public LocationServiceUnavailableException( Throwable throwable ) {
        super( throwable );
    }
}
