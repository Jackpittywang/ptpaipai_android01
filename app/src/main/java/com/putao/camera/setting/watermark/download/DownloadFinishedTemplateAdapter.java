
package com.putao.camera.setting.watermark.download;

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
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/5.
 */
public class DownloadFinishedTemplateAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<TemplateIconInfo> mDatas;

    public void setDatas(ArrayList<TemplateIconInfo> datas) {
        mDatas = datas;
    }

    public DownloadFinishedTemplateAdapter(Context context) {
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
    public TemplateIconInfo getItem(int position) {
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
            holder.collage_photo_new_iv= (ImageView) convertView.findViewById(R.id.collage_photo_new_iv);
            holder.collage_download_iv = (ImageView) convertView.findViewById(R.id.collage_download_iv);
            holder.download_status_pb = (ProgressBar) convertView.findViewById(R.id.download_status_pb);
            holder.collage_photo_ok_iv = (ImageView) convertView.findViewById(R.id.collage_photo_ok_iv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final TemplateIconInfo info = getItem(position);
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.collage_download_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(info.cover_pic, holder.collage_download_iv, options);
        return convertView;
    }

    class ViewHolder {
        public ImageView collage_download_iv;
        public ImageView collage_photo_ok_iv,collage_photo_new_iv;
        public ProgressBar download_status_pb;
    }
}