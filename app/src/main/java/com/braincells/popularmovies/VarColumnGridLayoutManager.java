package com.braincells.popularmovies;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Intelligently set number of columns in a RecyclerView based on the
 * width of the device.
 *
 * Taken from:
 * http://stackoverflow.com/questions/27744788/changing-number-of-columns-in-recyclerview-gridlayout
 *
 * @author http://stackoverflow.com/users/1635133/jasxir
 * Created by jaldhar on 5/5/16.
 */
class VarColumnGridLayoutManager extends GridLayoutManager {

    private final int minItemWidth;

    public VarColumnGridLayoutManager(Context context, int minItemWidth) {
        super(context, 1);
        this.minItemWidth = minItemWidth;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler,
                                 RecyclerView.State state) {
        updateSpanCount();
        super.onLayoutChildren(recycler, state);
    }

    private void updateSpanCount() {
        int spanCount = getWidth() / minItemWidth;
        if (spanCount < 1) {
            spanCount = 1;
        }
        this.setSpanCount(spanCount);
    }
}
