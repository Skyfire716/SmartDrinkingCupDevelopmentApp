package com.jonas.weigand.thesis.smartdrinkingcup;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

public class AD5932SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}