package com.example.miguel.guiadusseldorf.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.miguel.guiadusseldorf.R;

/**
 * Fragment for manage the preferences.
 */
public class Preferences extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
