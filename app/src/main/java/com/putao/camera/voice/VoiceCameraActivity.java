
package com.putao.camera.voice;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.PCameraFragment;
import com.putao.camera.camera.PCameraFragment.TakePictureListener;
import com.putao.camera.camera.PCameraFragment.flashModeCode;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.utils.RoundUtil;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;

public class VoiceCameraActivity extends BaseActivity implements OnClickListener {
    private PCameraFragment std = null;
    private PCameraFragment ffc = null;
    private PCameraFragment current = null;
    private boolean hasTwoCameras = (Camera.getNumberOfCameras() > 1);
    private Button flash_light_btn, switch_camera_btn, take_photo_btn;
    //    private CircleControlPanelView circle_control_panel;
    private FrameLayout container;
    private OrientationEventListener mOrientationEvent;
    private int mOrientation = 0;
    private int mOrientationCompensation = 0;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_voice_camera;
    }

    @Override
    public void doInitSubViews(View view) {
        fullScreen(true);
        flash_light_btn = (Button) findViewById(R.id.flash_light_btn);
        switch_camera_btn = (Button) findViewById(R.id.switch_camera_btn);
        take_photo_btn = (Button) findViewById(R.id.take_photo_btn);
        addOnClickListener(switch_camera_btn, flash_light_btn, take_photo_btn);
        if (hasTwoCameras) {
            std = PCameraFragment.newInstance(false);
            ffc = PCameraFragment.newInstance(true);
            std.setSaveLocalPhotoState(false);//本地不保存照片
            ffc.setSaveLocalPhotoState(false);
            std.setPhotoSaveListener(photoListener);
            ffc.setPhotoSaveListener(photoListener);
        } else {
            std = PCameraFragment.newInstance(false);
            switch_camera_btn.setVisibility(View.GONE);
            std.setPhotoSaveListener(photoListener);
            ffc.setPhotoSaveListener(photoListener);
        }
        current = std;
        getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
        //        setAlbumBtn();
    }

    TakePictureListener photoListener = new TakePictureListener() {
        @Override
        public void saved(final Bitmap photo) {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    take_photo_btn.setEnabled(true);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("PhotoInfo", photo);
                    EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.VOICE_PHOTO_TAKE_PHOTO_BACK, bundle));
                    finish();
                }
            });
        }

        @Override
        public void focusChanged(boolean isfocusing) {
            // TODO Auto-generated method stub
            if (take_photo_btn != null)
                take_photo_btn.setEnabled(!isfocusing);
        }
    };

    @Override
    public void doInitData() {
        mOrientationEvent = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                // TODO Auto-generated method stub
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                mOrientation = RoundUtil.roundOrientation(orientation, mOrientation);
                int orientationCompensation = (mOrientation + RoundUtil.getDisplayRotation(VoiceCameraActivity.this)) % 360;
                if (mOrientationCompensation != orientationCompensation) {
                    mOrientationCompensation = orientationCompensation;
                    OrientationUtil.setOrientation(mOrientationCompensation == -1 ? 0 : mOrientationCompensation);
                    setOrientation(OrientationUtil.getOrientation(), true, flash_light_btn, switch_camera_btn);
                }
            }
        };
        //        mMarkViewList = new ArrayList<WaterMarkView>();
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mOrientationEvent.enable();
        //        resetAlbumPhoto();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mOrientationEvent.disable();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.flash_light_btn:
                switchFlashMode();
                break;
            case R.id.switch_camera_btn:
                if (hasTwoCameras) {
                    current = (current == std) ? ffc : std;
                    flash_light_btn.setVisibility((current == std) ? View.VISIBLE : View.GONE);
                    getFragmentManager().beginTransaction().replace(R.id.container, current).commit();
                    /*
                     * Umeng事件统计
                     */
//                    if (current == std)
//                    {
//                        UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_OUT_CAMERA);
//                    }
//                    else
//                    {
//                        UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_SELF_CAMERA);
//                    }
                }
                //                ClearWaterMark();
                break;
            case R.id.take_photo_btn:
//                UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_TAKE_PHOTO);
                take_photo_btn.setEnabled(false);
                current.takeSimplePicture();
                //                for (WaterMarkView watermark : mMarkViewList)
                //                {
                //                    if (watermark.getIsMoved())
                //                    {
                //                        UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_MOVE);
                //                    }
                //                    if (watermark.isZoomedIn())
                //                    {
                //                        UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_ZOOMIN);
                //                    }
                //                    if (watermark.isZoomedOut())
                //                    {
                //                        UmengAnalysisHelper.getInstance().onEvent(mActivity, UmengAnalysisConstants.UMENG_COUNT_EVENT_WATER_MARK_ZOOMOUT);
                //                    }
                //                }
                break;
            default:
                break;
        }
    }

    private void switchFlashMode() {
        if (current == std) {
            flashModeCode code = current.getCurrentModeCode();
//            if (code == flashModeCode.on)
//            {
//                UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_NO_FLASH);
//                current.setflashMode(flashModeCode.off);
//            }
//            else if (code == flashModeCode.off)
//            {
//                UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_AUTO_FLASH);
//                current.setflashMode(flashModeCode.auto);
//            }
//            else if (code == flashModeCode.auto)
//            {
//                UmengAnalysisHelper.onEvent(this, UmengAnalysisConstants.UMENG_COUNT_EVENT_FLASH);
//                current.setflashMode(flashModeCode.on);
//            }
            setFlashResource(current.getCurrentModeCode());
        }
    }

    public void setFlashResource(flashModeCode code) {
        int resId = 0;
        if (code == flashModeCode.auto) {
            resId = R.drawable.camera_flash_auto;
        } else if (code == flashModeCode.on) {
            resId = R.drawable.camera_flash_on;
        } else if (code == flashModeCode.off) {
            resId = R.drawable.camera_flash_off;
        }
        flash_light_btn.setBackgroundDrawable(getResources().getDrawable(resId));
    }

    public void setOrientation(int orientation, boolean isAnimator, View... views) {
        float degree = 0;
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
        for (int i = 0; i < views.length; i++) {
            int mFromRotation = 0;
            int mToRotation = 0;
            if (mOrientation == 0) {
                mFromRotation = (int) views[i].getRotation();
                mToRotation = 0;
                setViewRotation(mFromRotation, mToRotation, views[i]);
            } else if (mOrientation == 270) {
                mFromRotation = (int) views[i].getRotation();
                mToRotation = 90;
                setViewRotation(mFromRotation, mToRotation, views[i]);
            }
            //            else if (mOrientation == 180)
            //            {
            //                mFromRotation = (int) views[i].getRotation();
            //             
            //                setViewRotation11(mFromRotation, mToRotation, views[i]);
            //            }
            else if (mOrientation == 90) {
                mFromRotation = (int) views[i].getRotation();
                mToRotation = -90;
                setViewRotation(mFromRotation, mToRotation, views[i]);
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
}
