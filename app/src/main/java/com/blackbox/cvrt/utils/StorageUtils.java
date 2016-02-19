package com.blackbox.cvrt.utils;

import android.os.Environment;

import java.io.File;

/**
 * @author Jerrick Pua
 */
public class StorageUtils {

    public static File getStorageBasePath() {
        String directoryAbsolutePath = null;
        String externalStorageState = Environment.getExternalStorageState();
        if( Environment.MEDIA_MOUNTED.equals( externalStorageState ) ) {
            directoryAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else { //use internal directory
            directoryAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return new File( directoryAbsolutePath );
    }

}
