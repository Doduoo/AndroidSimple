package com.android.simple.v6;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author LiuYong
 */
public class V6StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";
    // 垂直方向总的偏移量
    private int mOffsetY = 0;
    // 列表的总高度
    private int mTotalHeight;

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
        View child0 = recycler.getViewForPosition(0);


        Log.d(TAG, String.format("mOffsetY = %d, consumed = %d, bottom = %d，mTotalHeight = %d", mOffsetY, consumed, child0.getBottom(), getHeight()));
        if (dy < 0) {
            // 向下滑动, 防止越界
            if (mOffsetY + consumed <= 0) {
                mOffsetY = 0;
                consumed = Math.abs(mOffsetY);
            } else {
                mOffsetY += consumed;
            }
        } else {
            // 向上滑动，防止越界
            Rect rect = new Rect();
            View lastChild = getChildAt(getItemCount() - 1);
            if (lastChild != null) {
                lastChild.getHitRect(rect);
//                Log.d(TAG, String.format("last item rect = %s, height = %d", rect.toShortString(), getHeight()));
                if (lastChild.getBottom() - consumed <= getHeight()) {
                    mOffsetY += getHeight();
                    consumed = lastChild.getBottom() - getHeight();
                } else {
                    mOffsetY += consumed;
                }
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

            if(dy > 0) {
                Rect rect = new Rect();
                View lastChild = getChildAt(getItemCount() - 1);
                if (lastChild != null) {
                    lastChild.getHitRect(rect);
                    Log.d(TAG, String.format("last item rect = %s, height = %d", rect.toShortString(), getHeight()));
                    if (lastChild.getBottom() - consumed <= getHeight()) {
                        mOffsetY += getHeight();
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

            childrenTop += itemHeight;
            mTotalHeight += itemHeight;
        }

        Log.d(TAG, String.format("height = %d, width = %d", getHeight(), getWidth()));

        return consumed;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return fill(recycler, state, dy);
    }

    private void getBounds(int position) {
        Rect rect = new Rect();
        View lastChild = getChildAt(position);
        if (lastChild != null) {
            lastChild.getHitRect(rect);
            Log.d(TAG, String.format("last item rect = %s, position = %d", rect.toShortString(), position));
        }
    }
}
