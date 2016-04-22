package com.putao.camera.setting.watermark.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.util.BitmapHelper;
import com.sunnybear.library.view.image.ImageDraweeView;
import com.sunnybear.library.view.recycler.BasicViewHolder;
import com.sunnybear.library.view.recycler.adapter.BasicAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/4/11.
 */

public class DynamicPicAdapter extends BasicAdapter<DynamicIconInfo, DynamicPicAdapter.DynamicPicHolder> {


    public DynamicPicAdapter(Context context, ArrayList<DynamicIconInfo> pics) {
        super(context, pics);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.activity_dynamic_pic_item;
    }

    @Override
    public DynamicPicHolder getViewHolder(View itemView, int viewType) {
        return new DynamicPicHolder(itemView);
    }

    @Override
    public void onBindItem(DynamicPicHolder holder, final DynamicIconInfo dynamicIconInfo, final int position) {


        String path = dynamicIconInfo.cover_pic;
        Map<String, String> map = new HashMap<String, String>();
        List<DynamicIconInfo> list = null;
        map.put("cover_pic", path);
        try {
            list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != list && list.size() > 0) {
            holder.photo_download_iv.setVisibility(View.GONE);
            if(dynamicIconInfo.getSelect()){
                holder.photo_download_iv.setVisibility(View.VISIBLE);
                holder.photo_download_iv.setImageResource(R.drawable.btn_22_03);
            }else {
                holder.photo_download_iv.setVisibility(View.GONE);
            }

        } else {
            holder.photo_download_iv.setVisibility(View.VISIBLE);
            holder.photo_download_iv.setImageResource(R.drawable.btn_22_01);
        }

        if (dynamicIconInfo.isShowProgress()) {
            holder.photo_download_iv.setVisibility(View.GONE);
            holder.pb_download.setVisibility(View.VISIBLE);
        } else {
            holder.pb_download.setVisibility(View.GONE);

        }



        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.iv_user_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ImageLoader.getInstance().

                displayImage(path, holder.iv_user_icon, options);
    }

    public static class DynamicPicHolder extends BasicViewHolder {

        @Bind(R.id.iv_user_icon)
        ImageDraweeView iv_user_icon;
        @Bind(R.id.photo_download_iv)
        ImageView photo_download_iv;
        @Bind(R.id.pb_download)
        ProgressBar pb_download;


        public DynamicPicHolder(View itemView) {
            super(itemView);
        }
    }
}