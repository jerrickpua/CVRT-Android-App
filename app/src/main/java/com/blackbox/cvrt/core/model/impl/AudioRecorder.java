package com.blackbox.cvrt.core.model.impl;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.blackbox.cvrt.core.exception.RecordException;
import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.model.Record;
import com.blackbox.cvrt.core.model.Recorder;
import com.blackbox.cvrt.utils.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Jerrick Pua
 */
@Deprecated
public class AudioRecorder implements Recorder {

    private static final Logger logger = new Logger( AudioRecorder.class );

    private final int sampleRate;

    private final int channel;

    private final int encoding;

    private final int bufferSize;

    private FileOutputStream outputStream;

    private final File cacheDirectory;

    private AudioRecord recorder;

    private Thread recordThread;

    private Record record;

    private volatile boolean state = false;


    public AudioRecorder( int sampleRate, int channel, int encoding, File cacheDirectory ) {
        this.cacheDirectory = cacheDirectory;
        this.sampleRate = sampleRate;
        this.channel = channel;
        this.encoding = encoding;
        this.bufferSize = AudioRecord.getMinBufferSize( sampleRate, channel, encoding );
    }

    private AudioRecord getRecorder() {
        if( recorder == null )
            recorder = new AudioRecord( MediaRecorder.AudioSource.MIC, sampleRate, channel, encoding, bufferSize );
        return recorder;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannel() {
        return channel;
    }

    public int getEncoding() {
        return encoding;
    }

    public int getBufferSize() {
        return bufferSize;
    }


    public synchronized boolean isRecording() {
        return state;
    }

    private OutputStream getOutputStream() {
        return outputStream;
    }

    private void writeAudioToOutputStream() throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream( getOutputStream() );
        byte[] data = new byte[ bufferSize ];
        while ( state && !Thread.interrupted() ) {
            int result = recorder.read( data, 0, bufferSize );
            if( result == AudioRecord.ERROR_BAD_VALUE && result == AudioRecord.ERROR_INVALID_OPERATION ) {
                logger.d( String.format( "Unable to read from recorder. Status code: %s", result ) );
                continue;
            }
            IOUtils.write( data, bos );
        }
    }

    public synchronized Record stop() {
        if( !isRecording() ) {
            logger.i( "Recorder already stopped" );
            throw new RecorderStateException( "Recording already stopped" );
        }
        state = false;
        cleanUpCurrentThread();
        cleanUpRecorder();
        try {
            record.setEnd( new DateTime() );
            return record;
        } finally {
            cleanUp();
        }
    }

    public synchronized void start() {
        if( isRecording() ) {
            logger.i( "Recorder already started" );
            throw new RecorderStateException( "Recording already started." );
        }


        recorder = getRecorder();
//        isRecorderInitialized();
        final File rawFile = new File( cacheDirectory, UUID.randomUUID().toString() + ".raw" );
        try {
            state = true;
            outputStream = new FileOutputStream( rawFile );
            recorder.startRecording();
            recordThread = new Thread( new Runnable() {
                @Override
                public void run() {
                    try {
                        writeAudioToOutputStream();
                    } catch ( IOException ex ) {
                        FileUtils.deleteQuietly( rawFile );
                        cleanUp();
                        throw new RuntimeException( ex );
                    }
                }

            } );
            recordThread.setPriority( Thread.MAX_PRIORITY );
            recordThread.start();
            record = new Record( new DateTime(), rawFile );
        } catch ( Throwable ex ) {
            logger.e( "Unable to start recorder", ex );
            FileUtils.deleteQuietly( rawFile );
            cleanUp();
            throw new RecordException( ex );
        }
    }

    public void cleanUp() {
        state = false;
        cleanUpCurrentThread();
        cleanUpRecorder();
        cleanUpOutputStream();
        cleanUpRecordModel();
    }

    private void cleanUpCurrentThread() {
        if( recordThread != null ) {
            Thread temp = recordThread;
            recordThread = null;
            temp.interrupt();
        }
    }

    private void cleanUpRecorder() {
        if( recorder != null ) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
    }

    private void cleanUpOutputStream() {
        if( outputStream != null ) {
            IOUtils.closeQuietly( outputStream );
            outputStream = null;
        }
    }

    private void cleanUpRecordModel() {
        if( record != null )
            record = null;
    }

}
