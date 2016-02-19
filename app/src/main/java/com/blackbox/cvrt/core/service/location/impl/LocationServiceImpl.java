package com.blackbox.cvrt.core.service.location.impl;

import android.content.Context;
import android.location.Location;

import com.blackbox.cvrt.activities.main.views.GoogleLocation;
import com.blackbox.cvrt.context.ApplicationContext;
import com.blackbox.cvrt.core.service.location.LocationService;
import com.blackbox.cvrt.utils.Logger;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * @author Jerrick Pua
 */
public class LocationServiceImpl implements LocationService, LocationListener {

    public static final LocationRequest LOCATION_REQUEST;

    private Location lastKnownLocation;

    static {
        LOCATION_REQUEST = new LocationRequest();
        LOCATION_REQUEST.setInterval( ApplicationContext.getLocationServiceUpdateInterval() );
        LOCATION_REQUEST
                .setFastestInterval( ApplicationContext.getLocationServiceFastestUpdateInterval() );
        LOCATION_REQUEST.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    private static final Logger logger = new Logger( LocationServiceImpl.class );

    private final GoogleLocation googleLocation;

    private GoogleApiClient googleApiClient;

    public LocationServiceImpl( Context context ) {
        if( !( context instanceof GoogleLocation ) ) {
            throw new IllegalArgumentException(
                    "Context must implement " + GoogleLocation.class.getCanonicalName() );
        }
        this.googleLocation = ( GoogleLocation ) context;
        googleApiClient = new GoogleApiClient.Builder( context )
                .addOnConnectionFailedListener( googleLocation )
                .addConnectionCallbacks( googleLocation ).addApi(
                        LocationServices.API ).build();
    }


    @Override
    public void connectGooglePlayService() {
        googleApiClient.connect();
    }

    @Override
    public boolean isConnecting() {
        return googleApiClient.isConnecting();
    }

    @Override
    public Location getLastLocation() {
        if( LocationServices.FusedLocationApi.getLocationAvailability( googleApiClient ) == null ) {
            logger.i( "Google location service disconnected. Using last known location" );
            return lastKnownLocation;
        }
        return LocationServices.FusedLocationApi.getLastLocation( googleApiClient );
    }

    @Override
    public boolean isConnected() {
        return googleApiClient.isConnected();
    }

    @Override
    public void disconnectGooglePlayService() {
        disablePeriodicUpdate();
        googleApiClient.disconnect();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void enablePeriodicUpdate() {
        LocationServices.FusedLocationApi
                .requestLocationUpdates( googleApiClient, LOCATION_REQUEST, googleLocation );
    }

    public void disablePeriodicUpdate() {
        LocationServices.FusedLocationApi.removeLocationUpdates( googleApiClient, googleLocation );
    }

    @Override
    public void onLocationChanged( Location location ) {
        lastKnownLocation = location;
    }
}
