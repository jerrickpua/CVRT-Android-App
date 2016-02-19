package com.blackbox.cvrt.activities.settings.views.impl;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.blackbox.cvrt.R;

/**
 * @author Jerrick Pua
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        addPreferencesFromResource( R.xml.preference );
    }


}
