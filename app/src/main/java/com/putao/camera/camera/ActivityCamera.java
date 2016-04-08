
package com.putao.camera.camera;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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

import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.camera.PCameraFragment.TakePictureListener;
import com.putao.camera.camera.PCameraFragment.flashModeCode;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.view.ARImageView;
import com.putao.camera.camera.view.AlbumButton;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.CitySelectActivity;
import com.putao.camera.editor.FestivalSelectActivity;
import com.putao.camera.editor.PhotoARShowActivity;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.editor.dialog.WaterTextDialog;
import com.putao.camera.editor.view.NormalWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView.WaterTextEventType;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gps.CityMap;
import com.putao.camera.gps.GpsUtil;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.setting.watermark.management.TemplateManagemenActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.ToasterHelper;
import com.putao.camera.util.WaterMarkHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityCamera extends BaseActivity implements OnClickListener {
    private String TAG = ActivityCamera.class.getName();
    private TextView tv_takephoto;
    private PCameraFragment std, ffc, current;
    private LinearLayout camera_top_rl, bar, layout_sticker, layout_sticker_list, show_sticker_ll,show_filter_btn, show_material_ll, camera_scale_ll, camera_timer_ll, flash_light_ll, switch_camera_ll, back_home_ll, camera_set_ll;
    private Button camera_scale_btn, camera_timer_btn, flash_light_btn, switch_camera_btn, back_home_btn, camera_set_btn, take_photo_btn, btn_enhance_switch, btn_clear_ar;
    private ImageButton btn_close_ar_list;
    //    private RedPointBaseButton show_material_ll;
  private ImageView Tips;
    private View fill_blank_top, fill_blank_bottom;
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
    public void doBefore() {
        super.doBefore();
        isFristUse = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);
        lastVersionCode = SharedPreferencesHelper.readIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, 0);
        curVersionCode = MainApplication.getVersionCode();


    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_camera;
    }

    @Override
    public void doInitSubViews(View view) {
        fullScreen(true);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenDensity = metric.density;  // 屏幕密度（0.75 (120) / 1.0(160) / 1.5 (240)）

        EventBus.getEventBus().register(this);
        flash_light_ll = queryViewById(R.id.flash_light_ll);
        camera_timer_ll = queryViewById(R.id.camera_timer_ll);
        camera_scale_ll = queryViewById(R.id.camera_scale_ll);
        switch_camera_ll = queryViewById(R.id.switch_camera_ll);
        back_home_ll = queryViewById(R.id.back_home_ll);
        camera_set_ll = queryViewById(R.id.camera_set_ll);

        Tips = queryViewById(R.id.Tips);
        tv_takephoto = queryViewById(R.id.tv_takephoto);
        show_material_ll = queryViewById(R.id.show_material_ll);
        container = queryViewById(R.id.container);
        camera_top_rl = queryViewById(R.id.camera_top_rl);
        flash_light_btn = queryViewById(R.id.flash_light_btn);
        camera_timer_btn = queryViewById(R.id.camera_timer_btn);
        camera_scale_btn = queryViewById(R.id.camera_scale_btn);
        switch_camera_btn = queryViewById(R.id.switch_camera_btn);
        show_filter_btn=queryViewById(R.id.show_filter_btn);
        show_sticker_ll = queryViewById(R.id.show_sticker_ll);
        take_photo_btn = queryViewById(R.id.take_photo_btn);
        back_home_btn = queryViewById(R.id.back_home_btn);
        album_btn = queryViewById(R.id.album_btn);
        camera_set_btn = queryViewById(R.id.camera_set_btn);
        bar = queryViewById(R.id.bar);
        layout_sticker = queryViewById(R.id.layout_sticker);
        camera_activy = queryViewById(R.id.camera_activy);
        layout_sticker_list = queryViewById(R.id.layout_sticker_list);
        fill_blank_top = queryViewById(R.id.fill_blank_top);
        fill_blank_bottom = queryViewById(R.id.fill_blank_bottom);
        btn_enhance_switch = queryViewById(R.id.btn_enhance_switch);
        btn_close_ar_list = queryViewById(R.id.btn_close_ar_list);
        btn_clear_ar = queryViewById(R.id.btn_clear_ar);

        animation_view = queryViewById(R.id.animation_view);
        // 必须设置图片的文件夹，否则显示不出图片
        animation_view.setImageFolder(FileUtils.getARStickersPath());
        animation_view.setScreenDensity(screenDensity);

        addOnClickListener( camera_scale_btn, camera_timer_btn, flash_light_btn, switch_camera_btn, back_home_btn, camera_set_btn,album_btn, show_sticker_ll, show_filter_btn,show_material_ll, take_photo_btn, btn_enhance_switch, btn_close_ar_list, btn_clear_ar, tv_takephoto,
                Tips, camera_scale_ll, camera_timer_ll, flash_light_ll, switch_camera_ll, back_home_ll, camera_set_ll);
        if (hasTwoCameras) {
            std = PCameraFragment.newInstance(false);
            ffc = PCameraFragment.newInstance(true);
            std.setPhotoSaveListener(photoListener);
            ffc.setPhotoSaveListener(photoListener);

        } else {
            std = PCameraFragment.newInstance(false);
            switch_camera_btn.setVisibility(View.GONE);
            std.setPhotoSaveListener(photoListener);
        }
        switchCamera();
        getFragmentManager().beginTransaction().replace(R.id.container, current).commit();

        if (isFristUse || lastVersionCode != curVersionCode) {
            SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.PREFERENC_VERSION_CODE, curVersionCode);
            isFristUse = false;
            SharedPreferencesHelper.saveBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, false);
            Tips.setVisibility(View.VISIBLE);
        }
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

    @Override
    public void doInitData() {
        camera_watermark_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_WATER_MARK_SETTING, false);
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
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferencesHelper.saveBooleanValue(this,"ispause",false);
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
        super.onPause();
        SharedPreferencesHelper.saveBooleanValue(this,"ispause",true);
        mOrientationEvent.disable();
        if (animation_view != null) {
            animation_view.clearData();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (animation_view != null) animation_view.clearData();
        EventBus.getEventBus().unregister(this);
    }


    public int i = 0;//0为全屏,1为1比1,2为4比3

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_light_ll:
                showFlashMenu(this, flash_light_btn);
                break;
            case R.id.flash_light_btn:
                showFlashMenu(this, flash_light_btn);
                break;
            case R.id.camera_timer_ll:
                setTakeDelay();
                break;
            case R.id.camera_timer_btn:
                setTakeDelay();
                break;
            case R.id.camera_scale_ll:
                showScaleType();
                break;
            case R.id.camera_scale_btn:
                showScaleType();
                break;
            case R.id.switch_camera_ll:
                switch_camera_btn.setEnabled(false);
                switch_camera_ll.setEnabled(false);
                clearAnimationData();
                if (hasTwoCameras) {
                        switchCamera();
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
                    if (current == std) {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);
//                        current.stopAnimation();
//                        current.stopGoogleFaceDetect();
                    } else {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
//                        current.sendMessage();
//                        current.startAnimation();
                    }
                }
                ClearWaterMark();
                break;
            case R.id.switch_camera_btn:
                switch_camera_btn.setEnabled(false);
                switch_camera_ll.setEnabled(false);
                clearAnimationData();
                if (hasTwoCameras) {

                    switchCamera();
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
                    if (current == std) {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);
//                        current.stopAnimation();
//                        current.stopGoogleFaceDetect();
                    } else {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
//                        current.sendMessage();
//                        current.startAnimation();
                    }
                }
                ClearWaterMark();
                break;
            case R.id.take_photo_btn:
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_TAKE_PHOTO);
                take_photo_btn.setEnabled(false);
                mMarkViewList.clear();
                if (last_mark_view != null) {
                    last_mark_view.setEditState(false);
                    mMarkViewList.add(last_mark_view);
                }
                saveAnimationImageData();
                //
                takePhoto();

//                clearAnimationData();
//                take_photo_btn.setEnabled(true);
//                current.sendMessage();
                break;
            case R.id.album_btn:
                i=0;
                SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.CUT_TYPE, i);
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST);
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class);
                break;
            case R.id.back_home_btn:
                ActivityHelper.startActivity(mActivity, MenuActivity.class);

                // 退出动画和进入动画
                overridePendingTransition(R.anim.activity_to_in, R.anim.activity_to_out);
                finish();
                break;
            case R.id.back_home_ll:
                ActivityHelper.startActivity(mActivity, MenuActivity.class);

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
            case R.id.show_filter_btn:
                //显示滤镜
                /*showSticker(true);
                if (!camera_watermark_setting) {
                    mShowSticker = !mShowSticker;
                }*/
                break;

            case R.id.camera_set_ll:
                showSetWindow(this, v);
                break;
            case R.id.camera_set_btn:
                showSetWindow(this, v);
                break;
            case R.id.btn_enhance_switch:
                current.setEnableEnhance(!current.isEnableEnhance());
                setEnhanceButton();
                break;
            case R.id.btn_clear_ar:
                clearAnimationData();
                break;
            case R.id.btn_close_ar_list:
                showSticker(false);
                break;
            case R.id.show_material_ll:
//                ActivityHelper.startActivity(this, CollageSampleSelectActivity.class);
                ActivityHelper.startActivity(this, TemplateManagemenActivity.class);

                break;
            case R.id.tv_takephoto:
                if (flag){
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
            flash_light_btn.setVisibility((current == std) ? View.VISIBLE : View.GONE);
            if (current == ffc) isMirror = true;
        }
//         current.setAnimationView(animation_view);
        animation_view.setIsMirror(isMirror);
        switch_camera_btn.setEnabled(true);
        switch_camera_ll.setEnabled(true);

    }

    private void setEnhanceButton() {
        btn_enhance_switch.setBackgroundResource(current.isEnableEnhance() ? R.drawable.button_enhance_on : R.drawable.button_enhance_off);
    }

    private void takePhoto() {
        SharedPreferencesHelper.saveIntValue(this, PuTaoConstants.CUT_TYPE, i);
        camera_set_ll.setEnabled(false);
        camera_set_btn.setEnabled(false);
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
            camera_set_btn.setEnabled(true);
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


    //设置点屏拍照默认为关闭
    private boolean flag = false;

    public void showSetWindow(final Context context, View parent) {
        flag = !flag;
        if (flag) {
            camera_set_btn.setBackgroundResource(R.drawable.icon_capture_20_13);
            tv_takephoto.setVisibility(View.VISIBLE);
//            ToasterHelper.show(this, "打开");
            ToasterHelper.showShort(this,"打开",R.drawable.img_blur_bg);
        } else {
            camera_set_btn.setBackgroundResource(R.drawable.icon_capture_20_12);
//            ToasterHelper.show(this, "关闭");
            ToasterHelper.showShort(this,"关闭",R.drawable.img_blur_bg);

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
                ToasterHelper.showShort(this,"闪光关",R.drawable.img_blur_bg);

                break;
            case FLASHMODECODE_OFF:

                current.setflashMode(flashModeCode.on);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_ON;
                mHdrState = HDRSTATE.ON;
                ToasterHelper.showShort(this,"闪光开",R.drawable.img_blur_bg);
                break;
            case FLASHMODECODE_ON:

                current.setflashMode(flashModeCode.light);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_LIGHT;
                mHdrState = HDRSTATE.ON;
                ToasterHelper.showShort(this,"长亮",R.drawable.img_blur_bg);
                break;
            case FLASHMODECODE_LIGHT:

                current.setflashMode(flashModeCode.auto);
                setFlashResource(current.getCurrentModeCode());
                flashType = FLASHMODECODE_AUTO;
                mHdrState = HDRSTATE.AUTO;
                ToasterHelper.showShort(this,"自动",R.drawable.img_blur_bg);
                break;
        }






     /*   LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.layout_flash_mode_selector, null);
        final PopupWindow pw = new PopupWindow(mContext);
        pw.setContentView(menuView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setAnimationStyle(R.style.popuStyle);
        pw.setOutsideTouchable(false);
        pw.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
        pw.setFocusable(true); // 如果把焦点设置为false，则其他部份是可以点击的，也就是说传递事件时，不会先走PopupWindow
        int[] location = new int[2];
        parent.getLocationOnScreen(location);
//        pw.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - pw.getHeight() + 20 + camera_top_rl.getHeight());
        pw.showAsDropDown(parent, -32, 62);
        Button flash_auto_btn = (Button) menuView.findViewById(R.id.flash_auto_btn);
        Button flash_on_btn = (Button) menuView.findViewById(R.id.flash_on_btn);
        Button flash_off_btn = (Button) menuView.findViewById(R.id.flash_off_btn);
        Button flash_light_btn = (Button) menuView.findViewById(R.id.flash_light_btn);
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.flash_auto_btn:
                        current.setflashMode(flashModeCode.auto);
                        setFlashResource(current.getCurrentModeCode());
                        pw.dismiss();
                        break;
                    case R.id.flash_on_btn:
                        current.setflashMode(flashModeCode.on);
                        setFlashResource(current.getCurrentModeCode());
                        pw.dismiss();
                        break;
                    case R.id.flash_off_btn:
                        current.setflashMode(flashModeCode.off);
                        setFlashResource(current.getCurrentModeCode());
                        pw.dismiss();
                        break;
                    case R.id.flash_light_btn:
                        current.setflashMode(flashModeCode.light);
                        setFlashResource(current.getCurrentModeCode());
                        pw.dismiss();
                        break;
                }
            }
        };*/
        if (current.getCurrentModeCode() == flashModeCode.auto) {
            flash_light_btn.setBackgroundResource(R.drawable.icon_capture_20_01);
        } else if (current.getCurrentModeCode() == flashModeCode.on) {
            flash_light_btn.setBackgroundResource(R.drawable.icon_capture_20_03);
        } else if (current.getCurrentModeCode() == flashModeCode.off) {
            flash_light_btn.setBackgroundResource(R.drawable.icon_capture_20_02);
        } else if (current.getCurrentModeCode() == flashModeCode.light) {
            flash_light_btn.setBackgroundResource(R.drawable.icon_capture_20_04);
        }
       /* flash_auto_btn.setOnClickListener(listener);
        flash_on_btn.setOnClickListener(listener);
        flash_off_btn.setOnClickListener(listener);
        flash_light_btn.setOnClickListener(listener);*/
    }

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
        final TextWaterMarkView mMarkView = new TextWaterMarkView(mActivity, bm, iconInfo.textElements, iconInfo, true);
        mMarkView.setTextOnclickListener(new TextWaterMarkView.TextOnClickListener() {
            @Override
            public void onclicked(WaterMarkIconInfo markIconInfo, int index) {
                text_index = index;
                waterView = mMarkView;
                if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_DISTANCE)) {
                    ActivityHelper.startActivity(mActivity, CitySelectActivity.class);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_FESTIVAL)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("name", waterView.getWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_NAME));
                    bundle.putString("date", waterView.getWaterTextByType(WaterTextEventType.TYPE_SELECT_FESTIVAL_DATE));
                    ActivityHelper.startActivity(mActivity, FestivalSelectActivity.class, bundle);
                } else if (markIconInfo.type.equals(WaterMarkView.WaterType.TYPE_TEXTEDIT)) {
                    showWaterTextEditDialog(waterView.getWaterTextByType(WaterTextEventType.TYPE_EDIT_TEXT));
                    //                    Bundle bundle = new Bundle();
                    //                    bundle.putString("watermark_text", waterView.getWaterTextByType(WaterTextEventType.TYPE_EDIT_TEXT));
                    //                    ActivityHelper.startActivity(mActivity, WatermarkTextEditActivity.class, bundle);
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
                showToast("打开GPS，测测离家还有多远!");
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
        NormalWaterMarkView mMarkView = new NormalWaterMarkView(mActivity, bm, true) {
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
        flash_light_btn.setBackgroundResource(resId);
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
            ARImageView arImageView = new ARImageView(mActivity);

            String imagePath = FileUtils.getARStickersPath() + iconInfo + "_icon.png";
            arImageView.setData(imagePath);
            arImageView.setTag(iconInfo);
            arImageView.setOnClickListener(arStickerOnclickListener);
            layout_sticker_list.addView(arImageView);

        }
    }
    //加载滤镜效果
    private void loadFilters() {
        List<String> filterEffectNameList = new ArrayList<String>();
        filterEffectNameList.addAll(Arrays.asList(getResources().getStringArray(R.array.filter_effect)));
        /*if (filter_origin == null) {
            filter_origin = zoomSmall(((BitmapDrawable) getResources().getDrawable(R.drawable.filter_none)).getBitmap());
        }
        for (final String item : filterEffectNameList) {
            new EffectImageTask(filter_origin, item, new EffectImageTask.FilterEffectListener() {
                @Override
                public void rendered(Bitmap bitmap) {
                    if (bitmap != null) {
                        AddFilterView(item, bitmap);
                    }
                }
            }).execute();
        }*/
    }



    // 点击动态贴图时候的处理逻辑，跟静态贴图分开处理
    OnClickListener arStickerOnclickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
//            ToasterHelper.show(getApplicationContext(), "请将正脸置于取景器内");
            ToasterHelper.showShort(ActivityCamera.this,"请将正脸置于取景器内",R.drawable.img_blur_bg);
            if (current == null) return;
            if (animation_view.isAnimationLoading()) {
                showToast("动画加载中请稍后");
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
                    ImageView imageView = new ImageView(mActivity);

                    imageView.setImageBitmap(bm);
                    imageView.setTag(iconInfo);
                    imageView.setLayoutParams(params);
                    imageView.setOnClickListener(stickerOnclickListener);
                    layout_sticker_list.addView(imageView);
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
private void showScaleType(){
    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
    switch (scaleType) {
        case SCALETYPE_ONE:

            camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_07);
            scaleType = SCALETYPE_FULL;
            mPictureRatio = PictureRatio.RATIO_DEFAULT;
            setCameraRatioFull();
            i = 0;
            ToasterHelper.showShort(this,"FULL",R.drawable.img_blur_bg);
            break;
        case SCALETYPE_THREE:
            camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_06);
            scaleType = SCALETYPE_ONE;
            mPictureRatio = PictureRatio.RATIO_ONE_TO_ONE;
            setCameraRatioOneToOne();
            i = PhotoEditorActivity.CROP_11;
            ToasterHelper.showShort(this,"1:1",R.drawable.img_blur_bg);
            break;
        case SCALETYPE_FULL:
            camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_05);
            scaleType = SCALETYPE_THREE;
            mPictureRatio = PictureRatio.RATIO_THREE_TO_FOUR;
            setCameraRatioThreeToFour();
            i = PhotoEditorActivity.CROP_43;
            ToasterHelper.showShort(this,"3:4",R.drawable.img_blur_bg);
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

                camera_timer_btn.setBackgroundResource(R.drawable.icon_capture_20_09);
                timeType = DELAY_THREE;
                ToasterHelper.showShort(this,"延时3秒",R.drawable.img_blur_bg);
                break;
            case DELAY_THREE:

                camera_timer_btn.setBackgroundResource(R.drawable.icon_capture_20_10);
                timeType = DELAY_FIVE;
                ToasterHelper.showShort(this,"延时5秒",R.drawable.img_blur_bg);
                break;
            case DELAY_FIVE:

                camera_timer_btn.setBackgroundResource(R.drawable.icon_capture_20_11);
                timeType = DELAY_TEN;
                ToasterHelper.showShort(this,"延时10秒",R.drawable.img_blur_bg);
                break;
            case DELAY_TEN:

                camera_timer_btn.setBackgroundResource(R.drawable.icon_capture_20_08);
                timeType = DELAY_NONE;
                ToasterHelper.showShort(this,"延时关闭",R.drawable.img_blur_bg);
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
        ActivityHelper.startActivity(mActivity, MenuActivity.class);
        this.finish();
    }
}
