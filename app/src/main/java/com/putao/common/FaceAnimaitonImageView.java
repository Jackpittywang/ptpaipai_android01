package com.putao.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 静态图面部识别的ImageView
 * Created by yanghx on 2016/2/16.
 */
public class FaceAnimaitonImageView extends ImageView {

    private static final String TAG = "FaceAnimaitonImageView";
    private Handler refreshHandler;
    private boolean animationRunning = false;
    private int animationPosition = 0;

    private List<Bitmap> bitmapArr = new ArrayList<>();
    private Bitmap mBitmap;

    private Location animationModel;
    private PointF midPoint;
    private float eyesDistance;
    private Matrix matrix = new Matrix();
    private Matrix matrixRotation = new Matrix();
    private Matrix matrixTranslate = new Matrix();
    private Matrix matrixScale = new Matrix();
    private Paint paint = new Paint();
    private float centerX;
    private float centerY;



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(msg.what == 0x123) {
                startAnimation();
            }
        }
    };



    public FaceAnimaitonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FaceAnimaitonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FaceAnimaitonImageView(Context context) {
        super(context);
    }

    /**
     * 设置动画资源
     */
    public void setData(final Location model, FaceDetector.Face face) {

        bitmapArr.clear();
        animationPosition = 0;
        animationModel = model;

        midPoint = new PointF();
        face.getMidPoint(midPoint);
        eyesDistance = face.eyesDistance();


        final BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = false;

        //加载本地资源图片
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("PCCamera", "image loaded ...");
                String stickersPath = FileUtils.getStickersPath();

                List<String> imageNames = model.getImageList().getImageName();
                for(int i = 0; i < imageNames.size(); i++) {
                    String imageName = stickersPath  + "/hy/" + imageNames.get(i);
                    if (mBitmap != null) {
                        mBitmap = null;
                    }
                    mBitmap = BitmapFactory.decodeFile(imageName, option);
                    bitmapArr.add(mBitmap);
                }

                mHandler.sendEmptyMessage(0x123);
            }
        }).start();

    }

    public void startAnimation(){
        if(refreshHandler != null) return;
        refreshHandler = new Handler();
        refreshHandler.post(refreshRunable);
        animationRunning = true;
        this.setVisibility(View.VISIBLE);
    }

    public void stopAnimation(){
//        animationRunning = false;
//        this.setVisibility(View.INVISIBLE);
//        animationModel = null;

    }

    /**
     * 图片轮播
     */
    Runnable refreshRunable = new Runnable(){
        @Override
        public void run() {
            if(bitmapArr.size() == 0 || animationRunning == false) return;
            refreshHandler.postDelayed(this, animationModel.getDurationLong());
            // Log.i(TAG, "animation is going:"+ animationPosition);
            animationPosition++;
            if(animationPosition >= bitmapArr.size()) {
                animationPosition = 0;
            }
            invalidate();
        }
    };


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(animationModel!=null){
//            matrixTranslate.setTranslate(centerX-imageScale* Float.valueOf(animationModel.getCenterX()), centerY - imageScale*Float.valueOf(animationModel.getCenterY()));
//            matrixScale.setScale(imageScale, imageScale);
            // 此处旋转用的是角度 不是弧度
//            matrixRotation.setRotate((float)(imageAngle*180f/Math.PI), centerX, centerY);
//            matrix.setConcat(matrixRotation, matrixTranslate);
//            matrix.setConcat(matrix, matrixScale);
            matrix.setTranslate(midPoint.x, midPoint.y);
            matrix.postTranslate(-eyesDistance, 0);
            Log.w(TAG, "midPoint.x = " + midPoint.x + " -- midPoint.y = " + midPoint.y);
//            canvas.drawBitmap(bitmapArr.get(animationPosition), midPoint.x - eyesDistance, midPoint.y, paint);
            canvas.drawBitmap(bitmapArr.get(animationPosition), matrix, paint);
        }

    }

}
