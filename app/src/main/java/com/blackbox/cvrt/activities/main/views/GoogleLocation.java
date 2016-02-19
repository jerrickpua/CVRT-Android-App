package com.blackbox.cvrt.activities.main.views;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

/**
 * @author Jerrick Pua
 */
public interface GoogleLocation extends GoogleApiClient.OnConnectionFailedListener,
                                        GoogleApiClient.ConnectionCallbacks, LocationListener {
}
