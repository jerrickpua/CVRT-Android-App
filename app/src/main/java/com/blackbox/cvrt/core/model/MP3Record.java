package com.blackbox.cvrt.core.model;

import com.blackbox.cvrt.utils.DateUtils;

import org.joda.time.DateTime;

import java.io.File;

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
                                    String.format( "%s-%s.mp3", startDate
                                            .toString( DateUtils.DEFAULT_TIME_STAMP_FORMAT ),
                                                   endDate.toString(
                                                           DateUtils.DEFAULT_TIME_STAMP_FORMAT ) ) );
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
