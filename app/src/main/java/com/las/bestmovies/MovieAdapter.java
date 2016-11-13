package com.las.bestmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Aleksey on 10.11.2016.
 */

public class MovieAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final ImageView posterView;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.grid_imageView);
        }
    }
    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);


        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
//        ImageView imageView = (ImageView) view;
//        imageView.setAdjustViewBounds(true);
//        imageView.setLayoutParams(new GridView.LayoutParams(85, 85));

        //imageView.setImageResource(mThumbIds[position]);
//        if (cursor != null)
        Picasso.with(context).load("http://image.tmdb.org/t/p/"+context.getString(R.string.MainPosterRes)+"/" + cursor.getString(MainFragment.COL_POSTER)).into(viewHolder.posterView);
        //System.out.println("http://image.tmdb.org/t/p/w185/" + cursor.getString(MainFragment.COL_POSTER));
        //Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(imageView);
    }

}
