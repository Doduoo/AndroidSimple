package com.android.simple.ui.v6;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        MarginLayoutParams anchorMargin = (MarginLayoutParams) mAnchorView.getLayoutParams();
        final int anchorBottom = mAnchorView.getBottom() + anchorMargin.bottomMargin;
        mRecyclerView.layout(mRecyclerView.getLeft(), anchorBottom, mRecyclerView.getRight(), mRecyclerView.getBottom());
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

}
