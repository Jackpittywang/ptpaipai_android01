
package com.putao.camera.setting.watermark.download;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.management.CollageManagementActivity;
import com.putao.camera.setting.watermark.management.WaterMarkCategoryManagementActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yanglun on 15/4/10.
 */
public class DownloadFinishMaterialCenterActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, right_btn;
    private TextView title_tv;
    private RelativeLayout water_mark_management_rl;
    private RelativeLayout collage_management_rl;
    private ImageView water_mark_icon_iv, collage_icon_iv;
    private TextView water_mark_name_tv, collage_name_tv;
    private TextView water_mark_count_tv, collage_count_tv;
    private ImageView water_mark_new_icon_iv, collage_new_icon_iv;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_download_finish_material_center;
    }

    @Override
    public void doInitSubViews(View view) {
        back_btn = (Button) this.findViewById(R.id.back_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        right_btn = (Button) this.findViewById(R.id.right_btn);
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        water_mark_name_tv = (TextView) this.findViewById(R.id.water_mark_name_tv);
        collage_name_tv = (TextView) this.findViewById(R.id.collage_name_tv);
        water_mark_count_tv = (TextView) this.findViewById(R.id.water_mark_count_tv);
        collage_count_tv = (TextView) this.findViewById(R.id.collage_count_tv);
        water_mark_icon_iv = (ImageView) this.findViewById(R.id.water_mark_icon_iv);
        collage_icon_iv = (ImageView) this.findViewById(R.id.collage_icon_iv);
        right_btn.setVisibility(View.INVISIBLE);
        water_mark_management_rl = (RelativeLayout) this.findViewById(R.id.water_mark_management_rl);
        collage_management_rl = (RelativeLayout) this.findViewById(R.id.collage_management_rl);
        water_mark_new_icon_iv = (ImageView) this.findViewById(R.id.water_mark_new_icon_iv);
        collage_new_icon_iv = (ImageView) this.findViewById(R.id.collage_new_icon_iv);
        addOnClickListener(back_btn, water_mark_management_rl, collage_management_rl);
        queryMatericalCenterList();
    }

    @Override
    public void doInitData() {
//        right_btn.setText("素材管理");
        title_tv.setText("素材库");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.water_mark_management_rl:
                ActivityHelper.startActivity(this, WaterMarkCategoryManagementActivity.class);
                break;
            case R.id.collage_management_rl:
                ActivityHelper.startActivity(this, CollageManagementActivity.class);
                break;
            /*case R.id.puzzle_management_rl:
               ActivityHelper.startActivity(this, CollageManagementActivity.class);
                break;*/
        }
    }

    public void queryMatericalCenterList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                try {
                    Gson gson = new Gson();
                    MaterialInfoList mMaterialInfoList = (MaterialInfoList) gson.fromJson(json.toString(), MaterialInfoList.class);
                    MaterialInfo info0 = mMaterialInfoList.list.get(0);
                    MaterialInfo info1 = mMaterialInfoList.list.get(1);
                    water_mark_name_tv.setText(info0.category_name);
                    collage_name_tv.setText(info1.category_name);
//                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
//                            .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).displayer(new RoundedBitmapDisplayer(20) ).build();

//                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
//                            .showImageOnFail(BitmapHelper.getLoadingDrawable()).bitmapConfig(Bitmap.Config.RGB_565)
//                            .displayer(new RoundedBitmapDisplayer(20)).build();
                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                            .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();

                    water_mark_icon_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    collage_icon_iv.setScaleType(ImageView.ScaleType.CENTER_CROP);


                    ImageLoader.getInstance().displayImage(info0.sample_image, water_mark_icon_iv, options);
                    ImageLoader.getInstance().displayImage(info1.sample_image, collage_icon_iv, options);

                    water_mark_count_tv.setText(String.valueOf(info0.totals));
                    collage_count_tv.setText(String.valueOf(info1.totals));
                    if (info0.is_new == 0) {
                        water_mark_new_icon_iv.setVisibility(View.INVISIBLE);
                    } else {
                        water_mark_new_icon_iv.setVisibility(View.VISIBLE);
                    }
                    if (info1.is_new == 0) {
                        collage_new_icon_iv.setVisibility(View.INVISIBLE);
                    } else {
                        collage_new_icon_iv.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest("watermark/watermark/mtcenter", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    class MaterialInfoList {
        ArrayList<MaterialInfo> list;
    }

    class MaterialInfo {
        int is_new;
        String sample_image;
        int totals;
        String category_name;
        String category;
    }
}
