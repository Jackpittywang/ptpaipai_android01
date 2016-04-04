
package com.putao.camera.setting.watermark.download;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.setting.watermark.management.CollageManagementActivity;
import com.putao.camera.setting.watermark.management.DynamicManagementActivity;
import com.putao.camera.setting.watermark.management.WaterMarkCategoryManagementActivity;
import com.putao.camera.util.ActivityHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DownloadFinishMaterialCenterActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, right_btn;
    private TextView title_tv, sticker_count_tv, dynamic_count_tv, template_count_tv;
    private RelativeLayout sticker_management_rl, dynamic_pasting_management_rl, template_management_rl;
    ArrayList<StickerCategoryInfo> stickerCategoryInfo_list;
    ArrayList<DynamicIconInfo> dynamicIconInfo_list;
    ArrayList<TemplateIconInfo> templateIconInfo_list;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_download_finish_material_center;
    }

    @Override
    public void doInitSubViews(View view) {
        sticker_count_tv = queryViewById(R.id.sticker_count_tv);
        dynamic_count_tv = queryViewById(R.id.dynamic_count_tv);
        template_count_tv = queryViewById(R.id.template_count_tv);
        back_btn = queryViewById(R.id.back_btn);
        title_tv = queryViewById(R.id.title_tv);
        right_btn = queryViewById(R.id.right_btn);
        sticker_management_rl = queryViewById(R.id.sticker_management_rl);
        dynamic_pasting_management_rl = queryViewById(R.id.dynamic_pasting_management_rl);
        template_management_rl = queryViewById(R.id.template_management_rl);

        addOnClickListener(back_btn, sticker_management_rl, dynamic_pasting_management_rl, template_management_rl);
//        queryMatericalCenterList();
    }

    @Override
    public void doInitData() {
        title_tv.setText("素材管理");
       /* sticker_count_tv.setText(StickerCategoryInfo_list.size()+"套(共"+"文件大小MB"+")");
        dynamic_count_tv.setText(DynamicIconInfo_list.size()+"套(共"+"文件大小MB"+")");
        template_count_tv.setText(TemplateIconInfo_list.size()+"套(共"+"文件大小MB"+")");*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.sticker_management_rl:
                ActivityHelper.startActivity(this, WaterMarkCategoryManagementActivity.class);
                break;
            case R.id.dynamic_pasting_management_rl:
                ActivityHelper.startActivity(this, DynamicManagementActivity.class);
                break;
            case R.id.template_management_rl:
                ActivityHelper.startActivity(this, CollageManagementActivity.class);
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Map<String, String> map = new HashMap<String, String>();
        map.put("type", "sticker");
        stickerCategoryInfo_list = (ArrayList<StickerCategoryInfo>) MainApplication.getDBServer().getStickerCategoryInfoByWhere(map);
        double size = 0;
        if (null != stickerCategoryInfo_list) {
            for (StickerCategoryInfo stickerCategoryInfo : stickerCategoryInfo_list) {
                size = Double.parseDouble(stickerCategoryInfo.zipSize) + size;
            }
            sticker_count_tv.setText(stickerCategoryInfo_list.size() + "套(共" + size + "MB)");
        }
        map.put("type", "dynamic");
        dynamicIconInfo_list = (ArrayList<DynamicIconInfo>) MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        map.put("type", "template");
        templateIconInfo_list = (ArrayList<TemplateIconInfo>) MainApplication.getDBServer().getTemplateIconInfoByWhere(map);
        dynamic_count_tv.setText(dynamicIconInfo_list.size() + "套(共" + "文件大小MB" + ")");
        template_count_tv.setText(templateIconInfo_list.size() + "套(共" + "文件大小MB" + ")");

    }



   /* public void queryMatericalCenterList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                try {
                    Gson gson = new Gson();
                    MaterialInfoList mMaterialInfoList = (MaterialInfoList) gson.fromJson(json.toString(), MaterialInfoList.class);
                    MaterialInfo info0 = mMaterialInfoList.list.get(0);
                    MaterialInfo info1 = mMaterialInfoList.list.get(1);

                  *//*  water_mark_name_tv.setText(info0.category_name);
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
                    }*//*


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
    }*/

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
