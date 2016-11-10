package com.las.bestmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.las.bestmovies.Data.MoviesContract;

import java.util.ArrayList;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final String SELECTED_KEY = "selected_position";
    private static final int FORECAST_LOADER = 0;

    ImageAdapter mImageAdapter;
    ArrayList<MovieObj> movieList;
    final String Parce_TAG = "MovieData";
    final String Save_TAG = "SaveMovieData";
    Context mContext;

    SharedPreferences mSharedPref;
    String sort_method;


//    private ForecastAdapter mForecastAdapter;
//
//    private ListView mListView;
//    private int mPosition = ListView.INVALID_POSITION;
//    private boolean mUseTodayLayout;

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] MOVIE_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID,
            MoviesContract.MoviesEntry.TITLE,
            MoviesContract.MoviesEntry.OVERVIEW,
            MoviesContract.MoviesEntry.RELEASE_DATE,
            MoviesContract.MoviesEntry.POPULARITY,
            MoviesContract.MoviesEntry.VOTES,
            MoviesContract.MoviesEntry.POSTER,
            MoviesContract.MoviesEntry.FAVORITES
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 1;
    static final int COL_TITLE    = 2;
    static final int COL_OVERVIEW = 3;
    static final int COL_RELEASE_DATE = 4;
    static final int COL_POPULARITY = 5;
    static final int COL_VOTES      = 6;
    static final int COL_POSTER     = 7;
    static final int COL_FAVORITES  = 8;

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

        if(savedInstanceState == null || !savedInstanceState.containsKey(Save_TAG)) {
            movieList = new ArrayList<MovieObj>();
        }
        else {
            movieList = savedInstanceState.getParcelableArrayList(Save_TAG);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (isOnline()) {
            mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            sort_method = mSharedPref.getString(getString(R.string.settings_sort_key), getString(R.string.settings_sort_default));
            new FetchMovieTask(getActivity()).execute(sort_method);
        } else {
            Toast.makeText(getActivity(), "No Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Save_TAG, movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        mImageAdapter = new ImageAdapter(getActivity(), movieList);
        gridView.setAdapter(mImageAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                MovieObj movieExtras = (MovieObj) parent.getItemAtPosition(position);

                Intent detailIntent = new Intent(getActivity(),DetailActivity.class);
                detailIntent.putExtra(Parce_TAG,movieExtras);
                startActivity(detailIntent);
            }
        });
        return rootView;
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //String sortOrder = .COLUMN_DATE + " ASC";

        //String locationSetting = Utility.getPreferredLocation(getActivity());
//        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
//                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
