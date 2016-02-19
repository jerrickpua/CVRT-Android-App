package com.blackbox.cvrt.core.exception;

/**
 * @author Jerrick Pua
 */
public class RecordException extends RuntimeException {
    public RecordException() {
    }

    public RecordException( String detailMessage ) {
        super( detailMessage );
    }

    public RecordException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public RecordException( Throwable throwable ) {
        super( throwable );
    }
}
