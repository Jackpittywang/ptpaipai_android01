package com.putao.common.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.putao.camera.R;
import com.putao.camera.camera.utils.CameraView;


public class FaceView extends ImageView {
    private static final String TAG = "YanZiii";
    public static final int UPDATE_FACE_RECT = 0;
    public static final int CAMERA_HAS_STARTED_PREVIEW = 1;

    private Context mContext;
    private Paint mLinePaint;
    private Paint mPaint;
    private Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();
    private Drawable mFaceIndicator = null;
    private CameraView mCameraView;
    private boolean mMirror;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
//        initPaint();

//        mFaceIndicator = getResources().getDrawable(R.drawable.ic_face_find_2);

    }


    public void setFaces(Face[] faces) {
        this.mFaces = faces;
        invalidate();
    }

    public void clearFaces() {
        mFaces = null;
        invalidate();
    }

    public void setCameraView(CameraView cameraView) {
        mCameraView = cameraView;
    }

    @Override
    public boolean willNotCacheDrawing() {
        return super.willNotCacheDrawing();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mFaces == null || mFaces.length < 1) {
            return;
        }
        if (mCameraView.cameraId == CameraInfo.CAMERA_FACING_BACK) {
            mMirror = false;
        } else if (mCameraView.cameraId == CameraInfo.CAMERA_FACING_FRONT) {
            mMirror = true;
        }
        prepareMatrix(mMatrix, mMirror, 90, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0);
        canvas.rotate(-0);

//        for (int i = 0; i < mFaces.length; i++) {
//
//            Point leftEye = mFaces[i].leftEye;
//            Point rightEye = mFaces[i].rightEye;
//            Point mouth = mFaces[i].mouth;
//            Log.d(TAG, "左眼位置:x=" + leftEye.x + ",y=" + leftEye.y);
//             Log.d(TAG, "右眼位置:x=" + rightEye.x + ",y=" + rightEye.y);
//            Log.d(TAG, "嘴巴位置:x=" + mouth.x + ",y=" + mouth.y);
//
//            canvas.drawLine(leftEye.x, leftEye.y, rightEye.x, rightEye.y, mPaint);
////            canvas.drawLine(rightEye.x, rightEye.y, mouth.x, mouth.y, paint);
//            mRect.set(leftEye.x, leftEye.y, rightEye.x, rightEye.y);
////            mRect.set(mFaces[i].rect);
//            mMatrix.mapRect(mRect);
//            mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
//                    Math.round(mRect.right), Math.round(mRect.bottom));
//            mFaceIndicator.draw(canvas);
////			canvas.drawRect(mRect, mLinePaint);
//        }

        Point leftEye = mFaces[mFaces.length-1].leftEye;
        Point rightEye = mFaces[mFaces.length-1].rightEye;
        Point mouth = mFaces[mFaces.length-1].mouth;
        Log.d(TAG, "左眼位置:x=" + leftEye.x + ",y=" + leftEye.y);
        Log.d(TAG, "右眼位置:x=" + rightEye.x + ",y=" + rightEye.y);
        Log.d(TAG, "嘴巴位置:x=" + mouth.x + ",y=" + mouth.y);

//        Point midPoint = new Point((leftEye.x+rightEye.x)/2, (leftEye.y+rightEye.y)/2);
//        mMatrix.postRotate((float)(Math.atan2(midPoint.y, midPoint.x)*0.0174f*180f/Math.PI));

        mRect.set(mFaces[mFaces.length - 1].rect);
        mMatrix.mapRect(mRect);
        mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                Math.round(mRect.right), Math.round(mRect.bottom));
        mFaceIndicator.draw(canvas);

        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mPaint = new Paint();//依靠此类开始画线
//            paint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(0x80ff0000);
        mPaint.setStrokeWidth(3);
    }

    public void setImage(Bitmap bm) {
        mFaceIndicator = new BitmapDrawable(mContext.getResources(), bm);
    }

    public  void prepareMatrix(Matrix matrix, boolean mirror, int displayOrientation,
                                     int viewWidth, int viewHeight) {
        // Need mMirror for front camera.
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        matrix.postRotate(displayOrientation);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewWidth / 1500f, viewHeight / 1500f);
        matrix.postTranslate(viewWidth / 2f, viewHeight / 2f);
    }


}
