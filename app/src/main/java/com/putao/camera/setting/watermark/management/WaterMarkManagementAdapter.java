
package com.putao.camera.setting.watermark.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanglun on 15/4/5.
 */
public class WaterMarkManagementAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<StickerListInfo.PackageInfo> mDatas;
    private UpdateCallback updateCallback;

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public void setDatas(ArrayList<StickerListInfo.PackageInfo> datas) {
        mDatas = datas;
    }

    public WaterMarkManagementAdapter(Context context) {
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
    public StickerListInfo.PackageInfo getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_management_water_mark_grid_item, null);
            holder = new ViewHolder();
            holder.water_mark_photo_download_iv = (ImageView) convertView.findViewById(R.id.water_mark_photo_download_iv);
            holder.water_mark_category_name_tv = (TextView) convertView.findViewById(R.id.water_mark_category_name_tv);
            holder.water_mark_category_download_btn = (ImageView) convertView.findViewById(R.id.water_mark_category_download_btn);
            holder.download_status_pb = (ProgressBar) convertView.findViewById(R.id.download_status_pb);
            holder.water_mark_category_count_tv = (TextView) convertView.findViewById(R.id.water_mark_category_count_tv);
            holder.water_mark_category_size_tv = (TextView) convertView.findViewById(R.id.water_mark_category_size_tv);
            holder.water_mark_photo_ok_iv = (ImageView) convertView.findViewById(R.id.water_mark_photo_ok_iv);
            holder.water_mark_photo_new_iv = (ImageView) convertView.findViewById(R.id.water_mark_photo_new_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final StickerListInfo.PackageInfo info = getItem(position);
        holder.water_mark_category_name_tv.setText(info.name);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));
        List<WaterMarkCategoryInfo> list = null;
        try {
            list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            holder.water_mark_photo_ok_iv.setVisibility(View.VISIBLE);
//            holder.water_mark_category_download_btn.setBackgroundResource(R.drawable.gray_btn_bg);
//            holder.water_mark_category_download_btn.setText("删除");
            holder.water_mark_category_download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != updateCallback) {
                        updateCallback.delete(info, position);
                    }
                }
            });
            holder.water_mark_photo_new_iv.setVisibility(View.INVISIBLE);
        } else {
//            holder.water_mark_category_download_btn.setBackgroundResource(R.drawable.red_btn_bg);
            holder.water_mark_photo_ok_iv.setVisibility(View.INVISIBLE);
//            holder.water_mark_category_download_btn.setText("下载");
            holder.water_mark_category_download_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != updateCallback) {
                        updateCallback.startProgress(info, position);
                    }
                }
            });


            if (info.is_new == 1) {
                holder.water_mark_photo_new_iv.setVisibility(View.VISIBLE);
            } else {
                holder.water_mark_photo_new_iv.setVisibility(View.INVISIBLE);
            }
        }
        holder.download_status_pb.setVisibility(View.INVISIBLE);
        holder.water_mark_category_count_tv.setText(info.num + "枚");

        holder.water_mark_category_size_tv.setText(info.size);
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
//        holder.water_mark_photo_download_iv.setScaleType(ImageView.ScaleType.CENTER);
        ImageLoader.getInstance().displayImage(info.cover_pic, holder.water_mark_photo_download_iv, options);
        return convertView;
    }

    class ViewHolder {
        public TextView water_mark_category_name_tv;
        public TextView water_mark_category_count_tv;
        public TextView water_mark_category_size_tv;
        public ImageView water_mark_photo_download_iv,water_mark_category_download_btn;
//        public Button water_mark_category_download_btn;
        public ProgressBar download_status_pb;
        public ImageView water_mark_photo_ok_iv;
        public ImageView water_mark_photo_new_iv;
    }
}
