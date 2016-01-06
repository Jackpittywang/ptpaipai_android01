
package com.putao.camera.editor.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.putao.camera.R;

public class CitySearchAdapter extends BaseAdapter {
    private ArrayList<String> cityList;
    private Context mContext;

    public CitySearchAdapter(Context context, ArrayList<String> list) {
        cityList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return cityList != null ? cityList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return cityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewholder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listitem_city_select_search, null);
            holder = new viewholder();
            holder.cityText = (TextView) convertView.findViewById(R.id.txt_city);
            convertView.setTag(holder);
        } else {
            holder = (viewholder) convertView.getTag();
        }
        holder.cityText.setText(cityList.get(position));
        return convertView;
    }

    public class viewholder {
        public TextView cityText;
    }
}
