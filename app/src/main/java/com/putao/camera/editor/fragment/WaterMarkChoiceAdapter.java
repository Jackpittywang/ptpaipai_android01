package com.putao.camera.editor.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.StickerUnZipInfo;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.FileUtils;

import java.io.File;

public class WaterMarkChoiceAdapter extends BaseAdapter {
    private Context context;
    private boolean bMultiSelectState = false;
    //    private int[] mWaterMarkIconArray;
    //    private ArrayList<WaterMarkChoiceItem> mWaterMarkChoiceItemArray;
//    WaterMarkCategoryInfo mWaterMarkCategoryInfo;
    StickerCategoryInfo mStickerCategoryInfo;

    //    public WaterMarkChoiceAdapter(Context mContext, int[] aWaterMarkIconArray)
    //    {
    //        this.context = mContext;
    //        setData(aWaterMarkIconArray);
    //    }

   /* public WaterMarkChoiceAdapter(Context mContext, WaterMarkCategoryInfo aWaterMarkCategoryInfo) {
        this.context = mContext;
        setData(aWaterMarkCategoryInfo);
    }*/

    public WaterMarkChoiceAdapter(Context mContext, StickerCategoryInfo mStickerCategoryInfo) {
        this.context = mContext;
        setData(mStickerCategoryInfo);
    }

    /*public void setData(WaterMarkCategoryInfo aWaterMarkCategoryInfo) {
        this.mWaterMarkCategoryInfo = aWaterMarkCategoryInfo;
    }*/
    public void setData(StickerCategoryInfo mStickerCategoryInfo) {
        this.mStickerCategoryInfo = mStickerCategoryInfo;
    }



    @Override
    public int getCount() {
        return mStickerCategoryInfo == null ? 0 : mStickerCategoryInfo.elements.size();
//        return mStickerCategoryInfo == null ? 0 : mStickerCategoryInfo
    }

    @Override
    public StickerUnZipInfo getItem(int position) {
        return mStickerCategoryInfo == null ? null : mStickerCategoryInfo.elements.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoItemHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_water_mark_choice_item, null);
            holder = new PhotoItemHolder();
            holder.water_marker_item_icon_im = (ImageView) convertView.findViewById(R.id.water_marker_item_icon_im);
            //holder.editmark_icon = (ImageView) convertView.findViewById(R.id.editmark_icon);
            convertView.setTag(holder);
        } else {
            holder = (PhotoItemHolder) convertView.getTag();
        }
        StickerUnZipInfo iconInfo = getItem(position);
//        String path = WaterMarkHelper.getWaterMarkFilePath() + iconInfo.sample_image;
        String path = FileUtils.getPutaoCameraPath()  + File.separator+iconInfo.zipName +File.separator+ iconInfo.imgName;


        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        holder.water_marker_item_icon_im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.water_marker_item_icon_im.setTag(path);
        ImageLoader.getInstance().displayImage("file://" + path, holder.water_marker_item_icon_im, options);



      /*  DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

        holder.water_marker_item_icon_im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.water_marker_item_icon_im.setTag(path);
        ImageLoader.getInstance().displayImage(Uri.fromFile(new File(path)).toString(), holder.water_marker_item_icon_im, options);*/

//        holder.editmark_icon.setVisibility(View.GONE);
//        if (iconInfo.type != null) {
//            if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)
//                    || iconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)
//                    || iconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE))
//                holder.editmark_icon.setVisibility(View.VISIBLE);
//        }
        return convertView;
    }

    class PhotoItemHolder {
        //        public TextView date_tv;
        public ImageView water_marker_item_icon_im;
//        public ImageView editmark_icon;
    }
}
