package com.putao.camera.camera.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.putao.camera.camera.model.AnimationModel;
import com.putao.camera.camera.utils.AnimationUtils;
import com.putao.camera.camera.utils.NoiseFilter;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AnimationImageView extends ImageView {
    private String TAG = AnimationImageView.class.getName();
    // private Canvas mCanvas;
    private int animationPosition = 0;
    private Handler refreshHandler;
    private AnimationModel animationModel;
    private boolean isAnimationRunning = false;
    private Context context;
    private boolean isAnimtionLoading = false;
    private boolean isAnimationReady = false;

    private float imageAngle = 0f;
    private float imageScale = 0f;
    private float centerX;
    private float centerY;

    private float mouthX;
    private float mouthY;

    private RectF mRect = new RectF();
    private Matrix mMatrix = new Matrix();


    private Matrix matrix = new Matrix();
    private Matrix matrixRotation = new Matrix();
    private Matrix matrixTranslate = new Matrix();
    private Matrix matrixScale = new Matrix();

    private Matrix mouthMatrix = new Matrix();
    private Matrix mouthMatrixRotation = new Matrix();
    private Matrix mouthMatrixTranslate = new Matrix();
    private Matrix mouthMatrixScale = new Matrix();

    private Matrix bottomMatrix = new Matrix();
    private Matrix bottomMatrixScale = new Matrix();
    private Matrix bottomMatrixTranslate = new Matrix();

    // bottom放在屏幕上的Y
    private int bottomImageY = 0;

    private List<Bitmap> eyesBitmapArr = new ArrayList<Bitmap>();
    private List<Bitmap> mouthBitmapArr = new ArrayList<Bitmap>();
    private List<Bitmap> bottomBitmapArr = new ArrayList<Bitmap>();
    private String imageFolder = "";

    private String animationName = "";

    private int buttomGap = 80;

    private static final int START_ANIMATION = 0;

    // 数据过滤,移动更平滑
    public NoiseFilter centerXFilter = new NoiseFilter(10);
    public NoiseFilter centerYFilter = new NoiseFilter(10);
    public NoiseFilter angleFilter = new NoiseFilter(10);
    public NoiseFilter scaleFilter = new NoiseFilter(10);
    public NoiseFilter mouthXFilter = new NoiseFilter(10);
    public NoiseFilter mouthYFilter = new NoiseFilter(10);

    private float screenDensity = 1f;

    // 是否需要保存
    private boolean isNeedSave = false;
    // 保存最长多少帧
    private int saveCount = 36;
    private int curSaveCount = 0;
    // 保存图片文件的路径
    private String savePath = "";

    private Bitmap backgroundBitmap = null;

    private boolean isMirror = false;
    private int screenH;

    public AnimationImageView(Context c) {
        super(c);
        init(c);
    }

    public AnimationImageView(Context c, AttributeSet attrs) {
        super(c, attrs);
        init(c);
    }

    private void init(Context context) {
        this.context = context;
        screenH = context.getResources().getDisplayMetrics().heightPixels;
    }

    public void setSave(Bitmap backgroundBitmap, String savePath, int saveCount) {
        isNeedSave = true;
        this.savePath = savePath;
        this.saveCount = saveCount;
        this.backgroundBitmap = backgroundBitmap;
        curSaveCount = 0;
    }

    public void clearSave() {
        isNeedSave = false;
        curSaveCount = 0;
        backgroundBitmap = null;
    }

    public void setScreenDensity(float density) {
        screenDensity = density;
    }

    public void setImageFolder(String folder) {
        imageFolder = folder;
    }

    public String getImageFolder() {
        return imageFolder;
    }

    public void setBottomGap(int gap) {
        setBottomGap(gap);
    }

    public boolean isAnimationLoading() {
        return isAnimtionLoading;
    }

    public String getAnimtionName() {
        return animationName;
    }

    public AnimationModel getAnimationModel() {
        return animationModel;
    }

    public void setIsMirror(boolean flag) {
        isMirror = flag;
    }

    public void setData(String animName, final boolean startAnim) {
        screenH = context.getResources().getDisplayMetrics().heightPixels;
        final AnimationModel model = AnimationUtils.getModelFromXML(animName);
        if (model == null) return;
        if (isAnimtionLoading == true) return;
        if (this.animationName.equals(animName)) return;
        // Log.i(TAG, "animation image view, setdata called .... animationName is:" + animName);
        if (animationModel != null) clearData();
        animationPosition = 0;
        animationModel = model;
        this.animationName = animName;
        final Options option = new Options();
        option.inScaled = false;
        animationPosition = 0;
        isAnimationReady = false;
        isAnimtionLoading = true;
        this.setVisibility(View.VISIBLE);

        //加载本地资源图片
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (model.getEye() != null) {
                    for (int i = 0; i < model.getEye().getImageList().size(); i++) {
                        Bitmap bitmap = BitmapHelper.getBitmapFromPath(imageFolder + animationName + File.separator + model.getEye().getImageList().get(i), option);
                        if (bitmap != null) eyesBitmapArr.add(bitmap);
                    }
                }
                if (model.getMouth() != null) {
                    for (int i = 0; i < model.getMouth().getImageList().size(); i++) {
                        Bitmap bitmap = BitmapHelper.getBitmapFromPath(imageFolder + animationName + File.separator + model.getMouth().getImageList().get(i), option);
                        if (bitmap != null) mouthBitmapArr.add(bitmap);
                    }
                }
                if (model.getBottom() != null) {
                    isMatrixComplete = false;
                    for (int i = 0; i < model.getBottom().getImageList().size(); i++) {
                        File file = new File(imageFolder + animationName + File.separator + model.getBottom().getImageList().get(i));
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);
                        if (bitmap != null) bottomBitmapArr.add(bitmap);
//                        Matrix matrix = new Matrix();
//                        if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
//                            float scale = (float) getWidth() / (float) bitmap.getWidth();
//                            // Log.i(TAG, "screen width is:"+getWidth()+"  image width is:"+bitmap.getWidth()+" scale is:"+scale);
//                            bottomImageY = getHeight() - (int) (bitmap.getHeight() * scale) - buttomGap;
//                            matrix.postScale(scale, scale); //长和宽放大缩小的比例
//                            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                            bottomBitmapArr.add(resizeBmp);
//                            bitmap.recycle();
//                        }
                    }
                }
                isAnimtionLoading = false;
                isAnimationReady = true;
                if (startAnim == false) return;
                Message message = new Message();
                message.what = START_ANIMATION;
                handler.sendMessage(message);
            }
        }).start();

    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_ANIMATION) {
                startAnimation();
            }
        }
    };

    public void startAnimation() {
        if (refreshHandler == null) {
            refreshHandler = new Handler();
        }
        refreshHandler.post(refreshRunable);
        isAnimationRunning = true;
        this.setVisibility(View.VISIBLE);
    }

    /**
     * 只是停止当前的动画 不隐藏 不清楚数据
     */
    public void stopAnimation() {
        if (isAnimationRunning) {
            isAnimationRunning = false;
            centerXFilter.clearData();
            centerYFilter.clearData();
            angleFilter.clearData();
            scaleFilter.clearData();
            this.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 清除上一个动画的所有数据
     */
    public void clearData() {
        Log.i(TAG, "clear animation caled ....");
        stopAnimation();
        for (int i = 0; i < eyesBitmapArr.size(); i++) {
            eyesBitmapArr.get(i).recycle();
        }
        eyesBitmapArr.clear();
        for (int i = 0; i < mouthBitmapArr.size(); i++) {
            mouthBitmapArr.get(i).recycle();
        }
        mouthBitmapArr.clear();
        for (int i = 0; i < bottomBitmapArr.size(); i++) {
            bottomBitmapArr.get(i).recycle();
        }
        bottomBitmapArr.clear();

        for (int i = 0; i < bottomScaleBitmapArr.size(); i++) {
            bottomScaleBitmapArr.get(i).recycle();
        }
        bottomScaleBitmapArr.clear();

        animationModel = null;
        animationName = "";
        isAnimationReady = false;
        this.setVisibility(View.INVISIBLE);

    }

    public boolean isAnimationRunning() {
        return isAnimationRunning;
    }

    /**
     * 图片轮播
     */
    Runnable refreshRunable = new Runnable() {
        @Override
        public void run() {
            if (animationModel == null || animationModel.getAnimationImageSize() == 0 || isAnimationRunning == false)
                return;
            refreshHandler.postDelayed(this, animationModel.getDurationLong());
            // Log.i(TAG, "animation is going:"+ animationPosition);
            animationPosition++;
            if (animationPosition >= animationModel.getAnimationImageSize()) {
                animationPosition = 0;
            }

            // 保存图像逻辑
            saveImage();
            invalidate();
        }
    };

    /**
     * 设置位置，放大倍数和角度
     *
     * @param locationX
     * @param locationY
     * @param scale
     * @param angle
     */
    public void setPosition(float locationX, float locationY, float scale, float angle, float mx, float my) {
        centerX = centerXFilter.dataFilter(locationX);
        centerY = centerYFilter.dataFilter(locationY);
        imageAngle = angleFilter.dataFilter(angle);
        imageScale = scaleFilter.dataFilter(scale);
        mouthX = mouthXFilter.dataFilter(mx);
        mouthY = mouthYFilter.dataFilter(my);
    }


    public void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
                              int viewWidth, int viewHeight) {
        // Need mirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 2000f, viewHeight / 2000f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }

    /**
     * points 0: face
     *
     * @param points
     */

    public void setPositionAndStartAnimation(float[] points) {
        if (animationModel == null) return;
        float scale = 0f;
        float angle = 0f;
        //hezhiyun修改
        float leftEyeX = (points[19 * 2] + points[20 * 2] + points[21 * 2] + points[22 * 2] + points[23 * 2] + points[24 * 2]) / 6;
        float leftEyeY = (points[19 * 2 + 1] + points[20 * 2 + 1] + points[21 * 2 + 1] + points[22 * 2 + 1] + points[23 * 2 + 1] + points[24 * 2 + 1]) / 6;
        float rightEyeX = (points[25 * 2] + points[26 * 2] + points[27 * 2] + points[28 * 2] + points[29 * 2] + points[30 * 2]) / 6;
        float rightEyeY = (points[25 * 2 + 1] + points[26 * 2 + 1] + points[27 * 2 + 1] + points[28 * 2 + 1] + points[29 * 2 + 1] + points[30 * 2 + 1]) / 6;

        //这个版本的动态贴纸中的mouth其实是nouse，所以此利用nouse的坐标
        float mouthX = (points[10 * 2] + points[10 * 2]) / 2;
        float mouthY = (points[10 * 2 + 1] + points[10 * 2 + 1]) / 2;

        float cx = (leftEyeX + rightEyeX) / 2;
        float cy = (leftEyeY + rightEyeY) / 2;

        float eyeDistance = calDistance(leftEyeX, leftEyeY, rightEyeX, rightEyeY);
        // 计算旋转角度
        if (leftEyeX == leftEyeY) angle = (float) Math.PI / 2;
        else {
            angle = (float) Math.atan((rightEyeY - leftEyeY) / (rightEyeX - leftEyeX));
        }
        scale = eyeDistance / animationModel.getDistance();
        setPositionAndStartAnimation((int) cx, (int) cy, scale, angle, (int) mouthX, (int) mouthY);
    }

    public void setPositionAndStartAnimation(int centerX, int centerY, float scale, float angle, int mouthX, int mouthY) {
        if (animationModel == null) return;
        if (isAnimationRunning == false) {
            startAnimation();
        }
        setPosition(centerX, centerY, scale, angle, mouthX, mouthY);

    }

    // 此方法获取不到脸的角度，未采用
//    public void setPositionAndStartAnimation(FaceDetector.Face face, int bitmapWidth, int bitmapHeight, int screenWidth, int screenHeight) {
//        Log.i(TAG, "set Pointion by face called .... !!! ... ");
//        if (animationModel == null) return;
//        if (isAnimationRunning == false) {
//            startAnimation();
//        }
//        Log.i(TAG, "dd:" + face.confidence() + ":::::" + face.pose(face.EULER_X));
//        PointF midPoint = new PointF();
//        face.getMidPoint(midPoint);
//
////        if (mRect.left == mRect.right) angle = (float) 3.14 / 2;
////        else {
////            if (eyeRect.left > eyeRect.right) {
////                angle = (float) Math.atan((mRect.bottom - mRect.top) / (mRect.right - mRect.left));
////            } else {
////                angle = -(float) Math.atan((mRect.bottom - mRect.top) / (mRect.right - mRect.left));
////            }
////        }
//
//        float imageScaleW = screenWidth / bitmapWidth;
//        float imageScaleH = screenHeight / bitmapHeight;
//        float imageScale = imageScaleW;
//        if (imageScaleH < imageScaleW) imageScale = imageScaleH;
//        float animationScale = imageScale * face.eyesDistance() / animationModel.getDistance();
//        int faceCenterX = (int) (screenWidth - imageScale * bitmapWidth) / 2 + (int) (imageScale * midPoint.x);
//        int faceCenterY = (int) (screenHeight - imageScale * bitmapHeight) / 2 + (int) (imageScale * midPoint.y);
//        setPosition(faceCenterX, faceCenterY, animationScale, 0, 0, 0);
//    }


    /*

     */
    public void setPositionAndStartAnimation(Camera.Face face) {
        //Log.i(TAG, "set Pointion by face called .... face eys is:"+face.leftEye.x);
        if (isAnimationReady == false || animationModel == null) return;
        if (isAnimationRunning == false) {
            startAnimation();
        }
        float cx = 0; // centerx in face
        float cy = 0; // centery on face
        float scale = 0; // scale
        float mx = 0; // mouthx
        float my = 0; // mouthy
        float angle = 0; // 旋转角度

        Log.i("FACE", "set Pointion by face called .... is Mirror is:" + isMirror);
        prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());

        if (face.leftEye.x == -1000 || face.leftEye.x == -2000) {
            // 找到脸但是没有眼睛位置
            // 2倍眼睛的距离来计算脸的宽度
            cx = (face.rect.left + face.rect.right) / 2;
            cy = face.rect.top + (face.rect.bottom - face.rect.top) / 3;
            scale = (face.rect.right - face.rect.left) / (screenDensity * 2.5f * animationModel.getDistance());
            mx = cx;
            my = face.rect.bottom - (face.rect.bottom - face.rect.top) / 3;
            Rect tempRect = new Rect();
            tempRect.left = (int) cx;
            tempRect.top = (int) cy;
            tempRect.right = (int) mx;
            tempRect.bottom = (int) my;
            mRect.set(tempRect);
            mMatrix.mapRect(mRect);
            // Log.i(TAG, "cx cy , scale, my, my:" + cx + ":" + cy + ":" + scale + ":" + mx + ":" + my);
            setPosition(mRect.left, mRect.top, scale, 0, mRect.right, mRect.bottom);
        } else {
            // 通过matrix把眼睛的位置矫正
            Rect eyeRect = new Rect();
            eyeRect.left = face.leftEye.x;
            eyeRect.top = face.leftEye.y;
            eyeRect.right = face.rightEye.x;
            eyeRect.bottom = face.rightEye.y;
            int preCenterX = (face.leftEye.x + face.rightEye.x) / 2;
            int preCenterY = (face.leftEye.y + face.rightEye.y) / 2;
            int mouthGapX = face.mouth.x - preCenterX;
            int mouthGapY = face.mouth.y - preCenterY;
            mRect.set(eyeRect);
            mMatrix.mapRect(mRect);
            // 脸的原点在左下角，转换到原点左上角
            mRect.top = getHeight() - mRect.top;
            mRect.bottom = getHeight() - mRect.bottom;
            // 计算距离用于计算放大倍数
            float eyeDistance = calDistance(mRect.left, mRect.top, mRect.right, mRect.bottom);
            // 计算旋转角度

            if (mRect.left == mRect.right) angle = (float) 3.14 / 2;
            else {
                if (eyeRect.left > eyeRect.right) {
                    angle = (float) Math.atan((mRect.bottom - mRect.top) / (mRect.right - mRect.left));
                } else {
                    angle = -(float) Math.atan((mRect.bottom - mRect.top) / (mRect.right - mRect.left));
                }
            }

            cx = (mRect.left + mRect.right) / 2;
            cy = (mRect.top + mRect.bottom) / 2;
            scale = eyeDistance / animationModel.getDistance();
            mx = mouthGapX + cx;
            my = mouthGapY + cy;

            setPosition(cx, cy, scale, angle, mx, my);
        }

    }

    private float calDistance(float fromX, float fromY, float toX, float toY) {
        return (float) Math.sqrt((toX - fromX) * (toX - fromX) + (toY - fromY) * (toY - fromY));
    }

    private void saveImage() {
        if (isNeedSave == false) return;
        setDrawingCacheEnabled(true);
        String countString = curSaveCount + "";
        if (curSaveCount < 10) countString = "0" + countString;
        Bitmap saveBitmap = BitmapHelper.combineBitmap(backgroundBitmap, this.getDrawingCache(), 0, 0);
        BitmapHelper.saveBitmap(saveBitmap, savePath + "image" + countString + ".jpg");
        setDrawingCacheEnabled(false);
        curSaveCount = curSaveCount + 1;
        if (curSaveCount > saveCount - 1) {
            // 保存文件结束
            isNeedSave = false;
            curSaveCount = 0;
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE, null));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (isAnimationReady == true && animationModel != null) {
            if (animationModel.getEye() != null && animationPosition < eyesBitmapArr.size()) {
                matrixTranslate.setTranslate(centerX - imageScale * Float.valueOf(animationModel.getCenterX()), centerY - imageScale * Float.valueOf(animationModel.getCenterY()));
                matrixScale.setScale(imageScale, imageScale);
                // 此处旋转用的是角度 不是弧度
                matrixRotation.setRotate((float) (imageAngle * 180f / Math.PI), centerX, centerY);
                matrix.setConcat(matrixRotation, matrixTranslate);
                matrix.setConcat(matrix, matrixScale);
                canvas.drawBitmap(eyesBitmapArr.get(animationPosition), matrix, null);
            }
            if (animationModel.getMouth() != null && animationPosition < mouthBitmapArr.size()) {
                // 此处获取数据可能会跟上门不一样
                mouthMatrixTranslate.setTranslate(mouthX - imageScale * Float.valueOf(animationModel.getCenterX()), mouthY - imageScale * Float.valueOf(animationModel.getCenterY()));
                mouthMatrixScale.setScale(imageScale, imageScale);
                // 此处旋转用的是角度 不是弧度
                mouthMatrixRotation.setRotate((float) (imageAngle * 180f / Math.PI), centerX, centerY);
                mouthMatrix.setConcat(mouthMatrixRotation, mouthMatrixTranslate);
                mouthMatrix.setConcat(mouthMatrix, mouthMatrixScale);
                canvas.drawBitmap(mouthBitmapArr.get(animationPosition), mouthMatrix, null);
            }
            if (animationModel.getBottom() != null && animationPosition < bottomBitmapArr.size()) {
                isMatrixComplete=false;
                if (!isMatrixComplete) {
                    float scale = (float) getWidth() / (float) bottomBitmapArr.get(animationPosition).getWidth();
                    int scaleH = (int) (bottomBitmapArr.get(animationPosition).getHeight() * scale);
                    bottomMatrixScale.setScale(scale, scale);
                    bottomMatrixTranslate.setTranslate(0, buttomGap);
                    bottomMatrix.setConcat(bottomMatrixScale, bottomMatrixTranslate);
//              canvas.drawBitmap(bottomBitmapArr.get(animationPosition), 0, bottomImageY, null);
                    canvas.drawBitmap(bottomBitmapArr.get(animationPosition), bottomMatrix, null);


                    singleThreadExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = bottomBitmapArr.get(animationPosition);
                            if (bitmap != null && bitmap.getWidth() > 0 && bitmap.getHeight() > 0) {
                                Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), bottomMatrix, true);
                                bottomScaleBitmapArr.add(resizeBmp);
                            }
                            if (animationPosition == bottomBitmapArr.size() - 1) {
                                isMatrixComplete = true;
                                Log.d(TAG, "onDraw: cache" + animationPosition + "" + bottomBitmapArr.size());
                            }
                        }
                    });
                } else {
                    if(animationPosition<bottomScaleBitmapArr.size()){
                        canvas.drawBitmap(bottomScaleBitmapArr.get(animationPosition), 0, buttomGap, null);
                    }
                    Log.d(TAG, "onDraw: cache");
                }

            }
        }
        canvas.restore();
        super.onDraw(canvas);
    }

    private boolean isMatrixComplete = false;
    ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private List<Bitmap> bottomScaleBitmapArr = new ArrayList<Bitmap>();
}