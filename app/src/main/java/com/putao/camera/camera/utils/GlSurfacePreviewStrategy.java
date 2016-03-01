package com.putao.camera.camera.utils;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.putao.camera.util.Loger;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by jidongdong on 15/5/25.
 */
public class GlSurfacePreviewStrategy implements PreviewStrategy, SurfaceTexture.OnFrameAvailableListener, Camera.PreviewCallback {
    private CameraView cameraView;
    private GLSurfaceView mGLView;
    private CameraSurfaceRenderer mRenderer;
    private Camera mCamera;
    private CameraHandler mCameraHandler;

    public GlSurfacePreviewStrategy(CameraView cameraView) {
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

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

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
