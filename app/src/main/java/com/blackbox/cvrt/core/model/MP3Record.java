package com.blackbox.cvrt.core.model;

import org.joda.time.DateTime;

import java.io.File;
import java.util.Date;

/**
 * @author Jerrick Pua
 */
public class MP3Record {

    private File outputFile;

    private DateTime startDate;

    private DateTime endDate;

    public MP3Record( DateTime startDate, DateTime endDate, File baseDirectory ) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.outputFile = new File( baseDirectory,
                                    String.format( "%s-%s.mp3",
                                                   Record.RECORDER_TIMESTAMP_FORMAT.print(
                                                           startDate ),
                                                   Record.RECORDER_TIMESTAMP_FORMAT
                                                           .print( endDate ) ) );
    }

    public File getOutputFile() {
        return outputFile;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }
}
