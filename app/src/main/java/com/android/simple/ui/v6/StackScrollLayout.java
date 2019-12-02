package com.android.simple.ui.v6;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.simple.R;

/**
 * V6.0版本首页、产品列表层叠滑动刷新布局
 * 支持刷新、加载
 * 支持底部列表滑动覆盖头部
 *
 * @author LiuYong
 */
public class StackScrollLayout extends FrameLayout {

    private static final String TAG = "StackScrollLayout";

    /**
     * 底部列表顶部对齐的锚点
     */
    private View mAnchorView;
    private int mAnchorId;
    /**
     * recyclerView
     */
    private RecyclerView mRecyclerView;
    private Rect mTempRect = new Rect();
    /**
     * 初始recyclerView 上边缘坐标
     */
    private int mInitialTop;
    /**
     * 手势垂直方向滑动距离，正数向下滑动、负数向上滑动
     */
    private int mScrollY;
    private int mScrollX;
    /**
     * 上一个滑动位置的Y坐标
     */
    private int mLastFocusY;
    private int mLastFocusX;
    private boolean mIsScrollUp = false;
    private int mTouchSlop;
    /**
     * 弹性滑动、自动吸附
     */
    private ValueAnimator mAdsorbAnimator;

    public StackScrollLayout(@NonNull Context context) {
        this(context, null);
    }

    public StackScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StackScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StackScrollLayout);
        mAnchorId = typedArray.getResourceId(0, -1);
        typedArray.recycle();

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        MarginLayoutParams anchorMargin = (MarginLayoutParams) mAnchorView.getLayoutParams();
        mInitialTop = mAnchorView.getBottom() + anchorMargin.bottomMargin;
        mRecyclerView.setTranslationY(mInitialTop + mScrollY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int y = (int) (ev.getY() + 0.5f);
        final int x = (int) (ev.getX() + 0.5f);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastFocusY = y;
                mLastFocusX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastFocusY;
                int deltaX = x - mLastFocusY;
                mScrollY = deltaY;
                mScrollX = deltaX;
                mLastFocusY = y;
                mIsScrollUp = deltaY < 0;
//                if (deltaY > 0) {
//                    Log.d(TAG, "向下滑动 = " + mScrollY);
//                } else {
//                    Log.d(TAG, "向上滑动 = " + mScrollY);
//                }

                break;
            case MotionEvent.ACTION_UP:
                mLastFocusY = 0;
                mScrollY = 0;
                mScrollX = 0;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean consumed = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if(ev.getPointerCount() != 1) return true;
                mRecyclerView.getGlobalVisibleRect(mTempRect);
                if (!mTempRect.contains((int) ev.getRawX(), (int) ev.getRawY())) return false;
                if (mIsScrollUp) {
                    consumed = mRecyclerView.getTranslationY() != 0;
                    final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                        // recyclerView 内部不能再向上滑动时(加载更多)
                        consumed = true;
                    }
                } else {
                    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();

                    if (linearLayoutManager != null) {
                        consumed = linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                    }
                }
                break;
            default:
                consumed = super.onInterceptTouchEvent(ev);
                break;
        }
        Log.d(TAG, "onInterceptTouchEvent consumed = " + consumed + ", action = " + ev.getAction());
        return consumed;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = false;
        final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(mScrollY) < mTouchSlop) return false;
                if(event.getPointerCount() != 1) return true;
                if (mIsScrollUp) {
                    consumed = mRecyclerView.getTranslationY() != 0;
                    if (mRecyclerView.getTranslationY() != 0) {
                        updateRecyclerViewTransitionY(mScrollY);
                        consumed = true;
                    } else if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                        // recyclerView 内部不能再向上滑动时(加载更多)
                        Log.d(TAG, "加载更多");
                        consumed = true;
                    }
                } else {
                    if (layoutManager != null && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (mRecyclerView.getTranslationY() == mInitialTop) {
                            Log.d(TAG, "下拉刷新");
                        } else {
                            updateRecyclerViewTransitionY(mScrollY);
                        }
                        consumed = true;
                    } else {
                        consumed = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                autoScrollAnimator();
                break;
            default:
                consumed = super.onTouchEvent(event);
                break;
        }
        Log.d(TAG, "onTouchEvent consumed = " + consumed + ", action = " + event.getAction());
        return consumed;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mAnchorId != -1) {
            mAnchorView = findViewById(mAnchorId);
            if (mAnchorView == null) {
                throw new IllegalStateException("anchor is null!");
            }
            Log.d(TAG, "anchor view is " + mAnchorView.getClass().getName());
        } else {
            throw new IllegalArgumentException("please set attr anchor!");
        }

        View view = getChildAt(getChildCount() - 1);
        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) getChildAt(getChildCount() - 1);
        } else {
            throw new IllegalArgumentException(String.format("index %d child must is RecyclerView!", getChildCount() - 1));
        }
    }

    private void updateRecyclerViewTransitionY(int deltaY) {
        final float transitionY = mRecyclerView.getTranslationY();
        if (transitionY + deltaY >= mInitialTop) {
            mRecyclerView.setTranslationY(mInitialTop);
        } else if (transitionY + deltaY <= 0) {
            mRecyclerView.setTranslationY(0);
        } else {
            mRecyclerView.setTranslationY(transitionY + deltaY);
        }
    }

    private void autoScrollAnimator() {
        final float tranY = mRecyclerView.getTranslationY();
        if (tranY >= 0 && tranY <= mInitialTop) {
            if (tranY <= mInitialTop * 0.15 && tranY >= 0) {
                mAdsorbAnimator = ValueAnimator.ofFloat(tranY, 0.0f);
            } else if (tranY >= mInitialTop * 0.85 && tranY <= mInitialTop) {
                mAdsorbAnimator = ValueAnimator.ofFloat(tranY, mInitialTop);
            } else {
                if (mIsScrollUp) {
                    mAdsorbAnimator = ValueAnimator.ofFloat(tranY, 0.0f);
                } else {
                    mAdsorbAnimator = ValueAnimator.ofFloat(tranY, mInitialTop);
                }
            }
            mAdsorbAnimator.setDuration(250);
            mAdsorbAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            mAdsorbAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float value = (float) animation.getAnimatedValue();
                    mRecyclerView.setTranslationY(value);
                }
            });
            mAdsorbAnimator.start();
        }
    }
}
