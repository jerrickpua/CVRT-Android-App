package com.blackbox.cvrt.core.model;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @author Jerrick Pua
 */
@Deprecated
public class Record {

    public static final DateTimeFormatter RECORDER_TIMESTAMP_FORMAT = DateTimeFormat
            .forPattern( "yyyy.MM.dd'T'HH.mm.ss" );

    private File outputLocation;

    private DateTime start;

    private DateTime end;

    public Record( DateTime start, DateTime end ) {
        this.start = start;
        this.end = end;

    }

    public Record( DateTime start, File outputLocation ) {
        this.start = start;
        this.outputLocation = outputLocation;
    }

    public File getOutputLocation() {
        return outputLocation;
    }

    public void setOutputLocation( File outputLocation ) {
        this.outputLocation = outputLocation;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd( DateTime end ) {
        this.end = end;
    }

    public Record clone() {
        Record model = new Record( start, outputLocation );
        model.setEnd( end );
        return model;
    }

    /*
    *   Return duration in seconds
    *
     */
    public long getDuration() {
        Duration duration = new Duration( start, end );
        return duration.getStandardSeconds();
    }

    public String getTimeStampFileName() {
        return String.format( "%s-%s", start.toDateTimeISO(),
                              end.toDateTimeISO() );
    }

    public String getFileExtension() {
        return FilenameUtils.getExtension( getOutputLocation().getAbsolutePath() );
    }

}
