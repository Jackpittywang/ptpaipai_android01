package com.putao.camera.editor.adapter;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.editor.fragment.WaterMarkChoiceFragment;
import com.putao.widget.viewpagerindicator.IconPagerAdapter;

import java.util.ArrayList;

/**
 * Created by jidongdong on 15/3/3.
 */
public class MyIconPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    private WaterMarkConfigInfo mWaterMarkConfigInfo;
    private FragmentManager fm;
    private ArrayList<WaterMarkCategoryInfo> mPhoto_watermark;

    public MyIconPagerAdapter(FragmentManager fm, WaterMarkConfigInfo markConfigInfo) {
        super(fm);
        this.fm = fm;
        this.mWaterMarkConfigInfo = markConfigInfo;
        mPhoto_watermark = mWaterMarkConfigInfo.content.photo_watermark;
    }

    @Override
    public Fragment getItem(int position) {
        WaterMarkCategoryInfo aWaterMarkCategoryInfo = mPhoto_watermark.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("WaterMarkCategoryInfo", aWaterMarkCategoryInfo);
        return WaterMarkChoiceFragment.newInstance(bundle);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int size = mPhoto_watermark.size();
        return mPhoto_watermark.get(position % size).category;
    }

    @Override
    public WaterMarkCategoryInfo getPageCategoryInfo(int position) {
        return mPhoto_watermark.get(position);
    }

    @Override
    public int getIconResId(int index) {
        return -1;
    }

    @Override
    public int getCount() {
        return mPhoto_watermark.size();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Drawable getIconDrawable(int index) {
        return null;
//        if (mWaterMarkConfigInfo == null)
//            return null;
//        ArrayList<WaterMarkCategoryInfo> content = mPhoto_watermark;
//        WaterMarkCategoryInfo info = content.get(index);
//        String icon_selected = info.icon_selected;
//        String icon_normal = info.icon;
//        StateListDrawable drawable = new StateListDrawable();
//        String normal_path = WaterMarkHelper.getWaterMarkFilePath() + icon_normal;
//        Bitmap normal_bitmap = BitmapHelper.getInstance().loadBitmap(normal_path);
//        int size = DisplayHelper.dipTopx(65);
//        normal_bitmap = Bitmap.createScaledBitmap(normal_bitmap, size, size, false);
//        String selected_path = WaterMarkHelper.getWaterMarkFilePath() + icon_selected;
//        Bitmap selected_bitmap = BitmapHelper.getInstance().loadBitmap(selected_path);
//        selected_bitmap = Bitmap.createScaledBitmap(selected_bitmap, size, size, false);
//        drawable.addState(new int[]{android.R.attr.state_focused}, new BitmapDrawable(selected_bitmap));
//        drawable.addState(new int[]{android.R.attr.state_pressed}, new BitmapDrawable(selected_bitmap));
//        drawable.addState(new int[]{android.R.attr.state_selected}, new BitmapDrawable(selected_bitmap));
//        drawable.addState(new int[]{}, new BitmapDrawable(normal_bitmap));
//        return drawable;
    }
}
