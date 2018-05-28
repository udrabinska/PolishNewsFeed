/* Solution to a bug described here: https://issuetracker.google.com/issues/37091247
 * CheckBoxes in preference weren't initially checked. */
package pl.pisze_czytam.polishnews;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.MultiSelectListPreference;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckedTextView;
import android.widget.ListView;

public class MyCheckBoxList extends MultiSelectListPreference {

    public MyCheckBoxList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyCheckBoxList(Context context) {
        super(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyCheckBoxList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyCheckBoxList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        AlertDialog dialog = (AlertDialog)getDialog();
        if (dialog == null)
            return;

        if (Build.VERSION.SDK_INT >= 23) {
            ListView listView = dialog.getListView();

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    int size = view.getChildCount();
                    for (int i = 0; i < size; i++) {
                        View v = view.getChildAt(i);
                        if (v instanceof CheckedTextView)
                            v.refreshDrawableState();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int size = view.getChildCount();
                    for (int i = 0;  i< size; i++) {
                        View v = view.getChildAt(i);
                        if (v instanceof CheckedTextView)
                            v.refreshDrawableState();
                    }
                }
            });
        }
    }
}
