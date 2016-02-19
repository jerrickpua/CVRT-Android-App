package com.blackbox.cvrt.core.model.impl;

import android.media.MediaRecorder;

import com.blackbox.cvrt.core.model.MP3Record;
import com.blackbox.cvrt.utils.Logger;

import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;

/**
 * @author Jerrick Pua
 */
public class AudioRecorderModel {

    private static final Logger logger = new Logger( AudioRecorderModel.class );
    private MediaRecorder recorder;
    private MP3Record record;
    private final File baseDirectory;
    private final int duration;
    private final int sampleRate;
    private final MediaRecorder.OnInfoListener listener;

    public AudioRecorderModel( File baseDirectory, int duration, int sampleRate,
                               MediaRecorder.OnInfoListener listener ) {
        this.baseDirectory = baseDirectory;
        this.duration = duration;
        this.sampleRate = sampleRate;
        this.listener = listener;
    }

    public MP3Record getRecord() {
        return record;
    }

    public void start() throws IOException {
        if( recorder != null ) {
            logger.w( "Recorder already started" );
            return;
        }
        recorder = new MediaRecorder();
        recorder.setAudioSource( MediaRecorder.AudioSource.MIC );
        recorder.setMaxDuration( duration );
        recorder.setOutputFormat( MediaRecorder.OutputFormat.MPEG_4 );
        recorder.setAudioEncoder( MediaRecorder.AudioEncoder.AAC );
        recorder.setAudioSamplingRate( sampleRate );
        recorder.setOnInfoListener( listener );
        DateTime startDate = new DateTime();
        DateTime endDate = startDate.plusMillis( duration );
        record = new MP3Record( startDate, endDate, baseDirectory );
        recorder.setOutputFile( record.getOutputFile().getAbsolutePath() );
        recorder.prepare();
        recorder.start();
    }

    public void stop() {
        if( recorder == null ) {
            logger.w( "Recorder already stopped" );
            return;
        }
        try {
            recorder.stop();
            recorder.reset();
            recorder.release();
            recorder = null;
        } catch ( IllegalStateException ex ) {
            logger.w( "Recorder already stopped" );
        } catch ( RuntimeException ex ) {
            logger.w( "Stopped called immediately after start.", ex );
        } catch ( Throwable ex ) {
            logger.e( "There is an error while stopping recorder", ex );
            throw ex;
        }

    }
}
