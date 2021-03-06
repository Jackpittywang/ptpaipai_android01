/**
 * Copyright (c) 2013-2014 CommonsWare, LLC
 * Portions Copyright (C) 2007 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.putao.camera.camera.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.media.ExifInterface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.FrameLayout;

import com.putao.camera.camera.utils.CameraHost.FailureReason;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.camera.view.DrawingFocusView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.Loger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressLint("NewApi")
public class CameraView extends FrameLayout implements AutoFocusCallback {
    static final String TAG = "Pt-Camera";
    private GlSurfacePreviewStrategy previewStrategy;
    public GlSurfacePreviewStrategy glSurfacePreviewStrategy;
    private Camera.Size previewSize;
    private Camera camera = null;
    private boolean inPreview = false;
    private CameraHost host = null;
    private OnOrientationChange onOrientationChange = null;
    private int displayOrientation = -1;
    private int outputOrientation = -1;
    public int cameraId = -1;
    private Parameters previewParams = null;
    private boolean isDetectingFaces = false;
    private boolean isAutoFocusing = false;
    private Context mContext;
    // 是否需要显示AR贴纸
    private boolean isShowAR = false;
//    private GPUImage mGPUImage;
//    private GPUImageFilter filetr;

    private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                camera.cancelAutoFocus();
            }
            isAutoFocusing = false;
            drawingView.setVisibility(GONE);
            if (mOnCameraFocusChangeListener != null) {
                mOnCameraFocusChangeListener.onFocusEnd();
            }
        }
    };
    private DrawingFocusView drawingView;
    private onCameraFocusChangeListener mOnCameraFocusChangeListener;

    public CameraView(Context context) {
        super(context);
        onOrientationChange = new OnOrientationChange(context.getApplicationContext());
        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mContext = context;
    }

    public CameraView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        onOrientationChange = new OnOrientationChange(context.getApplicationContext());
        if (context instanceof CameraHostProvider) {
            setHost(((CameraHostProvider) context).getCameraHost());
        } else {
            throw new IllegalArgumentException("To use the two- or " + "three-parameter constructors on CameraView, "
                    + "your activity needs to implement the " + "CameraHostProvider interface");
        }
    }

    public GLSurfaceView getmGLView() {
        return glSurfacePreviewStrategy.getmGLView();
    }
    public boolean getFace() {
        return glSurfacePreviewStrategy.getFace();
    }
    public void setIsStart(boolean isStart) {
        glSurfacePreviewStrategy.setVedio(isStart);
    }

    public GlSurfacePreviewStrategy getPreviewStrategy() {
        return previewStrategy;
    }

    public Camera getCamera() {
        return camera;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setDrawingView(DrawingFocusView view) {
        drawingView = view;
    }

    public void setOnCameraFocusChangeListener(onCameraFocusChangeListener listener) {
        mOnCameraFocusChangeListener = listener;
    }

    public interface onCameraFocusChangeListener {
        void onFocusStart();

        void onFocusEnd();
    }

    public CameraHost getHost() {
        return (host);
    }

    public void setAnmationView(AnimationImageView view) {
        if (glSurfacePreviewStrategy != null) glSurfacePreviewStrategy.setAnimationView(view);
    }


    public void clearAnmationView() {
        if (glSurfacePreviewStrategy != null) glSurfacePreviewStrategy.clearAnimationView();
    }



    // must call this after constructor, before onResume()
    public void setHost(CameraHost host) {
        this.host = host;
        glSurfacePreviewStrategy = new GlSurfacePreviewStrategy(mContext, this);
        previewStrategy = glSurfacePreviewStrategy;
    }

    public void startCamera() {
        openCamera();
        doAutoFocus();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.setPreviewCallbackWithBuffer(null);
            camera.release();
            camera = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onResume() {
        Loger.d("CameraView onResume!!!");
//        openCamera();
        doAutoFocus();
    }

    /**
     * 自动当中聚焦
     */
    void doAutoFocus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int w = getWidth();
                int h = getHeight();
                int left = w / 2 - 4;
                int top = h / 2 - 4;
                int right = w / 2 + 4;
                int bottom = h / 2 + 4;
                Rect rect = new Rect(left, top, right, bottom);
                doSpecialRectFocus(rect);
            }
        }, 2000);
    }

    /**
     * 启动相机
     */
    void openCamera() {
        if (camera == null) {
            cameraId = getHost().getCameraId();
            if (cameraId >= 0) {
                try {
                    Loger.d("open camera");
                    camera = Camera.open(cameraId);
                    if (getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) {
                        onOrientationChange.enable();
                    }
                    setCameraDisplayOrientation();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && getHost() instanceof Camera.FaceDetectionListener) {
                        camera.setFaceDetectionListener((Camera.FaceDetectionListener) getHost());
                    }
                    previewStrategy.bindCamera(camera);
                    addView(previewStrategy.getWidget(), 0);
                } catch (Exception e) {
                    getHost().onCameraFail(FailureReason.UNKNOWN);
                }
            } else {
                getHost().onCameraFail(FailureReason.NO_CAMERAS_REPORTED);
            }
        }
    }

    public void onPause() {
        if (camera != null) {
            previewStrategy.onPause();
        }
        removeView(previewStrategy.getWidget());
        onOrientationChange.disable();
    }

    public void onDestory() {
        previewStrategy.onDestory();
    }

    /**
     * 获取合适的相机预览大小
     *
     * @param width  期望宽
     * @param height 期望高
     */
    public void setCameraPreviewSize(int width, int height) {
        if (camera != null) {
            Camera.Size newSize = null;
            try {
                newSize = getHost().getPreferredPreviewSizeForVideo(getDisplayOrientation(), width, height, camera.getParameters(), null);
                if (newSize == null || newSize.width * newSize.height < 65536) {
                    newSize = getHost().getPreviewSize(getDisplayOrientation(), width, height, camera.getParameters());
                }
            } catch (Exception e) {
                Loger.e(getClass().getSimpleName() + "Could not work with camera parameters?", e);
                // TODO get this out to library clients
            }
            if (newSize != null) {
                if (previewSize == null) {
                    previewSize = newSize;
                } else if (previewSize.width != newSize.width || previewSize.height != newSize.height) {
                    if (inPreview) {
                        stopPreview();
                    }
                    previewSize = newSize;
                    Loger.d("set preview size");
//                    previewStrategy.setCameraPreviewSize(previewSize.width, previewSize.height);
                }
            } else {
                Loger.d("get the camera PreviewSize is failed!!!");
            }
        } else {
            Loger.d("camera is null");
        }
    }


    public void doSpecialRectFocus(final Rect tfocusRect) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            return;
        }
        if (camera == null || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        final Rect targetFocusRect = new Rect(tfocusRect.left * 2000 / getWidth() - 1000, tfocusRect.top * 2000 / getHeight() - 1000,
                tfocusRect.right * 2000 / getWidth() - 1000, tfocusRect.bottom * 2000 / getHeight() - 1000);
        final List<Camera.Area> focusList = new ArrayList<Camera.Area>();
        Camera.Area focusArea = new Camera.Area(targetFocusRect, 1000);
        focusList.add(focusArea);
        Parameters para = camera.getParameters();
        if (para.getMaxNumDetectedFaces() > 0) {
            try {
                para.setFocusAreas(focusList);
                para.setMeteringAreas(focusList);
                camera.setParameters(para);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mOnCameraFocusChangeListener != null)
            mOnCameraFocusChangeListener.onFocusStart();
        isAutoFocusing = true;
        try {
            camera.autoFocus(myAutoFocusCallback);
        }catch (Exception e){

        }

        if (drawingView != null) {
            drawingView.setHaveTouch(true, tfocusRect);
            drawingView.startAnimal();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAutoFocusing = false;
                if (mOnCameraFocusChangeListener != null) {
                    mOnCameraFocusChangeListener.onFocusEnd();
                }
            }
        }, 1500);
    }

    public int getDisplayOrientation() {
        return (displayOrientation);
    }

    public void lockToLandscape(boolean enable) {
        if (enable) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            onOrientationChange.enable();
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            onOrientationChange.disable();
        }
    }

    public void restartPreview() {
        if (inPreview) {
            stopPreview();
        }
        startPreview();
    }

    public void takePicture(boolean needBitmap, boolean needByteArray) {
        PictureTransaction xact = new PictureTransaction(getHost());
        takePicture(xact.needBitmap(needBitmap).needByteArray(needByteArray));
    }

    /**
     * 返回是否正在预览模式
     *
     * @return
     */
    public boolean isInPreview() {
        return inPreview;
    }

    public void takePicture(final PictureTransaction xact) {
        if (inPreview) {
            if (isAutoFocusing) {
                throw new IllegalStateException("Camera cannot take a picture while auto-focusing");
            } else {
                previewParams = camera.getParameters();
                Parameters pictureParams = camera.getParameters();
                Camera.Size pictureSize = xact.host.getPictureSize(xact, pictureParams);
                pictureParams.setPictureSize(pictureSize.width, pictureSize.height);
                Log.i(TAG, "width :" + pictureSize.width + " height:" + pictureSize.height);
                pictureParams.setPictureFormat(ImageFormat.JPEG);
                pictureParams.setPreviewFormat(ImageFormat.NV21);
                setOptimalPreviewSize(pictureParams, 960, 960);
                if (xact.flashMode != null) {
                    pictureParams.setFlashMode(xact.flashMode);
                }
                if (onOrientationChange.isEnabled()) {
                    setCameraPictureOrientation(pictureParams);
                }
                String model = android.os.Build.MODEL.toLowerCase();
                String brand = Build.BRAND.toLowerCase();
                // 所有华为的机器不要做set处理,
                if (model.contains("huawei") || brand.contains("huawei") || model.contains("cl00") || model.contains("honor")) {
                } else {
                    camera.setParameters(xact.host.adjustPictureParameters(xact, pictureParams));
                }

                /*try {
                    camera.setParameters(xact.host.adjustPictureParameters(xact, pictureParams));
                } catch (Exception e) {
                    android.util.Log.e(getClass().getSimpleName(), "Exception taking a picture", e);
                }*/

                xact.cameraView = this;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            camera.takePicture(xact, null, new PictureTransactionCallback(xact));
                        } catch (Exception e) {
                            android.util.Log.e(getClass().getSimpleName(), "Exception taking a picture", e);
                            // TODO get this out to library clients
                        }
                    }
                }, xact.host.getDeviceProfile().getPictureDelay());
                inPreview = false;
            }
        } else {
            throw new IllegalStateException("Preview mode must have started before you can take a picture");
        }
    }


    private void setOptimalPreviewSize(Camera.Parameters cameraParams,
                                       int targetWidth, int targetHeight) {
        List<Camera.Size> supportedPreviewSizes = cameraParams
                .getSupportedPreviewSizes();
        if (null == supportedPreviewSizes) {
        } else {
            Camera.Size optimalSize = null;
            double minDiff = 1.7976931348623157E308D;
            Iterator mIterator = supportedPreviewSizes.iterator();

            while (mIterator.hasNext()) {
                Camera.Size size = (Camera.Size) mIterator.next();
                if ((double) Math.abs(size.width - targetWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = (double) Math.abs(size.width - targetWidth);
                }
            }

            int iw = optimalSize.width;
            int ih = optimalSize.height;

            cameraParams.setPreviewSize(iw, ih);
        }
    }


    public void takePicture(final PictureTransaction xact, boolean flag) {
        this.isShowAR = flag;
        takePicture(xact);
    }

    public void autoFocus() {
        if (inPreview) {
            camera.autoFocus(this);
            isAutoFocusing = true;
        }
    }

    public void cancelAutoFocus() {
        camera.cancelAutoFocus();
    }

    public boolean isAutoFocusAvailable() {
        return (inPreview);
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {
        isAutoFocusing = false;
        if (getHost() instanceof AutoFocusCallback) {
            getHost().onAutoFocus(success, camera);
        }
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void startFaceDetection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && camera != null && !isDetectingFaces
                && camera.getParameters().getMaxNumDetectedFaces() > 0) {
            camera.startFaceDetection();
            isDetectingFaces = true;
        }
    }

    public void stopFaceDetection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && camera != null && isDetectingFaces) {
            try {
                camera.stopFaceDetection();
            } catch (Exception e) {
                // TODO get this out to hosting app
            }
            isDetectingFaces = false;
        }
    }

    /**
     * 停止预览
     */
    private void previewStopped() {
        if (inPreview) {
            stopPreview();
        }
    }

    /**
     * 2073600
     * 启动相机预览
     */
    public void startPreview() {
        try {
            if (camera != null) {
                Parameters parameters = camera.getParameters();
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                previewStrategy.setPreviewSize(previewSize.width, previewSize.height);
                camera.setParameters(getHost().getExposureCompensation(parameters));
                camera.startPreview();
                inPreview = true;
                getHost().autoFocusAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止相机预览
     */
    private void stopPreview() {
        inPreview = false;
        getHost().autoFocusUnavailable();
        camera.setPreviewCallback(null);
        camera.stopPreview();
    }


    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        DisplayMetrics dm = new DisplayMetrics();
        Camera.getCameraInfo(cameraId, info);
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayOrientation = (info.orientation + degrees) % 360;
            displayOrientation = (360 - displayOrientation) % 360;
        } else {
            displayOrientation = (info.orientation - degrees + 360) % 360;
        }

        boolean wasInPreview = inPreview;
        if (inPreview) {
            stopPreview();
        }
        Loger.d("displayOrientation:" + displayOrientation);
        camera.setDisplayOrientation(displayOrientation);

        //Log.i("QQQ", "set display orientation:" + displayOrientation);
        if (wasInPreview) {
            startPreview();
        }
    }

    // need modify
    private void setCameraPictureOrientation(Parameters params) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int mOrientation = OrientationUtil.getOrientation();
        int outputOrientation = 0;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            switch (mOrientation) {
                case 0:
                    outputOrientation = 270;
                    break;
                case 90:
                    outputOrientation = 180;
                    break;
                case 180:
                    outputOrientation = 90;
                    break;
                case 270:
                    outputOrientation = 0;
                    break;
                default:
                    break;
            }
        } else {
            switch (mOrientation) {
                case 0:
                    outputOrientation = 90;
                    break;
                case 90:
                    outputOrientation = 180;
                    break;
                case 270:
                    outputOrientation = 0;
                    break;
                case 180:
                    outputOrientation = 270;
                    break;
                default:
                    break;
            }
        }
        //		if (lastPictureOrientation != outputOrientation) {
        Loger.i("outputOrientation:" + outputOrientation);
        params.setRotation(outputOrientation);
        //			lastPictureOrientation = outputOrientation;
        //		}
    }

    // based on:
    // http://developer.android.com/reference/android/hardware/Camera.Parameters.html#setRotation(int)
    private int getCameraPictureRotation(int orientation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = 0;
        orientation = (orientation + 45) / 90 * 90;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            rotation = (info.orientation - orientation + 360) % 360;
        } else { // back-facing camera
            rotation = (info.orientation + orientation) % 360;
        }
        //Log.i("QQQ", "camera picture roation is:"+rotation);
        return (rotation);
    }

    Activity getActivity() {
        return ((Activity) getContext());
    }

    private class OnOrientationChange extends OrientationEventListener {
        private boolean isEnabled = false;

        public OnOrientationChange(Context context) {
            super(context);
            disable();
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (camera != null && orientation != ORIENTATION_UNKNOWN) {
                int newOutputOrientation = getCameraPictureRotation(orientation);
                if (newOutputOrientation != outputOrientation) {
                    outputOrientation = newOutputOrientation;
                    try {
                        Parameters params = camera.getParameters();
                        params.setRotation(outputOrientation);
                        //Log.i("QQQ", "orientation change:" + outputOrientation);
                        camera.setParameters(params);
                    } catch (Exception e) {
                        Loger.d(getClass().getSimpleName() + "Exception updating camera parameters in orientation change", e);
                        // TODO: get this info out to hosting app
                    }
                }
            }
        }

        @Override
        public void enable() {
            isEnabled = true;
            super.enable();
        }

        @Override
        public void disable() {
            isEnabled = false;
            super.disable();
        }

        boolean isEnabled() {
            return isEnabled;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();
            Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2), (int) (y + touchMinor / 2));
            doSpecialRectFocus(touchRect);
        }
        performClick();
        return true;
    }

    private class PictureTransactionCallback implements Camera.PictureCallback {
        PictureTransaction xact = null;

        PictureTransactionCallback(PictureTransaction xact) {
            this.xact = xact;
        }

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // 动态贴纸之后，如果有动态贴纸就出动态贴纸的保存，否则出图像编辑的页面
            // 先保存临时文件
            imagePath = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "temp.jpg";
            Bitmap tempBitmap = BitmapHelper.Bytes2Bimap(data);
            Bitmap saveBitmap = null;
            if (tempBitmap.getHeight() < tempBitmap.getWidth()) {
                Log.e("onPictureTaken", "onPictureTaken: ");
                saveBitmap = BitmapHelper.orientBitmap(tempBitmap, ExifInterface.ORIENTATION_ROTATE_90);
            } else saveBitmap = tempBitmap;

            BitmapHelper.saveBitmap(saveBitmap, imagePath);
            saveBitmap.recycle();
            tempBitmap.recycle();
            if (isShowAR == true) {
                handler.sendEmptyMessageDelayed(0x001, 100);
            } else {
                handler.sendEmptyMessageDelayed(0x002, 0);
            }
        }
    }

    private String imagePath = "";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x001) {
                Bundle bundle = new Bundle();
                bundle.putString("imagePath", imagePath);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.OPEN_AR_SHOW_ACTIVITY, bundle));
//                EventBusHelper.post(bundle, PuTaoConstants.OPEN_AR_SHOW_ACTIVITY+"");
            } else if (msg.what == 0x002) {
                Intent intent = new Intent(mContext, PhotoEditorActivity.class);
                intent.putExtra("photo_data", imagePath);
                intent.putExtra("from", "cameraview");
                mContext.startActivity(intent);
            }
        }
    };

    /**
     * 获取照相机实例
     */
    public Camera getCameraInstance() {
        return camera;
    }

}