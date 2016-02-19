package com.blackbox.cvrt.core.thread;

import android.content.Context;

import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.service.recorder.RecorderService;
import com.blackbox.cvrt.utils.Logger;

/**
 * @author Jerrick Pua
 */
public class RecorderRunnable implements Runnable {

    private static final Logger logger = new Logger( RecorderRunnable.class );

    private final RecorderService recorderService;

    private final Context context;

    private int interval = 301;

    public RecorderRunnable( Context context, RecorderService recorderService ) {
        this.recorderService = recorderService;
        this.context = context;
    }

    public void setInterval( int interval ) {
        this.interval = interval;
    }

    @Override
    public void run() {
        try {
            recorderService.startRecord( interval );
        } catch ( Throwable ex ) {
            if( !( ex instanceof RecorderStateException ) ) {
                logger.e( "Failed to start recordings", ex );
                recorderService.stopRecord();
            }
        }

    }

}
