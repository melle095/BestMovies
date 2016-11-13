package com.las.bestmovies;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.las.bestmovies.Data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Vector;

import static com.las.bestmovies.MainFragment.LOG_TAG;

/**
 * Created by Aleksey on 13.11.2016.
 */

public class Utility {

    public static void getMovieDataFromJson(String movieJsonStr, Context mContext)
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
                contentValues.put(MoviesContract.MoviesEntry.POSTER, jsonNode.getString(poster_path));

                cVVector.add(contentValues);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MoviesContract.MoviesEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static ArrayList<Reviews> getMovieReviewFromJson(String reviewJsonStr, Context mContext)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String root = "results";
        final String author = "author";
        final String content = "content";
        final String url = "url";

        try {
            JSONArray movieArray = new JSONObject(reviewJsonStr).getJSONArray(root);
            JSONObject jsonNode;

            ArrayList<Reviews> reviewList = new ArrayList<Reviews>();


            for (int i = 0; i < movieArray.length(); i++) {
                jsonNode = movieArray.getJSONObject(i);

                Reviews review = new Reviews(jsonNode.getString(author),
                        jsonNode.getString(content),
                        jsonNode.getString(url)
                );
                reviewList.add(review);

            }

            int inserted = reviewList.size();

            Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");

            return reviewList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Trailers> getMovieTrailersFromJson(String trailerJsonStr, Context mContext)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String root = "results";
        final String name = "name";
        final String key = "key";

        try {
            JSONArray movieArray = new JSONObject(trailerJsonStr).getJSONArray(root);
            JSONObject jsonNode;

            ArrayList<Trailers> trailerList = new ArrayList<Trailers>();


            for (int i = 0; i < movieArray.length(); i++) {
                jsonNode = movieArray.getJSONObject(i);

                Trailers trailer = new Trailers(jsonNode.getString(key),
                        jsonNode.getString(name)
                );
                trailerList.add(trailer);

            }

            int inserted = trailerList.size();

            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");

            return trailerList;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

}
