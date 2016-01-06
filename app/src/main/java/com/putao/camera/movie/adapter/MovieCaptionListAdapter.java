package com.putao.camera.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.movie.model.MovieCaptionConfig;

import java.util.ArrayList;

/**
 * Created by jidongdong on 15/3/17.
 */
public class MovieCaptionListAdapter extends BaseAdapter {

    private ArrayList<MovieCaptionConfig.MovieCaptionItem> datalist;
    private Context mContext;

    public MovieCaptionListAdapter(Context context, ArrayList<MovieCaptionConfig.MovieCaptionItem> data) {
        mContext = context;
        datalist = data;
    }

    @Override
    public int getCount() {
        return datalist == null ? 0 : datalist.size();
    }

    @Override
    public Object getItem(int position) {
        return datalist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.movie_caption_list_item, null);
            holder = new ViewHolder();
            holder.tv_caption_en = (TextView) convertView.findViewById(R.id.caption_en_tv);
            holder.tv_caption_zh = (TextView) convertView.findViewById(R.id.caption_zh_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MovieCaptionConfig.MovieCaptionItem item = datalist.get(position);
        holder.tv_caption_zh.setText(item.cn_line);
        holder.tv_caption_en.setText(item.en_line);
        return convertView;
    }

    private class ViewHolder {
        TextView tv_caption_en;
        TextView tv_caption_zh;
    }
}
