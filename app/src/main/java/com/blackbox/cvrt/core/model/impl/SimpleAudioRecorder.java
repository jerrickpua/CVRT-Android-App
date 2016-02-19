package com.blackbox.cvrt.core.model.impl;

import android.media.MediaRecorder;

import com.blackbox.cvrt.core.exception.RecordException;
import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.model.Record;
import com.blackbox.cvrt.core.model.Recorder;
import com.blackbox.cvrt.utils.Logger;

import org.joda.time.DateTime;

import java.io.File;
import java.util.UUID;

/**
 * @author Jerrick Pua
 */
@Deprecated
public class SimpleAudioRecorder implements Recorder {

    private static final Logger logger = new Logger( SimpleAudioRecorder.class );

    private final int sampleRate;

    private final File cacheDirectory;

    private MediaRecorder mediaRecorder;

    private Record record;

    private volatile boolean state = false;

    public SimpleAudioRecorder( File cacheDirectory, int sampleRate ) {
        this.cacheDirectory = cacheDirectory;
        this.sampleRate = sampleRate;
    }


    @Override
    public synchronized Record stop() {
        if( !isRecording() ) {
            logger.i( "Recorder already stopped" );
            throw new RecorderStateException( "Recording already stopped." );
        }

        cleanUpMediaRecorder();
        state = false;
        record.setEnd( new DateTime() );
        Record clone = record.clone();
        record = null;
        return clone;
    }

    @Override
    public void cleanUp() {
        cleanUpMediaRecorder();
        record = null;

    }

    private void cleanUpMediaRecorder() {
        try {
            if( mediaRecorder != null && isRecording() ) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder = null;
            }
        } catch ( IllegalStateException ex ) {
            logger.e( "Unable to clean up recorder", ex );
            throw ex;
        } catch ( RuntimeException ex ) {
            logger.d( "Stop was called immediately after start. Ignoring the exception. ", ex );
            /**
             * RuntimeException is intentionally thrown to the
             * application, if no valid audio/video data has been received when stop()
             * is called. This happens if stop() is called immediately after
             * start(). The failure lets the application take action accordingly to
             * clean up the output file (delete the output file, for instance), since
             * the output file is not properly constructed when this happens.
             */
            //ignoring
        }

    }

    @Override
    public synchronized void start() {
        if( isRecording() ) {
            logger.i( "Recorder already started" );
            throw new RecorderStateException( "Recording already started." );
        }

        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource( MediaRecorder.AudioSource.MIC );
            mediaRecorder.setOutputFormat( MediaRecorder.OutputFormat.MPEG_4 );
            mediaRecorder.setAudioEncoder( MediaRecorder.AudioEncoder.AAC );
            mediaRecorder.setAudioSamplingRate( sampleRate );
            File file = new File( cacheDirectory,
                    String.format( "%s.mp3", UUID.randomUUID().toString() ) );
            mediaRecorder.setOutputFile( file.getAbsolutePath() );
            mediaRecorder.prepare();
            mediaRecorder.start();
            state = true;
            DateTime start = new DateTime();
            record = new Record( start, file );

        } catch ( Exception ex ) {
            logger.e( "Unable to start recorder", ex );
            throw new RecordException( ex );
        }
    }

    @Override
    public synchronized boolean isRecording() {
        return state;
    }


}
