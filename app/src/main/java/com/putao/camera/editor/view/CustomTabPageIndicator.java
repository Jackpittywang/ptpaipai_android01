package com.putao.camera.editor.view;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.widget.viewpagerindicator.IconPagerAdapter;
import com.putao.widget.viewpagerindicator.TabPageIndicator;

public class CustomTabPageIndicator extends TabPageIndicator {
    public CustomTabPageIndicator(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    TabPageItemOnClickListener mTabPageItemOnClickListener;

    public void setTabPageItemOnClickListener(TabPageItemOnClickListener listener) {
        mTabPageItemOnClickListener = listener;
    }

    public CustomTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static interface TabPageItemOnClickListener {
        void onClick();
    }

    @Override
    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
                if (iconResId == -1) {
                    WaterMarkCategoryInfo info = iconAdapter.getPageCategoryInfo(i);
                    addTab(i, title, null, info);
                } else {
                    WaterMarkCategoryInfo info = iconAdapter.getPageCategoryInfo(i);
                    addTab(i, title, iconResId, info);
                }
            }
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    protected final OnClickListener mTabClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
            if ((WaterMarkCategoryInfo) view.getTag() != null) {
                WaterMarkCategoryInfo info = (WaterMarkCategoryInfo) view.getTag();
                if (info.updated.equals("1")) {
                    try {
                        WaterMarkConfigInfo mWaterMarkConfigInfo = WaterMarkHelper.getWaterMarkConfigInfoFromDB(getContext());
                        for (int i = 0; i < mWaterMarkConfigInfo.content.photo_watermark.size(); i++) {
                            WaterMarkCategoryInfo categoryInfoInfo = mWaterMarkConfigInfo.content.photo_watermark.get(i);
                            if (categoryInfoInfo.category.equals(info.category)) {
                                categoryInfoInfo.updated = "0";
                                tabView.setShowRedPoint(false);
                                break;
                            }
                        }
                        String json_str = new Gson().toJson(mWaterMarkConfigInfo);
                        SharedPreferencesHelper.saveStringValue(getContext(), PuTaoConstants.PREFERENC_WATERMARK_JSON, json_str);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mTabPageItemOnClickListener != null) {
                mTabPageItemOnClickListener.onClick();
            }
        }
    };

    protected void addTab(int index, CharSequence text, int iconResId, WaterMarkCategoryInfo info) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);

        if (iconResId != 0) {
            tabView.setCompoundDrawablesWithIntrinsicBounds(0, iconResId, 0, 0);
        }
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }

    protected void addTab(int index, CharSequence text, Drawable drawableIcon, WaterMarkCategoryInfo info) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);
        tabView.setGravity(Gravity.CENTER);
        if (info != null) {
            boolean bShow = info.updated.equals("1");
            tabView.setShowRedPoint(bShow);
        }
        tabView.setTag(info);
        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
    }
}