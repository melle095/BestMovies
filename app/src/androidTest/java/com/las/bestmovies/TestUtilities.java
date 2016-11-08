package com.las.bestmovies;


import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.las.bestmovies.Data.MoviesContract;
import com.las.bestmovies.Data.MoviesDbHelper;
import com.las.bestmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final long movie_id = 188927;
    static final String movie_id_str = "188927";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MoviesContract.MoviesEntry._ID, movie_id); // ID 188927
        movieValues.put(MoviesContract.MoviesEntry.TITLE, "Star Trek: Alternate Reality Collection");
        movieValues.put(MoviesContract.MoviesEntry.OVERVIEW, "The USS Enterprise crew explores the furthest reaches " +
                                                                "of uncharted space, where they encounter a mysterious new enemy " +
                                                                "who puts them and everything the Federation stands for to the test.");
        movieValues.put(MoviesContract.MoviesEntry.POPULARITY, 28.1845);
        movieValues.put(MoviesContract.MoviesEntry.VOTES, 6.3);
        movieValues.put(MoviesContract.MoviesEntry.POSTER, "/mLrQMqyZgLeP8FrT5LCobKAiqmK.jpg");
        movieValues.put(MoviesContract.MoviesEntry.RELEASE_DATE, "2016-07-07");
        movieValues.put(MoviesContract.MoviesEntry.FAVORITES, 0);

        return movieValues;
    }

    static ContentValues createTrailerValues(long movieRowId) {
        ContentValues trailerValues = new ContentValues();

//        trailerValues.put(MoviesContract.TrailersEntry.ID, String.valueOf("571bf094c3a368525f006b86")); // ID 188927
        trailerValues.put(MoviesContract.TrailersEntry.KEY, "dCyv5xKIqlw");
        trailerValues.put(MoviesContract.TrailersEntry.NAME, "Star Trek Beyond - Official Trailer");
        trailerValues.put(MoviesContract.TrailersEntry.SITE, "http://YouTube.com");
        trailerValues.put(MoviesContract.TrailersEntry.MOVIE_ID, movieRowId);

        return trailerValues;
    }

    static ContentValues createReviewValues(long movieRowId) {
        ContentValues reviewValues = new ContentValues();

//        reviewValues.put(MoviesContract.ReviewsEntry.ID, "57932005c3a368636b006bd1");
        reviewValues.put(MoviesContract.ReviewsEntry.MOVIE_ID, movieRowId);
        reviewValues.put(MoviesContract.ReviewsEntry.AUTHOR, "Frank Ochieng");
        reviewValues.put(MoviesContract.ReviewsEntry.CONTENT, "Some diehard **Star Trek** fans may not necessarily feel that the " +
                                                                "Justin Lin-directed third installment of this science fiction/space saga film franchise ");
        reviewValues.put(MoviesContract.ReviewsEntry.URL, "https://www.themoviedb.org/review/57932005c3a368636b006bd1");

        return reviewValues;
    }

    static long insertMovieValues(Context context) {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long MovieRowId;
        MovieRowId = db.insert(MoviesContract.MoviesEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert movie Values", MovieRowId != -1);

        return MovieRowId;
    }


    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
