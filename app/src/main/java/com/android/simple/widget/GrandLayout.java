package com.android.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class GrandLayout extends LinearLayout {

    private final static String TAG = "TouchEvent";

    public GrandLayout(Context context) {
        super(context);
    }

    public GrandLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GrandLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "GrandLayout - dispatchTouchEvent: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "GrandLayout - onInterceptTouchEvent: " + ev.getAction());
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "GrandLayout onTouchEvent:" + event.getAction());
        return super.onTouchEvent(event);
    }
}
