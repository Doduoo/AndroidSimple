package com.android.simple.v6;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author LiuYong
 */
public class V6StackLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = "StackLayoutManager";
    // 垂直方向总的偏移量
    private int mOffsetY = 0;
    private boolean isScrollUp = false;
    private ValueAnimator mValueAnimator;
    private RecyclerView.Recycler mRecycler;
    private RecyclerView.State mState;
    private RecyclerView mRecyclerView;
    private StackSnapHelper mStackSnapHelper;

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
        mRecycler = recycler;
        mState = state;
        fill(recycler, state, 0, false);
    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy, boolean isAnimator) {
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
            if (mOffsetY + consumed <= 0 && !isAnimator) {
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
                    Log.d(TAG, String.format("last item rect = %s, height = %d", rect.toShortString(), getHeight()));
                    if (lastChild.getBottom() - consumed <= getHeight() && !isAnimator) {
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

            childrenTop += itemHeight;
        }

        Log.d(TAG, String.format("height = %d, width = %d", getHeight(), getWidth()));

        return consumed;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        mRecyclerView = view;
        if(mStackSnapHelper == null) {
            mStackSnapHelper = new StackSnapHelper();
        }
        mStackSnapHelper.attachToRecyclerView(view);
//        view.setOnTouchListener(mOnTouchListener);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        isScrollUp = dy > 0;
        return fill(recycler, state, dy, false);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (getItemCount() < 2) return false;
                View child0 = getChildAt(0);
                View child1 = getChildAt(1);
                Rect rect0 = new Rect();
                Rect rect1 = new Rect();
                if (child0 != null && child1 != null) {
                    child0.getHitRect(rect0);
                    child1.getHitRect(rect1);

                    Log.d(TAG, String.format("action = %d, child0 top = %d, bottom = %d, child1 top = %d, bottom = %d", event.getAction(), rect0.top, rect0.bottom, rect1.top, rect1.bottom));

                    if (rect1.top > rect0.top && rect1.top < rect0.bottom) {
                        if (isScrollUp) {
                            smoothScrollToPosition(mRecyclerView, mState, 1);
                            Log.d(TAG, "isScrollUp true top = " + rect1.top);
                        } else {
//                            autoScroll(0, rect1.top - rect0.bottom);

                            smoothScrollToPosition(mRecyclerView, mState, 0);
                            Log.d(TAG, "isScrollUp false top = " + (rect1.top - rect0.bottom));
                        }
                    }
                }
            }
            return false;
        }
    };


    private void autoScroll(int start, int end) {
        mValueAnimator = ValueAnimator.ofInt(start, end);
        mValueAnimator.setDuration(2000);
        mValueAnimator.start();
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                fill(mRecycler, mState, animatedValue, true);
            }
        });
    }
}
