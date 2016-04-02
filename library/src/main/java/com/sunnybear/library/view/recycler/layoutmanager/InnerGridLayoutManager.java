package com.sunnybear.library.view.recycler.layoutmanager;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sunnybear.library.util.Logger;

/**
 * 内部GridLayoutManager
 * Created by guchenkai on 2015/11/13.
 */
public class InnerGridLayoutManager extends GridLayoutManager {

    public InnerGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public InnerGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    private int[] mMeasuredDimension = new int[2];

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        int height = 0;
        Logger.i("onMeasure---MeasureSpec-" + View.MeasureSpec.getSize(heightSpec));
        int childCount = getItemCount();
        for (int i = 0; i < childCount; i++) {
            View child = recycler.getViewForPosition(i);
            measureChild(child, widthSpec, heightSpec);
            if (i % getSpanCount() == 0) {
                int measuredHeight = child.getMeasuredHeight()/* + getDecoratedBottom(child)*/;
                height += measuredHeight;
            }
        }
        Logger.i("onMeasure---height-" + height);
        setMeasuredDimension(View.MeasureSpec.getSize(widthSpec), height);
//        final int widthMode = View.MeasureSpec.getMode(widthSpec);
//        final int heightMode = View.MeasureSpec.getMode(heightSpec);
//        final int widthSize = View.MeasureSpec.getSize(widthSpec);
//        final int heightSize = View.MeasureSpec.getSize(heightSpec);
//
//        int width = 0;
//        int height = 0;
//        int count = getItemCount();
//
//        for (int i = 0; i < count; i++) {
//            int span = getSpanCount()/* / getSpanSizeLookup().getSpanSize(i)*/;
//            measureScrapChild(recycler, i,
//                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(i, View.MeasureSpec.UNSPECIFIED),
//                    mMeasuredDimension);
//
//            if (getOrientation() == HORIZONTAL) {
//                if (i % span == 0)
//                    width = width + mMeasuredDimension[0];
//                if (i == 0)
//                    height = mMeasuredDimension[1];
//            } else {
//                if (i % span == 0)
//                    height = height + mMeasuredDimension[1];
//                if (i == 0)
//                    width = mMeasuredDimension[0];
//            }
//        }
//
//        switch (widthMode) {
//            case View.MeasureSpec.EXACTLY:
//                width = widthSize;
//            case View.MeasureSpec.AT_MOST:
//            case View.MeasureSpec.UNSPECIFIED:
//        }
//
//        switch (heightMode) {
//            case View.MeasureSpec.EXACTLY:
//                height = heightSize;
//            case View.MeasureSpec.AT_MOST:
//            case View.MeasureSpec.UNSPECIFIED:
//        }
//        setMeasuredDimension(width, height);
    }

    private void measureScrapChild(RecyclerView.Recycler recycler, int position, int widthSpec,
                                   int heightSpec, int[] measuredDimension) {
        try {
            View child = recycler.getViewForPosition(position);//fix 动态添加时报IndexOutOfBoundsException
            if (child != null) {
                RecyclerView.LayoutParams p = (RecyclerView.LayoutParams) child.getLayoutParams();
//                int childWidthSpec = RecyclerView.getChildMeasureSpec(widthSpec,
//                        getPaddingLeft() + getPaddingRight(), p.width, canScrollVertically());
//                int childHeightSpec = RecyclerView.getChildMeasureSpec(heightSpec,
//                        getPaddingTop() + getPaddingBottom(), p.height, canScrollVertically());
//                child.measure(childWidthSpec, childHeightSpec);
                measureChildWithMargins(child, widthSpec, heightSpec);
                measuredDimension[0] = child.getMeasuredWidth() + p.leftMargin + p.rightMargin;
                measuredDimension[1] = child.getMeasuredHeight() + p.bottomMargin + p.topMargin;
                recycler.recycleView(child);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
