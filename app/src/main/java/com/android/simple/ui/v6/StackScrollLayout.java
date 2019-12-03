package com.android.simple.ui.v6;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

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
     * 下拉状态
     */
    private final int STATUS_PULL_TO_REFRESH = 0;
    /**
     * 上拉状态
     */
    private static final int STATUS_LOAD_MORE = 1;
    /**
     * 刷新、加载释放状态
     */
    private final int STATUS_RELEASE = 2;
    /**
     * 正在刷新状态
     */
    private static final int STATUS_REFRESHING_LOADING = 3;
    /**
     * 刷新完成或未刷新状态
     */
    private static final int STATUS_REFRESH_FINISHED = 4;
    /**
     * 当前刷新状态
     */
    private int mCurrentStatus = STATUS_REFRESH_FINISHED;
    /**
     * 触发下拉刷新高度
     */
    private final int DEFAULT_HEADER_HEIGHT = 100;
    /**
     * 触发上拉加载高度
     */
    private final int DEFAULT_FOOTER_HEIGHT = 60;
    /**
     * 最大拖动比率(最大高度/Header高度)
     */
    protected float mHeaderMaxDragRate = 2.0f;
    /**
     * 最大拖动比率(最大高度/Footer高度)
     */
    protected float mFooterMaxDragRate = 2.0f;
    /**
     * 触发刷新距离 与 HeaderHeight 的比率
     */
    protected float mHeaderTriggerRate = 1.0f;
    /**
     * 触发加载距离 与 FooterHeight 的比率
     */
    protected float mFooterTriggerRate = 1.0f;
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
    private int mDistanceY;
    /**
     * 上一个滑动位置的Y坐标
     */
    private int mLastFocusY;
    /**
     * 摩擦系数
     */
    private float mScrollFriction = 0.5f;
    private int mTotalScrollY;
    private boolean mIsScrollUp = false;
    private int mTouchSlop;
    private Scroller mScroller;

    private OnRefreshListener mOnRefreshListener;

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
        mScroller = new Scroller(context);
    }

    public OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    public void setOnRefreshListener(OnRefreshListener refreshListener) {
        this.mOnRefreshListener = refreshListener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        MarginLayoutParams anchorMargin = (MarginLayoutParams) mAnchorView.getLayoutParams();
        mInitialTop = mAnchorView.getBottom() + anchorMargin.bottomMargin;
        mRecyclerView.setTranslationY(mInitialTop + mDistanceY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int y = (int) (ev.getY() + 0.5f);
        final int x = (int) (ev.getX() + 0.5f);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastFocusY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastFocusY;
                mDistanceY = deltaY;
                mLastFocusY = y;
                mIsScrollUp = deltaY < 0;
                break;
            case MotionEvent.ACTION_UP:
                mLastFocusY = 0;
                mDistanceY = 0;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = false;
        final LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() != 1) return true;
                if (mIsScrollUp) {
                    consumed = mRecyclerView.getTranslationY() != 0;
                    if (mRecyclerView.getTranslationY() != 0) {
                        if (Math.abs(mDistanceY) < mTouchSlop) return false;
                        updateRecyclerViewTransitionY(mDistanceY);
                        consumed = true;
                    } else if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                        // recyclerView 内部不能再向上滑动时(加载更多)
                        mCurrentStatus = STATUS_LOAD_MORE;
                        consumed = true;
                        invalidate();
                    }
                } else {
                    if (layoutManager != null && layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (mRecyclerView.getTranslationY() == mInitialTop) {
                            // recyclerView 滑动到底部并且内部不能再向下滑动时(下拉刷新)
                            mCurrentStatus = STATUS_PULL_TO_REFRESH;
                            final int distance = (int) (-mDistanceY * mScrollFriction);
                            mScroller.startScroll(0, mTotalScrollY, 0, distance);
                            mTotalScrollY += distance;
                            invalidate();
                        } else {
                            if (Math.abs(mDistanceY) < mTouchSlop) return false;
                            updateRecyclerViewTransitionY(mDistanceY);
                        }
                        consumed = true;
                    } else {
                        consumed = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if(mCurrentStatus == STATUS_PULL_TO_REFRESH || mCurrentStatus == STATUS_LOAD_MORE) {
                    mCurrentStatus = STATUS_RELEASE;
                    rollBack();
                } else {
                    autoScrollAnimator();
                }
                break;
            default:
                consumed = super.onTouchEvent(event);
                break;
        }
        Log.d(TAG, "onTouchEvent consumed = " + consumed + ", action = " + event.getAction());
        return consumed;
    }

    @Override
    public void computeScroll() {
        if(mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
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

    private void rollBack() {
        if(mCurrentStatus == STATUS_RELEASE) {
            mScroller.startScroll(0, mTotalScrollY, 0, -mTotalScrollY);
            mTotalScrollY = 0;
            invalidate();
        }
    }

    private void autoScrollAnimator() {
        final float tranY = mRecyclerView.getTranslationY();
        if (tranY >= 0 && tranY <= mInitialTop) {
            ValueAnimator mAdsorbAnimator;
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

    /**
     * 刷新接口
     */
    public interface OnRefreshListener {
        /**
         * 触发刷新时候回调
         */
        void onRefresh();

        /**
         * 刷新下拉进度，[-1.0-1.0]，当progress为1.0时回调{@link OnRefreshListener#onRefresh()}
         * 0~-1 刷新完成回滚状态，0-1为进行刷新，
         *
         * @param progress 刷新下来进度
         */
        void onRefreshProgress(float progress);
    }
}
