package com.las.bestmovies;


import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.las.bestmovies.Data.MoviesContract;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.las.bestmovies.MainFragment.LOG_TAG;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String DETAIL_URI = "URI";
    static final String TMDB_IMG_URL = "http://image.tmdb.org/t/p/";

    private static final int DETAIL_LOADER = 1;

    private Uri mUri;

    TextView title;
    TextView overview;
    TextView vote_average;
    TextView release_date;
    ImageView posterView;
    ListView reviewList;
    ListView trailersList;

    private static final String[] MOVIE_COLUMNS = {

            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.TITLE,
            MoviesContract.MoviesEntry.VOTES,
            MoviesContract.MoviesEntry.POPULARITY,
            MoviesContract.MoviesEntry.RELEASE_DATE,
            MoviesContract.MoviesEntry.OVERVIEW,
            MoviesContract.MoviesEntry.POSTER,
            MoviesContract.MoviesEntry.FAVORITES,
            MoviesContract.MoviesEntry.MOVIE_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID         = 0;
    static final int COL_TITLE      = 1;
    static final int COL_VOTES      = 2;
    static final int COL_POPULARITY = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_OVERVIEW   = 5;
    static final int COL_POSTER     = 6;
    static final int COL_FAVORITES  = 7;
    static final int COL_MOVIE_ID   = 8;

    public DetailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        title = (TextView) rootView.findViewById(R.id.movieTitle);
        overview = (TextView) rootView.findViewById(R.id.overview);
        vote_average = (TextView) rootView.findViewById(R.id.votes);
        release_date = (TextView) rootView.findViewById(R.id.releaseDate);
        posterView = (ImageView) rootView.findViewById(R.id.imageView);

        reviewList = (ListView) rootView.findViewById(R.id.listView_reviews);
        trailersList = (ListView) rootView.findViewById(R.id.listView_trailers);

        return rootView;
    }

    void onMovieChanged() {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            String movie_id = MoviesContract.MoviesEntry.getMovieIDFromUri(uri);
            Uri updatedUri = MoviesContract.MoviesEntry.buildMoviesUri(Long.parseLong(movie_id));
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            //long moId = data.getInt(COL_WEATHER_CONDITION_ID);
            title.setText(data.getString(COL_TITLE));
            overview.setText(data.getString(COL_OVERVIEW));
            vote_average.setText(data.getString(COL_VOTES));
            release_date.setText(data.getString(COL_RELEASE_DATE));
            Picasso.with(getContext()).load(TMDB_IMG_URL + getString(R.string.DetailPosterRes) +"/" + data.getString(COL_POSTER)).into(posterView);

            new MovieReviewLoader().execute(data.getString(COL_MOVIE_ID));
            new MovieTrailerLoader().execute(data.getString(COL_MOVIE_ID));
        }
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class MovieReviewLoader extends AsyncTask<String, Void, ArrayList<Reviews>> {

        @Override
        protected void onPostExecute(ArrayList<Reviews> reviewsArrayList) {
            super.onPostExecute(reviewsArrayList);

            ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(),reviewsArrayList);
            reviewList.setAdapter(reviewAdapter);
        }

        @Override
        protected ArrayList<Reviews> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {

                Uri buildUri = Reviews.getReviewsUrl(params[0]);

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

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "reviews string: " + movieJsonStr);

                return Utility.getMovieReviewFromJson(movieJsonStr, getActivity());

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

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

    public class MovieTrailerLoader extends AsyncTask<String, Void, ArrayList<Trailers>> {

        @Override
        protected void onPostExecute(ArrayList<Trailers> trailersArrayList) {
            super.onPostExecute(trailersArrayList);

            TrailerAdapter trailerAdapter = new TrailerAdapter(getActivity(),trailersArrayList);
            trailersList.setAdapter(trailerAdapter);
        }

        @Override
        protected ArrayList<Trailers> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {

                Uri buildUri = Trailers.getFetchUri(params[0]);

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

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "trailer string: " + movieJsonStr);

                return Utility.getMovieTrailersFromJson(movieJsonStr, getActivity());

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);

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
}