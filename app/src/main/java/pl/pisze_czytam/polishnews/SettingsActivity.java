package pl.pisze_czytam.polishnews;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;

import static pl.pisze_czytam.polishnews.NewsActivity.leadContentChecked;

public class SettingsActivity extends AppCompatActivity {
    static Calendar calendar = Calendar.getInstance();
    public static final int DATE_PICKER_FROM = 0;
    public static final int DATE_PICKER_TO = 1;
    int from_year, from_month, from_day, to_year, to_month, to_day;
    DatePickerDialog.OnDateSetListener from_dateListener, to_dateListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener,
            DatePickerDialog.OnDateSetListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);

            String[] preferencesKeys = getResources().getStringArray(R.array.preferences_keys);
            for (String preferenceKey : preferencesKeys) {
                Preference preference = findPreference(preferenceKey);
                bindPreferencesSummaryToValue(preference);
            }

//            Preference dateFrom = findPreference(getString(R.string.from_date_key));
//            dateFrom.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    showDateDialog();
//                    return false;
//                }
//            });
            String[] dateKeys = getResources().getStringArray(R.array.date_keys);
            for (String dateKey : dateKeys) {
                Preference preference = findPreference(dateKey);
                preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        showDateDialog();
                        return false;
                    }
                });
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
            } else if (preference instanceof EditTextPreference) {
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            } else {
                String datePreference = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, datePreference);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof MultiSelectListPreference)) {
                String stringValue = newValue.toString();
                preference.setSummary(stringValue);
//            } else {
//                String dateFormat = "yyyy-MM-dd";
//                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
//                preference.setSummary(sdf.format(newValue.toString()));
            }
            return true;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);

            Preference dateFrom = findPreference(getString(R.string.from_date_key));
            Preference dateTo = findPreference(getString(R.string.to_date_key));
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.GERMAN);
            dateFrom.setSummary(sdf.format(calendar.getTime()));
            dateTo.setSummary(sdf.format(calendar.getTime()));
        }

        private void showDateDialog(){
            // Show the current date at the launching the calendar.
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            new DatePickerDialog(getActivity(),this, year, month, day).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent (SettingsActivity.this, NewsActivity.class));
    }
}
