package com.rommelosc.kalpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference defaultImgaePreference = getPreferenceScreen()
				.findPreference("imageDefaultPreference");
		defaultImgaePreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {

						SharedPreferences sharedPreferences = getSharedPreferences(
								"settings", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = sharedPreferences
								.edit();
						editor.putString("imageCameraOrGallery", "");
						editor.putString("imageDefaultPreference",
								newValue.toString());
						editor.commit();

						return true;
					}
				});

	}

}
