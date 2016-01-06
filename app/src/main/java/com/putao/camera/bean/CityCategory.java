
package com.putao.camera.bean;

import android.widget.Adapter;

import com.putao.camera.base.BaseItem;

public class CityCategory extends BaseItem {
    private String mTitle;
    private Adapter mAdapter;

    public CityCategory(String title, Adapter adapter) {
        mTitle = title;
        mAdapter = adapter;
    }

    public void setTile(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
    }

    public Adapter getAdapter() {
        return mAdapter;
    }
}
