package com.las.bestmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Aleksey on 14.11.2016.
 */

public class TrailerAdapter extends ArrayAdapter<Trailers> {

    public TrailerAdapter(Context context, ArrayList<Trailers> movieTrailers) {
        super(context, 0, movieTrailers);
    }

    static class ViewHolder {
        public TextView trailerName;
        public Button trailerWatch;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trailers trailer = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.trailer_item, parent, false);

            holder = new ViewHolder();
            holder.trailerName = (TextView) convertView.findViewById(R.id.trailer_name);
            holder.trailerWatch = (Button) convertView.findViewById(R.id.trailer_btn);

            convertView.setTag(holder);

            if (trailer != null) {
                if (holder.trailerName != null) {
                    holder.trailerName.setText(trailer.getName());
                }
                if (holder.trailerWatch != null) {
                    final Uri url = trailer.getYouTubeUri();
                    holder.trailerWatch.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            {
                                //String url =//YOUTUBE_BASE + key;
                                Intent watchIntent = new Intent(Intent.ACTION_VIEW, url);
                                getContext().startActivity(watchIntent);
                            }

                        }
                    });

                }
            }
        }
        return convertView;
    }
}
