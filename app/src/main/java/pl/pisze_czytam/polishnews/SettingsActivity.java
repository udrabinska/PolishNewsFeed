package pl.pisze_czytam.polishnews;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.BaseAdapter;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static pl.pisze_czytam.polishnews.NewsActivity.leadContentChecked;

public class SettingsActivity extends AppCompatActivity {
    static Calendar calendar = Calendar.getInstance();
    public static final int DATE_PICKER_FROM = 0;
    public static final int DATE_PICKER_TO = 1;
    static int from_year, from_month, from_day, to_year, to_month, to_day;
    static DatePickerDialog.OnDateSetListener from_dateListener, to_dateListener;
    static SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        dateFormat = new SimpleDateFormat(getString(R.string.date_format), Locale.GERMAN);
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_fragment);

            Preference fromDate = findPreference(getString(R.string.from_date_key));
            Preference toDate = findPreference(getString(R.string.to_date_key));
            long currentTimeInMillis = System.currentTimeMillis();
            SimpleDateFormat dateFormatter = new SimpleDateFormat(getString(R.string.date_format), Locale.GERMAN);
            String dateString = dateFormatter.format(currentTimeInMillis);

            String[] preferencesKeys = getResources().getStringArray(R.array.preferences_keys);
            for (String preferenceKey : preferencesKeys) {
                Preference preference = findPreference(preferenceKey);
                bindPreferencesSummaryToValue(preference);
            }
            fromDate.setSummary(getString(R.string.default_date_start));
            toDate.setSummary(dateString);

            from_dateListener = new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    Preference dateFrom = findPreference(getString(R.string.from_date_key));
                    dateFrom.setSummary(dateFormat.format(calendar.getTime()));
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dateFrom.getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.from_date_key), dateFormat.format(calendar.getTime()));
                    editor.apply();
                }
            };

            to_dateListener = new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, day);
                    Preference dateTo = findPreference(getString(R.string.to_date_key));
                    dateTo.setSummary(dateFormat.format(calendar.getTime()));
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(dateTo.getContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.to_date_key), dateFormat.format(calendar.getTime()));
                    editor.apply();
                }
            };
        }

        private void bindPreferencesSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            preference.setOnPreferenceClickListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof SwitchPreference) {
                leadContentChecked = sharedPreferences.getBoolean(preference.getKey(), true);
                onPreferenceChange(preference, leadContentChecked);
            } else if (preference instanceof MyMultiSelectListPreference) {
                Object value = sharedPreferences.getStringSet(preference.getKey(), new HashSet<String>());
                onPreferenceChange(preference, value);
            } else {
                String preferenceString = sharedPreferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof MyMultiSelectListPreference)) {
                String stringValue = newValue.toString();
                preference.setSummary(stringValue);
            } else {
                CharSequence summary = preference.getSummary();
                preference.setSummary(summary.toString());
            }
            return true;
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference == findPreference(getString(R.string.from_date_key))) {
                showDateDialog(DATE_PICKER_FROM);
            } else if (preference == findPreference(getString(R.string.to_date_key))) {
                showDateDialog(DATE_PICKER_TO);
            }
            return false;
        }

        private void showDateDialog(int id) {
            // Find the current date at the first launch of the calendar.
            from_year = calendar.get(Calendar.YEAR);
            from_month = calendar.get(Calendar.MONTH);
            from_day = calendar.get(Calendar.DAY_OF_MONTH);
            to_year = calendar.get(Calendar.YEAR);
            to_month = calendar.get(Calendar.MONTH);
            to_day = calendar.get(Calendar.DAY_OF_MONTH);

            switch (id) {
                case DATE_PICKER_FROM:
                    new DatePickerDialog(getActivity(), from_dateListener, from_year, from_month, from_day).show();
                    break;
                case DATE_PICKER_TO:
                    new DatePickerDialog(getActivity(), to_dateListener, to_year, to_month, to_day).show();
                    break;
            }
        }

        // Clear dates when user set them, but kill the app while being on settings.
        @Override
        public void onStop() {
            super.onStop();
            String[] datesKeys = getResources().getStringArray(R.array.dates_keys);
            for (String dateKey : datesKeys) {
                Preference preference = findPreference(dateKey);
                SharedPreferences savedDate = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                SharedPreferences.Editor editor = savedDate.edit();
                editor.remove(preference.getKey()).apply();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, NewsActivity.class));
    }
}
