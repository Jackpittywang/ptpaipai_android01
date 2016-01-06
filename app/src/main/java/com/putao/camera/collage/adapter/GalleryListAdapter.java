package com.putao.camera.collage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.collage.mode.GalleryEntity;
import com.putao.camera.util.BitmapHelper;

import java.util.ArrayList;

/**
 * Created by jidongdong on 15/3/13.
 */
public class GalleryListAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<GalleryEntity> mDatasource;

    public GalleryListAdapter(Context context, ArrayList<GalleryEntity> datasource) {
        mContext = context;
        mDatasource = datasource;
    }

    @Override
    public int getCount() {
        return mDatasource != null ? mDatasource.size() : 0;
    }

    @Override
    public GalleryEntity getItem(int position) {
        return mDatasource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_list_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.gallery_image);
            holder.textView = (TextView) convertView.findViewById(R.id.gallery_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        GalleryEntity item = mDatasource.get(position);
//        try {
//            Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(item.getImage_path(), 200, 200);
//            int min_size = Math.min(bitmap.getWidth(), bitmap.getHeight());
//            if (min_size < 200) {
//                Matrix matrix = new Matrix();
//                float ratio = 200.0f / min_size;
//                matrix.postScale(ratio, ratio);
//                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
//            }
//            holder.imageView.setImageBitmap(bitmap);
//
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        ImageLoader.getInstance().displayImage("file://" + item.getImage_path(), holder.imageView, options);

        String name = item.getBucket_name();
        if (name.length() > 7) {
            name = name.substring(0, 7) + "...";
        }
        holder.textView.setText(name + "(" + item.getCount() + ")");
        return convertView;
    }

    public class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
