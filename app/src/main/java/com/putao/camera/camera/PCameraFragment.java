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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nineoldandroids.animation.ObjectAnimator;
import com.putao.camera.JNIFUN;
import com.putao.camera.R;
import com.putao.camera.camera.enhance.HdrBitmap;
import com.putao.camera.camera.enhance.PtHdrMergeTask;
import com.putao.camera.camera.utils.CameraFragment;
import com.putao.camera.camera.utils.CameraView;
import com.putao.camera.camera.utils.CameraView.onCameraFocusChangeListener;
import com.putao.camera.camera.utils.OrientationUtil;
import com.putao.camera.camera.utils.PictureTransaction;
import com.putao.camera.camera.utils.SimpleCameraHost;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.camera.view.DrawingFocusView;
import com.putao.camera.camera.view.StarsView;
import com.putao.camera.editor.view.WaterMarkView;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;

import java.util.ArrayList;
import java.util.List;


public class PCameraFragment extends CameraFragment {

    private String TAG = PCameraFragment.class.getName();

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
        //TODO:DEBUG PRINT THE LIBRARY VERSION
        JNIFUN.getHdrLibraryVersion();
        return (results);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        // sendMessage();
        // refreshHandler = new Handler();
        // refreshHandler.post(refreshRunable);

    }

    void showGif() {
        if (isEnableEnhance()) {
            starsView.setVisibility(View.VISIBLE);
            starsView.Play(new StarsView.PlayListener() {
                @Override
                public void playOver() {
                    starsView.setVisibility(View.INVISIBLE);
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
            Toast.makeText(getActivity(), "摄像头连接失败，请重试", Toast.LENGTH_LONG).show();
            return;
        }
        flashScreen();
        takeSimplePicture(new PictureTransaction(getHost()));
    }

    public void takeSimplePicture(PictureTransaction xact) {
        if (flashMode != null) {
            xact.flashMode(flashMode);
        }
        if (mHdrEnable) {
            if (mExposureLevel == ExposureLevel.NORMAL) {
                setExposureLevel(ExposureLevel.LOW);
            } else if (mExposureLevel == ExposureLevel.LOW) {
                setExposureLevel(ExposureLevel.HIGH);
            } else {
                setExposureLevel(ExposureLevel.NORMAL);
            }
        } else {
            setExposureLevel(ExposureLevel.NORMAL);
        }
        Loger.d("exposure level:" + mExposureLevel);
        takePicture(xact);
    }


    public void takeSimplePicture(List<WaterMarkView> wmList) {
        mWaterMarkImageViewsList = wmList;
        takeSimplePicture();
    }


    /**
     * 拍摄HDR照片
     *
     * @param wmList
     * @param hdrenable
     */
    public void takeSimplePicture(List<WaterMarkView> wmList, boolean hdrenable) {
        mHdrEnable = hdrenable;
        if (mHdrEnable) {
            mHdrBitmaps.clear();
            mCountHdr = 0;
        }
        takeSimplePicture(wmList);
    }

    /**
     * 拍摄HDR照片
     *
     * @param wmList
     * @param hdrenable
     */
    public void takeSimplePicture(List<WaterMarkView> wmList, boolean hdrenable, boolean isAuto) {
        mHdrAuto = isAuto;
        takeSimplePicture(wmList, hdrenable);
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
                        flash_view.setVisibility(View.GONE);
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
            Toast.makeText(getActivity(), "摄像头连接失败，请尝试重启手机!", Toast.LENGTH_LONG).show();
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