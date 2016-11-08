package com.las.bestmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aleksey on 03.11.2016.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_TRAILERS_TABLE = "CREATE TABLE " + MoviesContract.TrailersEntry.TABLE_NAME + " (" +
                MoviesContract.TrailersEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                MoviesContract.TrailersEntry.ID + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.MOVIE_ID + " INTEGER NOT NULL, " +
                MoviesContract.TrailersEntry.KEY + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.NAME + " TEXT NOT NULL, " +
                MoviesContract.TrailersEntry.SITE + " TEXT NOT NULL, " +

                " FOREIGN KEY (" + MoviesContract.TrailersEntry.MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" +  MoviesContract.MoviesEntry._ID + "));";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + MoviesContract.ReviewsEntry.TABLE_NAME + " (" +
                MoviesContract.ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.ReviewsEntry.MOVIE_ID + " INTEGER NOT NULL, " +
//                MoviesContract.ReviewsEntry.ID + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.AUTHOR + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.CONTENT + " TEXT NOT NULL, " +
                MoviesContract.ReviewsEntry.URL + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + MoviesContract.ReviewsEntry.MOVIE_ID + ") REFERENCES " +
                MoviesContract.MoviesEntry.TABLE_NAME + " (" + MoviesContract.MoviesEntry._ID + "));";

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY ," +
                MoviesContract.MoviesEntry.TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.OVERVIEW + " TEXT , " +
                MoviesContract.MoviesEntry.POPULARITY + " REAL , " +
                MoviesContract.MoviesEntry.VOTES + " REAL , " +
                MoviesContract.MoviesEntry.POSTER + " TEXT , " +
                MoviesContract.MoviesEntry.RELEASE_DATE + " TEXT , " +
                MoviesContract.MoviesEntry.FAVORITES + " INTEGER ," +

                " UNIQUE (" + MoviesContract.MoviesEntry._ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_REVIEWS_TABLE);
        db.execSQL(SQL_CREATE_TRAILERS_TABLE);
        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.TrailersEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.ReviewsEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
