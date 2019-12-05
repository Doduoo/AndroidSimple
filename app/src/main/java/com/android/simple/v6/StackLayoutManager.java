package com.android.simple.v6;

import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author LiuYong
 */
public class StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";
    private int mOffsetX = 0;
    private int mMaxItemCount = 3;
    private int mItemOffsetX = 60;
    private float mScaleY = 0.9f;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return false;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);

        int displayItemCount = Math.min(mMaxItemCount, getItemCount());

        for (int i = displayItemCount - 1; i >= 0; i--) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            layoutDecoratedWithMargins(child, -mOffsetX + mItemOffsetX * i, 0, getDecoratedMeasuredWidth(child) - mOffsetX + mItemOffsetX * i, getDecoratedMeasuredHeight(child));
            child.setScaleY((float) Math.pow(mScaleY, i));
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "scrollHorizontallyBy: dx = " + dx);
        int distance = dx;
        detachAndScrapAttachedViews(recycler);
        mOffsetX += distance;
        offsetChildrenHorizontal(-dx);
        fill(recycler, state);
        return distance;
    }
}
