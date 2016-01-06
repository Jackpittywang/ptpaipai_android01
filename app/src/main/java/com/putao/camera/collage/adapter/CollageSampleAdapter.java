package com.putao.camera.collage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.putao.camera.R;
import com.putao.camera.collage.mode.CollageSampleItem;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.collage.view.RoundCornerImageView;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;

import java.util.ArrayList;

public class CollageSampleAdapter extends BaseAdapter {
    private ArrayList<CollageSampleItem> mDataList;
    private Context mContext;

    public CollageSampleAdapter(Context context,
                                ArrayList<CollageSampleItem> dataList) {
        mContext = context;
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    @Override
    public CollageSampleItem getItem(int position) {
        return mDataList != null ? mDataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.collage_sample_item, null);
            RoundCornerImageView imageView = (RoundCornerImageView) convertView
                    .findViewById(R.id.collage_imageView);
            holder = new ViewHolder();
            holder.collage_imageView = imageView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CollageSampleItem itemInfo = getItem(position);
        String path = CollageHelper.getCollageFilePath() + itemInfo.sample_image;
        Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(path);
        holder.collage_imageView.setImageBitmap(autoSizeBitmap(bitmap));

//        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
//       // holder.collage_imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//        holder.collage_imageView.setTag(path);
//        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(path)).toString(),   holder.collage_imageView, options);

        return convertView;
    }

    private Bitmap autoSizeBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Matrix matrix = new Matrix();
        int new_width = DisplayHelper.getScreenWidth() / 2 - 20;
        float scale = (float) new_width / bitmap.getWidth();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public class ViewHolder {
        public RoundCornerImageView collage_imageView;
    }
}
