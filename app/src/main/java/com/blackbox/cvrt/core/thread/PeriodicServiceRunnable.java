package com.blackbox.cvrt.core.thread;

import android.location.Location;
import android.media.MediaRecorder;

import com.blackbox.cvrt.context.ApplicationContext;
import com.blackbox.cvrt.core.model.MP3Record;
import com.blackbox.cvrt.core.model.impl.AudioRecorderModel;
import com.blackbox.cvrt.core.service.location.LocationRetriever;
import com.blackbox.cvrt.utils.Logger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author Jerrick Pua
 */
public class PeriodicServiceRunnable implements Runnable, MediaRecorder.OnInfoListener {
    private static final Logger logger = new Logger( PeriodicServiceRunnable.class );

    private ScheduledExecutorService taskExecutor = Executors.newScheduledThreadPool( 5 );

    private boolean isRecording = false;
    private final File recordDirectory;
    private final int sampleRate;
    private AudioRecorderModel mediaRecorder;
    private final LocationRetriever locationRetriever;

    public PeriodicServiceRunnable( File recordDirector, int sampleRate,
                                    LocationRetriever locationRetriever ) {
        this.recordDirectory = recordDirector;
        this.sampleRate = sampleRate;
        this.locationRetriever = locationRetriever;
    }

    @Override
    public void run() {
        try {
            while ( !Thread.interrupted() ) {
                if( isRecording ) {
                    continue;
                }
                if( mediaRecorder != null ) {
                    processAudio();
                }
                int duration = ApplicationContext.getRecordingInterval();
                mediaRecorder = getMediaRecorder( duration );
                mediaRecorder.start();
                isRecording = true;
                int sleepDuration = duration;
                if( sleepDuration > 3000 ) {
                    sleepDuration -= 2000;
                } else if( sleepDuration > 2000 ) {
                    sleepDuration -= 1000;
                } else if( sleepDuration > 1000 ) {
                    sleepDuration -= 300;
                } else {
                    sleepDuration = 0;
                }
                logger.i( "Record thread sleeping for " + sleepDuration );
                Thread.sleep( sleepDuration );
                logger.i( "Record thread awake" );

            }
        } catch ( InterruptedException ex ) {
            logger.i( "Recorder has been stopped" );
        } catch ( Throwable ex ) {
            logger.e( "Recorder encountered an error", ex );
        } finally {
            mediaRecorder.stop();
            MP3Record record = mediaRecorder.getRecord();
            FileUtils.deleteQuietly( record.getOutputFile() );
            mediaRecorder = null;
        }
    }

    private void processAudio() {
        mediaRecorder.stop();
        final MP3Record record = mediaRecorder.getRecord();
        mediaRecorder = null;
        Location location = locationRetriever.getLastLocation();
        new Thread( new UploaderRunnable( record, location ) ).start();
    }

    private AudioRecorderModel getMediaRecorder( int duration ) {
        return new AudioRecorderModel( recordDirectory, duration, sampleRate, this );
    }

    @Override
    public void onInfo( MediaRecorder mr, int what, int extra ) {
        if( what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED ) {
            logger.i( "Media recorder max duration reached" );
            try {
                mr.stop();
//                mr.reset();
//                mr.release();
            } catch ( IllegalStateException e ) {
                logger.w( "Recorder already stopped", e );
            } catch ( Throwable e ) {
                logger.e( "Recorder encounter an error while stopping", e );
            } finally {
                isRecording = false;
            }

        }
    }

}