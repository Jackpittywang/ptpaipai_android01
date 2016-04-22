
package com.putao.camera.camera;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.RedDotReceiver;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.DynamicCategoryInfo;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.camera.PCameraFragment.TakePictureListener;
import com.putao.camera.camera.PCameraFragment.flashModeCode;
import com.putao.camera.camera.filter.CustomerFilter;
import com.putao.camera.camera.gpuimage.GPUImageFilter;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.view.ARImageView;
import com.putao.camera.camera.view.AlbumButton;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.editor.CitySelectActivity;
import com.putao.camera.editor.FestivalSelectActivity;
import com.putao.camera.editor.PhotoARShowActivity;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.editor.dialog.WaterTextDialog;
import com.putao.camera.editor.filtereffect.EffectCollection;
import com.putao.camera.editor.filtereffect.EffectImageTask;
import com.putao.camera.editor.filtereffect.GLEffectRender;
import com.putao.camera.editor.view.FilterEffectThumbnailView;
import com.putao.camera.editor.view.NormalWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView.WaterTextEventType;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gps.CityMap;
import com.putao.camera.gps.GpsUtil;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.setting.watermark.management.DynamicListInfo;
import com.putao.camera.setting.watermark.management.DynamicPicAdapter;
import com.putao.camera.setting.watermark.management.TemplateManagemenActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.ToasterHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.controller.eventbus.Subcriber;
import com.sunnybear.library.util.PreferenceUtils;
import com.sunnybear.library.view.recycler.BasicRecyclerView;
import com.sunnybear.library.view.recycler.listener.OnItemClickListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;

public class ActivityCamera extends BasicFragmentActivity implements OnClickListener {
    private String TAG = ActivityCamera.class.getSimpleName();
    private TextView tv_takephoto;
    private PCameraFragment std, ffc, current;
    private LinearLayout camera_top_rl, bar, layout_sticker, layout_filter, layout_filter_list, show_sticker_ll, show_filter_ll, show_material_ll, camera_scale_ll, camera_timer_ll, flash_light_ll, switch_camera_ll, back_home_ll, camera_set_ll;
    private Button take_photo_btn, btn_enhance_switch, btn_clear_ar, btn_clear_filter;
    private ImageButton btn_close_ar_list, btn_close_filter_list;
    //    private RedPointBaseButton show_material_ll;
    private ImageView Tips, back_home_iv, flash_light_iv, camera_scale_iv, camera_timer_iv, camera_set_iv, switch_camera_iv;
    private View fill_blank_top, fill_blank_bottom, v_red_dot;
    private AlbumButton album_btn;
    private FrameLayout container;
    private RelativeLayout camera_activy;
    private List<WaterMarkView> mMarkViewList;
    private int text_index = -1;
    private int mOrientation = 0;
    private int mOrientationCompensation = 0;
    private int bar_height_diff = 0;
    private TextWaterMarkView waterView;
    private OrientationEventListener mOrientationEvent;
    private boolean hasTwoCameras = (Camera.getNumberOfCameras() > 1);
    private List<View> mSceneWaterMarkViewList;
    private PictureRatio mPictureRatio = PictureRatio.RATIO_THREE_TO_FOUR;
    private boolean mShowSticker = false;
    private WaterMarkView last_mark_view;
    private DynamicPicAdapter mDynamicPicAdapter;
    private BasicRecyclerView rv_articlesdetail_applyusers;
    private List<DynamicIconInfo> nativeList = null;
    private int currentSelectDynamic = 0;


//    private TakeDelayTime mTakedelaytime = TakeDelayTime.DELAY_NONE;

    // 上一次选中的图标
    private ARImageView lastSelectArImageView = null;

    private static final String SCALETYPE_FULL = "full";
    private static final String SCALETYPE_ONE = "1;1";
    private static final String SCALETYPE_THREE = "3:4";
    private String scaleType = SCALETYPE_THREE;//拍照预览界面比例标志

    private static final String FLASHMODECODE_ON = "ON";
    private static final String FLASHMODECODE_OFF = "OFF";
    private static final String FLASHMODECODE_LIGHT = "LIGHT";
    private static final String FLASHMODECODE_AUTO = "AUTO";
    private String flashType = FLASHMODECODE_OFF;


    private AnimationImageView animation_view;
    private float screenDensity = 1.0f;
    private int saveFaceCenterX = 0;
    private int saveFaceCenterY = 0;
    private int saveMouthX = 0;
    private int saveMouthY = 0;
    private float saveFaceScale = 0;
    private float saveFaceAngle = 0;

    /**
     * 延时拍照倒计时
     */
    Thread finalTime_thread;
    private boolean camera_watermark_setting = false;
    /**
     * hdr
     */
    private HDRSTATE mHdrState = HDRSTATE.OFF;

    /**
     * 照片比例
     */
    public enum PictureRatio {
        RATIO_DEFAULT, RATIO_THREE_TO_FOUR, RATIO_ONE_TO_ONE
    }

    private static final String DELAY_NONE = "off";
    private static final String DELAY_THREE = "3s";
    private static final String DELAY_FIVE = "5s";
    private static final String DELAY_TEN = "10s";

    private String timeType = DELAY_NONE;//拍照预览界面比例标志

    /**
     * 延时拍摄
     */
    public enum TakeDelayTime {
        DELAY_NONE, DELAY_THREE, DELAY_FIVE, DELAY_TEN
    }

    /**
     * HDR
     */
    public enum HDRSTATE {
        ON,
        OFF,
        AUTO
    }

    private boolean isFristUse;
    private int lastVersionCode;
    private int curVersionCode;


    @Override
    protected int getLayoutId() {
        isFristUse = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);
        lastVersionCode = SharedPreferencesHelper.readIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, 0);
        curVersionCode = MainApplication.getVersionCode();
        return R.layout.activity_camera;
    }

    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        doInitSubViews();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDynamicPicAdapter = new DynamicPicAdapter(mContext, null);
        rv_articlesdetail_applyusers.setAdapter(mDynamicPicAdapter);
        rv_articlesdetail_applyusers.setLayoutManager(linearLayoutManager);
        rv_articlesdetail_applyusers.setOnItemClickListener(new OnItemClickListener<DynamicIconInfo>() {
            @Override
            public void onItemClick(DynamicIconInfo dynamicIconInfo, int position) {
                Map<String, String> map = new HashMap<String, String>();
                List<DynamicIconInfo> list = null;
                map.put("cover_pic", dynamicIconInfo.cover_pic);
                try {
                    list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != list && list.size() > 0) {
                    mDynamicPicAdapter.getItem(currentSelectDynamic).setSelect(false);
                    mDynamicPicAdapter.notifyItemChanged(currentSelectDynamic);

                    mDynamicPicAdapter.getItem(position).setSelect(true);
//                    dynamicIconInfo.setSelect(true);
                    mDynamicPicAdapter.notifyItemChanged(position);
                    ToasterHelper.showShort(ActivityCamera.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
                    if (current == null) return;
                    if (animation_view.isAnimationLoading()) {
                        ToasterHelper.showShort(ActivityCamera.this, "动画加载中请稍后", R.drawable.img_blur_bg);
                        return;
                    }
                    animation_view.clearData();
                    animation_view.setData(list.get(0).zipName, false);
                    std.setAnimationView(animation_view);
                    ffc.setAnimationView(animation_view);
                    currentSelectDynamic = position;
                } else {
                    dynamicIconInfo.setShowProgress(true);
                    mDynamicPicAdapter.notifyItemChanged(position);
                    String path = CollageHelper.getCollageUnzipFilePath();
                    startDownloadService(dynamicIconInfo.download_url, path, position - nativeList.size());
                }

            }
        });

        doInitData();
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "0");
        try {
            nativeList = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDynamicPicAdapter.addAll(nativeList);
    }

    private void startDownloadService(final String url, final String folderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(mContext, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        if (null == url || null == folderPath) return;
        mDynamicIconInfo.get(position).type = "dynamic";
        Intent bindIntent = new Intent(mContext, DownloadFileService.class);
        bindIntent.putExtra("item", mDynamicIconInfo.get(position));
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_DYNAMIC);
        mContext.startService(bindIntent);
    }

    public void doInitSubViews() {
//        fullScreen(true);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenDensity = metric.density;  // 屏幕密度（0.75 (120) / 1.0(160) / 1.5 (240)）

        EventBus.getEventBus().register(this);
        rv_articlesdetail_applyusers = (BasicRecyclerView) findViewById(R.id.rv_articlesdetail_applyusers);
        flash_light_ll = (LinearLayout) findViewById(R.id.flash_light_ll);
        camera_timer_ll = (LinearLayout) findViewById(R.id.camera_timer_ll);
        camera_scale_ll = (LinearLayout) findViewById(R.id.camera_scale_ll);
        switch_camera_ll = (LinearLayout) findViewById(R.id.switch_camera_ll);
        back_home_ll = (LinearLayout) findViewById(R.id.back_home_ll);
        camera_set_ll = (LinearLayout) findViewById(R.id.camera_set_ll);

        Tips = (ImageView) findViewById(R.id.Tips);
        tv_takephoto = (TextView) findViewById(R.id.tv_takephoto);
        show_material_ll = (LinearLayout) findViewById(R.id.show_material_ll);
        container = (FrameLayout) findViewById(R.id.container);
        camera_top_rl = (LinearLayout) findViewById(R.id.camera_top_rl);
        flash_light_iv = (ImageView) findViewById(R.id.flash_light_iv);
        camera_timer_iv = (ImageView) findViewById(R.id.camera_timer_iv);
        camera_scale_iv = (ImageView) findViewById(R.id.camera_scale_iv);
        switch_camera_iv = (ImageView) findViewById(R.id.switch_camera_iv);
        show_filter_ll = (LinearLayout) findViewById(R.id.show_filter_ll);
        layout_filter_list = (LinearLayout) findViewById(R.id.layout_filter_list);
        show_sticker_ll = (LinearLayout) findViewById(R.id.show_sticker_ll);
        take_photo_btn = (Button) findViewById(R.id.take_photo_btn);
        back_home_iv = (ImageView) findViewById(R.id.back_home_iv);
        album_btn = (AlbumButton) findViewById(R.id.album_btn);
        camera_set_iv = (ImageView) findViewById(R.id.camera_set_iv);
        bar = (LinearLayout) findViewById(R.id.bar);
        layout_filter = (LinearLayout) findViewById(R.id.layout_filter);
        layout_sticker = (LinearLayout) findViewById(R.id.layout_sticker);
        camera_activy = (RelativeLayout) findViewById(R.id.camera_activy);
//        layout_sticker_list = (LinearLayout) findViewById(R.id.layout_sticker_list);
        fill_blank_top = findViewById(R.id.fill_blank_top);
        fill_blank_bottom = findViewById(R.id.fill_blank_bottom);
        btn_enhance_switch = (Button) findViewById(R.id.btn_enhance_switch);
        btn_close_ar_list = (ImageButton) findViewById(R.id.btn_close_ar_list);
        btn_close_filter_list = (ImageButton) findViewById(R.id.btn_close_filter_list);
        btn_clear_ar = (Button) findViewById(R.id.btn_clear_ar);
        btn_clear_filter = (Button) findViewById(R.id.btn_clear_filter);
        v_red_dot = (View) findViewById(R.id.v_red_dot);

        animation_view = (AnimationImageView) findViewById(R.id.animation_view);
        // 必须设置图片的文件夹，否则显示不出图片
        animation_view.setImageFolder(FileUtils.getARStickersPath());
        animation_view.setScreenDensity(screenDensity);

        /*addOnClickListener(camera_scale_btn, camera_timer_btn, flash_light_btn, switch_camera_btn, back_home_btn, camera_set_btn, album_btn, show_sticker_ll, show_filter_ll, show_material_ll, take_photo_btn, btn_enhance_switch, btn_close_filter_list, btn_close_ar_list, btn_clear_filter, btn_clear_ar, tv_takephoto,
                Tips, camera_scale_ll, camera_timer_ll, flash_light_ll, switch_camera_ll, back_home_ll, camera_set_ll);*/
        if (hasTwoCameras) {
            std = PCameraFragment.newInstance(false);
            ffc = PCameraFragment.newInstance(true);
            std.setPhotoSaveListener(photoListener);
            ffc.setPhotoSaveListener(photoListener);

        } else {
            std = PCameraFragment.newInstance(false);
            switch_camera_iv.setVisibility(View.GONE);
            std.setPhotoSaveListener(photoListener);
        }


        switchCamera();
//        getFragmentManager().beginTransaction().replace(R.id.container, current).commit();

        if (isFristUse || lastVersionCode != curVersionCode) {
            SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, curVersionCode);
            isFristUse = false;
            SharedPreferencesHelper.saveBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, false);
            Tips.setVisibility(View.VISIBLE);
        }
        loadFilters();


    }

    TakePictureListener photoListener = new TakePictureListener() {
        @Override
        public void saved(final Bitmap photo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    album_btn.setImageBitmap(photo, true);
                    take_photo_btn.setEnabled(true);
                    last_mark_view = null;
                    // ClearWaterMark();
                }
            });
        }

        @Override
        public void focusChanged(boolean isfocusing) {
            if (take_photo_btn != null)
                take_photo_btn.setEnabled(!isfocusing);
        }
    };

    private void ClearWaterMark() {
        if (mMarkViewList != null && mMarkViewList.size() > 0) {
            for (int i = 0; i < mMarkViewList.size(); i++) {
                container.removeView(mMarkViewList.get(i));
            }
            mMarkViewList.clear();
        }
    }

    private void resetAlbumPhoto() {
        Bitmap albumBitmap = getLastTakePhoto();
        if (albumBitmap == null) {
            albumBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.photo_button_album)).getBitmap();
        }
        album_btn.setImageBitmap(albumBitmap, false);
    }

    private Bitmap getLastTakePhoto() {
        Bitmap bitmap = null;
        PhotoInfo photo = PhotoLoaderHelper.getInstance(this).getLastPhotoInfo();
        if (photo != null && !StringHelper.isEmpty(photo._ID)) {
            bitmap = PhotoLoaderHelper.getThumbnailLocalBitmap(photo._ID);
        }
        return bitmap;
    }


    public void doInitData() {
        camera_watermark_setting = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_CAMERA_WATER_MARK_SETTING, false);
        mOrientationEvent = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                return;
                //
//                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
//                    return;
//                }
//                mOrientation = RoundUtil.roundOrientation(orientation, mOrientation);
//                int orientationCompensation = (mOrientation + RoundUtil.getDisplayRotation(ActivityCamera.this)) % 360;
//                if (mOrientationCompensation != orientationCompensation) {
//                    mOrientationCompensation = orientationCompensation;
//                    OrientationUtil.setOrientation(mOrientationCompensation == -1 ? 0 : mOrientationCompensation);
//                    setOrientation(OrientationUtil.getOrientation(), true, flash_light_btn, switch_camera_btn, album_btn, show_sticker_ll, show_material_ll,
//                            take_photo_btn, back_home_btn, camera_set_btn);
//
//                }
            }
        };
        mMarkViewList = new ArrayList<WaterMarkView>();
        mSceneWaterMarkViewList = new ArrayList<View>();
        // 加载静态贴图
        // doInitWaterMarkScene(0);
        //加载滤镜效果
        doInitARFilter();
        // 加载动态贴图
        doInitARStick();
        queryCollageList();
    }

    private DynamicListInfo aDynamicListInfo;
    ArrayList<DynamicIconInfo> mDynamicIconInfo;

    public void queryCollageList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                try {
                    Gson gson = new Gson();
                    aDynamicListInfo = (DynamicListInfo) gson.fromJson(json.toString(), DynamicListInfo.class);
                    Gson gson1 = new Gson();
                    mDynamicIconInfo = gson1.fromJson(json.toString(), DynamicCategoryInfo.class).data;
                    mDynamicPicAdapter.addAll(mDynamicIconInfo);

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
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=dynamic_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setCameraRatio();
        }
    }

    void setCameraRatio() {
        if (mPictureRatio == PictureRatio.RATIO_ONE_TO_ONE) {
            setCameraRatioOneToOne();
        } else if (mPictureRatio == PictureRatio.RATIO_THREE_TO_FOUR) {
            setCameraRatioThreeToFour();
        } else {
            setCameraRatioFull();
        }
    }

    void setCameraRatioThreeToFour() {
        int camera_width = DisplayHelper.getScreenWidth();
        int btm_bar_height_old = bar.getHeight();
        int btm_bar_height_new;
        int top_bar_height = camera_top_rl.getHeight();
        int camera_height = (int) ((float) camera_width * 4 / 3);
        btm_bar_height_new = DisplayHelper.getScreenHeight() - top_bar_height - camera_height;
        RelativeLayout.LayoutParams btm_params = (RelativeLayout.LayoutParams) bar.getLayoutParams();
        btm_params.height = btm_bar_height_new;
        bar.setLayoutParams(btm_params);
        fill_blank_top.setVisibility(View.GONE);
        fill_blank_bottom.setVisibility(View.GONE);
        bar_height_diff = btm_bar_height_new - btm_bar_height_old;
        camera_top_rl.getBackground().setAlpha(255);
        bar.getBackground().setAlpha(255);
        setStickerStatus();
        setFilterStatus();
    }

    void setCameraRatioFull() {

        int camera_width = DisplayHelper.getScreenWidth();//宽
        int btm_bar_height_old = bar.getHeight();//下面高度
        int btm_bar_height_new;//新的高度
        int top_bar_height = camera_top_rl.getHeight();//上面条目高度
        int camera_height = (int) ((float) camera_width * 16 / 9);
        btm_bar_height_new = DisplayHelper.getScreenHeight() - top_bar_height - camera_height;
        RelativeLayout.LayoutParams btm_params = (RelativeLayout.LayoutParams) bar.getLayoutParams();
        btm_params.height = btm_bar_height_new;
        bar.setLayoutParams(btm_params);
        fill_blank_top.setVisibility(View.GONE);
        fill_blank_bottom.setVisibility(View.GONE);
        bar_height_diff = btm_bar_height_new - btm_bar_height_old;
        camera_top_rl.getBackground().setAlpha(100);
        bar.getBackground().setAlpha(0);
        setStickerStatus();
        setFilterStatus();
    }

    void setCameraRatioOneToOne() {
        int camera_width = DisplayHelper.getScreenWidth();
        int btm_bar_height_old = bar.getHeight();
        int btm_bar_height_new;
        int top_bar_height = camera_top_rl.getHeight();
        fill_blank_top.setVisibility(View.VISIBLE);
        fill_blank_bottom.setVisibility(View.VISIBLE);
        int camera_height = (int) ((float) camera_width * 4 / 3);
        btm_bar_height_new = DisplayHelper.getScreenHeight() - top_bar_height - camera_height;
        int fill_blank_height = camera_height - camera_width;
        RelativeLayout.LayoutParams btm_params = (RelativeLayout.LayoutParams) bar.getLayoutParams();
        btm_params.height = btm_bar_height_new;
        bar.setLayoutParams(btm_params);
        RelativeLayout.LayoutParams fill_blank_params = (RelativeLayout.LayoutParams) fill_blank_top.getLayoutParams();
        fill_blank_params.height = fill_blank_height / 2;
        fill_blank_top.setLayoutParams(fill_blank_params);
        RelativeLayout.LayoutParams fill_blank_btm_params = (RelativeLayout.LayoutParams) fill_blank_bottom.getLayoutParams();
        fill_blank_btm_params.height = fill_blank_height / 2;
        fill_blank_bottom.setLayoutParams(fill_blank_btm_params);
        bar_height_diff = btm_bar_height_new - btm_bar_height_old;
        camera_top_rl.getBackground().setAlpha(255);
        bar.getBackground().setAlpha(255);
        setStickerStatus();
        setFilterStatus();

    }

    @Override
    public void onResume() {
        super.onResume();
//        switchCamera();
        getFragmentManager().beginTransaction().replace(R.id.container, current).commit();

        SharedPreferencesHelper.saveBooleanValue(this, "ispause", false);
        mOrientationEvent.enable();
        resetAlbumPhoto();
        if (lastSelectArImageView != null) {
            String animationName = (String) lastSelectArImageView.getTag();
            animation_view.setData(animationName, false);
            // 这里启动脸检测
            if (current != null) {
                current.setAnimationView(animation_view);
            }
        }
    }

    @Override
    public void onPause() {
        getFragmentManager().beginTransaction().remove(current).commit();
        super.onPause();
        SharedPreferencesHelper.saveBooleanValue(this, "ispause", true);
        mOrientationEvent.disable();
        if (animation_view != null) {
            animation_view.clearData();
        }
    }


    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animation_view != null) animation_view.clearData();
        EventBus.getEventBus().unregister(this);
    }


    public int photoSize = 2;//0为全屏,1为1比1,2为4比3

    @OnClick({
            R.id.camera_scale_iv, R.id.camera_timer_iv, R.id.flash_light_iv, R.id.switch_camera_iv, R.id.back_home_iv, R.id.camera_set_iv,
            R.id.album_btn, R.id.show_sticker_ll, R.id.show_filter_ll, R.id.show_material_ll, R.id.take_photo_btn, R.id.btn_enhance_switch, R.id.btn_close_filter_list, R.id.btn_close_ar_list,
            R.id.btn_clear_filter, R.id.btn_clear_ar, R.id.tv_takephoto,
            R.id.Tips, R.id.camera_scale_ll, R.id.camera_timer_ll, R.id.flash_light_ll, R.id.switch_camera_ll, R.id.back_home_ll, R.id.camera_set_ll
    })


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_light_ll:
                showFlashMenu(this, flash_light_iv);
                break;
            case R.id.flash_light_iv:
                showFlashMenu(this, flash_light_iv);
                break;
            case R.id.camera_timer_ll:
                setTakeDelay();
                break;
            case R.id.camera_timer_iv:
                setTakeDelay();
                break;
            case R.id.camera_scale_ll:
                showScaleType();
                break;
            case R.id.camera_scale_iv:
                showScaleType();
                break;
            case R.id.switch_camera_ll:
                switch_camera_iv.setEnabled(false);
                switch_camera_ll.setEnabled(false);
                clearAnimationData();
                if (hasTwoCameras) {
                    switchCamera();
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
                    if (current == std) {
//                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);
//                        current.stopAnimation();
//                        current.stopGoogleFaceDetect();
                    } else {
//                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
//                        current.sendMessage();
//                        current.startAnimation();
                    }
                }
                ClearWaterMark();
                break;
            case R.id.switch_camera_iv:
                switch_camera_iv.setEnabled(false);
                switch_camera_ll.setEnabled(false);
                clearAnimationData();
                if (hasTwoCameras) {

                    switchCamera();
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
                    if (current == std) {
//                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);

//                        current.stopAnimation();
//                        current.stopGoogleFaceDetect();
                    } else {
//                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
//                        current.sendMessage();
//                        current.startAnimation();
                    }
                }
                ClearWaterMark();
                break;
            case R.id.take_photo_btn:
//                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_TAKE_PHOTO);
                take_photo_btn.setEnabled(false);
                mMarkViewList.clear();
                if (last_mark_view != null) {
                    last_mark_view.setEditState(false);
                    mMarkViewList.add(last_mark_view);
                }
                saveAnimationImageData();
                takePhoto();
                break;
            case R.id.album_btn:
                SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.CUT_TYPE, 2);
//                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST);
                ActivityHelper.startActivity(this, AlbumPhotoSelectActivity.class);
                break;
            case R.id.back_home_iv:
                ActivityHelper.startActivity(this, MenuActivity.class);

                // 退出动画和进入动画
                overridePendingTransition(R.anim.activity_to_in, R.anim.activity_to_out);
                finish();
                break;
            case R.id.back_home_ll:
                ActivityHelper.startActivity(this, MenuActivity.class);

                // 退出动画和进入动画
                overridePendingTransition(R.anim.activity_to_in, R.anim.activity_to_out);
                finish();
                break;
            case R.id.show_sticker_ll:
                showSticker(true);
                if (!camera_watermark_setting) {
                    mShowSticker = !mShowSticker;
                }
                break;
            case R.id.show_filter_ll:

                //显示滤镜
                showFilter(true);
               /* if (!camera_watermark_setting) {
                    mShowSticker = !mShowSticker;
                }*/
                break;

            case R.id.camera_set_ll:
                showSetWindow(this, v);
                break;
            case R.id.camera_set_iv:
                showSetWindow(this, v);
                break;
           /* case R.id.btn_enhance_switch:
//                current.setEnableEnhance(!current.isEnableEnhance());
                current.setEnableEnhance(true);
                setEnhanceButton();
                break;*/
            case R.id.btn_clear_ar:
                mDynamicPicAdapter.getItem(currentSelectDynamic).setSelect(false);
                mDynamicPicAdapter.notifyItemChanged(currentSelectDynamic);
                clearAnimationData();
                break;
            case R.id.btn_clear_filter:
                current.setFilter(new GPUImageFilter());
//                new EffectImageTask(ImageCropBitmap, mCurrentFilter, mFilterEffectListener).execute();
                break;
            case R.id.btn_close_ar_list:
                showSticker(false);
                break;

            case R.id.btn_close_filter_list:
                showFilter(false);
                break;
            case R.id.show_material_ll:
                ActivityHelper.startActivity(this, TemplateManagemenActivity.class);

                break;
            case R.id.tv_takephoto:
                if (flag) {
                    take_photo_btn.setEnabled(false);
                    takePhoto();
                }
                break;
            case R.id.Tips:
                Tips.setVisibility(View.GONE);

                break;
            default:
                break;
        }
    }

    private void clearAnimationData() {
        if (animation_view == null) return;
        animation_view.clearData();
        std.clearAnimationView();
        ffc.clearAnimationView();
        if (lastSelectArImageView != null) lastSelectArImageView.setChecked(false);
    }

    private void saveAnimationImageData() {
        saveFaceCenterX = (int) animation_view.centerXFilter.getData();
        saveFaceCenterY = (int) animation_view.centerYFilter.getData();
        saveFaceScale = animation_view.scaleFilter.getData();
        saveFaceAngle = animation_view.angleFilter.getData();
        saveMouthX = (int) animation_view.mouthXFilter.getData();
        saveMouthY = (int) animation_view.mouthYFilter.getData();
    }

    private boolean isMirror = false;

    /**
     * 切换前后camera
     */
    private void switchCamera() {

        if (current == null) {
            current = std;
        } else {
            current = ((current == std) ? ffc : std);
            flash_light_iv.setVisibility((current == std) ? View.VISIBLE : View.GONE);
            if (current == ffc) isMirror = true;
        }
//         current.setAnimationView(animation_view);
        animation_view.setIsMirror(isMirror);
        switch_camera_iv.setEnabled(true);
        switch_camera_ll.setEnabled(true);

    }

    private void setEnhanceButton() {
        btn_enhance_switch.setBackgroundResource(current.isEnableEnhance() ? R.drawable.button_enhance_on : R.drawable.button_enhance_off);
    }

    private void takePhoto() {
        SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.CUT_TYPE, photoSize);
        camera_set_ll.setEnabled(false);
        camera_set_iv.setEnabled(false);
        take_photo_btn.setEnabled(false);
        final int delay;
        if (timeType == DELAY_THREE) {
            delay = 3 * 1000;
        } else if (timeType == DELAY_FIVE) {
            delay = 5 * 1000;
        } else if (timeType == DELAY_TEN) {
            delay = 10 * 1000;
        } else {
            delay = 0;
        }
        if (delay > 0) {
            take_photo_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.film_camera_bg_color));
            finalTime_thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int down_time = delay / 1000;
                    while (down_time > 0) {
                        final int finalDown_time = down_time;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                take_photo_btn.setText(finalDown_time + "");
                            }
                        });
                        down_time--;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            execTakePhoto();
                            take_photo_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.film_camera_btn));
                            take_photo_btn.setText("");
                        }
                    });
                }
            });
            finalTime_thread.start();
        } else {
            execTakePhoto();
            take_photo_btn.setEnabled(true);
            camera_set_iv.setEnabled(true);
            camera_set_ll.setEnabled(true);
        }
    }

    void execTakePhoto() {
        current.clearAnimationView();

        if (OrientationUtil.getOrientation() == 90 || OrientationUtil.getOrientation() == 180) {
            current.setPictureRatio(mPictureRatio, bar.getHeight() + fill_blank_bottom.getHeight());
        } else {
            current.setPictureRatio(mPictureRatio, camera_top_rl.getHeight() + fill_blank_top.getHeight());
        }

        // 是否要显示AR贴纸
        current.setShowAR(animation_view.isAnimationRunning());
        current.isShowAR(animation_view.isAnimationRunning());

        if (mHdrState == HDRSTATE.ON) {
            current.takeSimplePicture(mMarkViewList, true);
        } else if (mHdrState == HDRSTATE.AUTO) {
            current.takeSimplePicture(mMarkViewList, true, true);
        } else {
            current.takeSimplePicture(mMarkViewList);

        }

    }

    //  把layout_sticker设置到最下面
    private void setStickerStatus() {
        layout_sticker.setVisibility(View.INVISIBLE);
        layout_sticker.setAlpha(0);
    }

    private void showSticker(boolean show) {
        layout_sticker.setVisibility(View.VISIBLE);

        if (show) {
            layout_sticker.setAlpha(0.f);
            ObjectAnimator anim = ObjectAnimator//
                    .ofFloat(layout_sticker, "alpha", 0.0F, 1.0F)//
                    .setDuration(300);//
            anim.start();
        } else {
            layout_sticker.setAlpha(1.f);
            ObjectAnimator anim = ObjectAnimator//
                    .ofFloat(layout_sticker, "alpha", 1.0F, 0.0F)//
                    .setDuration(300);//
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    layout_sticker.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }
            });
        }
    }

    //  把layout_sticker设置到最下面
    private void setFilterStatus() {
        layout_filter.setVisibility(View.INVISIBLE);
        layout_filter.setAlpha(0);
    }

    private void showFilter(boolean show) {
        layout_filter.setVisibility(View.VISIBLE);

        if (show) {
            layout_filter.setAlpha(0.f);
            ObjectAnimator anim = ObjectAnimator//
                    .ofFloat(layout_filter, "alpha", 0.0F, 1.0F)//
                    .setDuration(300);//
            anim.start();
        } else {
            layout_filter.setAlpha(1.f);
            ObjectAnimator anim = ObjectAnimator//
                    .ofFloat(layout_filter, "alpha", 1.0F, 0.0F)//
                    .setDuration(300);//
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    layout_filter.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }
            });
        }
    }


    //设置点屏拍照默认为关闭
    private boolean flag = false;

    public void showSetWindow(final Context context, View parent) {
        flag = !flag;
        if (flag) {
            camera_set_iv.setBackgroundResource(R.drawable.icon_capture_20_13);
            tv_takephoto.setVisibility(View.VISIBLE);
//            ToasterHelper.show(this, "打开");
            ToasterHelper.showShort(this, "打开", R.drawable.img_blur_bg);
        } else {
            camera_set_iv.setBackgroundResource(R.drawable.icon_capture_20_12);
//            ToasterHelper.show(this, "关闭");
            ToasterHelper.showShort(this, "关闭", R.drawable.img_blur_bg);

        }





       /* LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.layout_camera_set_popupwindow, null);
        final PopupWindow pw = new PopupWindow(mContext);
        pw.setContentView(menuView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.popuStyle);
        pw.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        pw.setFocusable(true);

        int[] location = new int[2];
        parent.getLocationOnScreen(location);
        pw.showAtLocation(parent, Gravity.NO_GRAVITY, location[0] - 320, location[1] - pw.getHeight() + 20 + camera_top_rl.getHeight());
        final Button btn_camrea_ratio = (Button) menuView.findViewById(R.id.btn_camrea_ratio);
        final Button btn_delay_take_pic = (Button) menuView.findViewById(R.id.btn_delay_take_pic);
        //TODO:本版本隐藏HDR功能 5/25/2015
        final Button btn_camera_hdr = (Button) menuView.findViewById(R.id.btn_camera_hdr);
        setButtonText(btn_camrea_ratio);
        setButtonText(btn_delay_take_pic);
        setButtonText(btn_camera_hdr);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_delay_take_pic:
                        if (timeType == DELAY_NONE) {
                            timeType = DELAY_THREE;
                        } else if (timeType == DELAY_THREE) {
                            timeType = DELAY_FIVE;
                        }else if (timeType == DELAY_FIVE) {
                            timeType = DELAY_TEN;
                        } else {
                            timeType = DELAY_NONE;
                        }
                        setButtonText(btn_delay_take_pic);
                        dismisPw(pw);
                        break;
                    case R.id.btn_camrea_ratio:
                        if (mPictureRatio == PictureRatio.RATIO_DEFAULT) {
                            mPictureRatio = PictureRatio.RATIO_ONE_TO_ONE;
                            setCameraRatioOneToOne();
                        } else if (mPictureRatio == PictureRatio.RATIO_ONE_TO_ONE) {
                            mPictureRatio = PictureRatio.RATIO_THREE_TO_FOUR;
                            setCameraRatioThreeToFour();
                        } else {
                            mPictureRatio = PictureRatio.RATIO_DEFAULT;
                            setCameraRatioThreeToFour();
                        }
                        setButtonText(btn_camrea_ratio);
                        dismisPw(pw);
                        break;
                    case R.id.btn_camera_hdr:
                        if (mHdrState == HDRSTATE.OFF) {
                            mHdrState = HDRSTATE.ON;
                        } else if (mHdrState == HDRSTATE.ON) {
                            mHdrState = HDRSTATE.AUTO;
                        } else {
                            mHdrState = HDRSTATE.OFF;
                        }
                        setButtonText(btn_camera_hdr);
                        dismisPw(pw);
                        break;
                }
            }
        };
        btn_camrea_ratio.setOnClickListener(listener);
        btn_delay_take_pic.setOnClickListener(listener);
        btn_camera_hdr.setOnClickListener(listener);*/
    }

    private void dismisPw(final PopupWindow pw) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pw.dismiss();
            }
        }, 1000);
    }

    /**
     * 重置设置按钮文字
     *
     * @param btn
     */
    void setButtonText(Button btn) {
        switch (btn.getId()) {
            case R.id.btn_camrea_ratio:
                btn.setText((mPictureRatio == PictureRatio.RATIO_DEFAULT) ? "默认" : (mPictureRatio == PictureRatio.RATIO_ONE_TO_ONE) ? "1:1" : "3:4");
                break;
            case R.id.btn_delay_take_pic:
                btn.setText(timeType == DELAY_THREE ? "3″" : timeType == DELAY_FIVE ? "5″" : timeType == DELAY_FIVE ? "10″" : "默认");
                break;
            case R.id.btn_camera_hdr:
                btn.setText(mHdrState == HDRSTATE.OFF ? "关闭" : mHdrState == HDRSTATE.ON ? "开启" : "自动");
                break;
        }
    }


    public void showFlashMenu(Context context, View parent) {
        switch (flashType) {
            case FLASHMODECODE_AUTO:

                current.setflashMode(flashModeCode.off);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_OFF;
                mHdrState = HDRSTATE.OFF;
                ToasterHelper.showShort(this, "闪光关", R.drawable.img_blur_bg);

                break;
            case FLASHMODECODE_OFF:

                current.setflashMode(flashModeCode.on);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_ON;
                mHdrState = HDRSTATE.ON;
                ToasterHelper.showShort(this, "闪光开", R.drawable.img_blur_bg);
                break;
            case FLASHMODECODE_ON:

                current.setflashMode(flashModeCode.light);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_LIGHT;
                mHdrState = HDRSTATE.ON;
                ToasterHelper.showShort(this, "长亮", R.drawable.img_blur_bg);
                break;
            case FLASHMODECODE_LIGHT:

                current.setflashMode(flashModeCode.auto);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_AUTO;
                mHdrState = HDRSTATE.AUTO;
                ToasterHelper.showShort(this, "自动", R.drawable.img_blur_bg);
                break;
        }

        if (current.getCurrentModeCode() == flashModeCode.auto) {
            flash_light_iv.setBackgroundResource(R.drawable.icon_capture_20_01);
        } else if (current.getCurrentModeCode() == flashModeCode.on) {
            flash_light_iv.setBackgroundResource(R.drawable.icon_capture_20_03);
        } else if (current.getCurrentModeCode() == flashModeCode.off) {
            flash_light_iv.setBackgroundResource(R.drawable.icon_capture_20_02);
        } else if (current.getCurrentModeCode() == flashModeCode.light) {
            flash_light_iv.setBackgroundResource(R.drawable.icon_capture_20_04);
        }
       /* flash_auto_btn.setOnClickListener(listener);
        flash_on_btn.setOnClickListener(listener);
        flash_off_btn.setOnClickListener(listener);
        flash_light_btn.setOnClickListener(listener);*/
    }

    @Subcriber(tag = PuTaoConstants.DOWNLOAD_FILE_FINISH + "")
    public void downLoadFinish(Bundle bundle) {
        final int percent = bundle.getInt("percent");
        final int position = bundle.getInt("position");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dynamicIconInfo.setShowProgress(true);

                mDynamicPicAdapter.getItem(position + nativeList.size()).setShowProgress(false);
                mDynamicPicAdapter.notifyItemChanged(position + nativeList.size());
//                mDynamicPicAdapter.notifyDataSetChanged();
                ToasterHelper.showShort(ActivityCamera.this, "下载成功", R.drawable.img_blur_bg);
            }
        });
    }

   /* @Subcriber(tag = PuTaoConstants.HAVE_NO_FACE+"")
    public void haveNoFace(Bundle bundle) {
      boolean haveface =  bundle.getBoolean("noface");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String dd="lail";
            }
        });
    }*/


    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.WATER_MARK_CITY_SELECTED:
                Bundle bundle1 = event.bundle;
                String city = bundle1.getString("city");
                updateDistanceViewText(city);
                break;
            case PuTaoConstants.WATER_MARK_DATE_SELECTED:
                updateFestivalViewText(event.bundle);
                break;
            case PuTaoConstants.WATER_MARK_TEXT_EDIT:
                updateTextEditViewText(event.bundle);
                break;
            case PuTaoConstants.OPEN_AR_SHOW_ACTIVITY:
                Intent intent = new Intent(mContext, PhotoARShowActivity.class);
                intent.putExtra("imagePath", event.bundle.getString("imagePath"));
                intent.putExtra("animationName", animation_view.getAnimtionName());
                mContext.startActivity(intent);
                break;
        }
    }

    /**
     * 更新水印文字
     *
     * @param bundle
     */
    private void updateTextEditViewText(Bundle bundle) {
        String watermark_text = bundle.getString("watermark_text");
        if (!StringHelper.isEmpty(watermark_text)) {
            waterView.setWaterTextByType(WaterTextEventType.TYPE_EDIT_TEXT, watermark_text);
        }
    }

    /**
     * 根据选择的节日设定节日View倒计时
     *
     * @param bundle
     */
    private void updateFestivalViewText(Bundle bundle) {
        String name = bundle.getString("name");
        String date = bundle.getString("date");
        if (!StringHelper.isEmpty(name)) {
            waterView.setWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_NAME, name);
        }
        if (!StringHelper.isEmpty(date)) {
            waterView.setWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE,
                    String.valueOf(DateUtil.getDays(date, DateUtil.getStringDateShort())));
        }
    }

    /**
     * 根据当前选择城市更新界面上的文字信息
     *
     * @param city
     */
    private void updateDistanceViewText(String city) {
        CityMap.CityPositon pos = CityMap.getInstance().getLocationByCity(city);
        String current_city = SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENCE_CURRENT_CITY);
        // Loger.d("current_city------------->" + current_city);
        double lat1 = 0, lng1 = 0, lat2 = 0, lng2 = 0;
        try {
            lat1 = Double.valueOf(SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENC_LOCATION_LATITUDE, "0"));
            lng1 = Double.valueOf(SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENC_LOCATION_LONGITUDE, "0"));
            lng2 = Double.valueOf(pos.longitude);
            lat2 = Double.valueOf(pos.latitude);
        } catch (Exception e) {
        }
        waterView.setWaterText(text_index, city);
        if (!StringHelper.isEmpty(current_city)) {
            waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_CURRENT_CITY, current_city);
        } else {
            current_city = waterView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_CURRENT_CITY);
            CityMap.CityPositon c_pos = CityMap.getInstance().getLocationByCity(current_city);
            lat1 = Double.parseDouble(c_pos.latitude);
            lng1 = Double.parseDouble(c_pos.longitude);
        }
        waterView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_NONE, GpsUtil.GetDistance(lat1, lng1, lat2, lng2) + " 公里");
    }

    private TextWaterMarkView getTextWaterMarkView(WaterMarkIconInfo iconInfo, Bitmap bm) {
        final TextWaterMarkView mMarkView = new TextWaterMarkView(this, bm, iconInfo.textElements, iconInfo, true);
        mMarkView.setTextOnclickListener(new TextWaterMarkView.TextOnClickListener() {
            @Override
            public void onclicked(WaterMarkIconInfo markIconInfo, int index) {
                text_index = index;
                waterView = mMarkView;
                if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)) {
                    ActivityHelper.startActivity(ActivityCamera.this, CitySelectActivity.class);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", waterView.getWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_NAME));
                    bundle.putString("date", waterView.getWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE));
                    ActivityHelper.startActivity(ActivityCamera.this, FestivalSelectActivity.class, bundle);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)) {
                    showWaterTextEditDialog(waterView.getWaterTextByType(WaterTextEventType.TYPE_EDIT_TEXT));

                }
            }

            @Override
            public void onRemoveClick(WaterMarkView view) {
                container.removeView(view);
                mMarkViewList.remove(view);
            }
        });
        mMarkView.setTag(iconInfo.id);
        if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)) {
            String cur_city = SharedPreferencesHelper.readStringValue(mContext, PuTaoConstants.PREFERENCE_CURRENT_CITY);
            if (!StringHelper.isEmpty(cur_city)) {
                mMarkView.setWaterTextByType(WaterTextEventType.TYPE_SELECT_CURRENT_CITY, cur_city);
            }
            if (!GpsUtil.checkGpsState(mContext)) {
//                showToast("打开GPS，测测离家还有多远!");
            }
        } else if (iconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)) {
            String date = mMarkView.getWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE);
            String new_date = "";
            try {
                new_date = String.valueOf(DateUtil.getDays(date, DateUtil.getStringDateShort()));
            } catch (Exception e) {
            }
            if (!StringHelper.isEmpty(new_date)) {
                mMarkView.setWaterTextByType(TextWaterMarkView.WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE, new_date);
            }
        }
        return mMarkView;
    }

    void showWaterTextEditDialog(String def_str) {
        final WaterTextDialog dialog = new WaterTextDialog(this, DisplayHelper.getScreenWidth(), 180, R.layout.dialog_watertext_edit,
                R.style.dialog_style);
        final TextView mMessage = (TextView) dialog.findViewById(R.id.et_input);
        ImageView btn_close = (ImageView) dialog.findViewById(R.id.btn_close);
        ImageView btn_ok = (ImageView) dialog.findViewById(R.id.btn_ok);
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("watermark_text", mMessage.getText().toString());
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.WATER_MARK_TEXT_EDIT, bundle));
                dialog.dismiss();
            }
        });
        mMessage.setText(def_str);
        mMessage.findFocus();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(mMessage, 0);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mMessage.getWindowToken(), 0);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private NormalWaterMarkView getNormalWaterMarkView(WaterMarkIconInfo iconInfo, Bitmap bm) {
        NormalWaterMarkView mMarkView = new NormalWaterMarkView(this, bm, true) {
            @Override
            public void cancelMarkEdit() {
                super.cancelMarkEdit();
                //                if (current == std) {
                //                    current.autoFocus();
                //                }
            }
        };
        (mMarkView).setOnRemoveWaterListener(new WaterMarkView.OnRemoveWaterListener() {
            @Override
            public void onRemoveClick(WaterMarkView view) {
                removeWaterMarkView(view);
            }
        });
        mMarkView.setTag(iconInfo.id);
        return mMarkView;
    }

    /**
     * 移除贴图
     *
     * @param view
     */
    public void removeWaterMarkView(WaterMarkView view) {
        if (view != null) {
            container.removeView(view);
        }
    }

    public void setFlashResource(flashModeCode code) {
        int resId = 0;
        if (code == flashModeCode.auto) {
            resId = R.drawable.icon_capture_20_01;
        } else if (code == flashModeCode.on) {
            resId = R.drawable.icon_capture_20_03;
        } else if (code == flashModeCode.off) {
            resId = R.drawable.icon_capture_20_02;
        } else if (code == flashModeCode.light) {
            resId = R.drawable.icon_capture_20_04;
        }
        flash_light_iv.setBackgroundResource(resId);
    }

    float transDegree = 0;
    float currentDegree = 0;

    public void setOrientation(int orientation, boolean isAnimator, View... views) {
        float degree = 0;
        int mFromRotation = 0;
        int mToRotation = orientation;
        switch (mOrientation) {
            case 0:
                degree = 0;
                break;
            case 270:
                degree = 90;
                break;
            case 180:
                degree = 180;
                break;
            case 90:
                degree = 270;
                break;
            default:
                break;
        }
        transDegree = degree - currentDegree;
        currentDegree += transDegree;
        Loger.i("degree:" + ",orientation=" + orientation + ",transDegree=" + transDegree);
        rotateWaterMark(transDegree);
        for (int i = 0; i < views.length; i++) {
            mFromRotation = (int) views[i].getRotation();
            if (mOrientation == 0) {
                mToRotation = 0;
            } else if (mOrientation == 270) {
                mToRotation = 90;
            } else if (mOrientation == 180) {
                mToRotation = 180;
            } else if (mOrientation == 90) {
                mToRotation = -90;
            }
            setViewRotation(mFromRotation, mToRotation, views[i]);
        }
    }

    public void setViewRotation(int fromRotation, int toRotation, View view) {
        RotateAnimation rotate = null;
        if (fromRotation < 0 || toRotation < 0) {
            if (fromRotation > toRotation) {
                rotate = new RotateAnimation(fromRotation + 90, fromRotation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                rotate = new RotateAnimation(fromRotation, fromRotation + 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            }
        } else {
            if (fromRotation > toRotation) {
                rotate = new RotateAnimation(fromRotation, fromRotation - 90, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            } else {
                rotate = new RotateAnimation(fromRotation - 90, fromRotation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            }
        }
        rotate.setDuration(500);
        view.startAnimation(rotate);
        view.setRotation(toRotation);
    }

    private void rotateWaterMark(float degree) {
        if (last_mark_view != null)
            last_mark_view.rotateWaterMark(degree);
    }

    private WaterMarkConfigInfo mWaterMarkConfigInfo;

    private void doInitARStick() {
        loadARThumbnail();
    }

    //加载滤镜效果
    private void doInitARFilter() {
//        loadFilters();
//        loadARThumbnail();
    }

    // 加载静态贴图
    private void doInitWaterMarkScene(int index) {
        mSceneWaterMarkViewList.clear();
        if (mWaterMarkConfigInfo == null) {
            mWaterMarkConfigInfo = WaterMarkHelper.getWaterMarkConfigInfoFromDB(mContext);
        }
        if (mWaterMarkConfigInfo != null) {
            if (index != -1) {
                ArrayList<WaterMarkCategoryInfo> categoryInfos = mWaterMarkConfigInfo.content.camera_watermark;
                if (categoryInfos.size() > 0) {
                    WaterMarkCategoryInfo info = mWaterMarkConfigInfo.content.camera_watermark.get(index);
                    ArrayList<WaterMarkIconInfo> elements = info.elements;

                    if (elements != null) {
                        loadWaterMarkThumbnail(elements);
                    }
                }
            }
        }
    }


    /**
     * 加载相机拍照界面动态贴图缩略图
     */
    private void loadARThumbnail() {
        // 第一版本数据写死，因为后台接口都没有通，以后的版本此处要包括随app打包的和从服务器上下载的所有ar贴纸
        ArrayList<String> elements = new ArrayList<String>();
        elements.add("cn");
        elements.add("fd");
        elements.add("hy");
        elements.add("hz");
        elements.add("kq");
        elements.add("mhl");
        elements.add("xhx");
        elements.add("xm");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 5);

        for (int i = 0; i < elements.size(); i++) {
            String iconInfo = elements.get(i);
            if (StringHelper.isEmpty(iconInfo)) continue;
            ARImageView arImageView = new ARImageView(this);

            String imagePath = FileUtils.getARStickersPath() + iconInfo + "_icon.png";
            arImageView.setData(imagePath);
            arImageView.setTag(iconInfo);
            arImageView.setOnClickListener(arStickerOnclickListener);
//            layout_sticker_list.addView(arImageView);

        }
    }

    private Bitmap filter_origin;

    private String mTempFilter = GLEffectRender.DEFAULT_EFFECT_ID;
    final List<View> filterEffectViews = new ArrayList<View>();
    List<TextView> filterNameViews = new ArrayList<TextView>();

    //加载滤镜效果
    public void loadFilters() {
        List<String> filterEffectNameList = new ArrayList<String>();
        filterEffectNameList.addAll(Arrays.asList(getResources().getStringArray(R.array.filter_effect)));
        filter_origin = zoomSmall(((BitmapDrawable) getResources().getDrawable(R.drawable.filter_none)).getBitmap());
        for (final String item : filterEffectNameList) {
            new EffectImageTask(filter_origin, item, new EffectImageTask.FilterEffectListener() {
                @Override
                public void rendered(Bitmap bitmap) {
                    if (bitmap != null) {
                        AddFilterView(item, bitmap);
                    }
                }
            }).execute();
        }
    }

    private CustomerFilter filters = new CustomerFilter();

    private void AddFilterView(final String item, Bitmap bitmap_sample) {
        Log.e(TAG, "AddFilterView: " + item);
        View view = LayoutInflater.from(this).inflate(R.layout.filter_item, null);
        FilterEffectThumbnailView simple_image = (FilterEffectThumbnailView) view.findViewById(R.id.filter_preview);
        simple_image.setImageBitmap(bitmap_sample);
        TextView tv_filter_name = (TextView) view.findViewById(R.id.filter_name);
        tv_filter_name.setText(EffectCollection.getFilterName(item));
        tv_filter_name.setTag(item);
        view.setTag(item);
       /* originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, DisplayHelper.getScreenWidth(),
                DisplayHelper.getScreenHeight());*/
        final Bitmap bitmap = BitmapHelper.getLoadingBitmap(DisplayHelper.getScreenWidth(), DisplayHelper.getScreenHeight());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Umeng事件统计
                HashMap<String, String> filterMap = new HashMap<String, String>();
                filterMap.put((String) view.getTag(), (String) view.getTag());
                mTempFilter = (String) view.getTag();
                new EffectImageTask(bitmap, mTempFilter,
                        new EffectImageTask.FilterEffectListener() {
                            @Override
                            public void rendered(Bitmap bitmap) {
                                if (bitmap != null) {
//                                    show_image.setImageBitmap(bitmap);
                                }
                            }
                        }).execute();
                // 边框
                for (View viewTemp : filterEffectViews) {
                    FilterEffectThumbnailView aRoundCornnerImageView = ((FilterEffectThumbnailView) viewTemp.findViewById(R.id.filter_preview));
                    if ((viewTemp.getTag()).equals(view.getTag())) {
                        aRoundCornnerImageView.setPhotoSelected(true);
                    } else {
                        aRoundCornnerImageView.setPhotoSelected(false);
                    }
                }
                for (TextView tv : filterNameViews) {
                    if (tv.getTag().equals(view.getTag())) {
                        tv.setTextColor(Color.RED);
                    } else {
                        tv.setTextColor(getResources().getColor(R.color.text_color_dark_898989));
                    }
                }

                //设置当前滤镜
                GPUImageFilter filter = null;
                if (item.equals(EffectCollection.none)) {
                    //原画
                    filter = new GPUImageFilter();
                } else if (item.equals(EffectCollection.brightness)) {
                    //白亮晨曦
                    filter = filters.getFilterByType(CustomerFilter.FilterType.BLCX);
                } else if (item.equals(EffectCollection.crossprocess)) {
                    //陌上花开
                    filter = filters.getFilterByType(CustomerFilter.FilterType.MSHK);
                } else if (item.equals(EffectCollection.filllight)) {
                    //白白嫩嫩
                    filter = filters.getFilterByType(CustomerFilter.FilterType.BBNN);
                } else if (item.equals(EffectCollection.saturate)) {
                    // 秋日私语
                    filter = filters.getFilterByType(CustomerFilter.FilterType.QRSY);
                } else if (item.equals(EffectCollection.sepia)) {
                    //指尖流年
                    filter = filters.getFilterByType(CustomerFilter.FilterType.ZJLN);
                } else if (item.equals(EffectCollection.temperature)) {
                    //一米阳关
                    filter = filters.getFilterByType(CustomerFilter.FilterType.YMYG);
                } else if (item.equals(EffectCollection.tint)) {
                    //蔚蓝海岸
                    filter = filters.getFilterByType(CustomerFilter.FilterType.WLHA);
                } else if (item.equals(EffectCollection.vignette)) {
                    //闪亮登场
                    filter = filters.getFilterByType(CustomerFilter.FilterType.SLDC);
                }
                current.setFilter(filter);
            }
        });
        layout_filter_list.addView(view);
        filterEffectViews.add(view);
        filterNameViews.add(tv_filter_name);
    }

    private static Bitmap zoomSmall(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.65f, 0.65f);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }


    // 点击动态贴图时候的处理逻辑，跟静态贴图分开处理
    OnClickListener arStickerOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            ToasterHelper.show(getApplicationContext(), "请将正脸置于取景器内");
            ToasterHelper.showShort(ActivityCamera.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
            if (current == null) return;
            if (animation_view.isAnimationLoading()) {
                ToasterHelper.showShort(ActivityCamera.this, "动画加载中请稍后", R.drawable.img_blur_bg);

                return;
            }
            if (lastSelectArImageView != null) {
                lastSelectArImageView.setChecked(false);
            }
            lastSelectArImageView = (ARImageView) v;
            lastSelectArImageView.setChecked(true);
            String animationName = (String) v.getTag();
            animation_view.setData(animationName, false);
            std.setAnimationView(animation_view);
            ffc.setAnimationView(animation_view);

        }
    };


    /**
     * 加载相机拍照界面贴图缩略图
     *
     * @param elements
     */
    private void loadWaterMarkThumbnail(ArrayList<WaterMarkIconInfo> elements) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 5);
        Matrix matrix = new Matrix();
        matrix.postScale(1.0f, 1.0f);
        for (int i = 0; i < elements.size(); i++) {
            WaterMarkIconInfo iconInfo = elements.get(i);
            if (!StringHelper.isEmpty(iconInfo.sample_image)) {
                String image_path = WaterMarkHelper.getWaterMarkFilePath() + iconInfo.sample_image;
                Bitmap bm = BitmapHelper.getInstance().loadBitmap(image_path);
                if (bm != null) {
                    bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
                    ImageView imageView = new ImageView(this);

                    imageView.setImageBitmap(bm);
                    imageView.setTag(iconInfo);
                    imageView.setLayoutParams(params);
                    imageView.setOnClickListener(stickerOnclickListener);
//                    layout_sticker_list.addView(imageView);
                }
            }
        }
    }


    OnClickListener stickerOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (last_mark_view != null) {
                removeWaterMarkView(last_mark_view);
            }
            WaterMarkIconInfo info_mark = (WaterMarkIconInfo) v.getTag();
            String resName = info_mark.watermark_image;
            String image_path = WaterMarkHelper.getWaterMarkFilePath() + resName;
            Bitmap bm = BitmapHelper.getInstance().loadBitmap(image_path);
            if (info_mark.type.equals(WaterMarkView.WaterType.TYPE_Normal)) {
                NormalWaterMarkView mMarkView = getNormalWaterMarkView(info_mark, bm);
                container.addView(mMarkView);
                last_mark_view = mMarkView;
            } else if (info_mark.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE) || info_mark.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)
                    || info_mark.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)) {
                try {
                    TextWaterMarkView mMarkView = getTextWaterMarkView(info_mark, bm);
                    container.addView(mMarkView);
                    last_mark_view = mMarkView;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void showScaleType() {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        switch (scaleType) {
            case SCALETYPE_ONE:

                camera_scale_iv.setBackgroundResource(R.drawable.icon_capture_20_07);
                scaleType = SCALETYPE_FULL;
                mPictureRatio = PictureRatio.RATIO_DEFAULT;
                setCameraRatioFull();
                photoSize = 0;
                ToasterHelper.showShort(this, "FULL", R.drawable.img_blur_bg);
                break;
            case SCALETYPE_THREE:
                camera_scale_iv.setBackgroundResource(R.drawable.icon_capture_20_06);
                scaleType = SCALETYPE_ONE;
                mPictureRatio = PictureRatio.RATIO_ONE_TO_ONE;
                setCameraRatioOneToOne();
                photoSize = PhotoEditorActivity.CROP_11;
                ToasterHelper.showShort(this, "1:1", R.drawable.img_blur_bg);
                break;
            case SCALETYPE_FULL:
                camera_scale_iv.setBackgroundResource(R.drawable.icon_capture_20_05);
                scaleType = SCALETYPE_THREE;
                mPictureRatio = PictureRatio.RATIO_THREE_TO_FOUR;
                setCameraRatioThreeToFour();
                photoSize = PhotoEditorActivity.CROP_43;
                ToasterHelper.showShort(this, "3:4", R.drawable.img_blur_bg);
                break;
        }


        container.setLayoutParams(params);

    }


    /**
     * 设置拍照延时
     */
    private void setTakeDelay() {
//        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        switch (timeType) {
            case DELAY_NONE:
//                mTakedelaytime == TakeDelayTime.DELAY_THREE;

                camera_timer_iv.setBackgroundResource(R.drawable.icon_capture_20_09);
                timeType = DELAY_THREE;
                ToasterHelper.showShort(this, "延时3秒", R.drawable.img_blur_bg);
                break;
            case DELAY_THREE:

                camera_timer_iv.setBackgroundResource(R.drawable.icon_capture_20_10);
                timeType = DELAY_FIVE;
                ToasterHelper.showShort(this, "延时5秒", R.drawable.img_blur_bg);
                break;
            case DELAY_FIVE:

                camera_timer_iv.setBackgroundResource(R.drawable.icon_capture_20_11);
                timeType = DELAY_TEN;
                ToasterHelper.showShort(this, "延时10秒", R.drawable.img_blur_bg);
                break;
            case DELAY_TEN:

                camera_timer_iv.setBackgroundResource(R.drawable.icon_capture_20_08);
                timeType = DELAY_NONE;
                ToasterHelper.showShort(this, "延时关闭", R.drawable.img_blur_bg);
                break;
        }
//        container.setLayoutParams(params);

        /*//图片
        final Integer datas[] = {R.drawable.icon_capture_20_08, R.drawable.icon_capture_20_09, R.drawable.icon_capture_20_10, R.drawable.icon_capture_20_11};
        final ListPopupWindow popupWindow = new ListPopupWindow(mContext);
        popupWindow.setAdapter(new TimerAdapter(mContext, R.layout.popup_timer_item, datas));
        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                camera_timer_btn.setBackgroundResource(datas[position]);
                popupWindow.dismiss();
            }
        });
        popupWindow.setAnchorView(camera_timer_btn);
        popupWindow.show();*/
    }


    @Override
    public void onBackPressed() {
        ActivityHelper.startActivity(this, MenuActivity.class);
        this.finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        v_red_dot.setVisibility(View.GONE);
        //获取缓存红点数据
        boolean[] dots = new boolean[3];
        dots = PreferenceUtils.getValue(RedDotReceiver.EVENT_DOT_MATTER_CENTER, dots);
        for (int i = 0; i < 3; i++) {
            if (dots[i]) {
                v_red_dot.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    /**
     * 红点显示接收通知
     *
     * @param dot
     */
    @Subcriber(tag = RedDotReceiver.EVENT_DOT_MATERIAL)
    private void setRed_dot(String dot) {
        v_red_dot.setVisibility(View.VISIBLE);
    }

}
