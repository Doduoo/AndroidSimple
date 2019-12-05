package com.android.simple.v6;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.PagerSnapHelper;
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
    private PagerSnapHelper mPagerSnapHelper;

    public StackLayoutManager() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mPagerSnapHelper.attachToRecyclerView(view);
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

        int displayItemCount = Math.min(mCurrentPosition + mMaxItemCount, getItemCount());

        for (int i = displayItemCount - 1; i >= 0; i--) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);

            mItemWidth = getDecoratedMeasuredWidth(child) - mItemOffsetX * (mMaxItemCount + 1);
            mItemHeight = getDecoratedMeasuredHeight(child);

            int left = mItemOffsetX * i;
            int top = 0;
            int right = mItemWidth + mItemOffsetX * i;
            int bottom = mItemHeight;
            float scale = (float) Math.pow(mScaleY, i+1);

            if(mCurrentPosition == i) {
                left += (-mOffsetX);
                right += (-mOffsetX);
            }

            Log.d(TAG, String.format("left = %d, top = %d, right = %d, bottom = %d, scale = %f", left, top, right, bottom, scale));

            layoutDecoratedWithMargins(child, left, top, right, bottom);
            child.setScaleY(scale);
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        Log.d(TAG, "scrollHorizontallyBy: dx = " + dx);
        detachAndScrapAttachedViews(recycler);
        mOffsetX += dx;
        offsetChildrenHorizontal(-dx);
        fill(recycler, state);
        return dx;
    }

}
