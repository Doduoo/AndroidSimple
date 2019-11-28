package com.android.simple.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView

const val TAG = "CreditEaseV6Layout"

/**
 *
 * @author LiuYong
 */
class CreditEaseV6Layout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    /**RecyclerView背景默认圆角大小，单位px*/
    private val mCornerSize =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 23.0f, resources.displayMetrics)
    /**RecyclerView初始偏移距离*/
    private var mOffsetY = 0.0f
    /**手势监听*/
    private var mGestureDetector = GestureDetector(context, GestureListener())
    /**弹性滑动、自动吸附*/
    private var mAdsorbAnimator: ValueAnimator? = null

    /**正数向上，负数向下*/
    private var mScrollerDirection = 0.0f

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val headView = getChildAt(0)
        val recyclerView = getChildAt(1)
        mOffsetY = headView.bottom - mCornerSize
        recyclerView.translationY = mOffsetY
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "onInterceptTouchEvent")
        return mGestureDetector.onTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent")
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            autoScrollRecyclerViewLayout(mScrollerDirection > 0)
            return true
        }
        return mGestureDetector.onTouchEvent(event)
    }

    /**缓慢滑动到执行位置*/
    private fun autoScrollRecyclerViewLayout(isScrollToTop: Boolean) {
        val transitionY = getChildAt(1).translationY

        mAdsorbAnimator = if (transitionY <= mOffsetY * 0.15 && transitionY >= 0) {
            ValueAnimator.ofFloat(transitionY, 0.0f)
        } else if (transitionY >= mOffsetY * 0.85 && transitionY <= mOffsetY) {
            ValueAnimator.ofFloat(transitionY, mOffsetY)
        } else {
            if (isScrollToTop) {
                ValueAnimator.ofFloat(transitionY, 0.0f)
            } else {
                ValueAnimator.ofFloat(transitionY, mOffsetY)
            }
        }
        if (mAdsorbAnimator != null) {
            mAdsorbAnimator!!.duration = 250
            mAdsorbAnimator!!.interpolator = AccelerateInterpolator()
            mAdsorbAnimator!!.addUpdateListener {
                val v = it.animatedValue as Float
                getChildAt(1).translationY = v
            }
            mAdsorbAnimator!!.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    mAdsorbAnimator!!.removeAllUpdateListeners()
                    mAdsorbAnimator!!.removeAllListeners()
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })
            if (!mAdsorbAnimator!!.isRunning) {
                mAdsorbAnimator!!.start()
            }
        }
    }

    inner class GestureListener : GestureDetector.OnGestureListener {
        override fun onShowPress(e: MotionEvent?) {
            Log.d(TAG, "onShowPress")
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d(TAG, "onSingleTapUp")
            return false
        }

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d(TAG, "onDown")
            return false
        }

        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            Log.d(TAG, "onFling - velocityY = $velocityY")
            return false
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            Log.d(TAG, "onScroll - distanceY = $distanceY, e1 = ${e1?.actionMasked == MotionEvent.ACTION_UP}, e2 = ${e2?.actionMasked == MotionEvent.ACTION_UP}")
            mScrollerDirection = distanceY
            val rv = getChildAt(1) as RecyclerView
            val transitionY = rv.translationY
            if (distanceY < 0) { // 向下滑动

                // 如果RV内容还能向下滑动则不拦截滑动事件，事件会交给rv处理，否则进行rv相对于父布局的滑动
                if (rv.canScrollVertically(-1)) {
                    return false
                }

                if (transitionY < mOffsetY) {
                    if ((transitionY - distanceY) < mOffsetY) { // 处理快速滑动越界问题
                        getChildAt(1).translationY -= distanceY
                    } else {
                        getChildAt(1).translationY = mOffsetY
                    }
                } else {
                    return false
                }
            } else { // 向上滑动
                if (transitionY > 0) {
                    if ((transitionY - distanceY) > 0) { // 处理快速滑动越界问题
                        getChildAt(1).translationY -= distanceY
                    } else {
                        getChildAt(1).translationY = 0.0f
                    }
                } else {
                    return false
                }
            }
            return true
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d(TAG, "onLongPress")
        }
    }

}