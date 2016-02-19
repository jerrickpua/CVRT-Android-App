package com.blackbox.cvrt.core.thread;

import com.blackbox.cvrt.core.exception.RecordException;
import com.blackbox.cvrt.core.model.Record;
import com.blackbox.cvrt.utils.Logger;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Jerrick Pua
 */
@Deprecated
public class FileMoverRunnable implements Runnable {
    private static final Logger logger = new Logger( FileMoverRunnable.class );
    private final Record record;
    private final File outputDirectory;

    public FileMoverRunnable( Record record, File outputDirectory ) {
        this.record = record;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run() {
        try {
            File output = new File( outputDirectory, String.format( "%s.%s",
                    record.getTimeStampFileName(),
                    record.getFileExtension() ) );
            logger.d( String.format( "Moving file from '%s' to location '%s'",
                    record.getOutputLocation().getAbsolutePath(), output.getAbsolutePath() ) );
            FileUtils.moveFile( record.getOutputLocation(), output );
        } catch ( IOException ex ) {
            logger.e( "Unable to move file", ex );
            throw new RecordException( ex );
        }


    }
}
