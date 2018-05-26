package pl.pisze_czytam.polishnews;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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

            Preference newsNumber = findPreference(getString(R.string.news_number_key));
            bindPreferencesSummaryToValue(newsNumber);
            Preference leadContent = findPreference(getString(R.string.lead_content_key));
            bindPreferencesSummaryToValue(leadContent);
        }

        private void bindPreferencesSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof SwitchPreference) {
                leadContentChecked = sharedPreferences.getBoolean(preference.getKey(), true);
                onPreferenceChange(preference, leadContentChecked);
            } else {
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            preference.setSummary(stringValue);
            return true;
        }
    }
}
