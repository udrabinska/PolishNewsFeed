package pl.pisze_czytam.polishnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static pl.pisze_czytam.polishnews.NewsActivity.leadContentChecked;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);

            String[] preferencesKeys = getResources().getStringArray(R.array.preferences_keys);
            for (String preferenceKey : preferencesKeys) {
                Preference preference = findPreference(preferenceKey);
                bindPreferencesSummaryToValue(preference);
            }
        }

        private void bindPreferencesSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof SwitchPreference) {
                leadContentChecked = sharedPreferences.getBoolean(preference.getKey(), true);
                onPreferenceChange(preference, leadContentChecked);
            } else if (preference instanceof MultiSelectListPreference) {
                Object value = sharedPreferences.getStringSet(preference.getKey(), new HashSet<String>());
                onPreferenceChange(preference, value);
            } else {
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof MultiSelectListPreference)) {
                String stringValue = newValue.toString();
                preference.setSummary(stringValue);
            }
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent (SettingsActivity.this, NewsActivity.class));
    }
}
