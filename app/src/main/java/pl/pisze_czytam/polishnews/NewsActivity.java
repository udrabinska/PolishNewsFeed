package pl.pisze_czytam.polishnews;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {
    String requestWithoutKey = "https://content.guardianapis.com/world/poland?page-size=20&show-fields=trailText,byline,thumbnail&";
    String apiKey = BuildConfig.ApiKey;
    private final String REQUEST_URL = requestWithoutKey + apiKey;
    ListView newsList;
    TextView emptyView;
    ProgressBar progressBar;
    private NewsAdapter newsAdapter;

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

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            getLoaderManager().initLoader(0, null, this);
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
        return new NewsLoader(this, REQUEST_URL);
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
}
