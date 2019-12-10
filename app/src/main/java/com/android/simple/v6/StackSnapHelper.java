package com.android.simple.v6;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

/**
 * @author LiuYong
 */
public class StackSnapHelper extends SnapHelper {

    @Nullable
    private OrientationHelper mVerticalHelper;

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToTop(layoutManager, targetView, getVerticalHelper(layoutManager));
        }
        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager.canScrollVertically()) {
            int childCount = layoutManager.getChildCount();
            if (childCount > 2) {
                return layoutManager.getChildAt(1);
            }
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount == 0) {
            return RecyclerView.NO_POSITION;
        }

        return 1;
    }

    private int distanceToTop(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView, OrientationHelper helper) {
        int childCount = layoutManager.getChildCount();
        if (childCount > 2) {
            final View child0 = layoutManager.getChildAt(0);
            int childCenter = helper.getDecoratedStart(child0) + (helper.getDecoratedMeasurement(child0) / 2);
            final View child1 = layoutManager.getChildAt(1);
            if(helper.getDecoratedStart(child0) > helper.getDecoratedStart(child1)) {
                return 0;
            }
            if(childCenter > helper.getDecoratedStart(child1)) {
                return helper.getDecoratedStart(child1) - helper.getDecoratedStart(child0);
            } else {
                return helper.getDecoratedStart(child0) - helper.getDecoratedStart(child1);
            }
        } else {
            return 0;
        }
    }

    @NonNull
    private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mVerticalHelper == null || mVerticalHelper.getLayoutManager() != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager);
        }
        return mVerticalHelper;
    }
}
