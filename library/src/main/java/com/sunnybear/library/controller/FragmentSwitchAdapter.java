package com.sunnybear.library.controller;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

/**
 * Fragment切换适配器
 * Created by guchenkai on 2015/11/27.
 */
public class FragmentSwitchAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> mFragmentArray;

    public FragmentSwitchAdapter(FragmentManager fm, SparseArray<Fragment> fragmentArray) {
        super(fm);
        mFragmentArray = fragmentArray != null && fragmentArray.size() > 0 ? fragmentArray : new SparseArray<Fragment>();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentArray.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentArray.size();
    }
}
