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

    private final File cacheDirectory;

    private final int sampleRate;

    private final int channel;

    private final int encoding;

    private final LocationRetriever locationRetriever;


    public RecorderServiceImpl( File cacheDirectory, File recordDirectory,
                                LocationRetriever locationRetriever
    ) {
        this( DEFAULT_SAMPLE_RATE, DEFAULT_RECORDER_CHANNEL, DEFAULT_ENCODING, recordDirectory,
              cacheDirectory, locationRetriever );

    }

    public RecorderServiceImpl( int sampleRate, int channel, int encoding, File recordDirectory,
                                File cacheDirectory, LocationRetriever locationRetriever ) {
        this.recordDirectory = recordDirectory;
        this.cacheDirectory = cacheDirectory;
        this.sampleRate = sampleRate;
        this.channel = channel;
        this.encoding = encoding;
        this.locationRetriever = locationRetriever;
//        recorder = new SimpleAudioRecorder( cacheDirectory, sampleRate );
//        recorder = new AudioRecorder( sampleRate, channel, encoding, cacheDirectory ); // for complex audio recordings
    }

    @Override
    public void cleanUp() {
//        recorder.cleanUp();
    }

//    private Future future;

    private Thread thread;

    @Override
    public void startRecord( int interval ) {
        thread = new Thread( new PeriodicServiceRunnable( recordDirectory, sampleRate, interval, locationRetriever ) );
        thread.start();


//        future = taskExecutor.scheduleAtFixedRate( new Runnable() {
//            public void run() {
//                if( recorder.isRecording() ) {
//                    Record data = recorder.stop();
//                    new Thread( new FileMoverRunnable( data.clone(), recordDirectory ) ).start();
////                    new Thread( new RawAudioProcessorRunnable( data.clone(), recordDirectory, sampleRate ) )
////                            .start();
//                }
//                recorder.start();
//            }
//        }, 0, 301, TimeUnit.SECONDS );

    }

    @Override
    public void stopRecord() {
        Thread dummy = thread;
        thread = null;
        dummy.interrupt();

//        future.cancel( true );
//        taskExecutor.shutdown();
    }


}
