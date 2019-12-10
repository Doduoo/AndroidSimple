package com.android.simple.v6.banner;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author LiuYong
 */
public class StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";
    private int mOffsetX = 0;
    private int mMaxItemCount = 3;
    private int mItemOffsetX = 60;
    private float mScaleY = 0.95f;
    private int mItemWidth;
    private int mItemHeight;
    private int mCurrentPosition = 0;

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, state, dx);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dx) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            return 0;
        }

        detachAndScrapAttachedViews(recycler);

        mOffsetX += dx;
        int endPosition = Math.min(mCurrentPosition + mMaxItemCount, getItemCount()) - 1;


        for (int i = endPosition; i >= 0; i--) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);

            mItemWidth = getDecoratedMeasuredWidth(child) - mItemOffsetX * (mMaxItemCount + 1);
            mItemHeight = getDecoratedMeasuredHeight(child);

            int left = mItemOffsetX * i;
            int top = 0;
            int right = mItemWidth + mItemOffsetX * i;
            int bottom = mItemHeight;


            if (mCurrentPosition == i) {
                left += (-mOffsetX);
                right += (-mOffsetX);
            }

            layoutDecoratedWithMargins(child, left, top, right, bottom);
            scale(child, i);
        }
        return dx;
    }

    private void scale(View child, int position) {
        float scale = (float) Math.pow(mScaleY, position - mCurrentPosition);
        child.setScaleY(scale);
    }

}
