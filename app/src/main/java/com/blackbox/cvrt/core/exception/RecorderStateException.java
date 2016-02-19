package com.blackbox.cvrt.core.exception;

/**
 * @author Jerrick Pua
 */
public class RecorderStateException extends RecordException {
    public RecorderStateException() {
    }

    public RecorderStateException( String detailMessage ) {
        super( detailMessage );
    }

    public RecorderStateException( String detailMessage, Throwable throwable ) {
        super( detailMessage, throwable );
    }

    public RecorderStateException( Throwable throwable ) {
        super( throwable );
    }
}
