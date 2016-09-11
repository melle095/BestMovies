package com.las.bestmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieObj implements Parcelable {

    String title;
    String poster_path;
    String overview;
    String vote_average;
    String release_date;

    public MovieObj(Parcel in) {
        overview = in.readString();
        poster_path = in.readString();
        release_date = in.readString();
        title = in.readString();
        vote_average = in.readString();
    }

    public static final Creator<MovieObj> CREATOR = new Creator<MovieObj>() {
        @Override
        public MovieObj createFromParcel(Parcel in) {
            return new MovieObj(in);
        }

        @Override
        public MovieObj[] newArray(int size) {
            return new MovieObj[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(overview);
        dest.writeString(poster_path);
        dest.writeString(release_date);
        dest.writeString(title);
        dest.writeString(vote_average);
    }

    public MovieObj(String overview, String poster_path, String release_date, String title, String vote_average) {
        this.overview = overview;
        this.poster_path = poster_path;
        this.release_date = release_date;
        this.title = title;
        this.vote_average = vote_average;
    }

    public MovieObj() {
        this.overview = "";
        this.poster_path = "";
        this.release_date = "";
        this.title = "";
        this.vote_average = "";
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String toString(){
        return getTitle()+" "+getRelease_date();
    }
}
