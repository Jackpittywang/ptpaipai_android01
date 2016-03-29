
package com.putao.camera.menu;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.WaterMarkRequestInfo;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.movie.MovieCameraActivity;
import com.putao.camera.setting.AboutActivity;
import com.putao.camera.setting.watermark.management.MatterCenterActivity;
import com.putao.camera.umengfb.UmengFeedbackActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.NetType;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.UmengUpdateHelper;
import com.putao.camera.util.WaterMarkHelper;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by yanglun on 15/3/2.
 */
public class MenuActivity extends BaseActivity implements View.OnClickListener {
    Button menu_home_material_btn, menu_home_stickers_btn, menu_home_camera_btn, menu_home_jigsaw_btn, menu_home_movie_btn, menu_home_setting_btn;
    TextView name_tv;
    //    private int remote_waterMark_version_code;
    //    private ServiceConnection mDownloadFileServiceCon;

    private boolean openCVLibraryLoaded = false;

    @Override
    public void doBefore() {
        if (SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, false)) {
            ActivityHelper.startActivity(this, ActivityCamera.class);
//            finish();
        }
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_menu;
    }
    @Override
    public void doInitSubViews(View view) {
        Loger.i("current time:" + System.currentTimeMillis());
        menu_home_setting_btn = (Button) findViewById(R.id.menu_home_setting_btn);
        menu_home_material_btn = (Button) findViewById(R.id.menu_home_material_btn);
        menu_home_stickers_btn = (Button) findViewById(R.id.menu_home_stickers_btn);
        menu_home_camera_btn = (Button) findViewById(R.id.menu_home_camera_btn);
        menu_home_jigsaw_btn = (Button) findViewById(R.id.menu_home_jigsaw_btn);
        menu_home_movie_btn = (Button) findViewById(R.id.menu_home_movie_btn);
        name_tv=queryViewById(R.id.name_tv);
        addOnClickListener(menu_home_material_btn, menu_home_stickers_btn, menu_home_camera_btn, menu_home_jigsaw_btn, menu_home_movie_btn,
                menu_home_setting_btn);
        // Umeng更新
        UmengUpdateHelper.getInstance().setShowTip(false).autoUpdate(MainApplication.getInstance());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        EventBus.getEventBus().unregister(this);
        MainApplication.stopLocationClient();
    }

    @Override
    public void doInitData() {
        initIconInfo();

//        name_tv.setText();
//        menu_home_camera_btn.setBackground();
        //        initWaterMarkInfo();

//        GifView kid_gif = (GifView) findViewById(R.id.kid_gif);
//        kid_gif.setMovieResource(R.raw.kid);
//        GifView yun_gif = (GifView) findViewById(R.id.yun_gif);
//        yun_gif.setMovieResource(R.raw.yun);
//        GifView wenzi_gif = (GifView) findViewById(R.id.wenzi_gif);
//        wenzi_gif.setMovieResource(R.raw.wenzi);

//        GifView longzhou_gif = (GifView) findViewById(R.id.longzhou_gif);
//        longzhou_gif.setMovieResource(R.raw.longzhou);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_home_material_btn://素材中心--原最新素材
//                ActivityHelper.startActivity(this, MaterialCenterActivity.class);

                ActivityHelper.startActivity(this, MatterCenterActivity.class);

                break;
            case R.id.menu_home_stickers_btn://意见反馈--童趣美化
//                ActivityHelper.startActivity(this, AlbumPhotoSelectActivity.class);
                ActivityHelper.startActivity(this, UmengFeedbackActivity.class);
                break;
            case R.id.menu_home_camera_btn://葡萄纬度官网
                Uri uri = Uri.parse(PuTaoConstants.ORG_WEBSITE_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.menu_home_jigsaw_btn://关于我们--原萌萌拼图
               // ActivityHelper.startActivity(this, CollageSampleSelectActivity.class);
                ActivityHelper.startActivity(this, AboutActivity.class);
                break;
            case R.id.menu_home_movie_btn:
                ActivityHelper.startActivity(this, MovieCameraActivity.class);
                break;
            case R.id.menu_home_setting_btn://拍照--原设置界面
//                ActivityHelper.startActivity(this, SettingActivity.class);
                ActivityHelper.startActivity(this, ActivityCamera.class);
//                ActivityHelper.startActivity(this, MovieCameraActivity.class);
                break;
            //            case R.id.water_mark_btn:
            //                ActivityHelper.startActivity(this, AlbumPhotoSelectActivity.class);
            //                break;
            //            case R.id.take_photo_btn:
            //                ActivityHelper.startActivity(this, ActivityCamera.class);
            //                break;
            //            case R.id.collage_btn:
            //                ActivityHelper.startActivity(this, CollageSampleSelectActivity.class);
            //                break;
            //            //            case R.id.show_time_btn:
            //            //                ActivityHelper.startActivity(this, TestActivity.class);
            //            //                break;
            //            case R.id.menu_setting_btn:
            //                ActivityHelper.startActivity(this, SettingActivity.class);
            //                //                Bundle bundle = new Bundle();
            //                //                bundle.putString("photo_data", "/storage/emulated/0/DCIM/Camera/20150316_151027.jpg");
            //                //                ActivityHelper.startActivity(this, MovieMakeActivity.class, bundle);
            //                break;
            //            case R.id.movie_btn:
            //                ActivityHelper.startActivity(this, MovieCameraActivity.class);
            //                break;
            //            case R.id.menu_test_btn:
            //                ActivityHelper.startActivity(this, MaterialCenterActivity.class);
            //                break;
        }
    }

    public void initIconInfo(){
        CacheRequest.ICacheRequestCallBack mIconInfoCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                String iconInfo=json.toString();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest("/core/config", map,
                mIconInfoCallback);
        mCacheRequest.startGetRequest();
    }



    public void initWaterMarkInfo() {
        WaterMarkHelper.setWaterMarkConfigInfoFromSahrePreferences(this);
        if (NetType.getNetworkType(this) != -1) {
            queryWaterMarkUpdateRequest();
        }
    }

    public void queryWaterMarkUpdateRequest() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                int local_waterMark_version_code = SharedPreferencesHelper
                        .readIntValue(mContext, PuTaoConstants.PREFERENC_WATERMARK_SRC_VERSION_CODE);
                Gson gson = new Gson();
                final WaterMarkRequestInfo aWaterMarkRequestInfo = (WaterMarkRequestInfo) gson.fromJson(json.toString(), WaterMarkRequestInfo.class);
                WaterMarkHelper.hasNewWaterMarkUpdateLink = aWaterMarkRequestInfo.link;
                int remote_waterMark_version_code = Integer.valueOf(aWaterMarkRequestInfo.version).intValue();
                WaterMarkHelper.mark_resource_version_code = remote_waterMark_version_code;
                if (remote_waterMark_version_code > local_waterMark_version_code) {
                    boolean wifi_dl_setting = SharedPreferencesHelper.readBooleanValue(mContext, PuTaoConstants.PREFERENC_WIFI_AUTO_DOWNLOAD_SETTING,
                            true);
                    boolean mmcc_dl_setting = SharedPreferencesHelper.readBooleanValue(mContext, PuTaoConstants.PREFERENC_MMCC_AUTO_DOWNLOAD_SETTING,
                            false);
                    if (wifi_dl_setting || mmcc_dl_setting) {
                        String link = aWaterMarkRequestInfo.link;
                        String path = WaterMarkHelper.getWaterMarkUnzipFilePath();
                        //                        startDownloadService(link, path);
                    } else {
                        WaterMarkHelper.bHasNewWaterMarkUpdate = true;
                    }
                }
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_UPDATE_PACKAGE_URL + "config_watermark.json", map,
                mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }



}
