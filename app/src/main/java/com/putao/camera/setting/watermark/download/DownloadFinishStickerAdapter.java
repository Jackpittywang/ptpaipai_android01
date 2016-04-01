
package com.putao.camera.setting.watermark.download;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.management.WaterMarkCategoryDetailActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;

public class DownloadFinishStickerAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<StickerCategoryInfo> mDatas;

    public void setDatas(ArrayList<StickerCategoryInfo> datas) {
        mDatas = datas;
    }

    public ArrayList<StickerCategoryInfo> getDatas() {
        return mDatas;
    }

    public DownloadFinishStickerAdapter(Context context) {
        this.mContext = context;
    }

    private ViewHolder holder;

    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public StickerCategoryInfo getItem(int position) {
        if (mDatas == null) {
            return null;
        }
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_management_water_mark_downlaod_grid_item, null);
            holder = new ViewHolder();
            holder.water_mark_photo_download_iv = (ImageView) convertView.findViewById(R.id.water_mark_photo_download_iv);
            holder.water_mark_category_name_tv = (TextView) convertView.findViewById(R.id.water_mark_category_name_tv);
            holder.delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);
            holder.water_mark_category_count_tv = (TextView) convertView.findViewById(R.id.water_mark_category_count_tv);
            holder.water_mark_category_size_tv = (TextView) convertView.findViewById(R.id.water_mark_category_size_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.delete_iv.setVisibility(View.VISIBLE);
        holder.delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainApplication.getDBServer().deleteStickerCategoryInfo(mDatas.get(position));
                mDatas.remove(position);
                notifyDataSetChanged();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY, bundle));
            }
        });
        final StickerCategoryInfo info = getItem(position);
        holder.water_mark_category_name_tv.setText(info.name);
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.water_mark_photo_download_iv.setScaleType(ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(info.cover_pic, holder.water_mark_photo_download_iv, options);
        holder.water_mark_photo_download_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  Bundle bundle = new Bundle();
        bundle.putString("id", info.id);
        bundle.putInt("position", position);
        ActivityHelper.startActivity((Activity) mContext, WaterMarkCategoryDetailActivity.class, bundle);

            }
        });


        holder.water_mark_category_count_tv.setText(info.num + "枚");
        holder.water_mark_category_size_tv.setText(info.size);
        return convertView;
    }

    class ViewHolder {
        public ImageView delete_iv;
        public TextView water_mark_category_name_tv;
        public ImageView water_mark_photo_download_iv;
        public TextView water_mark_category_count_tv;
        public TextView water_mark_category_size_tv;
    }
}
