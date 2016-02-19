package com.blackbox.cvrt.utils;

import android.util.Log;

import org.joda.time.DateTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jerrick Pua
 */
public class Logger {
    private String className;

    public Logger( Class clazz ) {
        this.className = clazz.getCanonicalName();
    }

    public int v( String msg ) {
        appendLog( String.format( "[VERBOSE] %s", msg ) );
        return Log.v( className, msg );
    }

    public int v( String msg, Throwable tr ) {
        appendLog( msg, tr );
        return Log.v( className, msg, tr );
    }

    public int d( String msg ) {
        appendLog( msg );
        return Log.d( className, msg );
    }

    public int d( String msg, Throwable tr ) {
        appendLog( msg, tr );
        return Log.d( className, msg, tr );
    }


    public int i( String msg ) {
        appendLog( msg );
        return Log.i( className, msg );
    }

    public int i( String msg, Throwable tr ) {
        appendLog( msg, tr );
        return Log.i( className, msg, tr );
    }


    public int w( String msg ) {
        appendLog( msg );
        return Log.w( className, msg );
    }


    public int w( String msg, Throwable tr ) {
        appendLog( msg, tr );
        return Log.w( className, msg, tr );
    }

    public int w( Throwable tr ) {
        appendLog( "", tr );
        return Log.w( className, tr );
    }

    public int e( String msg ) {
        appendLog( msg );
        return Log.e( className, msg );
    }

    public int e( String msg, Throwable tr ) {
        appendLog( msg, tr );
        return Log.e( className, msg, tr );
    }

    public int println( int priority, String msg ) {
        appendLog( String.format( "Priority %s %s", priority, msg ) );
        return Log.println( priority, className, msg );
    }

    private void appendLog( String text, Throwable tr ) {
        if( tr == null ) {
            appendLog( text );
            return;
        }
        appendLog( String.format( "%s ERROR: %s", text, Log.getStackTraceString( tr ) ) );
    }

    private void appendLog( String text ) {
        text = String.format( "%s [%s] %s", new DateTime().toString(), className, text );
        File logFile = new File(
                StorageUtils.getStorageBasePath().getAbsolutePath() + "/recorderlogs" );
        logFile.mkdir();
        logFile = new File( logFile, "logs.txt" );
        if( !logFile.exists() ) {
            try {
                logFile.createNewFile();
            } catch ( IOException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter( new FileWriter( logFile, true ) );
            buf.append( text );
            buf.newLine();
            buf.newLine();
            buf.flush();
            buf.close();
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
