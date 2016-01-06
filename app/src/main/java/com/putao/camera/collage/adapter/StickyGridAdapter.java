package com.putao.camera.collage.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.collage.mode.PhotoGridItem;
import com.putao.camera.collage.util.CollagePhotoUtil;
import com.putao.camera.collage.view.GridImageView;
import com.putao.camera.collage.view.GridImageView.OnMeasureListener;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.StringHelper;
import com.putao.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

public class StickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    private List<PhotoGridItem> list;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Point mPoint = new Point(0, 0);

    public StickyGridAdapter(Context context, List<PhotoGridItem> list,
                             GridView mGridView) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.mGridView = mGridView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            mViewHolder.mImageView = (GridImageView) convertView
                    .findViewById(R.id.grid_item);
            convertView.setTag(mViewHolder);

            mViewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String path = list.get(position).getPath();

        if (path.startsWith(CollagePhotoUtil.IS_CAMERA_ICON)) {
            mViewHolder.mImageView.setBackgroundColor(0xffeae7e2);
            mViewHolder.mImageView.setImageResource(R.drawable.album_camera_icon);
            int pading = DisplayHelper.dipTopx(16);
            mViewHolder.mImageView.setPadding(pading, pading, pading, pading);
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                    .considerExifParams(true).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
            mViewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mViewHolder.mImageView.setTag(path);
            mViewHolder.mImageView.setPadding(0, 0, 0, 0);
            ImageLoader.getInstance().displayImage("file://" + path, mViewHolder.mImageView, options);
        }
        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            mHeaderHolder.tv_day = (TextView) convertView
                    .findViewById(R.id.day_tv);
            mHeaderHolder.tv_week = (TextView) convertView.findViewById(R.id.week_tv);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        String date_str = list.get(position).getTime();
        if (!StringHelper.isEmpty(date_str)) {
            // int month = DateUtil.getMonth(DateUtil.getDate(date_str));
            // int day = DateUtil.getDay(DateUtil.getDate(date_str));
            String week = DateUtil.getWeekSting(DateUtil.getDate(date_str));
            String curdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

            if (date_str.startsWith(curdate)) {
                mHeaderHolder.tv_day.setText("今天");
                mHeaderHolder.tv_week.setText(" " + date_str + " (" + week + ")");
            } else {
                mHeaderHolder.tv_week.setText(" (" + week + ")");
                mHeaderHolder.tv_day.setText(date_str);
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        public GridImageView mImageView;
    }

    public static class HeaderViewHolder {
        public TextView tv_day;
        public TextView tv_week;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }

}
