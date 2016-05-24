/**
 * 7  Copyright (c) 2013 CommonsWare, LLC
 * <p>
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

package com.putao.camera.camera;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.ExifInterface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.putao.camera.JNIFUN;
import com.putao.camera.R;
import com.putao.camera.camera.enhance.HdrBitmap;
import com.putao.camera.camera.enhance.PtHdrMergeTask;
import com.putao.camera.camera.filter.CustomerFilter;
import com.putao.camera.camera.gpuimage.GPUImage;
import com.putao.camera.camera.gpuimage.GPUImageFilter;
import com.putao.camera.camera.utils.CameraFragment;
import com.putao.camera.camera.utils.CameraView;
import com.putao.camera.camera.utils.CameraView.onCameraFocusChangeListener;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.utils.PictureTransaction;
import com.putao.camera.camera.utils.SimpleCameraHost;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.camera.view.DrawingFocusView;
import com.putao.camera.camera.view.StarsView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.editor.PhotoEditorActivity;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class PCameraFragment extends CameraFragment {

    private String TAG = PCameraFragment.class.getSimpleName();

    private static final String KEY_USE_FFC = "com.putao.camera.PCameraFragment.USE_FFC";
    private String flashMode = Parameters.FLASH_MODE_OFF;
    private flashModeCode flashModeCodeCurrent = flashModeCode.off;
    private TakePictureListener mTakePictureListener;
    private CameraView cameraView;
    private StarsView starsView;
    private TextView tv_def;
    private DrawingFocusView drawingView;
    private List<WaterMarkView> mWaterMarkImageViewsList;
    private boolean bSaveLocalPhoto = true;
    private ActivityCamera.PictureRatio mPictureRatio = ActivityCamera.PictureRatio.RATIO_THREE_TO_FOUR;
    private int mPictureOffSet = 0;
    private FrameLayout camera_control;
    private View flash_view;
    private boolean isFaceDetecting = false;
    private int screenW, screenH;
    private static boolean isFFC;
    private CustomerFilter.FilterType filterName;

    public void setSaveLocalPhotoState(boolean aSaveLocalPhoto) {
        bSaveLocalPhoto = aSaveLocalPhoto;
    }


    public boolean isEnableEnhance() {
        return mEnableEnhance;
    }

    public void setEnableEnhance(boolean mEnableEnhance) {
        this.mEnableEnhance = mEnableEnhance;
        showGif();
    }


    private boolean mEnableEnhance = false;
//    private boolean mEnableEnhance = true;

    /**
     * 是否启用HDR
     */
    private boolean mHdrEnable = false;
    /**
     * 自动HDR
     */
    private boolean mHdrAuto = false;

    /**
     * hdr图片列表
     */
    private List<HdrBitmap> mHdrBitmaps = new ArrayList<HdrBitmap>();

    /**
     * hdr 拍照次数
     */
    private int mCountHdr = 0;

    /**
     * 当前曝光补偿级别
     */
    private ExposureLevel mExposureLevel = ExposureLevel.NORMAL;


    public AnimationImageView animationView;
    // 没检测到脸的次数。累计到一定个数才会清除屏幕上门的动画
    private int noDetectFaceCount = 0;

    /**
     * 设置faceview
     *
     * @param view
     */
    public void setAnimationView(AnimationImageView view) {
        this.animationView = view;
        if (cameraView != null) cameraView.setAnmationView(view);
    }

    /**
     * 清除faceview
     */
    public void clearAnimationView() {

        this.animationView = null;
        if (cameraView != null) cameraView.clearAnmationView();
    }


    public static PCameraFragment newInstance(boolean useFFC) {
        PCameraFragment f = new PCameraFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_USE_FFC, useFFC);
        f.setArguments(args);
        return (f);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
        SimpleCameraHost.Builder builder = new SimpleCameraHost.Builder(new PtCameraHost(getActivity()));
        setHost(builder.useFullBleedPreview(true).build());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        drawingView = new DrawingFocusView(getActivity());
        cameraView = (CameraView) super.onCreateView(inflater, container, savedInstanceState);
        cameraView.setDrawingView(drawingView);

        cameraView.setOnCameraFocusChangeListener(cameraFocusChangeListener);
        View results = inflater.inflate(R.layout.fragment, container, false);
        camera_control = ((FrameLayout) results.findViewById(R.id.camera));
        camera_control.addView(cameraView);
        camera_control.addView(drawingView);
        addFlashView(camera_control);
        starsView = (StarsView) results.findViewById(R.id.stars_view);
        tv_def = (TextView) results.findViewById(R.id.tv_def);
//        setPtCameraPreviewCallback(ptCameraPreviewCallback);
        JNIFUN.getHdrLibraryVersion();
        mGPUImage = new GPUImage(getActivity());
        mGPUImage.setGLSurfaceView(cameraView.getmGLView());
        mGPUImage.setPreviewCallback(cameraView.getPreviewStrategy());

        WindowManager manager = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        screenW = manager.getDefaultDisplay().getWidth();
        screenH = manager.getDefaultDisplay().getHeight();

        /*LinkedList<byte[]> data=new LinkedList<>();
        while (data!=null && data.size()>0){
             byte[] temp=data.poll();

        }*/
        return (results);
    }

    private GPUImage mGPUImage;
    private Camera.Parameters cameraParams;

    private void initFilter() {
        boolean flipHorizontal = false;
        boolean flipVertical = false;
        int orientation = getActivity().getResources().getConfiguration().orientation;
        int degrees = 0;
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            if (cameraView.getCameraId() == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                degrees = 270;
                flipHorizontal = true;
            } else
                degrees = 90;
        }
        cameraParams = cameraView.getCamera().getParameters();
        if (cameraParams.getFocusMode().contains(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            cameraParams.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
//        cameraParams.setFlashMode();
        setOptimalPreviewSize(cameraParams, 800, 480);
        setOptimalPictureSize(cameraParams, 1280);
//        setOptimalPictureSize(cameraParams, 2592);
        cameraView.getCamera().setParameters(cameraParams);
        mGPUImage.setUpCamera(cameraView.getCamera(), degrees, flipHorizontal, flipVertical);
    }

    public void setFilter(GPUImageFilter filter) {
        if (filter == null)
            filter = new GPUImageFilter();
        mGPUImage.setFilter(filter);
    }

    public void setFilterName(CustomerFilter.FilterType filterName) {
        this.filterName = filterName;
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
               /* if ((double) Math.abs(size.width - targetWidth) < minDiff) {
                    optimalSize = size;
                    minDiff = (double) Math.abs(size.width - targetWidth);
                }*/
                if (Math.abs((float) size.width / size.height - 16f / 9) < 0.2 && size.width - targetWidth < 250) {

                    optimalSize = size;
                    break;
                }

            }

            int iw = optimalSize.width;
            int ih = optimalSize.height;
            cameraParams.setPreviewSize(iw, ih);
        }
    }

    private void setOptimalPictureSize(Camera.Parameters cameraParams, int targetWidth) {
        List<Camera.Size> supportedPictureSizes = cameraParams
                .getSupportedPictureSizes();
        if (null == supportedPictureSizes) return;
        Camera.Size optimalSize = null;
        Iterator mIterator = supportedPictureSizes.iterator();

        while (mIterator.hasNext()) {
            Camera.Size size = (Camera.Size) mIterator.next();
//            if (size.width - targetWidth < 100) {
            if (Math.abs((float) size.width / size.height - 16f / 9) < 0.2 && size.width - targetWidth < 250) {

                optimalSize = size;
                break;
            }
        }
        if (optimalSize == null) return;
        cameraParams.setPictureSize(optimalSize.width, optimalSize.height);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        cameraView.startCamera();
        initFilter();
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 释放相机
     */
    private void releaseCamera() {
        cameraView.releaseCamera();
    }

    void showGif() {
        if (isEnableEnhance()) {
            starsView.setVisibility(View.VISIBLE);
            starsView.Play(new StarsView.PlayListener() {
                @Override
                public void playOver() {
//                    starsView.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            ObjectAnimator.ofFloat(tv_def, "alpha", 0, 1, 1, 1, 0).setDuration(2000).start();
        }
    }


    /**
     * 增加拍摄效果View
     *
     * @param camera_control
     */
    private void addFlashView(ViewGroup camera_control) {
        flash_view = new View(getActivity());
        flash_view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        flash_view.setVisibility(View.GONE);
        flash_view.setBackgroundColor(Color.BLACK);
        camera_control.addView(flash_view);
    }

    /**
     * 监听焦点变化
     */
    onCameraFocusChangeListener cameraFocusChangeListener = new onCameraFocusChangeListener() {
        @Override
        public void onFocusStart() {
            // TODO Auto-generated method stub
            if (mTakePictureListener != null) {
                mTakePictureListener.focusChanged(true);
            }
        }

        @Override
        public void onFocusEnd() {
            // TODO Auto-generated method stub
            if (mTakePictureListener != null) {
                mTakePictureListener.focusChanged(false);
            }
        }
    };

    /**
     * 设置照片比例
     *
     * @param ratio
     * @param OffSetY
     */
    public void setPictureRatio(ActivityCamera.PictureRatio ratio, int OffSetY) {
        mPictureRatio = ratio;
        mPictureOffSet = OffSetY;
    }

    public void setPhotoSaveListener(TakePictureListener listener) {
        mTakePictureListener = listener;
    }

    /**
     * 设置曝光补偿值
     *
     * @param level
     */
    public void setExposureLevel(ExposureLevel level) {
        mExposureLevel = level;
    }


    public void takeSimplePicture() {
        if (!cameraView.isInPreview()) {
//            Toast.makeText(getActivity(), "摄像头连接失败，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        flashScreen();
        takeSimplePicture(new PictureTransaction(getHost()));
    }

    private boolean isShowAR = false;

    public void isShowAR(boolean isShowAR) {
        this.isShowAR = isShowAR;
    }

    public void isStart(boolean isStart) {
        if (isStart)
            setOptimalPictureSize(cameraParams, 800);
        else setOptimalPictureSize(cameraParams, 1280);

        cameraView.setIsStart(isStart);
    }


    private Parameters previewParams = null;
    private Matrix matrix;

    public void takeSimplePhoto() {
        flashScreen();
        Camera camera = cameraView.getCamera();
        Parameters pictureParams = camera.getParameters();
//        pictureParams.setExposureCompensation(3);

        if (mHdrEnable) {

            if (mHdrAuto) {
                pictureParams.setFlashMode(Parameters.FLASH_MODE_AUTO);
            } else {
                pictureParams.setFlashMode(Parameters.FLASH_MODE_TORCH);
            }
        } else {
            pictureParams.setFlashMode(Parameters.FLASH_MODE_OFF);
        }

        final String model = android.os.Build.MODEL.toLowerCase();
        final String brand = Build.BRAND.toLowerCase();
        // 所有华为的机器不要做set处理,

        if (model.contains("huawei") || brand.contains("huawei") || model.contains("cl00") || model.contains("honor")) {
        } else {
            camera.setParameters(pictureParams);
        }
        camera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, final Camera camera) {
//                获取当前情景模式设置。
                camera.getParameters().getSceneMode();

                camera.getParameters().flatten();
                camera.getParameters().getWhiteBalance();
                camera.startPreview();
//                imagePath = getActivity().getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "temp.jpg";
                imagePath = FileUtils.getARStickersPath() + File.separator + "temp.jpg";
//                imagePath = FileUtils.getSdcardPath() + File.separator + "temp.jpg";
                Bitmap tempBitmap = BitmapHelper.Bytes2Bimap(data);
                Bitmap saveBitmap = null;
                if (tempBitmap.getHeight() < tempBitmap.getWidth()) {
                    Log.e("onPictureTaken", "onPictureTaken: ");
                    saveBitmap = BitmapHelper.orientBitmap(tempBitmap, ExifInterface.ORIENTATION_ROTATE_90);
                } else saveBitmap = tempBitmap;

//                if (model.contains("OPPO") || brand.contains("OPPO")) {
                if (isFFC) {
                    if (model.contains("huawei") || brand.contains("huawei") || model.contains("honor") || brand.contains("honor") || brand.contains("xiaomi") || brand.contains("nubia")) {

                    } else {
                        saveBitmap = BitmapHelper.orientBitmap(saveBitmap, ExifInterface.ORIENTATION_ROTATE_180);
                    }

                    matrix = new Matrix();
                    matrix.postScale(-1, 1);      /*水平翻转180度*/
                    saveBitmap = Bitmap.createBitmap(saveBitmap, 0, 0, saveBitmap.getWidth(), saveBitmap.getHeight(), matrix, true);


                }

              /*  if (model.contains("huawei") || brand.contains("huawei") || model.contains("cl00") || model.contains("L09") || model.contains("honor") || model.contains("oppo") || brand.contains("oppo")) {
                } else {
                    if (isFFC) {
                        saveBitmap = BitmapHelper.orientBitmap(saveBitmap, ExifInterface.ORIENTATION_ROTATE_180);
                    }
                }*/

                cameraView.getmGLView().setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                boolean haveFace = cameraView.getFace();
                BitmapHelper.saveBitmap(saveBitmap, imagePath);
                saveBitmap.recycle();
                tempBitmap.recycle();
                if (isShowAR == true) {
                    if (haveFace) {
                        handler.sendEmptyMessageDelayed(0x001, 100);
                    } else {
                        handler.sendEmptyMessageDelayed(0x002, 0);
                    }
                } else {
                    handler.sendEmptyMessageDelayed(0x002, 0);
                }

        /*
         * 目前Android SDK定义的Tag有:
        TAG_DATETIME 时间日期
        TAG_FLASH 闪光灯
        TAG_GPS_LATITUDE 纬度
        TAG_GPS_LATITUDE_REF 纬度参考
        TAG_GPS_LONGITUDE 经度
        TAG_GPS_LONGITUDE_REF 经度参考
        TAG_IMAGE_LENGTH 图片长
        TAG_IMAGE_WIDTH 图片宽
        TAG_MAKE 设备制造商
        TAG_MODEL 设备型号
        TAG_ORIENTATION 方向
        TAG_WHITE_BALANCE 白平衡
        */

                try {
                    //android读取图片EXIF信息
                    ExifInterface exifInterface = new ExifInterface(imagePath);
                    String smodel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
                    String width = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
                    String tag_white_balance  = exifInterface.getAttribute(ExifInterface.TAG_WHITE_BALANCE );
                    String tag_datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                    String tag_gps_latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String iso = exifInterface.getAttribute(ExifInterface.TAG_ISO);
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });
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
                Intent intent = new Intent(getActivity(), PhotoEditorActivity.class);
                intent.putExtra("filterName", filterName);
                intent.putExtra("photo_data", imagePath);
                intent.putExtra("from", "camera");
                getActivity().startActivity(intent);
            }
        }
    };


    public void takeSimplePicture(PictureTransaction xact) {
        if (flashMode != null) {
            xact.flashMode(flashMode);
        }
     /*   if (mHdrEnable) {
            if (mExposureLevel == ExposureLevel.NORMAL) {
                setExposureLevel(ExposureLevel.LOW);
            } else if (mExposureLevel == ExposureLevel.LOW) {
                setExposureLevel(ExposureLevel.HIGH);
            } else {
                setExposureLevel(ExposureLevel.NORMAL);
            }
        } else {
            setExposureLevel(ExposureLevel.NORMAL);
        }*/
        Loger.d("exposure level:" + mExposureLevel);
        takePicture(xact);
    }


    public void takeSimplePicture(List<WaterMarkView> wmList, boolean isFC) {
        mWaterMarkImageViewsList = wmList;
        isFFC = isFC;
//        takeSimplePicture();
        takeSimplePhoto();
    }


    /**
     * 拍摄HDR照片
     *
     * @param wmList
     * @param hdrenable
     */
    public void takeSimplePicture(List<WaterMarkView> wmList, boolean hdrenable, boolean isFC) {
        mHdrEnable = hdrenable;
        if (mHdrEnable) {
            mHdrBitmaps.clear();
            mCountHdr = 0;
        }
        isFFC = isFC;
        takeSimplePicture(wmList, isFFC);
    }

    /**
     * 拍摄HDR照片
     *
     * @param wmList
     * @param hdrenable
     */
    public void takeSimplePicture(List<WaterMarkView> wmList, boolean hdrenable, boolean isAuto, boolean isFC) {
        mHdrAuto = isAuto;
        isFFC = isFC;
        takeSimplePicture(wmList, hdrenable, isFFC);
    }

    /**
     * 显示拍照效果
     */
    void flashScreen() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                flash_view.setVisibility(View.VISIBLE);
                flash_view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (flash_view != null) flash_view.setVisibility(View.GONE);
                    }
                }, 200);
            }
        });
    }


    public void setflashMode(flashModeCode mode) {
        if (mode == flashModeCode.on) {
            flashMode = Parameters.FLASH_MODE_ON;
        } else if (mode == flashModeCode.auto) {
            flashMode = Parameters.FLASH_MODE_AUTO;
        } else if (mode == flashModeCode.light) {
            flashMode = Parameters.FLASH_MODE_ON;
        } else {
            flashMode = Parameters.FLASH_MODE_OFF;
        }
        flashModeCodeCurrent = mode;
    }

    public flashModeCode getCurrentModeCode() {
        return flashModeCodeCurrent;
    }

    public enum flashModeCode {
        on, off, auto, light
    }

    /**
     * 曝光补偿级别
     */
    public enum ExposureLevel {
        LOW,
        NORMAL,
        HIGH
    }

    public interface TakePictureListener {
        void saved(Bitmap photo);

        void focusChanged(boolean isfocusing);
    }

    class PtCameraHost extends SimpleCameraHost {
        public PtCameraHost(Context _ctxt) {
            super(_ctxt);
        }

        @Override
        public boolean useFrontFacingCamera() {
            if (getArguments() == null) {
                return (false);
            }
            return (getArguments().getBoolean(KEY_USE_FFC));
        }


        @Override
        public void saveImage(final PictureTransaction xact, byte[] image) {
            final Bitmap bit_take = BitmapHelper.Bytes2Bimap(image);
            if (mHdrEnable) {
                if (!mHdrAuto) {
                    mHdrBitmaps.add(new HdrBitmap(bit_take));
                    if (mCountHdr < 2) {
                        countinueShoot();
                        return;
                    }
                } else {//自动
                    int ret_check = JNIFUN.PTHDRCheck(new HdrBitmap(bit_take).getRgbaArray(), bit_take.getWidth(), bit_take.getHeight());
                    Loger.d("auto expo check------->" + ret_check);
                    if (ret_check == 1) {//需要
                        mHdrAuto = false;
                        mHdrBitmaps.add(new HdrBitmap(bit_take));
                        countinueShoot();
                        return;
                    } else {//不需要
                        mHdrEnable = false;
                        mHdrAuto = false;
                        setExposureLevel(ExposureLevel.NORMAL);
                        restartPreview();
                    }
                }
            }
            if (mHdrEnable && mHdrBitmaps.size() > 2) {
                new PtHdrMergeTask(mHdrBitmaps, bit_take.getWidth(), bit_take.getHeight(), new PtHdrMergeTask.PtHdrMergeListener() {
                    @Override
                    public void merged(Bitmap bitmap) {
                        mHdrEnable = false;
                        mHdrAuto = false;
                        mergeAndSavePhoto(xact, bitmap);
                        setExposureLevel(ExposureLevel.NORMAL);
                        restartPreview();
                    }
                }).execute();
            } else {
                mergeAndSavePhoto(xact, bit_take);
            }
        }

        /**
         * HDR连拍
         */
        private void countinueShoot() {
            mCountHdr += 1;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            takeSimplePicture();
        }

        /**
         * 保存照片
         *
         * @param xact
         * @param bit_take
         */
        private void mergeAndSavePhoto(PictureTransaction xact, Bitmap bit_take) {

            if (isEnableEnhance()) {
                int w = bit_take.getWidth();
                int h = bit_take.getHeight();
                int[] pix = new int[w * h];
                bit_take.getPixels(pix, 0, w, 0, 0, w, h);
                int[] out = JNIFUN.PTEnhanceImg(pix, w, h, mHdrEnable);
                if (out != null) {
                    bit_take.recycle();
                    bit_take = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                    bit_take.setPixels(out, 0, w, 0, 0, w, h);
                }
            }
            Bitmap bitSource = bitmapZoom(bit_take);
            Bitmap bitWithWater = null;
            if (mWaterMarkImageViewsList != null) {
                int w = bitSource.getWidth();
                int h = bitSource.getHeight();
                bitWithWater = Bitmap.createBitmap(w, h, Config.ARGB_8888);
                Canvas cv = new Canvas(bitWithWater);
                if (useFrontFacingCamera()) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(-1, 1); // 镜像水平翻转
                    bitSource = Bitmap.createBitmap(bitSource, 0, 0, w, h, matrix, true);
                }
                cv.drawBitmap(bitSource, 0, 0, null);
                drawWaterMark(cv);
            }
            if (bSaveLocalPhoto) {
                super.saveImage(xact, BitmapHelper.Bitmap2Bytes(CropPhoto(bitWithWater == null ? bitSource : bitWithWater)));
            }
            if (mTakePictureListener != null) {
                mTakePictureListener.saved(bitWithWater == null ? bitSource : bitWithWater);
            }
        }

        @Override
        public Parameters getExposureCompensation(Parameters parameters) {
            if (mExposureLevel == ExposureLevel.LOW) {
                parameters.setExposureCompensation(parameters.getMinExposureCompensation());
            } else if (mExposureLevel == ExposureLevel.NORMAL) {
                parameters.setExposureCompensation(0);
            } else if (mExposureLevel == ExposureLevel.HIGH) {
                parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            }
            return super.getExposureCompensation(parameters);
        }

        @Override
        public void onCameraFail(FailureReason reason) {
            super.onCameraFail(reason);
            Toast.makeText(getActivity(), "摄像头连接失败，请开启摄像头权限!", Toast.LENGTH_LONG).show();
        }

    }


    /**
     * 合成照片贴图
     *
     * @param cv
     */
    private void drawWaterMark(Canvas cv) {
        Paint mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        for (int i = 0; i < mWaterMarkImageViewsList.size(); i++) {
            WaterMarkView watermark = mWaterMarkImageViewsList.get(i);
            watermark.setDrawingCacheEnabled(true);
            Bitmap waterBitmap = watermark.getDrawingCache();
            Matrix matrix = new Matrix();

            if (OrientationUtil.getOrientation() == 270) {
                matrix.postRotate(-90, 0, DisplayHelper.getScreenHeight() >> 1);
                matrix.postTranslate(DisplayHelper.getScreenHeight() >> 1, 0);
            } else if (OrientationUtil.getOrientation() == 90) {
                matrix.postRotate(90, DisplayHelper.getScreenWidth(), DisplayHelper.getScreenHeight() >> 1);
            } else if (OrientationUtil.getOrientation() == 180) {
                matrix.postRotate(180, DisplayHelper.getScreenWidth() >> 1, DisplayHelper.getScreenHeight() >> 1);
            }

            cv.drawBitmap(waterBitmap, matrix, mPaint);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();
            watermark.setDrawingCacheEnabled(false);
        }
    }

    /**
     * 等比缩放照片到屏幕大小
     *
     * @param bitmap
     * @return
     */
    private Bitmap bitmapZoom(Bitmap bitmap) {
        Matrix matrix = new Matrix();

        int max_wh = bitmap.getHeight() > bitmap.getWidth() ? bitmap.getHeight() : bitmap.getWidth();
        float scale_value = (float) DisplayHelper.getScreenHeight() / (float) max_wh;

        if (scale_value > 1.0f) {
            scale_value = 1.0f;
        }
        matrix.postScale(scale_value, scale_value);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 按照设定比例裁切照片
     *
     * @param bm
     * @return
     */
    public Bitmap CropPhoto(Bitmap bm) {
        Bitmap bitmap = bm;
        int cut_x = 0, cut_y = 0;
        int cut_width = 0, cut_height = 0;
        try {
            if (OrientationUtil.getOrientation() == 0 || OrientationUtil.getOrientation() == 180) {
                cut_y = mPictureOffSet;
                cut_width = bitmap.getWidth();
                cut_height = (mPictureRatio == ActivityCamera.PictureRatio.RATIO_ONE_TO_ONE) ? cut_width : (int) (cut_width * 4.0f / 3);
            } else {
                cut_x = mPictureOffSet;
                cut_height = bitmap.getHeight();
                cut_width = (mPictureRatio == ActivityCamera.PictureRatio.RATIO_ONE_TO_ONE) ? cut_height : (int) (cut_height * 4.0f / 3);
            }
            bitmap = Bitmap.createBitmap(bm, cut_x, cut_y, cut_width, cut_height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


}