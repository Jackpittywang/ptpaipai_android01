package com.sunnybear.library.view.viewpager.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanghao on 2016/3/15.
 */
public abstract class LoadMoreFragmentPagerAdapter<T extends Serializable> extends FragmentPagerAdapter {
    private List<T> mDatas;

    public LoadMoreFragmentPagerAdapter(FragmentManager fm, ArrayList<T> datas) {
        super(fm);
        this.mDatas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        return getItem(mDatas, position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (position >= mDatas.size() - 3) {
            /*mDatas.addAll(mDatas);
            notifyDataSetChanged();*/
            loadMoreData();
        }
    }


    public abstract void loadMoreData();

    public void addData(List<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public abstract Fragment getItem(List<T> datas, int position);
}
