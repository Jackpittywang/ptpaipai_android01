package com.putao.camera.album.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
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
import com.putao.camera.util.StringHelper;
import com.putao.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

public class AlbumGridAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

    private List<PhotoGridItem> list;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Point mPoint = new Point(0, 0);
    private boolean isMultiSelect;
    private Context mContext;

    public AlbumGridAdapter(Context context, List<PhotoGridItem> list, GridView mGridView) {
        mContext = context;
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
            convertView = mInflater.inflate(R.layout.album_grid_item, parent, false);
            mViewHolder.mImageView = (GridImageView) convertView.findViewById(R.id.grid_item);
            mViewHolder.mSelectView = (ImageView) convertView.findViewById(R.id.selector_album);
            mViewHolder.selected_background = (View) convertView.findViewById(R.id.selected_background);
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

        if (isMultiSelect) {
            mViewHolder.mSelectView.setVisibility(View.VISIBLE);
            if (list.get(position).isSelected()) {
                mViewHolder.mSelectView.setSelected(true);
                mViewHolder.selected_background.setVisibility(View.VISIBLE);
            } else {
                mViewHolder.mSelectView.setSelected(false);
                mViewHolder.selected_background.setVisibility(View.INVISIBLE);
            }
        } else {
            mViewHolder.mSelectView.setVisibility(View.INVISIBLE);
        }

        String path = list.get(position).getPath();
        mViewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mViewHolder.mImageView.setTag(path);
        if (CollagePhotoUtil.IS_CAMERA_ICON.equals(path)) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageForEmptyUri(new BitmapDrawable(mContext.getResources(), getCameraIon()))
                    .cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(null, mViewHolder.mImageView, options);

            mViewHolder.mSelectView.setVisibility(View.INVISIBLE);
        } else {
            DisplayImageOptions options = new DisplayImageOptions.Builder().
                    showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                    .considerExifParams(true)
                    .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();

            ImageLoader.getInstance().displayImage("file://" + path, mViewHolder.mImageView, options);
        }

        return convertView;
    }

    private Bitmap getCameraIon() {
        int with = 220;
        int height = 220;
        Bitmap bitmap = Bitmap.createBitmap(with, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawColor(0xffeae7e2);
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.album_camera_icon);
        canvas.drawBitmap(bmp, (with - bmp.getWidth()) / 2, (height - bmp.getHeight()) / 2, paint);
        return bitmap;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            mHeaderHolder.tv_day = (TextView) convertView.findViewById(R.id.day_tv);
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
                mHeaderHolder.tv_day.setText(date_str);
                mHeaderHolder.tv_week.setText(" (" + week + ")");
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        public View selected_background;
        public GridImageView mImageView;
        public ImageView mSelectView;
    }

    public static class HeaderViewHolder {
        public TextView tv_day;
        public TextView tv_week;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }

    public void setMultiSelect(boolean isMultiSelect) {
        this.isMultiSelect = isMultiSelect;
        notifyDataSetChanged();
    }
}
