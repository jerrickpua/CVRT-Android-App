package com.blackbox.cvrt.core.thread;

import com.blackbox.cvrt.core.exception.RecordException;
import com.blackbox.cvrt.core.model.Record;
import com.blackbox.cvrt.utils.Logger;
import com.blackbox.cvrt.utils.WaveUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jerrick Pua
 */
@Deprecated
public class RawAudioProcessorRunnable implements Runnable {
    private static final Logger logger = new Logger( RawAudioProcessorRunnable.class );
    private Record record;
    private File recordDirectory;
    private final int sampleRate;

    public RawAudioProcessorRunnable( Record record, File recordDirectory, int sampleRate ) {
        this.record = record;
        this.recordDirectory = recordDirectory;
        this.sampleRate = sampleRate;
    }

    @Override
    public void run() {
        processRecordData( record );
    }

    private void processRecordData( Record data ) {
        File cachePCMFile = data.getOutputLocation();
        File outputFile = new File( recordDirectory,
                String.format( "%s.wav", data.getTimeStampFileName() ) );
        try ( FileInputStream fis = new FileInputStream( cachePCMFile );
              OutputStream fos = new FileOutputStream( outputFile ) ) {
            logger.i( "Parsing raw file to audio" );
            WaveUtils.parseRawFileToWav( fis, fos, sampleRate );
        } catch ( IOException ex ) {
            logger.e( "Unable to prase raw audio to wav", ex );
            throw new RecordException( ex );
        } finally {
            FileUtils.deleteQuietly( cachePCMFile );
        }


    }


}