package com.sunnybear.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.sunnybear.library.R;

/**
 * Created by zhanghao on 2016/2/28.
 */
public class InterceptLinearLayout extends LinearLayout {
    public InterceptLinearLayout(Context context) {
        this(context, null);
    }

    public InterceptLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public InterceptLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InterceptLinearLayout, 0, 0);
        isIntercept = typedArray.getBoolean(R.styleable.InterceptLinearLayout_isIntercept, false);
    }

    private boolean isIntercept;

    public void setIntercept(boolean isIntercept) {
        this.isIntercept = isIntercept;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept ? true : super.onInterceptTouchEvent(ev);
    }
}
