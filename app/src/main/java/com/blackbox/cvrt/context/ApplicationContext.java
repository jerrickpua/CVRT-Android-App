package com.blackbox.cvrt.context;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.blackbox.cvrt.core.service.recorder.RecorderService;

/**
 * @author Jerrick Pua
 */
public class ApplicationContext extends Application {

    private static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences( this );
    }

    public static int getRecordingInterval() {
        return Integer.parseInt( prefs.getString( "recording_interval", String.valueOf(
                RecorderService.DEFAULT_INTERVAL ) ) ) * 60000;

    }

    public static String getServerUrl() {
        return prefs.getString( "server_url", null );

    }

    public static int getLocationServiceFastestUpdateInterval() {
        return Integer
                .parseInt( prefs.getString( "location_service_fastest_update_interval", "2500" ) );

    }

    public static int getLocationServiceUpdateInterval() {
        return Integer.parseInt( prefs.getString( "location_service_update_interval", "5000" ) );

    }
}
