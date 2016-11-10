package com.las.bestmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


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

}
