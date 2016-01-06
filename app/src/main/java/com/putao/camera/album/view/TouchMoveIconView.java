package com.putao.camera.album.view;

import android.content.Context;
import android.widget.ImageView;

public class TouchMoveIconView extends ImageView {
    private int _xDelta;
    private int _yDelta;

    public TouchMoveIconView(Context context) {
        super(context);
//		this.setOnTouchListener(this);
    }

//	@Override
//	public boolean onTouch(View view, MotionEvent event) {
//		final int X = (int) event.getRawX();
//		final int Y = (int) event.getRawY();
//		switch (event.getAction() & MotionEvent.ACTION_MASK) {
//		case MotionEvent.ACTION_DOWN:
//			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view
//					.getLayoutParams();
//			_xDelta = X - lParams.leftMargin;
//			_yDelta = Y - lParams.topMargin;
//			break;
//		case MotionEvent.ACTION_UP:
//			break;
//		case MotionEvent.ACTION_POINTER_DOWN:
//			break;
//		case MotionEvent.ACTION_POINTER_UP:
//			break;
//		case MotionEvent.ACTION_MOVE:
//			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
//					.getLayoutParams();
//			layoutParams.leftMargin = X - _xDelta;
//			layoutParams.topMargin = Y - _yDelta;
//			layoutParams.rightMargin = -250;
//			layoutParams.bottomMargin = -250;
//			view.setLayoutParams(layoutParams);
//			break;
//		}
//		((View)this.getParent()).invalidate();
//		return true;
//	}

}
