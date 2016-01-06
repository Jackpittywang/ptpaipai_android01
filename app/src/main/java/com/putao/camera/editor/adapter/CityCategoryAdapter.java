
package com.putao.camera.editor.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.putao.camera.bean.CityCategory;

public abstract class CityCategoryAdapter extends BaseAdapter {
    private List<CityCategory> categories = new ArrayList<CityCategory>();

    public void addCategory(String title, Adapter adapter) {
        categories.add(new CityCategory(title, adapter));
    }

    @Override
    public int getCount() {
        int total = 0;
        for (CityCategory category : categories) {
            total += category.getAdapter().getCount() + 1;
        }
        return total;
    }

    @Override
    public Object getItem(int position) {
        for (CityCategory category : categories) {
            if (position == 0) {
                return category;
            }
            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                return category.getAdapter().getItem(position - 1);
            }
            position -= size;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getViewTypeCount() {
        int total = 1;
        for (CityCategory category : categories) {
            total += category.getAdapter().getViewTypeCount();
        }
        return total;
    }

    public int getItemViewType(int position) {
        int typeOffset = 1;
        for (CityCategory category : categories) {
            if (position == 0) {
                return 0;
            }
            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                return typeOffset + category.getAdapter().getItemViewType(position - 1);
            }
            position -= size;
            typeOffset += category.getAdapter().getViewTypeCount();
        }
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int categoryIndex = 0;
        for (CityCategory category : categories) {
            if (position == 0) {
                return getTitleView(category.getTitle(), categoryIndex, convertView, parent);
            }
            int size = category.getAdapter().getCount() + 1;
            if (position < size) {
                return category.getAdapter().getView(position - 1, convertView, parent);
            }
            position -= size;
            categoryIndex++;
        }
        return null;
    }

    protected abstract View getTitleView(String caption, int index, View convertView, ViewGroup parent);
}