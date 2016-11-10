package com.las.bestmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Aleksey on 03.11.2016.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    public static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +
                MoviesContract.MoviesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MoviesContract.MoviesEntry.MOVIE_ID + " INTEGER NOT NULL , " +
                MoviesContract.MoviesEntry.TITLE + " TEXT NOT NULL, " +
                MoviesContract.MoviesEntry.OVERVIEW + " TEXT , " +
                MoviesContract.MoviesEntry.POPULARITY + " REAL , " +
                MoviesContract.MoviesEntry.VOTES + " REAL , " +
                MoviesContract.MoviesEntry.POSTER + " TEXT , " +
                MoviesContract.MoviesEntry.RELEASE_DATE + " TEXT , " +
                MoviesContract.MoviesEntry.FAVORITES + " INTEGER ," +

                " UNIQUE (" + MoviesContract.MoviesEntry.MOVIE_ID + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(db);
    }
}
