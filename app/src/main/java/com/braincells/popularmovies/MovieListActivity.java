package com.braincells.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.braincells.popularmovies.data.Movie;
import com.braincells.popularmovies.data.MovieCollection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity implements Callback {
    /**
     * A tag for logging.
     */
    private static final String TAG = "PopularMovies";

    /**
     * An HTTP client for interacting with the themoviedb.org API.
     */
    private final OkHttpClient client = new OkHttpClient();

    /**
     * A connection made by client
     */
    private Call mConnection;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    /**
     * The deserialized JSON received from the themoviedb.org API.  It represents
     * either the most popular or highest rated movies.
     */
    private MovieCollection mMovies;

    /**
     * A grid of images of movie posters.
     */
    private RecyclerView mPosterView;

    private static final int SORT_MOST_POPULAR = 0;
    private static final int SORT_TOP_RATED = 1;

    private int mSortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setTitle(getTitle());
        }

        Resources res = getResources();
        mPosterView = (RecyclerView)findViewById(R.id.movie_list);
        if (mPosterView != null) {
            mPosterView.setLayoutManager(new VarColumnGridLayoutManager(this,
                    res.getDimensionPixelSize(R.dimen.poster_width)));
            mPosterView.setAdapter(new PosterViewAdapter(
                    mPosterView.getContext()));
        }

        // leave out fab for now until we find a good use for it.
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mSortOrder = pref.getInt("SORTORDER", SORT_MOST_POPULAR);

        if (savedInstanceState == null || !savedInstanceState.containsKey("MOVIES")) {
            Log.d(TAG, "No saved state - connecting");
            connect(mSortOrder);
        } else {
            Log.d(TAG, "Using saved state");
            mMovies = savedInstanceState.getParcelable("MOVIES");
            updateMovies();
        }

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     * <p/>
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     * <p/>
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     * <p/>
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     * <p/>
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        MenuItem item;
        switch (mSortOrder) {
            case SORT_MOST_POPULAR:
                item = menu.findItem(R.id.popular);
                break;

            case SORT_TOP_RATED:
                item = menu.findItem(R.id.rated);
                break;

            default:
                Log.e(TAG, "Invalid sort order");
                return false;
        }
        if (item != null) {
            item.setChecked(true);
        }

        return true;
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.popular:
                mSortOrder = SORT_MOST_POPULAR;
                break;
            case R.id.rated:
                mSortOrder = SORT_TOP_RATED;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        SharedPreferences.Editor pref = PreferenceManager.getDefaultSharedPreferences(this).edit();
        pref.putInt("SORTORDER", mSortOrder);
        pref.commit();

        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        connect(mSortOrder);

        return true;
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mConnection != null) {
            mConnection.cancel();
        }
    }

    /**
     * Saves all appropriate fragment state for e.g when device rotation occurs.
     *
     * @param outState           Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("MOVIES", mMovies);
    }

    /**
     * Creates an Asynchronous connection to the server and fetches the
     * appropriate list of movies.
     *
     * @param sortOrder either "popular" or "top_rated"
     *
     * @see #onResponse
     * @see #onFailure
     */
    private void connect(int sortOrder) {
        final String SERVER_URL = "http://api.tmdb.org/3/movie/";

        String sortAs;
        switch (sortOrder) {
            case SORT_MOST_POPULAR:
                sortAs = "popular";
                break;
            case SORT_TOP_RATED:
                sortAs = "top_rated";
                break;
            default:
                Log.e(TAG, "Invalid sort order");
                return;
        }

        String apiCall = SERVER_URL +
                sortAs +
                "?api_key=" +
                getResources().getString(R.string.api_key);
        Log.d(TAG, apiCall);
        Request request = new Request.Builder().url(apiCall).build();

        mConnection = client.newCall(request);
        startProgress();
        mConnection.enqueue(this);
    }

    /**
     * Called when the request could not be executed due to cancellation, a connectivity problem or
     * timeout. Because networks can fail during an exchange, it is possible that the remote server
     * accepted the request before the failure.
     *
     * @param call
     * @param e
     */
    @Override
    public void onFailure(Call call, IOException e) {
        stopProgress();

        if (call.isCanceled()) {
            Log.d(TAG, "Connection cancelled.");
        } else {
            Log.e(TAG, "Failed to fetch url due to:" + e.getMessage());
            loadFailure();
        }
    }

    /**
     * Called when the HTTP response was successfully returned by the remote server. The callback may
     * proceed to read the response body with {@link Response#body}. The response is still live until
     * its response body is closed with {@code response.body().close()}. The recipient of the callback
     * may even consume the response body on another thread.
     * <p/>
     * <p>Note that transport-layer success (receiving a HTTP response code, headers and body) does
     * not necessarily indicate application-layer success: {@code response} may still indicate an
     * unhappy HTTP response code like 404 or 500.
     *
     * @param call
     * @param response
     */
    @Override
    public void onResponse(Call call, Response response) throws IOException {
        stopProgress();

        if (!response.isSuccessful()) {
            Log.e(TAG, "Response unsuccessful due to: " + response.code());
            loadFailure();
            return;
        }
        String result = response.body().string();
        Log.d(TAG, result);
        response.body().close();

        try {
            // Read the server response and attempt to parse it as JSON
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            Gson gson = gsonBuilder.create();
            mMovies = null;
            mMovies = gson.fromJson(result, MovieCollection.class);
            updateMovies();
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse JSON due to: " + e.getMessage());
            loadFailure();
        }
    }

    private void startProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
                if (progress != null) {
                    progress.setVisibility(ProgressBar.VISIBLE);
                }
            }
        });
    }

    private void stopProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
                if (progress != null) {
                    progress.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });
    }
    /**
     * Recreates the recycler view on the ui thread.
     */
    private void updateMovies() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMovies != null) {
                    ((PosterViewAdapter) mPosterView.getAdapter()).reset(mMovies.getResults());
                }
            }
        });
    }

    private void loadFailure() {
        runOnUiThread(new Runnable() {
            /**
             * Starts executing the active part of the class' code. This method is
             * called when a thread is started that has been created with a class which
             * implements {@code Runnable}.
             */
            @Override
            public void run() {
                RecyclerView view = (RecyclerView)findViewById(R.id.movie_list);
                if (view != null) {
                    Snackbar.make(view, R.string.load_failure,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.try_again, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                connect(mSortOrder);
                            }
                        })
                        .show();
                }
            }
        });
    }

    public class PosterViewAdapter
            extends RecyclerView.Adapter<PosterViewAdapter.ViewHolder> {
        static final String POSTER_URL = "http://image.tmdb.org/t/p/w185";
        private Movie[] mValues;
        private final Context mContext;
        private final Picasso mPicasso;

        public PosterViewAdapter(Context ctx) {
            mContext = ctx;
            mValues = null;
            mPicasso = Picasso.with(ctx);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.movie_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues[position];
            String url = POSTER_URL +
                    mValues[position].getPoster_path();
            mPicasso.load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .fit()
                    .into(holder.mView);
            Log.d(TAG, "loading: " + url);
            holder.mView.setId(holder.mItem.getId());
            holder.mView.setContentDescription(getResources().getString(R.string.poster_description,
                    holder.mItem.getTitle()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putParcelable("MOVIE", holder.mItem);
                        MovieDetailFragment fragment = new MovieDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MovieDetailActivity.class);
                        intent.putExtra("MOVIE", holder.mItem);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues == null ? 0 : mValues.length;
        }

        void reset(Movie[] items) {
            mValues = null;
            mValues = items;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final ImageView mView;
            public Movie mItem;

            public ViewHolder(View view) {
                super(view);
                mView = (ImageView) view;
            }

            @Override
            public String toString() {
                return super.toString()  + " '" + getItemId() + "'";
            }
        }
    }
}
