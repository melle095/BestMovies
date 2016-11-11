package com.las.bestmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.las.bestmovies.Data.MoviesContract;
import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    static final String DETAIL_URI = "URI";

    private static final int DETAIL_LOADER = 0;

    private Uri mUri;

    TextView title;
    TextView overview;
    RatingBar vote_average;
    TextView release_date;
    ImageView posterView;

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
            vote_average = (RatingBar) rootView.findViewById(R.id.votes);
            release_date = (TextView) rootView.findViewById(R.id.releaseDate);
            posterView = (ImageView) rootView.findViewById(R.id.imageView);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
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

                System.out.println("OnLoadFinished");
                // Read weather condition ID from cursor
                //long moId = data.getInt(COL_WEATHER_CONDITION_ID);
                title.setText(data.getString(COL_TITLE));
                overview.setText(data.getString(COL_OVERVIEW));
                vote_average.setRating(Float.valueOf(data.getFloat(COL_VOTES)));
                release_date.setText(data.getString(COL_RELEASE_DATE));
                Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + data.getString(COL_POSTER)).into(posterView);

            }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
