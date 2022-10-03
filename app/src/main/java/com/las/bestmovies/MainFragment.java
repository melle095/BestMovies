package com.las.bestmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.las.bestmovies.Data.MoviesContract;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String SELECTED_KEY = "selected_position";
    private static final int FORECAST_LOADER = 0;
    private int mPosition = GridView.INVALID_POSITION;
    GridView gridView;
    MovieAdapter mMovieAdapter;
    public boolean isTablet;
    public boolean show_favorites = false;
//    ArrayList<MovieObj> movieList;
//    final String Parce_TAG = "MovieData";
//    final String Save_TAG = "SaveMovieData";
//    Context mContext;

    SharedPreferences mSharedPref;
    String sort_method;

//    private boolean mUseTodayLayout;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {

            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.POSTER,
            MoviesContract.MoviesEntry.FAVORITES,
            MoviesContract.MoviesEntry.MOVIE_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ID         = 0;
    static final int COL_POSTER     = 1;
    static final int COL_FAVORITES  = 2;
    static final int COL_MOVIE_ID   = 3;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public interface Callback {
        public void onItemSelected(Uri movieUri);
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    public void onMovieUpdate() {
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort_method = mSharedPref.getString(getString(R.string.settings_sort_key), getString(R.string.settings_sort_default));
        if (sort_method == getString(R.string.Favorites))
            show_favorites = true;
        else {
            show_favorites = false;
            new FetchMovieTask(getActivity()).execute(sort_method);
        }
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridView = (GridView) rootView.findViewById(R.id.gridview);
        mMovieAdapter = new MovieAdapter(getActivity(),null,0);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null){
                    ((Callback) getActivity()).onItemSelected(MoviesContract.MoviesEntry.buildMoviesUri(cursor.getInt(COL_MOVIE_ID)));
                }

                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        isTablet = ((MainActivity) getActivity()).isTablet();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(getActivity(),
                (show_favorites? MoviesContract.MoviesEntry.CONTENT_FAVORITES_URI:MoviesContract.MoviesEntry.CONTENT_URI),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            gridView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }
}
