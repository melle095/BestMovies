package com.las.bestmovies.Data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


public class MoviesProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIE_DETAIL = 101;
    static final int REVIEWS = 300;
    static final int REVIEW_BY_MOVIE = 301;
    static final int TRAILERS = 400;
    static final int TRAILER_BY_MOVIE = 401;


    private static final SQLiteQueryBuilder sMoviesQueryBuilder;

    static {
        sMoviesQueryBuilder = new SQLiteQueryBuilder();

        sMoviesQueryBuilder.setTables(
                MoviesContract.MoviesEntry.TABLE_NAME + " LEFT JOIN " +
                        MoviesContract.TrailersEntry.TABLE_NAME +
                        " ON " + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID + " = " +
                        MoviesContract.TrailersEntry.TABLE_NAME + "." + MoviesContract.TrailersEntry.MOVIE_ID +" " +

                        " LEFT JOIN " +
                        MoviesContract.ReviewsEntry.TABLE_NAME +
                        " ON " + MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID + " = " +
                        MoviesContract.ReviewsEntry.TABLE_NAME + "." + MoviesContract.ReviewsEntry.MOVIE_ID);
    };

//    public static final String sMoviesPopularity = MoviesContract.MoviesEntry.POPULARITY + " DESC";
//    public static final String sMoviesRating = MoviesContract.MoviesEntry.VOTES + " DESC";
//    public static final String sMoviesFavorites = MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry.FAVORITES + " = ? ";
    public static final String sMovies = MoviesContract.MoviesEntry.TABLE_NAME + "." + MoviesContract.MoviesEntry._ID + " = ? ";
    public static final String sReviews = MoviesContract.ReviewsEntry.TABLE_NAME + "." + MoviesContract.ReviewsEntry.MOVIE_ID + " = ? ";
    public static final String sTrailers = MoviesContract.TrailersEntry.TABLE_NAME + "." + MoviesContract.TrailersEntry.MOVIE_ID + " = ? ";



    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {

            case MOVIE_DETAIL: {
                retCursor = sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sMovies,
                        new String[]{MoviesContract.MoviesEntry.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILER_BY_MOVIE: {
                retCursor = sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sTrailers,
                        new String[]{MoviesContract.MoviesEntry.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEW_BY_MOVIE: {
                retCursor = sMoviesQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        sReviews,
                        new String[]{MoviesContract.MoviesEntry.getMovieIDFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case MOVIES:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.MoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case TRAILERS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.TrailersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MoviesContract.ReviewsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIES:
                return MoviesContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MoviesContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case REVIEWS:
                return MoviesContract.ReviewsEntry.CONTENT_TYPE;
            case REVIEW_BY_MOVIE:
                return MoviesContract.ReviewsEntry.CONTENT_ITEM_TYPE;
            case TRAILERS:
                return MoviesContract.TrailersEntry.CONTENT_TYPE;
            case TRAILER_BY_MOVIE:
                return MoviesContract.TrailersEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIES: {
                //normalizeDate(values);
                long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.MoviesEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILERS: {
                long _id = db.insert(MoviesContract.TrailersEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.TrailersEntry.buildTrailersUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MoviesContract.ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MoviesContract.ReviewsEntry.buildReviewsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIES:
                rowsDeleted = db.delete(
                        MoviesContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MoviesContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILERS:
                rowsDeleted = db.delete(
                        MoviesContract.TrailersEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIES:
                //normalizeDate(values);
                rowsUpdated = db.update(MoviesContract.MoviesEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(MoviesContract.ReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRAILERS:
                rowsUpdated = db.update(MoviesContract.TrailersEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        //normalizeDate(value);
                        long _id = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MoviesContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS, REVIEWS);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS, TRAILERS);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES   + "/#", MOVIE_DETAIL);
        matcher.addURI(authority, MoviesContract.PATH_REVIEWS  + "/#", REVIEW_BY_MOVIE);
        matcher.addURI(authority, MoviesContract.PATH_TRAILERS + "/#", TRAILER_BY_MOVIE);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
