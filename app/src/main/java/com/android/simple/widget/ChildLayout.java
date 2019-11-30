package com.android.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class ChildLayout extends LinearLayout {

    private final static String TAG = "TouchEvent";

    public ChildLayout(Context context) {
        super(context);
    }

    public ChildLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChildLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "ChildLayout - dispatchTouchEvent: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "ChildLayout - onInterceptTouchEvent: " + ev.getAction());
//        return super.onInterceptTouchEvent(ev);
        return ev.getAction() == MotionEvent.ACTION_MOVE;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "ChildLayout onTouchEvent:" + event.getAction());
//        return super.onTouchEvent(event);
        return true;
    }
}
