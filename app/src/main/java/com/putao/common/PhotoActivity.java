package com.putao.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.media.FaceDetector;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.StringHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PhotoActivity extends BaseActivity {

    private static String TAG = "FaceDetect";
    private ImageView mIV;
    private ImageView postImage;
    private Bitmap mFaceBitmap;
    private Bitmap postBitmap;
    private int mFaceWidth = 300;
    private int mFaceHeight = 300;
    private static final int MAX_FACES = 1;

    private ViewGroup.LayoutParams postImagePara;

    private int screenWidth = 0;
    private int screenHeight = 0;

//    private Location model;
    private AnimationModel model;
    private Bitmap originImageBitmap;
    private String photo_data;
    private ImageView photoView;
    private FaceAnimaitonImageView faceView;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo;
    }

    @Override
    public void doInitSubViews(View view) {

        photoView = queryViewById(R.id.iv_photo);
        faceView = queryViewById(R.id.iv_animation);

        Animation animation = XmlUtils.xmlToModel(readSdcardFile(FileUtils.getStickersPath() +"/hy/hy.xml"), "animation", Animation.class);
        faceView.setData(animation.getEye());

//        model = new AnimationModel();
//        model.setWidth(291);
//        model.setHeight(191);
//        model.setDistance(100);
//        model.setCenterX(140);
//        model.setCenterY(191);
//        model.setDuration(0.5f);

        // 获取屏幕高宽
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        Intent intent = this.getIntent();
        photo_data = intent.getStringExtra("photo_data");
        if (!StringHelper.isEmpty(photo_data)) {
            originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, DisplayHelper.getScreenWidth(),
                    DisplayHelper.getScreenHeight());
            mFaceBitmap = originImageBitmap.copy(Bitmap.Config.RGB_565, true);
            originImageBitmap.recycle();
        }

        mFaceWidth = mFaceBitmap.getWidth();
        mFaceHeight = mFaceBitmap.getHeight();
        photoView.setImageBitmap(mFaceBitmap);

        // 加载贴图
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inScaled = false;
        postBitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.fd0006, option);

//        faceView.setImageBitmap(postBitmap);
//        postImagePara = new LinearLayout.LayoutParams(screenWidth, screenHeight);
//        this.addContentView(postImage, postImagePara);

        // 检测脸
        FaceDetector fd;
        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
        int count = 0;
        try {
            fd = new FaceDetector(mFaceWidth, mFaceHeight, MAX_FACES);
            count = fd.findFaces(mFaceBitmap, faces);
            if(count<1){
                Log.i(TAG, "no find face");
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "find face error");
            return;
        }
        PointF midPoint = new PointF();
        faces[0].getMidPoint(midPoint);

//        faceView.setFace(bitmap);

        // 需要算出 中心点位置，放大倍数和角度
//        setFace(faceView, bitmap, model, midPoint, 2.5f, 15*0.0174f);

    }

    @Override
    public void doInitData() {
    }

    /**
     * 读取本地资源
     * @param filePath
     * @return
     */
    private String readSdcardFile(String filePath) {
        String result = null;
        try {
            InputStream is = new FileInputStream(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    // angle是弧度
    private void setFace(ImageView image, Bitmap map, AnimationModel model, PointF location,
                         float scale, float angle) {

        Matrix matrix = new Matrix();
        Matrix matrixRotation = new Matrix();
        Matrix matrixTranslate = new Matrix();
        matrixTranslate.setTranslate(location.x-scale*model.getCenterX(), location.y - scale*model.getCenterY());
        Matrix matrixScale = new Matrix();
        matrixScale.setScale(scale, scale);

        // 此处旋转用的是角度 不是弧度
        matrixRotation.setRotate((float)(angle*180f/Math.PI), location.x, location.y);
        matrix.setConcat(matrixRotation, matrixTranslate);
        matrix.setConcat(matrix, matrixScale);

        Bitmap targetBitmap = Bitmap.createBitmap(screenWidth, screenHeight,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);

        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
//        canvas.drawBitmap(bitmap, matrix, new Paint());
        canvas.drawBitmap(map, matrix, new Paint());
        image.setImageBitmap(targetBitmap);


    }
}
