/**
 * Copyright (c) 2013 CommonsWare, LLC
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.putao.camera.util.Loger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

class SurfacePreviewStrategy implements Camera.PreviewCallback,
        SurfaceHolder.Callback, PreviewStrategy {
    private final CameraView cameraView;
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private PreviewRendererThread mRenderer;
    private SurfaceTexture mSurfaceTexture;
    private int TextureId = 0x10;
    private Camera mCamera;
    private int[] bitmapBuffer;
    private int PREVIEW_WIDTH, PREVIEW_HEIGHT;
    private ImageView imageView;

    @SuppressWarnings("deprecation")
    SurfacePreviewStrategy(CameraView cameraView) {
        this.cameraView = cameraView;
        preview = new SurfaceView(cameraView.getContext());
        previewHolder = preview.getHolder();
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        previewHolder.addCallback(this);
        mSurfaceTexture = new SurfaceTexture(TextureId);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.preview.setLayoutParams(params);
        imageView = new ImageView(cameraView.getContext());
        imageView.setLayoutParams(params);
        cameraView.addView(imageView);
        cameraView.addView(preview, 0);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mRenderer = new PreviewRendererThread(holder);
        mRenderer.start();
        cameraView.openCamera();
    }

    /**
     * @param camera
     */
    public void bindCamera(Camera camera) {
        mCamera = camera;
        try {
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.setPreviewCallback(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public View getWidget() {
        return preview;
    }

    @Override
    public void setPreviewSize(int w, int h) {
        PREVIEW_WIDTH = w;
        PREVIEW_HEIGHT = h;
    }

    public void onPause() {
        mRenderer.half();
    }

    @Override
    public void onDestory() {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
        cameraView.setCameraPreviewSize(width, height);
        cameraView.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Loger.d("surfaceDestroyed");
        cameraView.previewDestroyed();
        mRenderer.half();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (bitmapBuffer != null) {
            bitmapBuffer = null;
        }
        if (PREVIEW_WIDTH <= 0 || PREVIEW_HEIGHT <= 0) {
            Loger.d("preview width,height can not <=0");
            return;
        }

        if (PREVIEW_WIDTH > 0 && PREVIEW_HEIGHT > 0) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getPreviewFormat() == ImageFormat.NV21 && data != null) {
                mRenderer.setPreviewBitmapByte(data, parameters);
            }
        }
        mCamera.addCallbackBuffer(data);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true));
                    }
                    break;
            }
        }
    };

    public class PreviewRendererThread extends Thread {
        SurfaceHolder mSurfaceHolder;
        private Object mLock = new Object();
        private boolean mDone = false;
        private Bitmap previewBitmap;

        public PreviewRendererThread(SurfaceHolder holder) {
            mSurfaceHolder = holder;
        }

        public void setPreviewBitmapByte(byte[] data, Camera.Parameters parameters) {
            synchronized (mLock) {
                int w = parameters.getPreviewSize().width;
                int h = parameters.getPreviewSize().height;
                YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, w, h, null);
                Rect rect = new Rect(0, 0, w, h);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                yuvImage.compressToJpeg(rect, 80, outputStream);
                previewBitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
                mLock.notify();
                handler.sendMessage(handler.obtainMessage(1, previewBitmap));
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (mLock) {
                    while (!mDone && previewBitmap == null) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (mDone) {
                        break;
                    }
                }
//                drawPreviewBitmap();
            }
        }

        void drawPreviewBitmap() {
            Canvas preCanvas = null;
            try {
                synchronized (mLock) {
                    preCanvas = mSurfaceHolder.lockCanvas();
                    if (preCanvas != null && previewBitmap != null) {
                        Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        preCanvas.save();
//                        preCanvas.rotate(90, preCanvas.getWidth() / 2, preCanvas.getHeight() / 2);
                        previewBitmap = Bitmap.createBitmap(previewBitmap, 0, 0, previewBitmap.getWidth(), previewBitmap.getHeight(), matrix, true);
                        preCanvas.drawBitmap(previewBitmap, new Rect(0, 0, PREVIEW_WIDTH, PREVIEW_HEIGHT), new Rect(0, 0, preCanvas.getWidth(), preCanvas.getHeight()), paint);
                        preCanvas.restore();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mSurfaceHolder.unlockCanvasAndPost(preCanvas);
            }
        }

        public void half() {
            synchronized (mLock) {
                mDone = true;
                mLock.notify();
            }
        }
    }
}