package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.setting.watermark.management.UpdateCallback;
import com.putao.camera.util.BitmapHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yanguoqiang on 16/2/18.
 */
public class IntentARImageView extends RelativeLayout {

    // 高度宽度写死了 80dp
    private ImageView collage_photo_download_iv,collage_download_iv;
    private ImageView img_ar_checked;
    private  ProgressBar pb_download;
    private UpdateCallback updateCallback;
    private int position;

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public IntentARImageView(Context context) {
        super(context);

        initComponent(context);
    }



    public void setData(String imagePath) {
        collage_download_iv.setImageBitmap(BitmapHelper.getBitmapFromPath(imagePath));
    }
    public void setPosition(int i) {
        this.position=i;
    }
    public int getPosition() {
        return  position;
    }


    public void setDataFromInternt(String  imagePath) {


        Map<String, String> map = new HashMap<String, String>();
        map.put("cover_pic", imagePath);
        List<DynamicIconInfo> list = null;
        try {
            list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null!=list &&list.size() > 0) {
            collage_photo_download_iv.setVisibility(GONE);
//            collage_download_iv.setOnClickListener(null);
        } else {
            collage_photo_download_iv.setImageResource(R.drawable.btn_22_01);
           /* collage_download_iv.setOnClickListener(new View.OnClickListener() {
                private boolean isClick = false;
                @Override
                public void onClick(View v) {
                    if (null != updateCallback) {
                        if (isClick == false) {
                            isClick = true;
//                            updateCallback.startProgress(imagePath, 0);
                        }
                    }
                }
            });*/
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        collage_download_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImageLoader.getInstance().displayImage(imagePath, collage_download_iv, options);

    }

    private void initComponent(Context context) {
        RelativeLayout.inflate(context, R.layout.layout_intent_ar_sticker, this);

        collage_photo_download_iv= (ImageView) this.findViewById(R.id.collage_photo_download_iv);
        collage_download_iv = (ImageView) this.findViewById(R.id.collage_download_iv);
        pb_download = (ProgressBar) this.findViewById(R.id.pb_download);
        img_ar_checked = (ImageView) this.findViewById(R.id.img_ar_checked);

    }



    public void setChecked(boolean flag) {
        if (flag) img_ar_checked.setVisibility(View.VISIBLE);
        else img_ar_checked.setVisibility(View.INVISIBLE);
    }




}
