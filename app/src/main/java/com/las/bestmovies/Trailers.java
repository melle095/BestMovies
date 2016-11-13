package com.las.bestmovies;

import android.net.Uri;

/**
 * Created by Aleksey on 13.11.2016.
 */

public class Trailers {
    private static final Uri baseUrl = Uri.parse("http://api.themoviedb.org/3/movie/");
    private final Uri yuoTubeUrl = Uri.parse("https://www.youtube.com/watch");
    private final String yuoTube_param = "v";
    private static final String api_key = "api_key";

    private String name;
    private String key;

    public String getName() {
        return name;
    }

    public static Uri getFetchUri(String movie_ID) {
        return baseUrl.buildUpon().appendPath(movie_ID)
                                    .appendPath("videos")
                                    .appendQueryParameter(api_key,BuildConfig.MOVIEDB_API_KEY)
                                    .build();
    }

    public Uri getYouTubeUri() {
        return yuoTubeUrl.buildUpon().appendQueryParameter(yuoTube_param,key).build();
    }

    public Trailers(String key, String name) {

        this.key = key;
        this.name = name;
    }
}
