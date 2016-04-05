
package com.putao.camera.setting.watermark.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanglun on 15/4/5.
 */
public class CollageManagementAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TemplateListInfo.PackageInfo> mDatas;
    private UpdateCallback updateCallback;

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public void setDatas(ArrayList<TemplateListInfo.PackageInfo> datas) {
        mDatas = datas;
    }

    public CollageManagementAdapter(Context context) {
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
    public TemplateListInfo.PackageInfo getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_management_collage_grid_item, null);
            holder = new ViewHolder();
            holder.collage_photo_download_iv= (ImageView) convertView.findViewById(R.id.collage_photo_download_iv);
            holder.collage_photo_new_iv= (ImageView) convertView.findViewById(R.id.collage_photo_new_iv);
            holder.collage_download_iv = (ImageView) convertView.findViewById(R.id.collage_download_iv);
//            holder.download_status_pb = (ProgressBar) convertView.findViewById(R.id.download_status_pb);
            holder.pb_download = (ProgressBar) convertView.findViewById(R.id.pb_download);
            holder.collage_photo_ok_iv = (ImageView) convertView.findViewById(R.id.collage_photo_ok_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TemplateListInfo.PackageInfo info = getItem(position);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));
        List<TemplateIconInfo> list = null;
        try {
            list = MainApplication.getDBServer().getTemplateIconInfoByWhere(map);
//            list = TemplateDBHelper.getInstance().queryList(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null!=list &&list.size() > 0) {
            holder.collage_photo_download_iv.setImageResource(R.drawable.btn_22_03);
//            holder.collage_photo_ok_iv.setVisibility(View.VISIBLE);
            holder.collage_download_iv.setOnClickListener(null);
//            holder.collage_photo_new_iv.setVisibility(View.INVISIBLE);
        } else {
            holder.collage_download_iv.setOnClickListener(new View.OnClickListener() {
                private boolean isClick = false;

                @Override
                public void onClick(View v) {
                    if (null != updateCallback) {
                        if (isClick == false) {
                            isClick = true;
                            updateCallback.startProgress(info, position);
                        }
                    }
                }
            });
        }
        if (info.is_new == 1) {
            holder.collage_photo_new_iv.setVisibility(View.VISIBLE);
        } else {
            holder.collage_photo_new_iv.setVisibility(View.INVISIBLE);
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.collage_download_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(info.cover_pic, holder.collage_download_iv, options);
        return convertView;
    }

    class ViewHolder {
        public ImageView collage_download_iv;
        public ImageView collage_photo_ok_iv,collage_photo_new_iv,collage_photo_download_iv;
//        public ProgressBar download_status_pb;
        public ProgressBar pb_download;
    }
}