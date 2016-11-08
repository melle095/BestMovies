/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.las.bestmovies;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.las.bestmovies.Data.MoviesContract;

/*
    Students: This is NOT a complete test for the WeatherContract --- just for the functions
    that we expect you to write.
 */
public class TestWeatherContract extends AndroidTestCase {

    // intentionally includes a slash to make sure Uri is getting quoted correctly
    private static final long TEST_MOVIE_ID = 22;

    /*
        Students: Uncomment this out to test your weather location function.
     */
    public void testBuildWeatherLocation() {
        Uri movieUri = MoviesContract.MoviesEntry.buildMoviesUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildWeatherLocation in WeatherContract.", movieUri);
        assertEquals("Error: Weather location not properly appended to the end of the Uri",Long.toString(TEST_MOVIE_ID), movieUri.getLastPathSegment());
        assertEquals("Error: Movie ID Uri doesn't match our expected result", movieUri.toString(), MoviesContract.MoviesEntry.CONTENT_URI+"/"+String.valueOf(TEST_MOVIE_ID));
    }
}
