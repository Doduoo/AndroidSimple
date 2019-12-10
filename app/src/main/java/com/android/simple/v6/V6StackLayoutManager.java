package com.android.simple.v6;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author LiuYong
 */
public class V6StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";
    // 垂直方向总的偏移量
    private int mOffsetY = 0;
    private StackSnapHelper mStackSnapHelper;
    private OnCoverItemListener mOnCoverItemListener;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            removeAndRecycleAllViews(recycler);
            return 0;
        }

        detachAndScrapAttachedViews(recycler);

        int consumed = dy;

        if (dy < 0) {
            // 向下滑动, 防止越界
            if (mOffsetY + consumed <= 0) {
                mOffsetY = 0;
                consumed = Math.abs(mOffsetY);
            } else {
                mOffsetY += consumed;
            }
        }

        int childrenTop = 0;

        // layout children
        for (int i = 0; i <= getItemCount() - 1; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int itemWidth = getDecoratedMeasuredWidth(child);
            int itemHeight = getDecoratedMeasuredHeight(child);

            if (dy > 0) {
                Rect rect = new Rect();
                View lastChild = getChildAt(getItemCount() - 1);
                if (lastChild != null) {
                    lastChild.getHitRect(rect);
                    if (lastChild.getBottom() - consumed <= getHeight()) {
                        mOffsetY += (lastChild.getBottom() - getHeight());
                        consumed = lastChild.getBottom() - getHeight();
                    } else {
                        mOffsetY += consumed;
                    }
                }
            }

            int left = 0;
            int top = childrenTop - mOffsetY;
            int right = itemWidth;
            int bottom = childrenTop + itemHeight - mOffsetY;

            // 第一个Item位置不变
            if (i == 0) {
                top = childrenTop;
                bottom = childrenTop + itemHeight;
            }

            layoutDecoratedWithMargins(child, left, top, right, bottom);

            if (mOnCoverItemListener != null && getItemCount() >= 2) {
                View child0 = getChildAt(0);
                View child1 = getChildAt(1);
                if(child0 != null && child1 != null) {
                    final int child0Top = getDecoratedTop(child0);
                    final int child1Top = getDecoratedTop(child1);
                    mOnCoverItemListener.onCoverItem(child0Top >= child1Top);
                }
            }

            childrenTop += itemHeight;
        }
        return consumed;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        if (mStackSnapHelper == null) {
            mStackSnapHelper = new StackSnapHelper();
        }
        mStackSnapHelper.attachToRecyclerView(view);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, state, dy);
    }

    public void setOnCoverItemListener(OnCoverItemListener onCoverItemListener) {
        this.mOnCoverItemListener = onCoverItemListener;
    }

    public interface OnCoverItemListener {
        /**
         * @param isCover 第一个item是否被完全覆盖
         */
        void onCoverItem(boolean isCover);
    }
}
