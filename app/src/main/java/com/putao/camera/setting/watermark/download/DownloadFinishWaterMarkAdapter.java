
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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.setting.watermark.management.WaterMarkCategoryDetailActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.WaterMarkHelper;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/5.
 */
public class DownloadFinishWaterMarkAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<WaterMarkCategoryInfo> mDatas;

    public void setDatas(ArrayList<WaterMarkCategoryInfo> datas) {
        mDatas = datas;
    }

    public ArrayList<WaterMarkCategoryInfo> getDatas() {
        return mDatas;
    }

    public DownloadFinishWaterMarkAdapter(Context context) {
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
    public WaterMarkCategoryInfo getItem(int position) {
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
                MainApplication.getDBServer().deleteWaterMarkCategoryInfo(mDatas.get(position));
                mDatas.remove(position);
                notifyDataSetChanged();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_WATERMARK_MANAGEMENT_ACTIVITY, bundle));
            }
        });
        final WaterMarkCategoryInfo info = getItem(position);
        holder.water_mark_category_name_tv.setText(info.category);
        String path = WaterMarkHelper.getWaterMarkFilePath() + info.watermark_cover;
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(20)).build();
        holder.water_mark_photo_download_iv.setScaleType(ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage("file://" + path, holder.water_mark_photo_download_iv, options);
        holder.water_mark_photo_download_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("wid", info.id);
                bundle.putInt("position", position);
                ActivityHelper.startActivity((Activity) mContext, WaterMarkCategoryDetailActivity.class, bundle);

            }
        });


        holder.water_mark_category_count_tv.setText(info.totals + "枚");
        holder.water_mark_category_size_tv.setText(info.attachment_size + "k");
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
