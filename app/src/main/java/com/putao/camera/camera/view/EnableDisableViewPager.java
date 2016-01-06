
package com.putao.camera.camera.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by yanglun on 15/3/3.
 */
public class EnableDisableViewPager extends ViewPager {
    private boolean enabled = true;

    public EnableDisableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (enabled)
            return super.onInterceptTouchEvent(arg0);
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
