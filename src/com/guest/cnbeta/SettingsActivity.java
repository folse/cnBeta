package com.guest.cnbeta;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.guest.cnbeta.util.DataEngine;

public class SettingsActivity extends PreferenceActivity {

	DataEngine dataEngine;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
		
		dataEngine = new DataEngine(SettingsActivity.this);
		
		final CheckBoxPreference loadImgCheckBox = (CheckBoxPreference)findPreference("apply_loadImg");
		loadImgCheckBox.setChecked(dataEngine.getIsLoadImg());
		loadImgCheckBox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				loadImgCheckBox.setChecked((Boolean)newValue);
				dataEngine.setIsLoadImg((Boolean)newValue);
				
				return false;
			}
		});
		
	}

}
