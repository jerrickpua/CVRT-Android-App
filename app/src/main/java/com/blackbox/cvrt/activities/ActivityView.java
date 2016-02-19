package com.blackbox.cvrt.activities;

/**
 * Created by test on 2/7/16.
 */
public interface ActivityView {

    public void onActivityDestroy();

    public void onActivityResume();

    public void onActivityCreate();

    public void onActivityError( Throwable throwable );
}
