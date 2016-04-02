package com.sunnybear.library.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 懒加载fragment
 * Created by guchenkai on 2015/11/18.
 */
public abstract class LazyFragment extends Fragment {
    protected boolean isVisible;//是否可见
    private Bundle savedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 在这里实现Fragment数据的缓加载
     *
     * @param isVisibleToUser 是否可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInVisible();
        }
    }

    /**
     * fragment被设置为可见时调用
     */
    protected void onVisible() {
        lazyLoad(savedInstanceState);
    }

    protected abstract void lazyLoad(Bundle savedInstanceState);

    /**
     * fragment被设置为不可见时调用
     */
    protected void onInVisible() {

    }
}
