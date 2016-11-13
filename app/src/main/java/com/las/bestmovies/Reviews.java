package com.las.bestmovies;

import android.net.Uri;

/**
 * Created by Aleksey on 13.11.2016.
 */

public class Reviews {

    private static final Uri baseUrl = Uri.parse("http://api.themoviedb.org/3/movie/");

    private String author     = "author";
    private String content    = "content";
    private String url        = "url";
    private static final String api_key = "api_key";

    public Reviews(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public static Uri getReviewsUrl(String movie_ID) {
        return baseUrl.buildUpon().appendPath(movie_ID)
                                    .appendPath("reviews")
                                    .appendQueryParameter(api_key,BuildConfig.MOVIEDB_API_KEY)
                                    .build();
    }
}
