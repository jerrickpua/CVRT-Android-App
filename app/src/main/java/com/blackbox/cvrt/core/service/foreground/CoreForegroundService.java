package com.blackbox.cvrt.core.service.foreground;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.blackbox.cvrt.R;
import com.blackbox.cvrt.activities.main.views.GoogleLocation;
import com.blackbox.cvrt.activities.main.views.impl.MainActivity;
import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.service.location.LocationService;
import com.blackbox.cvrt.core.service.location.impl.LocationServiceImpl;
import com.blackbox.cvrt.core.service.recorder.RecorderService;
import com.blackbox.cvrt.core.service.recorder.impl.RecorderServiceImpl;
import com.blackbox.cvrt.core.thread.RecorderRunnable;
import com.blackbox.cvrt.utils.Logger;
import com.blackbox.cvrt.utils.StorageUtils;
import com.google.android.gms.common.ConnectionResult;

import java.io.File;

/**
 * @author Jerrick Pua
 */
public class CoreForegroundService extends Service implements GoogleLocation {

    private static final Logger logger = new Logger( CoreForegroundService.class );
    private RecorderRunnable runnable;
    private int interval = RecorderService.DEFAULT_INTERVAL;
    private RecorderService recorderService;
    private LocationService locationService;
    private Thread recorderThread = null;


    @Nullable
    @Override
    public IBinder onBind( Intent intent ) {
        return null;
    }

    @Override
    public int onStartCommand( Intent intent, int flags, int startId ) {
        Intent notificationIntent = new Intent( this, MainActivity.class );
        PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, notificationIntent, 0 );
        Notification notification = new Notification.Builder( this )
                .setSmallIcon( R.drawable.ic_plane_icon )
                .setContentTitle( "CVRT Blackbox Recorder" )
                .setContentText( "Recording..." )
                .setContentIntent( pendingIntent )
                .build();

        locationService.connectGooglePlayService();
        initRecorderThread( intent );
        recorderThread.start();

        startForeground( 1, notification );
        return START_NOT_STICKY; //do not recreate if service was terminated
    }

    private void initRecorderThread( Intent intent ) {
        int intentInterval = intent.getIntExtra( "interval", -1 );
        if( intentInterval <= 1000 ) {
            intentInterval = interval;
        }
        runnable.setInterval( intentInterval );
        recorderThread = new Thread( runnable );
        recorderThread.setPriority( Thread.MAX_PRIORITY );
    }

    @Override
    public boolean stopService( Intent name ) {
        cleanUp();
        stopForeground( true );
        return super.stopService( name );
    }

    private void cleanUp() {

        try {
            recorderService.stopRecord();
            recorderService.cleanUp();
        } catch ( RecorderStateException ex ) {
            logger.w( "Recorder already stopped", ex );
        }
        Thread temp = recorderThread;
        recorderThread = null;
        temp.interrupt();
        locationService.disconnectGooglePlayService();


    }


    @Override
    public void onCreate() {
        locationService = new LocationServiceImpl( this );
        recorderService = new RecorderServiceImpl( getCacheDir(),
                                                   getRecordingsBaseDirectory(
                                                           RecorderService.DEFAULT_RECORDINGS_FOLDER ),
                                                   locationService );

        runnable = new RecorderRunnable( getApplicationContext(), recorderService );

    }

    private File getRecordingsBaseDirectory( String folderName ) {
        File baseDirectory = new File(
                String.format( "%s/%s", StorageUtils.getStorageBasePath(), folderName ) );
        baseDirectory.mkdirs();
        return baseDirectory;
    }

    @Override
    public void onDestroy() {
        cleanUp();
        super.onDestroy();
    }

    @Override
    public void onConnected( @Nullable Bundle bundle ) {
        locationService.enablePeriodicUpdate();
    }

    @Override
    public void onConnectionSuspended( int i ) {

    }

    @Override
    public void onLocationChanged( Location location ) {
        logger.i( String.format( "Location changed: %s", location.toString() ) );
    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ) {
        logger.w( "Failed to use location api. ERROR:" + connectionResult.getErrorMessage() );

    }
}
