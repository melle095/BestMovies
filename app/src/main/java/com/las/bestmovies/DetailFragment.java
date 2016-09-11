package com.las.bestmovies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DetailFragment extends Fragment {

    TextView title;
    TextView overview;
    RatingBar vote_average;
    TextView release_date;
    MovieObj movieObj;
    final String Parce_TAG = "SaveDetailFrag";

    public DetailFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null || !savedInstanceState.containsKey(Parce_TAG)) {
            movieObj = new MovieObj();
        }
        else {
            movieObj = savedInstanceState.getParcelable(Parce_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Parce_TAG,movieObj);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("MovieData")) {
            movieObj = intent.getParcelableExtra("MovieData");
            title = (TextView) rootView.findViewById(R.id.movieTitle);
            overview = (TextView) rootView.findViewById(R.id.overview);
            vote_average = (RatingBar) rootView.findViewById(R.id.votes);
            release_date = (TextView) rootView.findViewById(R.id.releaseDate);
            ImageView posterView = (ImageView) rootView.findViewById(R.id.imageView);
            title.setText(movieObj.getTitle());
            overview.setText(movieObj.getOverview());
            vote_average.setRating(Float.valueOf(movieObj.getVote_average()));
            release_date.setText(movieObj.getRelease_date());
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w500/" + movieObj.getPoster_path()).into(posterView);
        }
        return rootView;
    }

}
