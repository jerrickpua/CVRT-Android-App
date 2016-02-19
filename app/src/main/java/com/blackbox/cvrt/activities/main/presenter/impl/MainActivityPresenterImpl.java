package com.blackbox.cvrt.activities.main.presenter.impl;

import android.content.Intent;

import com.blackbox.cvrt.activities.main.presenter.MainActivityPresenter;
import com.blackbox.cvrt.activities.main.views.impl.MainActivity;
import com.blackbox.cvrt.core.exception.RecorderStateException;
import com.blackbox.cvrt.core.service.foreground.CoreForegroundService;
import com.blackbox.cvrt.utils.ServiceUtils;

/**
 * Created by test on 2/7/16.
 */
public class MainActivityPresenterImpl implements MainActivityPresenter {

    private final MainActivity activity;

    private final Intent recordIntent;


    public MainActivityPresenterImpl( MainActivity activity ) {
        this.activity = activity;
        recordIntent = new Intent( activity, CoreForegroundService.class );
    }

    @Override
    public void stopRecordScheduler() {
        try {
            activity.stopService( recordIntent );
            activity.stopRecord();
        } catch ( Throwable throwable ) {
            activity.onActivityError( throwable );
        }
    }

    @Override
    public void onDestroy() {
        activity.stopService( recordIntent );
    }

    @Override
    public void startRecordScheduler( int interval ) {
        try {
            if( ServiceUtils.isServiceRunning( activity, CoreForegroundService.class ) ) {
                throw new RecorderStateException( "Recorder is already running" );
            }
            activity.startService( recordIntent );
            activity.doRecord();
        } catch ( Throwable throwable ) {
            activity.onActivityError( throwable );
        }
    }

}
