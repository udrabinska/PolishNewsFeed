package pl.pisze_czytam.polishnews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    public static boolean leadContentChecked;

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

            Preference newsNumber = findPreference(getString(R.string.news_number_key));
            bindPreferencesToValue(newsNumber);

            Preference leadContentPref = findPreference(getString(R.string.lead_content_key));
            bindPreferencesToValue(leadContentPref);
        }

        private void bindPreferencesToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof SwitchPreference) {
                boolean isSwitched = sharedPreferences.getBoolean(preference.getKey(), true);
                onPreferenceChange(preference, isSwitched);
            } else {
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            if (preference instanceof SwitchPreference) {
                if (stringValue.equals("false")) {
                    leadContentChecked = false;
                } else {
                    leadContentChecked = true;
                }
            }
                preference.setSummary(stringValue);

            return true;
        }
    }
}

