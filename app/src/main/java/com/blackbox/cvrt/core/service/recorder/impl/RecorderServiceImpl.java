package com.blackbox.cvrt.core.service.recorder.impl;

import com.blackbox.cvrt.core.service.location.LocationRetriever;
import com.blackbox.cvrt.core.service.recorder.RecorderService;
import com.blackbox.cvrt.core.thread.PeriodicServiceRunnable;

import java.io.File;

/**
 * Created by test on 2/7/16.
 */
public class RecorderServiceImpl implements RecorderService {


    private final File recordDirectory;

    private final int sampleRate;

    private final LocationRetriever locationRetriever;


    public RecorderServiceImpl( File recordDirectory,
                                LocationRetriever locationRetriever
    ) {
        this( DEFAULT_SAMPLE_RATE, recordDirectory, locationRetriever );

    }

    public RecorderServiceImpl( int sampleRate, File recordDirectory,
                                LocationRetriever locationRetriever ) {
        this.recordDirectory = recordDirectory;
        this.sampleRate = sampleRate;
        this.locationRetriever = locationRetriever;
    }

    @Override
    public void cleanUp() {
    }


    private Thread thread;

    @Override
    public void startRecord( ) {
        thread = new Thread( new PeriodicServiceRunnable( recordDirectory, sampleRate,
                                                          locationRetriever ) );
        thread.start();
    }

    @Override
    public void stopRecord() {
        Thread dummy = thread;
        thread = null;
        dummy.interrupt();
    }


}
