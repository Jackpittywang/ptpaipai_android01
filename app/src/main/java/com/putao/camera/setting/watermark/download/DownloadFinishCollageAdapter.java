package com.putao.camera.setting.watermark.download;

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
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.CollageDBHelper;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/5.
 */
public class DownloadFinishCollageAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<CollageItemInfo> mDatas;

    public void setDatas(ArrayList<CollageItemInfo> datas) {
        mDatas = datas;
    }

    public ArrayList<CollageItemInfo> getDatas() {
        return mDatas;
    }

    public DownloadFinishCollageAdapter(Context context) {
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
    public CollageItemInfo getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_management_collage_download_grid_item, null);
            holder = new ViewHolder();
            holder.water_mark_photo_download_iv = (ImageView) convertView.findViewById(R.id.water_mark_photo_download_iv);
            holder.water_mark_category_name_tv = (TextView) convertView.findViewById(R.id.water_mark_category_name_tv);
            holder.delete_iv = (ImageView) convertView.findViewById(R.id.delete_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.delete_iv.setVisibility(View.VISIBLE);
        holder.delete_iv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CollageDBHelper.getInstance().delete(mDatas.get(position));
                mDatas.remove(position);
                notifyDataSetChanged();
                Bundle bundle = new Bundle();
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.REFRESH_COLLAGE_MANAGEMENT_ACTIVITY, bundle));
            }
        });

        final CollageItemInfo info = getItem(position);
//        holder.water_mark_category_name_tv.setText(info.category);

        String path = CollageHelper.getCollageFilePath() + info.sample_image;
//        Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(path);
        // holder.water_mark_photo_download_iv.setImageBitmap(bitmap);

        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(20)).build();
        holder.water_mark_photo_download_iv.setScaleType(ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage("file://" + path, holder.water_mark_photo_download_iv, options);

        return convertView;
    }

    class ViewHolder {
        public ImageView delete_iv;
        TextView water_mark_category_name_tv;
        ImageView water_mark_photo_download_iv;
    }
}
