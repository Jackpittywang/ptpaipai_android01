
package com.putao.camera.movie;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.camera.PCameraFragment;
import com.putao.camera.camera.PCameraFragment.TakePictureListener;
import com.putao.camera.camera.PCameraFragment.flashModeCode;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.utils.RoundUtil;
import com.putao.camera.camera.view.AlbumButton;
import com.putao.camera.camera.view.RedPointBaseButton;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.movie.model.MovieCaption;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.PhotoLoaderHelper;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;

public class MovieCameraActivity extends BaseActivity implements OnClickListener {
    private PCameraFragment std = null;
    private PCameraFragment ffc = null;
    private PCameraFragment current = null;
    private boolean hasTwoCameras = (Camera.getNumberOfCameras() > 1);
    private Button flash_light_btn, switch_camera_btn, take_photo_btn, btn_enhance_switch;
    private RedPointBaseButton camera_back_btn;
    private AlbumButton album_btn;
    private OrientationEventListener mOrientationEvent;
    private int mOrientation = 0;
    private int mOrientationCompensation = 0;
    private RelativeLayout camera_top_rl;
    private ImageView orientationView;
    private LinearLayout camera_bottom;
    private boolean hideOrientation;
    private String takePhotoReason = "movie";
    ProgressDialog pg;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_movie_camera;
    }

    @Override
    public void doInitSubViews(View view) {
        fullScreen(true);
        camera_top_rl = queryViewById(R.id.camera_top_rl);
        flash_light_btn = queryViewById(R.id.flash_light_btn);
        switch_camera_btn = queryViewById(R.id.switch_camera_btn);
        camera_back_btn = queryViewById(R.id.camera_back_btn);
        take_photo_btn = queryViewById(R.id.take_photo_btn);
        orientationView = (ImageView) findViewById(R.id.orientation);
        camera_bottom = queryViewById(R.id.camera_bottom);
        album_btn = queryViewById(R.id.album_btn);
        btn_enhance_switch = queryViewById(R.id.btn_enhance_switch);
        addOnClickListener(switch_camera_btn, flash_light_btn, album_btn, camera_back_btn, take_photo_btn, orientationView, btn_enhance_switch);
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
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            takePhotoReason = bundle.getString("reason", "movie");
        }
        if (isReasonEdit() || isReasonCollage() || takePhotoReason.equals("connect_photo")) {
            album_btn.setVisibility(View.INVISIBLE);
        }
        EventBus.getEventBus().register(this);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pg.dismiss();
            PhotoInfo info = PhotoLoaderHelper.getInstance(MovieCameraActivity.this).getLastPhotoInfo();
            Bundle bundle = new Bundle();
            if (isReasonMovie()) {
                bundle.putString("photo_data", info._DATA);
                ActivityHelper.startActivity(MovieCameraActivity.this, MoviePhotoCutActivity.class, bundle);
            } else if (isReasonEdit()) {
                bundle.putString("photo_data", info._DATA);
                ActivityHelper.startActivity(MovieCameraActivity.this, PhotoEditorActivity.class, bundle);
                mActivity.finish();
            } else if (isReasonCollage()) {
                bundle.putString("photo_data", info._DATA);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.COLLAGE_CAMERA_FINISH, bundle));
                mActivity.finish();
            } else if (takePhotoReason.equals("connect_photo")) {
                bundle.putString("photo_path", info._DATA);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.EVENT_CONNECT_PHOTO_SELECT, bundle));
                mActivity.finish();
            } else if (takePhotoReason.equals("collage_photo")) {
                bundle.putString("photo_path", info._DATA);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.EVENT_COLLAGE_PHOTO_SELECT, bundle));
                mActivity.finish();
            }
        }
    };
    TakePictureListener photoListener = new TakePictureListener() {
        @Override
        public void saved(final Bitmap photo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    album_btn.setImageBitmap(photo, true);
                    take_photo_btn.setEnabled(true);
                    mHandler.sendEmptyMessageDelayed(0, 300);
                }
            });
        }

        @Override
        public void focusChanged(boolean isfocusing) {
            if (take_photo_btn != null)
                take_photo_btn.setEnabled(!isfocusing);
        }
    };

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
        mOrientationEvent = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                mOrientation = RoundUtil.roundOrientation(orientation, mOrientation);
                int orientationCompensation = (mOrientation + RoundUtil.getDisplayRotation(MovieCameraActivity.this)) % 360;
                if (mOrientationCompensation != orientationCompensation) {
                    mOrientationCompensation = orientationCompensation;
                    OrientationUtil.setOrientation(mOrientationCompensation == -1 ? 0 : mOrientationCompensation);
                    setOrientation(OrientationUtil.getOrientation(), true, flash_light_btn, switch_camera_btn, album_btn, camera_back_btn,
                            take_photo_btn);
                    setOrientation(OrientationUtil.getOrientation(), true);
                }
            }
        };
        hideOrientation = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_MOVIEW_CAMERA_ORIENTATION_HIDE, false);
        if (isReasonMovie()) {
            if (hideOrientation) {
                orientationView.setVisibility(View.INVISIBLE);
            } else {
                orientationView.setVisibility(View.VISIBLE);
            }
        }
        //初始化文字
        MovieCaption.newInstance();
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
                showFlashMenu(this, v);
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
                break;
            case R.id.take_photo_btn:
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_TAKE_PHOTO);
                take_photo_btn.setEnabled(false);
                current.takeSimplePicture();
                pg = new ProgressDialog(mContext);
                pg.setMessage("正在加载中...");
                pg.show();
                break;
            case R.id.album_btn:
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_PHOTO_LIST);
                Bundle bundle = new Bundle();
                if (isReasonMovie()) {
                    bundle.putBoolean("from_movie", true);
                }
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class, bundle);
                //                this.finish();
                break;
            case R.id.camera_back_btn:
                finish();
                break;
            case R.id.orientation:
                SharedPreferencesHelper.saveBooleanValue(this, PuTaoConstants.PREFERENC_MOVIEW_CAMERA_ORIENTATION_HIDE, true);
                orientationView.setVisibility(View.INVISIBLE);
                hideOrientation = !hideOrientation;
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            setCameraRatioThreeToFour();
        }
    }

    void setCameraRatioThreeToFour() {
        int camera_width = DisplayHelper.getScreenWidth();
        int btm_bar_height_new = 0;
        int top_bar_height = camera_top_rl.getHeight();
        int camera_height = (int) ((float) camera_width * 4 / 3);
        btm_bar_height_new = DisplayHelper.getScreenHeight() - top_bar_height - camera_height;
        RelativeLayout.LayoutParams btm_params = (RelativeLayout.LayoutParams) camera_bottom.getLayoutParams();
        btm_params.height = btm_bar_height_new;
        camera_bottom.setLayoutParams(btm_params);
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
        pw.showAtLocation(parent, Gravity.NO_GRAVITY, location[0], location[1] - pw.getHeight() + 20 + camera_top_rl.getHeight());
        Button flash_auto_btn = (Button) menuView.findViewById(R.id.flash_auto_btn);
        Button flash_on_btn = (Button) menuView.findViewById(R.id.flash_on_btn);
        Button flash_off_btn = (Button) menuView.findViewById(R.id.flash_off_btn);
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
                }
            }
        };
        if (current.getCurrentModeCode() == flashModeCode.auto) {
            flash_auto_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.photograph_flashmodeautoa));
        } else if (current.getCurrentModeCode() == flashModeCode.on) {
            flash_on_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.photograph_flashmodeb));
        } else if (current.getCurrentModeCode() == flashModeCode.off) {
            flash_off_btn.setBackgroundDrawable(getResources().getDrawable(R.drawable.photograph_flashmodeenablea));
        }
        flash_auto_btn.setOnClickListener(listener);
        flash_on_btn.setOnClickListener(listener);
        flash_off_btn.setOnClickListener(listener);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
        }
    }

    public void setFlashResource(flashModeCode code) {
        int resId = 0;
        if (code == flashModeCode.auto) {
            resId = R.drawable.photograph_flashmodeautoc_auto;
        } else if (code == flashModeCode.on) {
            resId = R.drawable.photograph_flashmodeaa;
        } else if (code == flashModeCode.off) {
            resId = R.drawable.photograph_flashmodeenablec;
        }
        flash_light_btn.setBackgroundDrawable(getResources().getDrawable(resId));
    }

    float transDegree = 0;

    public void setOrientation(int orientation, boolean isAnimator, View... views) {
        int mFromRotation = 0;
        int mToRotation = 0;
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
        if (isReasonMovie()) {
            if (!hideOrientation) {
                if (mOrientation == 90 || mOrientation == 270) {
                    orientationView.setVisibility(View.INVISIBLE);
                } else {
                    orientationView.setVisibility(View.VISIBLE);
                }
            }
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

    private boolean isReasonMovie() {
        if (takePhotoReason.compareTo("movie") == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isReasonEdit() {
        if (takePhotoReason.compareTo("edit") == 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isReasonCollage() {
        if (takePhotoReason.compareTo("collage") == 0) {
            return true;
        } else {
            return false;
        }
    }
}
