package com.las.bestmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.las.bestmovies.Data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, Void> {
    //movie_id = 188927;
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final String baseUrl = "http://api.themoviedb.org/3/movie/";
    private final String reviewsUrl = "http://api.themoviedb.org/3/movie/{movie_id}/reviews";
    private final String trailersUrl = "http://api.themoviedb.org/3/movie/{movie_id}/videos";

    private final String api_key = "api_key";

    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private void getMovieDataFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String root = "results";
        final String overview = "overview";
        final String movie_id = "id";
        final String poster_path = "poster_path";
        final String release_date = "release_date";
        final String title = "title";
        final String vote_average = "vote_average";
        final String reviews = "vote_average";

        try {
            JSONArray movieArray = new JSONObject(movieJsonStr).getJSONArray(root);
            JSONObject jsonNode;

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            for (int i = 0; i < movieArray.length(); i++) {
                jsonNode = movieArray.getJSONObject(i);

                ContentValues contentValues = new ContentValues();
                contentValues.put(MoviesContract.MoviesEntry.OVERVIEW, jsonNode.getString(overview));
                contentValues.put(MoviesContract.MoviesEntry.TITLE, jsonNode.getString(title));
                contentValues.put(MoviesContract.MoviesEntry.RELEASE_DATE, jsonNode.getString(release_date));
                contentValues.put(MoviesContract.MoviesEntry.MOVIE_ID, jsonNode.getString(movie_id));
                contentValues.put(MoviesContract.MoviesEntry.VOTES, jsonNode.getString(vote_average));
                contentValues.put(MoviesContract.MoviesEntry.POSTER,jsonNode.getString(poster_path));

                cVVector.add(contentValues);
            }

            int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        }catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String movieJsonStr = null;

        String mSort = params[0];
        try {

            Uri buildUri = Uri.parse(baseUrl).buildUpon()
                    .appendPath(mSort)
                    .appendQueryParameter(api_key, BuildConfig.MOVIEDB_API_KEY)
                    .build();

            URL url = new URL(buildUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            movieJsonStr = buffer.toString();
            getMovieDataFromJson(movieJsonStr);
            Log.v(LOG_TAG, "movie string: " + movieJsonStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

}
