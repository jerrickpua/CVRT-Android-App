package com.blackbox.cvrt.activities.main.views.impl;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.blackbox.cvrt.R;
import com.blackbox.cvrt.activities.main.presenter.MainActivityPresenter;
import com.blackbox.cvrt.activities.main.presenter.impl.MainActivityPresenterImpl;
import com.blackbox.cvrt.activities.settings.views.impl.SettingsActivity;
import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.service.location.impl.LocationServiceImpl;
import com.blackbox.cvrt.utils.Logger;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends ActionBarActivity implements OnClickListener,
                                                               GoogleApiClient.OnConnectionFailedListener,
                                                               GoogleApiClient.ConnectionCallbacks {

    private static final Logger logger = new Logger( MainActivity.class );

    private Button record;

    private Button stop;

    private Button showLocation;

    private MainActivityPresenter presenter;

    private GoogleApiClient googleApiClient;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static final int REQUEST_CHECK_SETTINGS = 1002;


    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        record = ( Button ) findViewById( R.id.btn_record );
        stop = ( Button ) findViewById( R.id.btn_stop_recording );
        showLocation = ( Button ) findViewById( R.id.btn_show_location );
        showLocation.setOnClickListener( this );

        record.setOnClickListener( this );
        stop.setOnClickListener( this );
        presenter = new MainActivityPresenterImpl( this );
        mResolvingError = savedInstanceState != null && savedInstanceState
                .getBoolean( STATE_RESOLVING_ERROR, false );
        googleApiClient = new GoogleApiClient.Builder( this ).addOnConnectionFailedListener( this )
                                                             .addConnectionCallbacks( this )
                                                             .addApi( LocationServices.API )
                                                             .build();

    }


    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if( id == R.id.action_settings ) {
            Intent intent = new Intent( this, SettingsActivity.class );
            startActivity( intent );
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onClick( View v ) {
        switch ( v.getId() ) {
            case R.id.btn_record:
                presenter.startRecordScheduler( 60 );
                break;
            case R.id.btn_stop_recording:
                presenter.stopRecordScheduler();
                break;
            case R.id.btn_show_location:
                try {

                    Location loc = LocationServices.FusedLocationApi
                            .getLastLocation( googleApiClient );
                    Toast.makeText( this,
                                    String.format( "Longitude: %s Latitude: %s", loc.getLongitude(),
                                                   loc.getLatitude() ), Toast.LENGTH_SHORT ).show();
                } catch ( Exception ex ) {
                    Toast.makeText( this, "Woops no location yet", Toast.LENGTH_SHORT ).show();
                }


        }
    }

    public void doRecord() {
        Toast.makeText( getApplicationContext(), "Recording starts", Toast.LENGTH_SHORT ).show();
    }

    public void stopRecord() {
        Toast.makeText( getApplicationContext(), "Recording stopped", Toast.LENGTH_SHORT ).show();
    }

    public void onActivityError( Throwable throwable ) {
        if( throwable instanceof RecorderStateException ) {
            Toast.makeText( getApplicationContext(), throwable.getMessage(), Toast.LENGTH_SHORT )
                 .show();
        } else
            Toast.makeText( getApplicationContext(),
                            "Opps! There's an error. " + throwable.getMessage(),
                            Toast.LENGTH_SHORT ).show();
    }

    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected( Bundle bundle ) {
        checkLocationSettings();
    }

    @Override
    public void onConnectionSuspended( int i ) {

    }

    @Override
    public void onConnectionFailed( ConnectionResult result ) {
        if( mResolvingError ) {
            // Already attempting to resolve an error.
            return;
        } else if( result.hasResolution() ) {
            try {
                mResolvingError = true;
                result.startResolutionForResult( this, REQUEST_RESOLVE_ERROR );
            } catch ( IntentSender.SendIntentException e ) {
                logger.e(
                        "There was an error with the resolution intent. Trying again to connect..." );
                googleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog( result.getErrorCode() );
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog( int errorCode ) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt( DIALOG_ERROR, errorCode );
        dialogFragment.setArguments( args );
        dialogFragment.show( getFragmentManager(), "errordialog" );
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog( Bundle savedInstanceState ) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt( DIALOG_ERROR );
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR );
        }

        @Override
        public void onDismiss( DialogInterface dialog ) {
            ( ( MainActivity ) getActivity() ).onDialogDismissed();
        }
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        switch ( requestCode ) {
            case REQUEST_RESOLVE_ERROR:
                mResolvingError = false;
                if( resultCode == RESULT_OK ) {
                    if( !googleApiClient.isConnecting() && !googleApiClient.isConnected() ) {
                        googleApiClient.connect();
                    }
                }
                break;
            case REQUEST_CHECK_SETTINGS:
                Toast.makeText( this, "Opps", Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    protected void onSaveInstanceState( Bundle outState ) {
        super.onSaveInstanceState( outState );
        outState.putBoolean( STATE_RESOLVING_ERROR, mResolvingError );
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest( LocationServiceImpl.LOCATION_REQUEST );
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                .checkLocationSettings( googleApiClient,
                                        builder.build() );
        result.setResultCallback( new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult( LocationSettingsResult result ) {
                final Status status = result.getStatus();
                final LocationSettingsStates states = result
                        .getLocationSettingsStates();
                switch ( status.getStatusCode() ) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult( MainActivity.this
                                    , REQUEST_CHECK_SETTINGS );
                        } catch ( IntentSender.SendIntentException e ) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        } );
    }
}
