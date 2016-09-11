package com.las.bestmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class ImageAdapter extends ArrayAdapter {
    public ImageAdapter(Context c, List<MovieObj> movieList) {
        super(c, 0, movieList);
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setAdjustViewBounds(true);
            //imageView.setLayoutParams(new GridView.LayoutParams(85, 85))

        } else {
            imageView = (ImageView) convertView;
        }

        MovieObj movieObj = (MovieObj) getItem(position);
        //imageView.setImageResource(mThumbIds[position]);
        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185/" + movieObj.getPoster_path()).into(imageView);

        //Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);

        return imageView;
    }

    public void replace(List<MovieObj> movieObjList){
        clear();
        addAll(movieObjList);
        notifyDataSetChanged();
    }


}
