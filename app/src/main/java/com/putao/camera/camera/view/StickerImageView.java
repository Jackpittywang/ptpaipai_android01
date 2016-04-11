package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.util.BitmapHelper;

/**
 * Created by yanguoqiang on 16/2/18.
 */
public class StickerImageView extends RelativeLayout {

    // 高度宽度写死了 80dp
    private ImageView img_ar_icon;
    private ImageView img_ar_checked;

    public StickerImageView(Context context) {
        super(context);

        initComponent(context);
    }

    public void setData(String imagePath) {
//        img_ar_icon.setImageBitmap(BitmapHelper.getBitmapFromPath(imagePath));

        DisplayImageOptions options = new DisplayImageOptions.Builder().
                showImageOnLoading(BitmapHelper.getLoadingDrawable()).showImageOnFail(BitmapHelper.getLoadingDrawable())
                .considerExifParams(true)
                .cacheInMemory(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        img_ar_icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage("file://" + imagePath, img_ar_icon, options);
    }
    public void setDataFromInternt(String imagePath) {
        img_ar_icon.setImageURI(Uri.parse(imagePath));
    }

    private void initComponent(Context context) {
        RelativeLayout.inflate(context, R.layout.layout_ar_sticker, this);

        img_ar_icon = (ImageView) this.findViewById(R.id.img_ar_icon);
        img_ar_checked = (ImageView) this.findViewById(R.id.img_ar_checked);
    }

    public void setChecked(boolean flag) {
        if (flag) img_ar_checked.setVisibility(View.VISIBLE);
        else img_ar_checked.setVisibility(View.INVISIBLE);
    }




}
