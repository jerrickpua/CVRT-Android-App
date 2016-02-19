package com.blackbox.cvrt.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * @author Jerrick Pua
 */
public class GPSUtils {

    public static boolean isLocationServiceEnabled( Context context ) {
        LocationManager service = ( LocationManager ) context.getSystemService( context.LOCATION_SERVICE );
        boolean isEnabled = service.isProviderEnabled( LocationManager.GPS_PROVIDER );
        return isEnabled;
    }
}
