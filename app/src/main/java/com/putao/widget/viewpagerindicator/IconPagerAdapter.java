
package com.putao.widget.viewpagerindicator;

import android.graphics.drawable.Drawable;

import com.putao.camera.bean.WaterMarkCategoryInfo;


public interface IconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    Drawable getIconDrawable(int index);

    // From PagerAdapter
    int getCount();

    public WaterMarkCategoryInfo getPageCategoryInfo(int position);

}
