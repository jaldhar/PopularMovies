package com.braincells.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the results of a call to the TMDB API.
 * Created by jaldhar on 4/24/16.
 */
public class MovieCollection implements Parcelable{
    private final int page;
    private final Movie[] results;
    private final int total_results;
    private final int total_pages;

    public int getPage() {
        return page;
    }

    public Movie[] getResults() {
        return results;
    }

    public int getTotal_results() {
        return total_results;
    }

    public int getTotal_pages() {
        return total_pages;
    }

    protected MovieCollection(Parcel in) {
        page = in.readInt();
        results = in.createTypedArray(Movie.CREATOR);
        total_results = in.readInt();
        total_pages = in.readInt();
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(page);
        dest.writeTypedArray(results, 0);
        dest.writeInt(total_results);
        dest.writeInt(total_pages);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MovieCollection> CREATOR =
            new Parcelable.Creator<MovieCollection>() {
                @Override
                public MovieCollection createFromParcel(Parcel in) {
                    return new MovieCollection(in);
                }

                @Override
                public MovieCollection[] newArray(int size) {
                    return new MovieCollection[size];
                }
            };

}
