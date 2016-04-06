
package com.putao.camera.menu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.SelectPopupWindow;
import com.putao.camera.bean.MenuIconInfo;
import com.putao.camera.bean.UserInfo;
import com.putao.camera.bean.WaterMarkRequestInfo;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UserApi;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.movie.MovieCameraActivity;
import com.putao.camera.setting.AboutActivity;
import com.putao.camera.setting.watermark.management.MatterCenterActivity;
import com.putao.camera.umengfb.UmengFeedbackActivity;
import com.putao.camera.user.CompleteActivity;
import com.putao.camera.user.LoginActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.NetType;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.UmengUpdateHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.controller.eventbus.Subcriber;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.view.image.FastBlur;
import com.sunnybear.library.view.image.ImageDraweeView;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by yanglun on 15/3/2.
 */
//public class MenuActivity extends BaseActivity implements View.OnClickListener {
public class MenuActivity<App extends BasicApplication> extends BasicFragmentActivity<App> implements View.OnClickListener {
//    Button menu_home_material_btn, menu_home_stickers_btn, menu_home_jigsaw_btn, menu_home_movie_btn, menu_home_setting_btn;
//    TextView name_tv, user_name_tv;
//    ImageView menu_home_camera_btn;
//    ImageDraweeView iv_header_icon;
//    LinearLayout login_ll;

   /* @Bind(R.id.menu_home_material_btn)
    Button menu_home_material_btn;
    @Bind(R.id.menu_home_stickers_btn)
    Button menu_home_stickers_btn;
    @Bind(R.id.menu_home_jigsaw_btn)
    Button menu_home_jigsaw_btn;
    @Bind(R.id.menu_home_movie_btn)
    Button menu_home_movie_btn;
    @Bind(R.id.menu_home_setting_btn)
    Button menu_home_setting_btn;*/

    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.user_name_tv)
    TextView user_name_tv;

    @Bind(R.id.menu_home_camera_btn)
    ImageView menu_home_camera_btn;


    @Bind(R.id.iv_header_icon)
    ImageDraweeView iv_header_icon;

    @Bind(R.id.fl_main)
    FrameLayout fl_main;

    private SelectPopupWindow mSelectPopupWindow;
    private MenuIconInfo aMenuIconInfo;
    private boolean openCVLibraryLoaded = false;

   /* @Override
    public void doBefore() {
        if (SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, false)) {
            ActivityHelper.startActivity(this, ActivityCamera.class);
//            finish();
        }
    }*/

   /* @Override
    public int doGetContentViewId() {
        return R.layout.activity_menu;
    }
    @Override
    public void doInitSubViews(View view) {
        iv_header_icon=queryViewById(R.id.iv_header_icon);
        user_name_tv=queryViewById(R.id.user_name_tv);

        Loger.i("current time:" + System.currentTimeMillis());
        menu_home_setting_btn = (Button) findViewById(R.id.menu_home_setting_btn);
        menu_home_material_btn = (Button) findViewById(R.id.menu_home_material_btn);
        menu_home_stickers_btn = (Button) findViewById(R.id.menu_home_stickers_btn);
        menu_home_camera_btn = queryViewById(R.id.menu_home_camera_btn);
        menu_home_jigsaw_btn = (Button) findViewById(R.id.menu_home_jigsaw_btn);
        menu_home_movie_btn = (Button) findViewById(R.id.menu_home_movie_btn);
        login_ll=queryViewById(R.id.login_ll);
        name_tv=queryViewById(R.id.name_tv);
        addOnClickListener(menu_home_material_btn, menu_home_stickers_btn, menu_home_camera_btn, menu_home_jigsaw_btn, menu_home_movie_btn,
                menu_home_setting_btn,login_ll);
        // Umeng更新
        UmengUpdateHelper.getInstance().setShowTip(false).autoUpdate(MainApplication.getInstance());
        initIconInfo();


    }*/

   /* @Override
    public void doInitData() {
//        initIconInfo();

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



    }*/




    @Override
    protected int getLayoutId() {
        /*if (SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, false)) {
            ActivityHelper.startActivity(this, ActivityCamera.class);
//            finish();
        }*/
        return R.layout.activity_menu;
    }



    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        /*addOnClickListener(menu_home_material_btn, menu_home_stickers_btn, menu_home_camera_btn, menu_home_jigsaw_btn, menu_home_movie_btn,
                menu_home_setting_btn,loading);*/
        // Umeng更新
        UmengUpdateHelper.getInstance().setShowTip(false).autoUpdate(MainApplication.getInstance());
        initIconInfo();

//        filePath = MainApplication.sdCardPath + File.separator + "head_icon.jpg";
        if (!AccountHelper.isLogin()) {
            setDefaultBlur();
        } else if ( AccountHelper.isLogin()) {
            getUserInfo();
        }
        mSelectPopupWindow = new SelectPopupWindow(mContext,"注销账户",R.color.blue,"修改用户信息",R.color.text_color_red) {
            @Override
            public void onFirstClick(View v) {
                AccountHelper.logout();
                setDefaultBlur();
                user_name_tv.setText("登录葡萄账户");
            }
            @Override
            public void onSecondClick(View v) {
                ActivityHelper.startActivity(MenuActivity.this, CompleteActivity.class);
                finish();
            }
        };



    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //        EventBus.getEventBus().unregister(this);
        MainApplication.stopLocationClient();
    }



    public static boolean ONREFRESH = true;
    private String mImg = "";
    public static final String ME_BLUR = "me_blur";

    /**
     * 获取用户信息
     */
    private void getUserInfo() {
        ONREFRESH = false;
        if (TextUtils.isEmpty(AccountHelper.getCurrentUid())) return;
        networkRequest(UserApi.getUserInfo(),
                new SimpleFastJsonCallback<UserInfo>(UserInfo.class, loading) {
                    @Override
                    public void onSuccess(String url, final UserInfo result) {
                        ONREFRESH = true;
                        //Message message = new Message();
                        AccountHelper.setUserInfo(result);
                        user_name_tv.setText(result.getNick_name());
                        if (mImg.equals(result.getHead_img())) {
                            if (TextUtils.isEmpty(result.getHead_img())) setDefaultBlur();
                            loading.dismiss();
                            return;
                        }
                        mImg = result.getHead_img();
                        iv_header_icon.setImageURL(setSmallImageUrl(result.getHead_img()), true);
                        if (TextUtils.isEmpty(mImg)) {
                            setDefaultBlur();
                            return;
                        }
                        //message.obj = result.getHead_img();
                        loading.dismiss();
                    }

                    @Override
                    public void onFailure(String url, int statusCode, String msg) {
                        super.onFailure(url, statusCode, msg);
                        ONREFRESH = true;
//                        ToastUtils.showToastLong(this, "登录失败请重新登录");
                    }
                });
    }




    private String setSmallImageUrl(String str) {
        return str.substring(0, str.length() - 4) + "_120x120" + str.substring(str.length() - 4);
    }

    private void setDefaultBlur() {
        Bitmap apply = FastBlur.doBlur(BitmapFactory.decodeResource(getResources(), R.drawable.img_head_signup), 50, false);
        EventBusHelper.post(apply, ME_BLUR);
    }

    @Subcriber(tag = ME_BLUR)
    private void setBlur(Bitmap bitmap) {
        iv_header_icon.setDefaultImage(bitmap);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @OnClick({R.id.menu_home_material_btn, R.id.menu_home_stickers_btn, R.id.menu_home_camera_btn, R.id.menu_home_jigsaw_btn, R.id.menu_home_movie_btn,
            R.id.menu_home_setting_btn, R.id.login_ll})
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
                Uri uri = Uri.parse(url);
//                Uri uri = Uri.parse(url);
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
            case R.id.login_ll:
                if (!AccountHelper.isLogin()) {
                    ActivityHelper.startActivity(this, LoginActivity.class);
                    finish();
                } else if ( AccountHelper.isLogin()) {
                    mSelectPopupWindow.show(fl_main);
                }




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

    private String url;

    public void initIconInfo() {
        CacheRequest.ICacheRequestCallBack mIconInfoCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
//                final MenuIconInfo aMenuIconInfo;
                try {
                    Thread.currentThread().getName();
                    Gson gson = new Gson();
                    aMenuIconInfo = (MenuIconInfo) gson.fromJson(json.toString(), MenuIconInfo.class);

                    name_tv.setText(aMenuIconInfo.data.app_name);

                    DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(BitmapHelper.getLoadingDrawable())
                            .showImageOnFail(BitmapHelper.getLoadingDrawable()).cacheInMemory(true).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
                    menu_home_camera_btn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ImageLoader.getInstance().displayImage(aMenuIconInfo.data.app_icon, menu_home_camera_btn, options);
//                    URL picUrl = new URL(aMenuIconInfo.data.app_icon);
//                    Bitmap pngBM = BitmapFactory.decodeStream(picUrl.openStream());
//                    menu_home_camera_btn.setImageBitmap(pngBM);
//                    url=aMenuIconInfo.data.android_link_url;
                    url = aMenuIconInfo.data.ios_link_url;

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
