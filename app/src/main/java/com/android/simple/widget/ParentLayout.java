package com.android.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ParentLayout extends LinearLayout {

    private final static String TAG = "TouchEvent";

    public ParentLayout(Context context) {
        super(context);
    }

    public ParentLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "ParentLayout - dispatchTouchEvent: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "ParentLayout - onInterceptTouchEvent: " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "ParentLayout onTouchEvent:" + event.getAction());
        return super.onTouchEvent(event);
    }
}
