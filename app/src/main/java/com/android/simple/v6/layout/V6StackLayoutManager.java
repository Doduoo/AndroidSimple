package com.android.simple.v6.layout;

import android.annotation.SuppressLint;
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

        mOffsetY += dy;

        int childrenTop = 0;

        // layout children
        for (int i = 0; i <= getItemCount() - 1; i++) {
            View child = recycler.getViewForPosition(i);
            addView(child);
            measureChildWithMargins(child, 0, 0);
            int itemWidth = getDecoratedMeasuredWidth(child);
            int itemHeight = getDecoratedMeasuredHeight(child);

            int top = childrenTop - mOffsetY;
            int bottom = childrenTop + itemHeight - mOffsetY;

            // 第一个Item位置不变
            if (i == 0) {
                top = childrenTop;
                bottom = childrenTop + itemHeight;
            }

            layoutDecoratedWithMargins(child, 0, top, itemWidth, bottom);

            if (mOnCoverItemListener != null && getItemCount() >= 2) {
                View child0 = getChildAt(0);
                View child1 = getChildAt(1);
                if (child0 != null && child1 != null) {
                    final int child0Top = getDecoratedTop(child0);
                    final int child1Top = getDecoratedTop(child1);
                    mOnCoverItemListener.onCoverItem(child0Top >= child1Top);
                }
            }

            childrenTop += itemHeight;
        }
        return dy;
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
        if (getItemCount() == 0) return 0;
        View child0 = getChildAt(0);
        View child1 = getChildAt(1);
        if (child0 == null || child1 == null) return 0;
        int child0Bottom = getDecoratedBottom(child0);
        int child1Top = getDecoratedTop(child1);

        // 向下滑动item1上边不能超过item0的下边缘
        if (dy < 0 && child1Top - dy >= child0Bottom) {
            return fill(recycler, state, child1Top - child0Bottom);
        }

        View lastView = getChildAt(getItemCount() - 1);
        // 向上滑动，最后一个item的底边不能超过屏幕的底边
        if (lastView != null && dy > 0) {
            int lastViewBottom = getDecoratedBottom(lastView);
            if(lastViewBottom - dy <= getHeight()) {
                return fill(recycler, state, lastViewBottom - getHeight());
            } else {
                return fill(recycler, state, dy);
            }
        } else {
            return fill(recycler, state, dy);
        }
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
