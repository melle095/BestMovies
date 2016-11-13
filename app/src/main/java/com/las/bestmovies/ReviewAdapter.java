package com.las.bestmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Aleksey on 14.11.2016.
 */

public class ReviewAdapter  extends ArrayAdapter<Reviews> {

    public ReviewAdapter(Context context, ArrayList<Reviews> movieReviews) {
        super(context, 0, movieReviews);
    }

    static class ViewHolder {
        public TextView reviewAuthor;
        public TextView reviewContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reviews review = getItem(position);
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_item, parent, false);

            holder = new ViewHolder();
            holder.reviewAuthor = (TextView) convertView.findViewById(R.id.author);
            holder.reviewContent = (TextView) convertView.findViewById(R.id.content);

            convertView.setTag(holder);

            if (review != null){
                if(holder.reviewAuthor != null){
                    holder.reviewAuthor.setText(review.getAuthor());
                }
                if(holder.reviewContent != null){
                    holder.reviewContent.setText(review.getContent());
                }
            }
        }

        return convertView;

    }
}