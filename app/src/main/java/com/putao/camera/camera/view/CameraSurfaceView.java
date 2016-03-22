
package com.putao.camera.camera.view;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.putao.camera.util.CameraManager;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private final String Tag = "CameraSurfaceView";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isFrontCamera = false;
    private Context mContext;
    private CamreaParametersChangedListener mCamreaParametersChangedListener;

    public CameraSurfaceView(Context context, Camera camera) {
        super(context);
        mContext = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
//        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    public void setCamreaParametersChangedListener(CamreaParametersChangedListener paramsChangeListener) {
        mCamreaParametersChangedListener = paramsChangeListener;
    }

    public void setCamera(Camera camera, boolean frontCamera) {
        mCamera = camera;
        isFrontCamera = frontCamera;
        try {
            mCamera.setPreviewDisplay(mHolder);
            updateCameraParams();
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();
            float y = event.getY();
            float touchMajor = event.getTouchMajor();
            float touchMinor = event.getTouchMinor();
            Rect touchRect = new Rect((int) (x - touchMajor / 2), (int) (y - touchMinor / 2), (int) (x + touchMajor / 2), (int) (y + touchMinor / 2));
            // ((CameraActivity) getContext()).doSpecialRectFocus(touchRect);
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Loger.d(Tag + ".......................surfaceCreated");
        try {
            mCamera.setPreviewDisplay(holder);
            updateCameraParams();
            mCamera.startPreview();
        } catch (IOException e) {
            Loger.d(Tag + "Error setting camera preview: " + e.getMessage());
        }
        //自动当中聚焦
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
                // ((CameraActivity) getContext()).doSpecialRectFocus(rect);
            }
        }, 500);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Loger.d(Tag + ".......................surfaceChanged" + "w::" + w + "----h::" + h);
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Loger.d(Tag + ".......................surfaceChanged" + e.getMessage());
        }
        if (mCamera != null) {
            Parameters parameters = mCamera.getParameters();
            // set preview size and make any resize, rotate or
            // reformatting changes here
            // start preview with new settings
            try {
                Size previewSize = findBestPreviewSize(parameters);
                parameters.setPreviewSize(previewSize.width, previewSize.height);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (Exception e) {
                Loger.d(Tag + "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Loger.d(Tag + ".......................surfaceDestroyed");
        try {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Camera getCamera() {
        return mCamera;
    }

    public void updateCameraParams() {
        Loger.d(Tag + ".......................updateCameraParams");
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraManager.getInstance().getBackCameraNo(), info);
        WindowManager window = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        int rotation = display.getRotation();
        Loger.d(Tag + ".......................display.getRotation()=====>" + rotation + "|||info.orientation" + info.orientation);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break; // Natural orientation
            case Surface.ROTATION_90:
                degrees = 90;
                break; // Landscape left
            case Surface.ROTATION_180:
                degrees = 180;
                break;// Upside down
            case Surface.ROTATION_270:
                degrees = 270;
                break;// Landscape right
        }
        int rotate = (info.orientation - degrees + 360) % 360;
        Loger.d(Tag + ".......................rotate=====>" + rotate);
        Parameters params = mCamera.getParameters();
        Size pictureSize = findBestPictureSize(params);
        params.setPictureSize(pictureSize.width, pictureSize.height);
        Size previewSize = findBestPreviewSize(params);
        params.setPreviewSize(previewSize.width, previewSize.height);
        params.setRotation(rotate);
        mCamera.setParameters(params);
        //        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        switchFlashLightStatus(Parameters.FLASH_MODE_OFF, params);
        mCamera.setDisplayOrientation(rotate);
        int supportPreviewWidth = previewSize.width;
        int supportPreviewHeight = previewSize.height;
        int srcWidth = getScreenWH().widthPixels;
        int srcHeight = getScreenWH().heightPixels;
        int width = Math.min(srcWidth, srcHeight);
        int height = width * supportPreviewWidth / supportPreviewHeight;
        this.setLayoutParams(new FrameLayout.LayoutParams(width, height));
    }

    public void switchFlashLightStatus(String currentFlashMode, Parameters parameters) {
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null) {
            if (flashModes.contains(currentFlashMode)) {
                parameters.setFlashMode(currentFlashMode);
                try {
                    mCamera.setParameters(parameters);
                    if (mCamreaParametersChangedListener != null) {
                        mCamreaParametersChangedListener.onFlashModeChanged(currentFlashMode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Loger.e(Tag + currentFlashMode + " not supported");
            }
        }
    }

    private Size findBestPictureSize(Parameters parameters) {
        List<Size> psizelist = parameters.getSupportedPictureSizes();
        if (psizelist == null) {
            return mCamera.new Size(getScreenWH().widthPixels, getScreenWH().heightPixels);
        }
        return CalcCameraSupportMaxSize(psizelist);
    }

    private Size findBestPreviewSize(Parameters parameters) {
        List<Size> psizelist = parameters.getSupportedPreviewSizes();
        if (psizelist == null) {
            return mCamera.new Size(getScreenWH().widthPixels, getScreenWH().heightPixels);
        }
        return getOptimalPreviewSize(psizelist, getScreenWH().widthPixels, getScreenWH().heightPixels);
    }

    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null)
            return null;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    /**
     * 计算相机支持的最小宽度的图片尺寸
     *
     * @param camera
     * @return
     */
    Size CalcCameraSupportMaxSize(List<Size> psizelist) {
        if (null != psizelist && 0 < psizelist.size()) {
            int t_width = psizelist.get(0).width;
            int index = 0;
            for (int i = 1; i < psizelist.size(); i++) {
                if (psizelist.get(i).width > t_width && psizelist.get(i).width < getScreenWH().widthPixels) {
                    t_width = psizelist.get(i).width;
                    index = i;
                }
            }
            return psizelist.get(index < psizelist.size() - 1 ? (index + 1) : index);
        }
        return null;
    }

    protected DisplayMetrics getScreenWH() {
        return DisplayHelper.metrics;
    }

    public static interface CamreaParametersChangedListener {
        void onFlashModeChanged(String currentMode);
    }
}
