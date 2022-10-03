package com.las.bestmovies.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aleksey on 02.11.2016.
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.las.bestmovies.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";
    public static final String PATH_FAVORITES = "favorites";

    public final static class MoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();
        public static final Uri CONTENT_FAVORITES_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVORITES).build();


        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String MOVIE_ID = "movie_id";
        public static final String OVERVIEW = "overview";
        public static final String POSTER = "poster";
        public static final String RELEASE_DATE = "release_date";
        public static final String TITLE = "title";
        public static final String VOTES = "votes";
        public static final String POPULARITY = "popularity";
        public static final String FAVORITES = "favorites";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getMovieIDFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

}
