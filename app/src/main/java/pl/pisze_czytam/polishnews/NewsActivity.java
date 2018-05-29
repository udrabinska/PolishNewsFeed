package pl.pisze_czytam.polishnews;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    String requestWithoutKey = "https://content.guardianapis.com/world/poland?show-fields=trailText,byline,thumbnail";
    String apiKey = BuildConfig.ApiKey;
    ListView newsList;
    TextView emptyView;
    ProgressBar progressBar;
    private NewsAdapter newsAdapter;
    private static final int NEWS_LOADER_ID = 1;
    public static boolean leadContentChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        newsList = findViewById(R.id.list);
        emptyView = findViewById(R.id.empty_view);
        progressBar = findViewById(R.id.progress_bar);

        newsList.setEmptyView(emptyView);
        newsAdapter = new NewsAdapter(this, R.layout.news_activity, new ArrayList<News>());
        newsList.setAdapter(newsAdapter);

        // check if preference is switched on to know, if leadContent should be loaded too
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        leadContentChecked = sharedPreferences.getBoolean(getString(R.string.lead_content_key), true);
        PreferenceManager.setDefaultValues(this, R.xml.settings_fragment, false);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            getLoaderManager().initLoader(NEWS_LOADER_ID, null, this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet);
            emptyView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.wifi_off), null, null);
        }

        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentNews = newsAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                startActivity(new Intent(Intent.ACTION_VIEW, newsUri));
            }
        });
    }

    @Override
    public Loader<List<News>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String newsNumber = sharedPreferences.getString(getString(R.string.news_number_key),
                getString(R.string.news_number_default));

        Uri baseUri = Uri.parse(requestWithoutKey);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("page-size", newsNumber);

        // Get the selected sections.
        Set<String> sectionsToInclude = sharedPreferences.getStringSet(getString(R.string.check_sections_key), new HashSet<String>());
        String[] sections = sectionsToInclude.toArray(new String[sectionsToInclude.size()]);
        StringBuilder addToQuery = new StringBuilder();
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i];
            addToQuery.append(section);
                if (i < sections.length - 1) {
                    String prefix = ",";
                    addToQuery.append(prefix);
                }
        }

            String query = addToQuery.toString();
            if (!query.equals("")) {
                uriBuilder.appendQueryParameter("section", addToQuery.toString());
            }
            uriBuilder.appendQueryParameter("api-key", apiKey);
            return new NewsLoader(this, uriBuilder.toString());
        }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        emptyView.setText(R.string.no_news);
        emptyView.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.scan_wifi), null, null);
        newsAdapter.clear();

        if (newsList != null && !newsList.isEmpty()) {
            newsAdapter.addAll(newsList);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        newsAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
