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
import android.content.pm.ActivityInfo;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.widget.FrameLayout;

import com.putao.camera.camera.utils.CameraHost.FailureReason;
import com.putao.camera.camera.view.DrawingFocusView;
import com.putao.camera.util.Loger;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("NewApi")
public class CameraView extends FrameLayout implements AutoFocusCallback {
    static final String TAG = "Pt-Camera";
    //    private PreviewStrategy previewStrategy;
    private PreviewStrategy previewStrategy;
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

    // must call this after constructor, before onResume()
    public void setHost(CameraHost host) {
        this.host = host;
        previewStrategy = new GlSurfacePreviewStrategy(this);
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onResume() {
        Loger.d("CameraView onResume!!!");
        openCamera();
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
        }, 500);
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
//            previewStrategy.invalidateHandler();
            previewDestroyed();
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
        if (camera == null) {
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
        camera.autoFocus(myAutoFocusCallback);
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
                pictureParams.setPictureFormat(ImageFormat.JPEG);
                if (xact.flashMode != null) {
                    pictureParams.setFlashMode(xact.flashMode);
                }
                if (onOrientationChange.isEnabled()) {
                    setCameraPictureOrientation(pictureParams);
                }
                camera.setParameters(xact.host.adjustPictureParameters(xact, pictureParams));
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

    void previewDestroyed() {
        if (camera != null) {
            previewStopped();
            camera.release();
            camera = null;
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
            Loger.d("start preview..........." + previewSize.width + "," + previewSize.height);
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

    // based on
    // http://developer.android.com/reference/android/hardware/Camera.html#setDisplayOrientation(int)
    // and http://stackoverflow.com/a/10383164/115145
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
            camera.setParameters(previewParams);
            if (data != null) {
                Loger.d("picture_camera_take_over------->" + SystemClock.currentThreadTimeMillis());
                new ImageCleanupTask(getContext(), data, cameraId, xact).start();
            }
            if (!xact.useSingleShotMode()) {
                startPreview();
            }
        }
    }

    /**
     * 获取照相机实例
     */
    public Camera getCameraInstance() {
        return camera;
    }

}