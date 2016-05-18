package com.putao.camera.camera.utils;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.putao.camera.camera.gpuimage.GPUImageRenderer;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.Loger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;

/**
 * Created by jidongdong on 15/5/25.
 */
public class GlSurfacePreviewStrategy implements PreviewStrategy, SurfaceTexture.OnFrameAvailableListener, GPUImageRenderer.PreviewCallback {
    private String TAG = GlSurfacePreviewStrategy.class.getSimpleName();
    private Context context;
    private CameraView cameraView;
    private GLSurfaceView mGLView;
    private Camera mCamera;
    private CameraHandler mCameraHandler;

    private float mainRadio = 0;
    private float mainRadioY = 0;
    private int iw;
    private int ih;
    private int screenW, screenH;
    private YMDetector mDetector;
    private Size cameraSize;
    private boolean haveFace = false;
    //    private Mat mYuv;
//    private Mat previewMat;
    private boolean isStartVedio = false;

    private AnimationImageView animationImageView;
    private boolean newRecorderManager=false;

    public void setVedio(boolean isStart ) {
        if (!isStart) {
            recorderManager.stopRecording();
            recorderManager.releaseRecord();
            recorderManager=null;
        } else {
            recorderManager.startRecord();
        }
        isStartVedio = isStart;
    }

    public GlSurfacePreviewStrategy(Context context, CameraView cameraView) {
        this.cameraView = cameraView;
        this.mCameraHandler = new CameraHandler(this);
        this.mGLView = new GLSurfaceView(cameraView.getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.mGLView.setLayoutParams(params);
//        this.cameraView.addView(this.mGLView, 0);
//        this.mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
//        this.mRenderer = new CameraSurfaceRenderer(mCameraHandler);
//        this.mGLView.setRenderer(mRenderer);
//        this.mGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        if (cameraView.getHost().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            mDetector = new YMDetector(context, YMDetector.Config.FACE_270, YMDetector.Config.RESIZE_WIDTH_640);
        } else {
            mDetector = new YMDetector(context, YMDetector.Config.FACE_90, YMDetector.Config.RESIZE_WIDTH_640);
        }
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        screenW = manager.getDefaultDisplay().getWidth();
        screenH = manager.getDefaultDisplay().getHeight();
    }

    public GLSurfaceView getmGLView() {
        return mGLView;
    }

    public boolean getFace() {
        return haveFace;
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
//                mCamera.setPreviewCallback(this);
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

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private YMFace face;
    private boolean detecting = false;
    float[] points;
    private RecorderManager recorderManager = null;

    @Override
    public void onPreviewFrame(final byte[] data, Camera camera) {
        if (recorderManager == null){
            //            recorderManager = new RecorderManager(10 * 1000, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, FileUtils.getSdcardPath() + File.separator + "test.mp4");
            recorderManager = new RecorderManager(20 * 1000, camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, CommonUtils.getOutputVideoFile().getAbsolutePath());
        }

        /*if (isStartVedio) {
            recorderManager.recordVideo(data,camera,mDetector);
        }*/
        if (animationImageView == null) return;

        if (mainRadio == 0 || mainRadioY == 0) {
            iw = camera.getParameters().getPreviewSize().width;
            ih = camera.getParameters().getPreviewSize().height;
            mainRadio = (float) screenW / (float) ih;
            mainRadioY = (float) screenH / (float) iw;
        }

        if (detecting) return;
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                detecting = true;
                face = mDetector.onDetector(data, iw, ih);
                //        YMFace face = mDetector.onDetector(data, iw, ih);
                if (face != null) {
                    float[] landmarks = face.getLandmarks();
                    float[] emotions = face.getEmotions();
                    float[] rect = face.getRect();

                    points = new float[landmarks.length];
                    for (int i = 0; i < landmarks.length / 2; i++) {
                        float x = landmarks[i * 2] * mainRadio;
                        if (cameraView.getHost().getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            x = screenW - x;
                        }
                        float y = landmarks[i * 2 + 1] * mainRadioY;
                        points[i * 2] = x;
                        points[i * 2 + 1] = y;
                    }
                }/*else {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("noface", true);
                    EventBusHelper.post(bundle, PuTaoConstants.HAVE_NO_FACE+"");
                }*/
                detecting = false;
            }
        });
        if (face != null) {
            animationImageView.setVisibility(View.VISIBLE);
            animationImageView.setPositionAndStartAnimation(points);
            haveFace = true;
        } else {
            haveFace = false;
            animationImageView.setVisibility(View.GONE);
        }
        if (isStartVedio) {
            recorderManager.recordVideo(data,camera,mDetector);
        }
    }

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

            }
        });
    }

    @Override
    public void onPause() {

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
