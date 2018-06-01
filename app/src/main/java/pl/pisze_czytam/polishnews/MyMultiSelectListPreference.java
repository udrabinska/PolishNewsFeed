/* A custom subclass created to handle nice summary of MultiSelectListPrefence. */
package pl.pisze_czytam.polishnews;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

public class MyMultiSelectListPreference extends MultiSelectListPreference {
    public MyMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyMultiSelectListPreference(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        CharSequence cs = super.getSummary();
        String summary = cs.toString();

        if (summary.contains("%s")) {
            String text = "";
            StringBuilder builder = new StringBuilder();
            CharSequence[] entries = getEntries();
            if (entries.length > 0) {
                CharSequence[] entryValues = getEntryValues();
                Set<String> values = getValues();
                int pos = 0;

                for (String value : values) {
                    pos++;
                    int index = -1;
                    for (int i = 0; i < entryValues.length; i++) {
                        if (entryValues[i].equals(value)) {
                            index = i;
                            break;
                        }
                    }
                    builder.append(entries[index]);
                    if (pos < values.size())
                        builder.append(", ");
                }
                text = builder.toString();
            }
            summary = String.format(summary, text);
        }
        return summary;
    }
}
