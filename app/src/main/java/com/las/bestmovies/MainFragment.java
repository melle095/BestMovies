package com.las.bestmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment {
    ImageAdapter mImageAdapter;
    ArrayList<MovieObj> movieList;
    final String Parce_TAG = "MovieData";
    final String Save_TAG = "SaveMovieData";
    Context mContext;

    SharedPreferences mSharedPref;
    String sort_method;

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
            new FetchMovieTask().execute(sort_method);
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
//                Toast.makeText(getActivity(), "Pos" + position + " id:" + id,
//                        Toast.LENGTH_SHORT).show();
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

    public class FetchMovieTask extends AsyncTask<String, Void, List<MovieObj>> {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private final String baseUrl = "http://api.themoviedb.org/3/movie/";
        private final String api_key = "api_key";


        private List<MovieObj> getMovieDataFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String root = "results";
            final String overview = "overview";
            final String poster_path = "poster_path";
            final String release_date = "release_date";
            final String title = "title";
            final String vote_average = "vote_average";

            JSONArray movieArray = new JSONObject(movieJsonStr).getJSONArray(root);

            ArrayList<MovieObj> movieList = new ArrayList<MovieObj>();
            JSONObject jsonNode;

            for (int i = 0; i < movieArray.length(); i++) {
                jsonNode = movieArray.getJSONObject(i);
                MovieObj movie_item = new MovieObj(
                        jsonNode.getString(overview),
                        jsonNode.getString(poster_path),
                        jsonNode.getString(release_date),
                        jsonNode.getString(title),
                        jsonNode.getString(vote_average)
                );
                movieList.add(movie_item);
                System.out.println(movie_item.toString());
            }

            return movieList;

        }

        @Override
        protected void onPostExecute(List<MovieObj> movieObjList) {
            super.onPostExecute(movieObjList);

            mImageAdapter.replace(movieObjList);
        }

        @Override
        protected List<MovieObj> doInBackground(String... params) {

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
                    movieJsonStr = null;
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
                    movieJsonStr = null;

                }
                movieJsonStr = buffer.toString();

                Log.v(LOG_TAG, "movie string: " + movieJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
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

            try {
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

    }
}
