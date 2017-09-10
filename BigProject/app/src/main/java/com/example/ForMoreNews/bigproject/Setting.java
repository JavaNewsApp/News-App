package com.example.ForMoreNews.bigproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;

public class Setting extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    private SwitchPreference [] ss = new SwitchPreference[13];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
    }

    private void initPreferences() {
        for(int i = 1; i <= 12; i++) {
            ss[i] = (SwitchPreference) findPreference("s" + i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra("gl","Good Luck!");
        setResult(RESULT_OK, i);
        finish();
    }

}