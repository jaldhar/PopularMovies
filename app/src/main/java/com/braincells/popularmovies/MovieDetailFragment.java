package com.braincells.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.braincells.popularmovies.data.Movie;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * A fragment representing a single com.braincells.popularmovies.data.Movie detail screen.
 * This fragment is either contained in a {@link MovieListActivity}
 * in two-pane mode (on tablets) or a {@link MovieDetailActivity}
 * on handsets.
 */
public class MovieDetailFragment extends Fragment {
    /**
     * The content this fragment is presenting.
     */
    private Movie mItem;

    /**
     * A tag for logging.
     */
    private static final String TAG = "PopularMovies";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("MOVIE")) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = getArguments().getParcelable("MOVIE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final String POSTER_URL = "http://image.tmdb.org/t/p/w185";

        View rootView = inflater.inflate(R.layout.movie_detail, container, false);
        if (mItem != null) {
            TextView release_date = (TextView) rootView.findViewById(R.id.release_date);
            if (release_date != null) {
                Calendar cal = new GregorianCalendar();
                cal.setTime(mItem.getRelease_date());
                String text = String.format(Locale.ENGLISH, "%d", cal.get(Calendar.YEAR));
                release_date.setText(text);
                Log.d(TAG, text);
            }

            ImageView poster = (ImageView) rootView.findViewById(R.id.poster);
            if (poster != null) {
                String url = POSTER_URL +
                        mItem.getPoster_path();
                Picasso.with(getContext()).load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .fit()
                        .into(poster);
                poster.setContentDescription(getResources().getString(R.string.poster_description,
                        mItem.getTitle()));
                Log.d(TAG, "loading: " + url);
            }

            TextView vote_average = (TextView) rootView.findViewById(R.id.vote_average);
            if (vote_average != null) {
                String text = String.format(Locale.ENGLISH, "%.2f/10", mItem.getVote_average());
                vote_average.setText(text);
                Log.d(TAG, text);
            }

            TextView overview = (TextView) rootView.findViewById(R.id.overview);
            if (overview != null) {
                overview.setText(mItem.getOverview());
            }

            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle(mItem.getTitle());
            }
        }

        return rootView;
    }

}
