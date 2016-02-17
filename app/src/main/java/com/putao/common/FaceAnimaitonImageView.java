package com.putao.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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

    private Handler refreshHandler;
    private boolean animationRunning = false;
    private int animationPosition = 0;
    private Location animationModel;



    private List<Bitmap> bitmapArr = new ArrayList<>();
    private Bitmap mBitmap;



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
    public void setData(final Location model) {

        bitmapArr.clear();
        animationPosition = 0;
        animationModel = model;
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
            }
        }).start();

        startAnimation();
    }

    public void startAnimation(){
        if(refreshHandler != null) return;
        refreshHandler = new Handler();
        refreshHandler.post(refreshRunable);
        animationRunning = true;
        this.setVisibility(View.VISIBLE);
    }

    public void stopAnimation(){
        animationRunning = false;
        this.setVisibility(View.INVISIBLE);
        animationModel = null;

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

        Matrix matrix = new Matrix();

        canvas.drawBitmap(bitmapArr.get(animationPosition), matrix, new Paint());

    }

}
