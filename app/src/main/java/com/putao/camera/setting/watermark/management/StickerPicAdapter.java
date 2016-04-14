package com.putao.camera.setting.watermark.management;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.bean.StickerUnZipInfo;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.FileUtils;
import com.sunnybear.library.view.image.ImageDraweeView;
import com.sunnybear.library.view.recycler.BasicViewHolder;
import com.sunnybear.library.view.recycler.adapter.BasicAdapter;

import java.io.File;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Administrator on 2016/4/11.
 */

public class StickerPicAdapter extends BasicAdapter<StickerUnZipInfo, StickerPicAdapter.StickerPicHolder> {


    public StickerPicAdapter(Context context, List<StickerUnZipInfo> pics) {
        super(context, pics);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getLayoutId(int viewType) {
        return R.layout.activity_sticker_pic_item;
    }

    @Override
    public StickerPicHolder getViewHolder(View itemView, int viewType) {
        return new StickerPicHolder(itemView);
    }

    @Override
    public void onBindItem(StickerPicHolder holder, StickerUnZipInfo stickerUnZipInfo, int position) {
//        holder.iv_user_icon.setImageURL(stickerUnZipInfo.imgName);
        String path = FileUtils.getPutaoCameraPath() + File.separator + stickerUnZipInfo.zipName + File.separator + stickerUnZipInfo.imgName;
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.iv_user_icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.iv_user_icon.setTag(path);
        ImageLoader.getInstance().displayImage("file://" + path, holder.iv_user_icon, options);
//        holder.iv_user_icon.setImageURL("file://" + path);
    }

    static class StickerPicHolder extends BasicViewHolder {

        @Bind(R.id.iv_user_icon)
        ImageDraweeView iv_user_icon;

        public StickerPicHolder(View itemView) {
            super(itemView);
        }
    }
}