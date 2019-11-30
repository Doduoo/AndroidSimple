package com.android.simple.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class InternalView extends View {

    private static final String TAG = "TouchEvent";

    public InternalView(Context context) {
        super(context);
    }

    public InternalView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InternalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "InternalView - dispatchTouchEvent: " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "InternalView onTouchEvent:" + event.getAction());
        return super.onTouchEvent(event);
    }

}
