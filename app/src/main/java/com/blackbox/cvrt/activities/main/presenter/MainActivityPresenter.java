package com.blackbox.cvrt.activities.main.presenter;

import com.blackbox.cvrt.core.service.location.LocationService;

/**
 * Created by test on 2/7/16.
 */
public interface MainActivityPresenter  {


    public void startRecordScheduler( int interval );

    public void stopRecordScheduler();

    public void onDestroy();


}
