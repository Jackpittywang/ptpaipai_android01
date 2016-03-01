package com.putao.camera.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.putao.camera.R;

/**
 * 计时适配器
 * Created by yanghx on 2016/1/20.
 */
public class TimerAdapter extends ArrayAdapter{

    private Context context;
    private Integer[] datas;
    private int layoutId;

    public TimerAdapter(Context context, int resource, Integer[] datas) {
        super(context, resource, datas);
        this.context = context;
        this.datas = datas;
        layoutId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(layoutId, null);
            holder.img_timer = (ImageView) convertView.findViewById(R.id.img_timer);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.img_timer.setBackgroundResource(datas[position]);
        return convertView;
    }


    class ViewHolder {

        ImageView img_timer;

        public ViewHolder() {
        }
    }

}
