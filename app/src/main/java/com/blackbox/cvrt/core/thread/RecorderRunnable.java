package com.blackbox.cvrt.core.thread;

import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.service.recorder.RecorderService;
import com.blackbox.cvrt.utils.Logger;

/**
 * @author Jerrick Pua
 */
public class RecorderRunnable implements Runnable {

    private static final Logger logger = new Logger( RecorderRunnable.class );

    private final RecorderService recorderService;

    public RecorderRunnable( RecorderService recorderService ) {
        this.recorderService = recorderService;
    }


    @Override
    public void run() {
        try {
            recorderService.startRecord();
        } catch ( Throwable ex ) {
            if( !( ex instanceof RecorderStateException ) ) {
                logger.e( "Failed to start recordings", ex );
                recorderService.stopRecord();
            }
        }

    }

}
