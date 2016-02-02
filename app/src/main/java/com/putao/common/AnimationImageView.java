package com.putao.common;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.putao.camera.R;
import com.putao.common.util.CameraInterface;

public class AnimationImageView extends ImageView {
	private String TAG = AnimationImageView.class.getName();
	// private Canvas mCanvas;
	private int animationPosition = 0;
	private Handler refreshHandler;
	private AnimationModel animationModel;
	private boolean animationRunning = false;
	private float imageAngle = 0f;
	private float imageScale = 0f;
	// private PointF centerLocation;
	private float centerX;
	private float centerY;

	private RectF mRect = new RectF();
	private Matrix mMatrix = new Matrix();

	
	private Matrix matrix = new Matrix();
    private Matrix matrixRotation = new Matrix();
    private Matrix matrixTranslate = new Matrix();
    private Matrix matrixScale = new Matrix();
	
	private List<Bitmap> bitmapArr = new ArrayList<Bitmap>();
	    
	public AnimationImageView(Context c) {
		super(c);         

	}             

	public AnimationImageView(Context c, AttributeSet attrs) {
		super(c, attrs);

	}
	
	public void setData(AnimationModel model, int screenWidth, int screenHeight){
		bitmapArr.clear();
		animationPosition = 0;
		animationModel = model;
		Options option = new Options();
		option.inScaled = false;
		
	    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0001, option);
	    bitmapArr.add(bitmap1);
	    Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0002, option);
	    bitmapArr.add(bitmap2);
	    Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0003, option);
	    bitmapArr.add(bitmap3);
	    Bitmap bitmap4 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0004, option);
	    bitmapArr.add(bitmap4);
	    Bitmap bitmap5 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0005, option);
	    bitmapArr.add(bitmap5);
	    Bitmap bitmap6 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0006, option);
	    bitmapArr.add(bitmap6);
	    Bitmap bitmap7 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0007, option);
	    bitmapArr.add(bitmap7);
	    Bitmap bitmap8 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0008, option);
	    bitmapArr.add(bitmap8);
	    Bitmap bitmap9 = BitmapFactory.decodeResource(getResources(),
				R.drawable.fd0009, option);
	    bitmapArr.add(bitmap9);
	    
	    
/*		for(int i = 0; i<model.getImageList().size(); i++){
			File file = new File(Environment.getExternalStorageDirectory()+File.separator+"paipai"+File.separator+model.getImageList().get(i));
		    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), option);

			bitmapArr.add(bitmap);
		}*/
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

	/**
	 * 设置位置，放大倍数和角度
	 * @param locationX
	 * @param locationY
	 * @param scale
	 * @param angle
	 */
    public void setPosition(float locationX, float locationY, float scale, float angle){
    	centerX = locationX;
		centerY = locationY;
    	imageAngle = angle;
    	imageScale = scale;
		Log.i(TAG, "locationx, locationy, angle, scale:" + locationX + ":" + locationY + ":" + angle + ":" + scale);
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

	/*

	 */
	public void setPositionByFace(Camera.Face face){

		boolean isMirror = false;
		if(face.leftEye.x == -1000 ) {
			return;
		}
		int Id = CameraInterface.getInstance().getCameraId();
		if (Id == CameraInfo.CAMERA_FACING_BACK) {
			isMirror = false;
		} else if (Id == CameraInfo.CAMERA_FACING_FRONT) {
			isMirror = true;
		}
		prepareMatrix(mMatrix, isMirror, 90, getWidth(), getHeight());

		Rect eyeRect = new Rect();
		eyeRect.left = face.leftEye.x;
		eyeRect.top = face.leftEye.y;
		eyeRect.right = face.rightEye.x;
		eyeRect.bottom = face.rightEye.y;

		mRect.set(eyeRect);
		mMatrix.mapRect(mRect);
		// 脸的原点在左下角，转换到原点左上角
		mRect.top = getHeight() - mRect.top;
		mRect.bottom = getHeight() - mRect.bottom;
		// 计算距离用于计算放大倍数
		float eyeDistance = calDistance(mRect.left, mRect.top, mRect.right, mRect.bottom);
		// 计算旋转角度
		float angle = 0;
		if(mRect.left == mRect.right) angle = (float)3.14/2;
		else{
			if(eyeRect.top>eyeRect.bottom){
				Log.i(TAG, "big ====================>"+eyeRect.left +":" + eyeRect.top+":"+eyeRect.right+":"+eyeRect.bottom);
				Log.i(TAG, "big ====================>"+mRect.left +":" + mRect.top+":"+mRect.right+":"+mRect.bottom);
				angle = (float)Math.atan((mRect.bottom - mRect.top)/(mRect.right - mRect.left));
			}
			else{
				Log.i(TAG, "small ******************>");
				angle = - (float)Math.atan((mRect.bottom - mRect.top)/(mRect.right - mRect.left));
			}
		}
		setPosition((mRect.left + mRect.right)/2, (mRect.top + mRect.bottom)/2, eyeDistance/animationModel.getDistance(), angle);

	}

	private float calDistance(float fromX, float fromY, float toX, float toY){
		return (float) Math.sqrt((toX-fromX)*(toX - fromX)+(toY - fromY)*(toY - fromY));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {	
		canvas.save();
		// Log.i(TAG, "on draw called ...");
		if(animationModel!=null){
	        matrixTranslate.setTranslate(centerX-imageScale* Float.valueOf(animationModel.getCenterX()), centerY - imageScale*Float.valueOf(animationModel.getCenterY()));
	        matrixScale.setScale(imageScale, imageScale);
	        // 此处旋转用的是角度 不是弧度
	        matrixRotation.setRotate((float)(imageAngle*180f/Math.PI), centerX, centerY);
	        matrix.setConcat(matrixRotation, matrixTranslate);
	        matrix.setConcat(matrix, matrixScale);
	        canvas.drawBitmap(bitmapArr.get(animationPosition), matrix, null);
		}
        canvas.restore();
	    
        super.onDraw(canvas); 
	}       
}