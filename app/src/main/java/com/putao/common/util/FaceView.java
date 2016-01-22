package com.putao.common.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.putao.camera.R;
import com.putao.camera.camera.utils.CameraView;


public class FaceView extends ImageView {
    private static final String TAG = "YanZi";
    public static final int UPDATE_FACE_RECT = 0;
    public static final int CAMERA_HAS_STARTED_PREVIEW = 1;

    private Context mContext;
    private Paint mLinePaint;
    private Face[] mFaces;
    private Matrix mMatrix = new Matrix();
    private RectF mRect = new RectF();
    private Drawable mFaceIndicator = null;
    private CameraView mCameraView;

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initPaint();
        mContext = context;
        mFaceIndicator = getResources().getDrawable(R.drawable.ic_face_find_2);
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
        boolean isMirror = false;
        if (mCameraView.cameraId == CameraInfo.CAMERA_FACING_BACK) {
            isMirror = false;
        } else if (mCameraView.cameraId == CameraInfo.CAMERA_FACING_FRONT) {
            isMirror = true;
        }
        Util.prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());
        canvas.save();
        mMatrix.postRotate(0);
        canvas.rotate(-0);
        for (int i = 0; i < mFaces.length; i++) {
            Paint paint = new Paint();//依靠此类开始画线
            paint.setColor(Color.RED);
            Point leftEye = mFaces[i].leftEye;
            Log.d(TAG, "左眼位置:x=" + leftEye.x + ",y=" + leftEye.y);
            Point rightEye = mFaces[i].rightEye;
            Log.d(TAG, "右眼位置:x=" + rightEye.x + ",y=" + rightEye.y);
            Point mouth = mFaces[i].mouth;
            Log.d(TAG, "嘴巴位置:x=" + mouth.x + ",y=" + mouth.y);

            canvas.drawLine(leftEye.x, leftEye.y, rightEye.x, rightEye.y, paint);
//            canvas.drawLine(rightEye.x, rightEye.y, mouth.x, mouth.y, paint);
            mRect.set(mFaces[i].rect);
            mMatrix.mapRect(mRect);
            mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
                    Math.round(mRect.right), Math.round(mRect.bottom));
            mFaceIndicator.draw(canvas);
//			canvas.drawRect(mRect, mLinePaint);
        }

//        Paint paint = new Paint();//依靠此类开始画线
//        paint.setColor(Color.RED);
//        Point leftEye = mFaces[mFaces.length-1].leftEye;
//        Log.d(TAG, "左眼位置:x=" + leftEye.x + ",y=" + leftEye.y);
//        Point rightEye = mFaces[mFaces.length-1].rightEye;
//        Log.d(TAG, "右眼位置:x=" + rightEye.x + ",y=" + rightEye.y);
//        Point mouth = mFaces[mFaces.length-1].mouth;
//        Log.d(TAG, "嘴巴位置:x=" + mouth.x + ",y=" + mouth.y);
//        canvas.drawLine(leftEye.x, leftEye.y, rightEye.x, rightEye.y, paint);
//
//        mRect.set(mFaces[mFaces.length - 1].rect);
////        mRect.set(leftEye.x, leftEye.y, rightEye.x, rightEye.y);
//        mMatrix.mapRect(mRect);
//        mFaceIndicator.setBounds(Math.round(mRect.left), Math.round(mRect.top),
//                Math.round(mRect.right), Math.round(mRect.bottom));
//        mFaceIndicator.draw(canvas);

        canvas.restore();
        super.onDraw(canvas);
    }

    private void initPaint() {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		int color = Color.rgb(0, 150, 255);
        int color = Color.rgb(98, 212, 68);
//		mLinePaint.setColor(Color.RED);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(5f);
        mLinePaint.setAlpha(180);
    }
}
