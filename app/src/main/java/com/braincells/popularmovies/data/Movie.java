package com.braincells.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents an entry returned from the TMDB API
 *
 * Created by jaldhar on 4/23/16.
 */
public class Movie implements Parcelable {
    private final String poster_path;
    private final boolean adult;
    private final String overview;
    private final String release_date;
    private final int[] genre_ids;
    private final int id;
    private final String original_title;
    private final String original_language;
    private final String title;
    private final String backdrop_path;
    private final float popularity;
    private final int vote_count;
    private final boolean video;
    private final float vote_average;

    public String getPoster_path() {
        return poster_path;
    }

    public boolean isAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public Date getRelease_date() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return format.parse(release_date);
        }
        catch (java.text.ParseException e) {
            Log.d("PopularMovies", "Date parse error");
            return null;
        }
    }

    public int[] getGenre_ids() {
        return genre_ids;
    }

    public int getId() {
        return id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdrop_path() {
        return backdrop_path;
    }

    public float getPopularity() {
        return popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public boolean isVideo() {
        return video;
    }

    public float getVote_average() {
        return vote_average;
    }

    private Movie(Parcel in) {
        poster_path = in.readString();
        adult = in.readByte() != 0x00;
        overview = in.readString();
        release_date = in.readString();
        genre_ids = in.createIntArray();
        id = in.readInt();
        original_title = in.readString();
        original_language = in.readString();
        title = in.readString();
        backdrop_path = in.readString();
        popularity = in.readFloat();
        vote_count = in.readInt();
        video = in.readByte() != 0x00;
        vote_average = in.readFloat();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(poster_path);
        dest.writeByte((byte) (adult ? 0x01 : 0x00));
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeIntArray(genre_ids);
        dest.writeInt(id);
        dest.writeString(original_title);
        dest.writeString(original_language);
        dest.writeString(title);
        dest.writeString(backdrop_path);
        dest.writeFloat(popularity);
        dest.writeInt(vote_count);
        dest.writeByte((byte) (video ? 0x01 : 0x00));
        dest.writeFloat(vote_average);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
