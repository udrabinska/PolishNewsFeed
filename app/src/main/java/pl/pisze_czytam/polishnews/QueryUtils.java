package pl.pisze_czytam.polishnews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static pl.pisze_czytam.polishnews.SettingsActivity.leadContentChecked;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static ArrayList<News> newsList = new ArrayList<>();

    private QueryUtils() {
    }

    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream.", e);
        }
        return extractResultsFromJSON(jsonResponse);
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem with building URL.", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(15000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results with news.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader streamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader lineReader = new BufferedReader(streamReader);
            String line = lineReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = lineReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    private static ArrayList<News> extractResultsFromJSON(String newsJson) {
        if (TextUtils.isEmpty(newsJson)) {
            return null;
        }
        try {
            JSONObject rootObject = new JSONObject(newsJson);
            JSONObject response = rootObject.optJSONObject("response");
            JSONArray resultsArray = response.optJSONArray("results");
            getThroughArray(resultsArray);
            boolean isChecked = leadContentChecked;
            if (isChecked) {
                JSONArray leadContentArray = response.optJSONArray("leadContent");
                getThroughArray(leadContentArray);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem with parsing news JSON results.", e);
        }
        // sort recent news ("results") and lead content by date
        Collections.sort(newsList, new Comparator<News>() {
            public int compare(News n1, News n2) {
                return n1.getDate().compareTo(n2.getDate());
            }
        });
        Collections.reverse(newsList);

        // Check if news from results aren't the same as from leadContent. If yes, delete duplicate one.
        for (int i = 0; i < newsList.size() - 1; i++) {
            while (newsList.get(i).getTitle().equals(newsList.get(i + 1).getTitle())) {
                newsList.remove(i + 1);
                if (i == newsList.size() - 1) {
                    break;
                }
            }
        }
        return newsList;
    }

    private static void getThroughArray(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject news = jsonArray.optJSONObject(i);

            // get title and cut it before author (when he's added to title)
            String title = news.optString("webTitle");
            String[] titleParts = title.split(" [|]");
            title = titleParts[0];

            // get date and reformat it
            String date = news.optString("webPublicationDate");
            try {
                date = changeFormatDateToPolish(date);
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Problem with changing data.", e);
            }
            String section = news.optString("sectionName");
            String url = news.optString("webUrl");

            JSONObject fields = news.optJSONObject("fields");
            // get author; if it is empty, add "Author unknown"; cut before "in" (when added)
            String author = fields.optString("byline");
            author = formatAuthor(author);

            // get trailer and clean it from html tags
            String trailer = fields.optString("trailText");
            trailer = cleanTrailer(trailer);

            String imageUrl = fields.optString("thumbnail");
            Drawable image = null;
            try {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                image = new BitmapDrawable(Resources.getSystem(), bitmap);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem with getting bitmap from URL.", e);
            }
            newsList.add(new News(title, author, date, section, trailer, url, image));
        }
    }
    private static Bitmap getBitmapFromURL(String imageUrl) throws IOException {
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(20000);
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with getting bitmap from URL.", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                input.close();
            }
        }
    }
    private static String cleanTrailer(String trailer) {
        if (trailer.contains("<")) {
            return trailer.replaceAll("<[^>]*>", "");
        }
        return trailer;
    }

    private static String changeFormatDateToPolish(String givenDate) throws ParseException {
        SimpleDateFormat baseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy.MM.dd, HH:mm", Locale.GERMAN);
        Date dateValue = baseFormat.parse(givenDate);
        return newFormat.format(dateValue);
    }
    private static String formatAuthor(String author) {
        if (author.isEmpty()) {
            author = "Author unknown";
        }
        if (author.contains(" in ")) {
            String[] cutAuthor = author.split(" in ");
            author = cutAuthor[0];
        }
        return author;
    }
}