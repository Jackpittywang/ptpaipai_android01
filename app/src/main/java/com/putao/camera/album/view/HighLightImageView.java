package com.putao.camera.album.view;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.widget.ImageView;


public class HighLightImageView extends ImageView {
    private boolean bEditStatus;

    public HighLightImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
//		setOnTouchListener(onTouchListener);
    }

    public HighLightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
//		setOnTouchListener(onTouchListener);
    }

//	public OnTouchListener onTouchListener = new View.OnTouchListener() {
//		@Override
//		public boolean onTouch(View view, MotionEvent event) {
//			switch (event.getAction()) {
//			case MotionEvent.ACTION_UP:
//				changeLight((ImageView) view, 0);
//				view.performClick();
//				break;
//			case MotionEvent.ACTION_DOWN:
//				changeLight((ImageView) view, -80);
//				break;
//			case MotionEvent.ACTION_MOVE:
//				// changeLight(view, 0);
//				break;
//			case MotionEvent.ACTION_CANCEL:
//				changeLight((ImageView) view, 0);
//				break;
//			default:
//				break;
//			}
//			return super.o;
//		}
//	};

    // public boolean onTouchEvent(MotionEvent event) {
    //
    // switch (event.getAction()) {
    // case MotionEvent.ACTION_UP:
    //
    // break;
    // case MotionEvent.ACTION_DOWN:
    //
    // break;
    // case MotionEvent.ACTION_MOVE:
    // break;
    // case MotionEvent.ACTION_CANCEL:
    // break;
    // default:
    // break;
    // }
    // return true;
    //
    // };

    public void setViewEditStatus(boolean bEdit) {
        bEditStatus = bEdit;
        if (bEdit) {
            changeLight(this, -80);
        } else {
            changeLight(this, 0);
        }
    }

    private void changeLight(ImageView imageview, int brightness) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0,
                brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        imageview.setColorFilter(new ColorMatrixColorFilter(matrix));
    }
}
