package com.putao.camera.camera.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.util.Loger;

import android.hardware.Camera.Size;


import java.io.IOException;
import java.lang.ref.WeakReference;

import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;

/**
 * Created by jidongdong on 15/5/25.
 */
public class GlSurfacePreviewStrategy implements PreviewStrategy, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private String TAG = GlSurfacePreviewStrategy.class.getName();
    private Context context;
    private CameraView cameraView;
    private GLSurfaceView mGLView;
    private CameraSurfaceRenderer mRenderer;
    private Camera mCamera;
    private CameraHandler mCameraHandler;

    private float mainRadio = 0;
    private int iw;
    private int ih;
    private int screenW, screenH;
    private YMDetector mDetector;
    private Size cameraSize;
//    private Mat mYuv;
//    private Mat previewMat;

    private AnimationImageView animationImageView;

    public GlSurfacePreviewStrategy(Context context, CameraView cameraView) {
        this.cameraView = cameraView;
        this.mCameraHandler = new CameraHandler(this);
        this.mGLView = new GLSurfaceView(cameraView.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.mGLView.setLayoutParams(params);
//        this.cameraView.addView(this.mGLView, 0);
        this.mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        this.mRenderer = new CameraSurfaceRenderer(mCameraHandler);
        this.mGLView.setRenderer(mRenderer);
        this.mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        if (cameraView.getHost().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDetector = new YMDetector(context, YMDetector.Config.FACE_270, YMDetector.Config.RESIZE_WIDTH_640);
        } else {
            mDetector = new YMDetector(context, YMDetector.Config.FACE_90, YMDetector.Config.RESIZE_WIDTH_640);
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenW = dm.widthPixels;
        screenH = dm.heightPixels;
    }

    public void setAnimationView(AnimationImageView view) {
        animationImageView = view;
    }

    public void clearAnimationView() {
        animationImageView = null;
    }

    void handleSetSurfaceTexture(SurfaceTexture st) {
        try {
            Loger.d("set PreviewTexture:" + st + "camera:" + mCamera);
            if (mCamera != null) {
                mCamera.setPreviewCallback(this);
                st.setOnFrameAvailableListener(this);
                mCamera.setPreviewTexture(st);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void handleSetTextureSize(PtTextureSize size) {
        Loger.d("set CameraPreviewSize...." + size.width + "," + size.height);
        cameraView.setCameraPreviewSize(size.width, size.height);
        cameraView.startPreview();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // Loger.d("onFrameAvailable" + surfaceTexture);
        this.mGLView.requestRender();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        // Loger.d("onPreviewFrame  ....   .... .. animationImageView is:"+animationImageView);

//        if (animationImageView == null) return;

//        long startTime = System.currentTimeMillis();
//        long gap = 0;
//        if (cameraSize == null)
//            cameraSize = camera.getParameters().getPreviewSize();
//
//        int width = cameraSize.width;
//        int height = cameraSize.height;
//
//        if(mYuv == null) mYuv = new Mat( height + height/2, width, CvType.CV_8UC1 );
//        mYuv.put(0, 0, data);
//
//        if(previewMat == null) previewMat = new Mat();
//        Imgproc.cvtColor(mYuv, previewMat, Imgproc.COLOR_YUV420sp2RGB, 4);
//        Imgproc.cvtColor(previewMat, previewMat, Imgproc.COLOR_RGB2GRAY);
//
//        Core.flip(previewMat.t(), previewMat, 1);
//        // Highgui.imwrite("/mnt/sdcard/test.jpg", previewMat);
//
////        gap = System.currentTimeMillis() -startTime;
////        Log.i("PaiPai", "gap time 111111 is:" + gap);
////        startTime = System.currentTimeMillis();
//
//        int [] points = NativeCode.FaceDetectAndFlandmarks(previewMat);
//        Highgui.imwrite("/mnt/sdcard/test.jpg", previewMat);
//
//
//        animationImageView.setPositionAndStartAnimation(points);

        if (animationImageView == null) return;

        if (mainRadio == 0) {
            iw = camera.getParameters().getPreviewSize().width;
            ih = camera.getParameters().getPreviewSize().height;
            float hh = ih;
            mainRadio = screenW / hh;
        }
        YMFace face = mDetector.onDetector(data, iw, ih);
        if (face != null) {
            float[] landmarks = face.getLandmarks();
            float[] emotions = face.getEmotions();
            float[] rect = face.getRect();

            String resuil = "";
            for (int i = 0; i < emotions.length; i++) {
                if (i == 3 || i == 2) continue;
                resuil += emo[i] + "--" + (int) (emotions[i] * 100) + "  \n";
            }

            float[] points = new float[landmarks.length];
            for (int i = 0; i < landmarks.length / 2; i++) {
                float x = landmarks[i * 2] * mainRadio;
                if (cameraView.getHost().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    x = screenW - x;
                }
                float y = landmarks[i * 2 + 1] * mainRadio;
                points[i * 2] = x;
                points[i * 2 + 1] = y;
            }


            animationImageView.setPositionAndStartAnimation(points);
        }else{
//            cameraView.clearAnmationView();
        }


    }

    private String[] emo = {"喜悦", "悲伤", "", "", "惊讶", "厌怒", "正常"};

    public static class PtTextureSize {
        public int width;
        public int height;

        public PtTextureSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public void bindCamera(Camera camera) {
        mCamera = camera;
        Loger.d("bind camera.");
    }


    public void invalidateHandler() {
        mCameraHandler.invalidateHandler();
    }


    @Override
    public View getWidget() {
        return mGLView;
    }

    @Override
    public void setPreviewSize(final int w, final int h) {
        mGLView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.setCameraPreviewSize(w, h);
            }
        });
    }

    @Override
    public void onPause() {
        mRenderer.notifyPausing();
    }

    @Override
    public void onDestory() {
        invalidateHandler();
    }


    static class CameraHandler extends Handler {
        public static final int MSG_SET_SURFACE_TEXTURE = 0;
        public static final int MSG_SET_SURFACE_TEXTURE_SIZE = 1;

        // Weak reference to the Activity; only access this from the UI thread.
        private WeakReference<GlSurfacePreviewStrategy> mWeakStrategy;

        public CameraHandler(GlSurfacePreviewStrategy strategy) {
            mWeakStrategy = new WeakReference<GlSurfacePreviewStrategy>(strategy);
        }

        /**
         * Drop the reference to the activity.  Useful as a paranoid measure to ensure that
         * attempts to access a stale Activity through a handler are caught.
         */
        public void invalidateHandler() {
            mWeakStrategy.clear();
        }

        @Override  // runs on UI thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Loger.d("CameraHandler [" + this + "]: what=" + what);

            GlSurfacePreviewStrategy strategy = mWeakStrategy.get();
            if (strategy == null) {
                Loger.w("CameraHandler.handleMessage: strategy is null");
                return;
            }

            switch (what) {
                case MSG_SET_SURFACE_TEXTURE:
                    strategy.handleSetSurfaceTexture((SurfaceTexture) inputMessage.obj);
                    break;
                case MSG_SET_SURFACE_TEXTURE_SIZE:
                    strategy.handleSetTextureSize((PtTextureSize) inputMessage.obj);
                    break;
                default:
                    throw new RuntimeException("unknown msg " + what);
            }
        }
    }

}
