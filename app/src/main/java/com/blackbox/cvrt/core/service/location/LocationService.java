package com.blackbox.cvrt.core.service.location;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * @author Jerrick Pua
 */
public interface LocationService extends LocationRetriever {

    public void connectGooglePlayService();

    public void disconnectGooglePlayService();

    public boolean isConnecting();

    public boolean isConnected();

    public GoogleApiClient getGoogleApiClient();

    public void enablePeriodicUpdate();

    public void disablePeriodicUpdate();

}
