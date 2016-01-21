
package com.putao.camera.camera;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ListPopupWindow;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.camera.PCameraFragment.TakePictureListener;
import com.putao.camera.camera.PCameraFragment.flashModeCode;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.utils.RoundUtil;
import com.putao.camera.camera.view.AlbumButton;
import com.putao.camera.camera.view.RedPointBaseButton;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.CitySelectActivity;
import com.putao.camera.editor.FestivalSelectActivity;
import com.putao.camera.editor.dialog.WaterTextDialog;
import com.putao.camera.editor.view.NormalWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView;
import com.putao.camera.editor.view.TextWaterMarkView.WaterTextEventType;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.gps.CityMap;
import com.putao.camera.gps.GpsUtil;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DateUtil;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.common.TimerAdapter;
import com.putao.common.util.CameraInterface;
import com.putao.common.util.FaceView;
import com.putao.common.util.GoogleFaceDetect;

import java.util.ArrayList;
import java.util.List;

public class ActivityCamera extends BaseActivity implements OnClickListener {
    private PCameraFragment std, ffc, current;
    private LinearLayout camera_top_rl, bar, layout_sticker, layout_sticker_list;
    private Button camera_scale_btn, camera_timer_btn, flash_light_btn, switch_camera_btn, back_home_btn, camera_set_btn, take_photo_btn, btn_enhance_switch;
    private RedPointBaseButton show_sticker_btn;
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
    private PictureRatio mPictureRatio = PictureRatio.RATIO_DEFAULT;
    private boolean mShowSticker = false;
    private WaterMarkView last_mark_view;
    private TakeDelayTime mTakedelaytime = TakeDelayTime.DELAY_NONE;

    private static final String SCALETYPE_FULL = "full";
    private static final String SCALETYPE_ONE = "1;1";
    private static final String SCALETYPE_THREE = "3:4";
    private String scaleType = SCALETYPE_THREE;//拍照预览界面比例标志


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

    /**
     * 延时拍摄
     */
    public enum TakeDelayTime {
        DELAY_NONE, DELAY_THREE, DELAY_FIVE
    }

    /**
     * HDR
     */
    public enum HDRSTATE {
        ON,
        OFF,
        AUTO
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_camera;
    }

    @Override
    public void doInitSubViews(View view) {
        fullScreen(true);
        EventBus.getEventBus().register(this);
        container = queryViewById(R.id.container);
        camera_top_rl = queryViewById(R.id.camera_top_rl);
        flash_light_btn = queryViewById(R.id.flash_light_btn);
        camera_timer_btn = queryViewById(R.id.camera_timer_btn);
        camera_scale_btn = queryViewById(R.id.camera_scale_btn);
        switch_camera_btn = queryViewById(R.id.switch_camera_btn);
        show_sticker_btn = queryViewById(R.id.show_sticker_btn);
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
        addOnClickListener(camera_scale_btn, camera_timer_btn, switch_camera_btn, flash_light_btn, album_btn, show_sticker_btn, take_photo_btn,
                back_home_btn, camera_set_btn, btn_enhance_switch);
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
        current = std;
        getFragmentManager().beginTransaction().replace(R.id.container, current).commit();


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
                    ClearWaterMark();
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
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                mOrientation = RoundUtil.roundOrientation(orientation, mOrientation);
                int orientationCompensation = (mOrientation + RoundUtil.getDisplayRotation(ActivityCamera.this)) % 360;
                if (mOrientationCompensation != orientationCompensation) {
                    mOrientationCompensation = orientationCompensation;
                    OrientationUtil.setOrientation(mOrientationCompensation == -1 ? 0 : mOrientationCompensation);
                    setOrientation(OrientationUtil.getOrientation(), true, flash_light_btn, switch_camera_btn, album_btn, show_sticker_btn,
                            take_photo_btn, back_home_btn, camera_set_btn);
                }
            }
        };
        mMarkViewList = new ArrayList<WaterMarkView>();
        mSceneWaterMarkViewList = new ArrayList<View>();
        doInitWaterMarkScene(0);
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
        } else if (mPictureRatio == PictureRatio.RATIO_THREE_TO_FOUR || mPictureRatio == PictureRatio.RATIO_DEFAULT) {
            setCameraRatioThreeToFour();
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
        RelativeLayout.LayoutParams layout_sticker_params = (RelativeLayout.LayoutParams) layout_sticker.getLayoutParams();
        if (camera_watermark_setting) {
            layout_sticker_params.topMargin = bar.getTop() - layout_sticker.getHeight() - bar_height_diff;
        } else {
            layout_sticker_params.topMargin = bar.getTop();
        }
        layout_sticker.setVisibility(View.GONE);
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
        RelativeLayout.LayoutParams layout_sticker_params = (RelativeLayout.LayoutParams) layout_sticker.getLayoutParams();
        if (camera_watermark_setting) {
            layout_sticker_params.topMargin = bar.getTop() - layout_sticker.getHeight() - bar_height_diff;
        } else {
            layout_sticker_params.topMargin = bar.getTop();
        }
        layout_sticker.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        mOrientationEvent.enable();
        resetAlbumPhoto();
    }

    @Override
    public void onPause() {
        super.onPause();
        mOrientationEvent.disable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_light_btn:
                showFlashMenu(this, flash_light_btn);
                break;
            case R.id.camera_timer_btn:
                setTakeDelay();
                break;
            case R.id.camera_scale_btn:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) container.getLayoutParams();
                switch (scaleType) {
                    case SCALETYPE_ONE :
                        camera_activy.getBackground().setAlpha(0);
                        camera_top_rl.getBackground().setAlpha(0);
                        bar.getBackground().setAlpha(0);
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                        camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_07);
                        scaleType = SCALETYPE_FULL;
                        break;
                    case SCALETYPE_THREE :
                        camera_activy.getBackground().setAlpha(255);
                        camera_top_rl.getBackground().setAlpha(255);
                        bar.getBackground().setAlpha(255);
                        params.height = getResources().getDisplayMetrics().widthPixels;
                        camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_06);
                        scaleType = SCALETYPE_ONE;
                        break;
                    case SCALETYPE_FULL :
                        camera_activy.getBackground().setAlpha(255);
                        camera_top_rl.getBackground().setAlpha(255);
                        bar.getBackground().setAlpha(255);
                        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        camera_scale_btn.setBackgroundResource(R.drawable.icon_capture_20_05);
                        scaleType = SCALETYPE_THREE;
                        break;
                }
                container.setLayoutParams(params);
                break;
            case R.id.switch_camera_btn:
                if (hasTwoCameras) {
                    current = (current == std) ? ffc : std;
                    flash_light_btn.setVisibility((current == std) ? View.VISIBLE : View.GONE);
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
                    if (current == std) {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);
                    } else {
                        doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
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
                takePhoto();
                break;
            case R.id.album_btn:
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST);
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class);
                break;
            case R.id.back_home_btn:
                finish();
                break;
            case R.id.show_sticker_btn:
                if (!camera_watermark_setting) {
                    mShowSticker = !mShowSticker;
                    showSticker(mShowSticker);
                }
                break;
            case R.id.camera_set_btn:
                showSetWindow(this, v);
                break;
            case R.id.btn_enhance_switch:
                current.setEnableEnhance(!current.isEnableEnhance());
                setEnhanceButton();
                break;
            default:
                break;
        }
    }

    private void setEnhanceButton() {
        btn_enhance_switch.setBackgroundResource(current.isEnableEnhance() ? R.drawable.button_enhance_on : R.drawable.button_enhance_off);
    }

    private void takePhoto() {
        final int delay;
        if (mTakedelaytime == TakeDelayTime.DELAY_THREE) {
            delay = 3 * 1000;
        } else if (mTakedelaytime == TakeDelayTime.DELAY_FIVE) {
            delay = 5 * 1000;
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
        }
    }

    void execTakePhoto() {
        if (OrientationUtil.getOrientation() == 90 || OrientationUtil.getOrientation() == 180) {
            current.setPictureRatio(mPictureRatio, bar.getHeight() + fill_blank_bottom.getHeight());
        } else {
            current.setPictureRatio(mPictureRatio, camera_top_rl.getHeight() + fill_blank_top.getHeight());
        }

        if (mHdrState == HDRSTATE.ON) {
            current.takeSimplePicture(mMarkViewList, true);
        } else if (mHdrState == HDRSTATE.AUTO) {
            current.takeSimplePicture(mMarkViewList, true, true);
        } else {
            current.takeSimplePicture(mMarkViewList);
        }
    }

    void showSticker(boolean show) {
        int start = 0;
        int end = 0;
        if (show) {
            end = -layout_sticker.getHeight() - bar_height_diff;
        } else {
            start = -layout_sticker.getHeight() - bar_height_diff;
        }
        ObjectAnimator.ofFloat(layout_sticker, "translationY", start, end).setDuration(300).start();
    }

    public void showSetWindow(final Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.layout_camera_set_popupwindow, null);
        final PopupWindow pw = new PopupWindow(mContext);
        pw.setContentView(menuView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setOutsideTouchable(false);
        pw.setAnimationStyle(R.style.popuStyle);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
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
                        if (mTakedelaytime == TakeDelayTime.DELAY_NONE) {
                            mTakedelaytime = TakeDelayTime.DELAY_THREE;
                        } else if (mTakedelaytime == TakeDelayTime.DELAY_THREE) {
                            mTakedelaytime = TakeDelayTime.DELAY_FIVE;
                        } else {
                            mTakedelaytime = TakeDelayTime.DELAY_NONE;
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
        btn_camera_hdr.setOnClickListener(listener);
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
                btn.setText(mTakedelaytime == TakeDelayTime.DELAY_THREE ? "3″" : mTakedelaytime == TakeDelayTime.DELAY_FIVE ? "5″" : "默认");
                break;
            case R.id.btn_camera_hdr:
                btn.setText(mHdrState == HDRSTATE.OFF ? "关闭" : mHdrState == HDRSTATE.ON ? "开启" : "自动");
                break;
        }
    }


    public void showFlashMenu(Context context, View parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.layout_flash_mode_selector, null);
        final PopupWindow pw = new PopupWindow(mContext);
        pw.setContentView(menuView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setAnimationStyle(R.style.popuStyle);
        pw.setOutsideTouchable(false);
        pw.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
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
        };
        if (current.getCurrentModeCode() == flashModeCode.auto) {
            flash_auto_btn.setBackgroundResource(R.drawable.icon_capture_20_01);
        } else if (current.getCurrentModeCode() == flashModeCode.on) {
            flash_on_btn.setBackgroundResource(R.drawable.icon_capture_20_03);
        } else if (current.getCurrentModeCode() == flashModeCode.off) {
            flash_off_btn.setBackgroundResource(R.drawable.icon_capture_20_02);
        } else if (current.getCurrentModeCode() == flashModeCode.light) {
            flash_off_btn.setBackgroundResource(R.drawable.icon_capture_20_04);
        }
        flash_auto_btn.setOnClickListener(listener);
        flash_on_btn.setOnClickListener(listener);
        flash_off_btn.setOnClickListener(listener);
        flash_light_btn.setOnClickListener(listener);
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


    /**
     * 设置拍照延时
     */
    private void setTakeDelay() {
        final Integer datas[] = { R.drawable.icon_capture_20_08, R.drawable.icon_capture_20_09, R.drawable.icon_capture_20_10, R.drawable.icon_capture_20_11 };
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
        popupWindow.show();
    }


}
