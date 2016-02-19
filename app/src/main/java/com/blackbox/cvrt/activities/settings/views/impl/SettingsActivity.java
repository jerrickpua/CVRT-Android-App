package com.blackbox.cvrt.activities.settings.views.impl;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

/**
 * @author Jerrick Pua
 */
public class SettingsActivity extends Activity {
    @Override
    public void onCreate( Bundle savedInstanceState, PersistableBundle persistentState ) {
        super.onCreate( savedInstanceState, persistentState );
        getFragmentManager().beginTransaction()
                            .replace( android.R.id.content, new SettingsFragment() ).commit();
    }
}
