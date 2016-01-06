
package com.putao.camera.camera.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.putao.camera.R;

public class DrawingFocusView extends View {
    private boolean haveFace;
    private Paint drawingPaint;
    private boolean haveTouch;
    private Rect touchArea;
    private ValueAnimator valueAnimator;
    private int currentValue = 0;
    private int width = 0, height = 0;
    private Bitmap mFocusBitmap;

    public DrawingFocusView(Context context) {
        super(context);
        init();
    }

    public DrawingFocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingFocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setHaveFace(boolean h) {
        haveFace = h;
    }

    private void init() {
        haveFace = false;
        drawingPaint = new Paint();
        drawingPaint.setColor(Color.GREEN);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(2);
        haveTouch = false;
        valueAnimator = ValueAnimator.ofInt(1, 50);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentValue = (Integer) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        valueAnimator.setDuration(300);
        mFocusBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.camera_focus);
        width = mFocusBitmap.getWidth();
        height = mFocusBitmap.getWidth();
        setVisibility(GONE);
    }

    public void setHaveTouch(boolean t, Rect tArea) {
        haveTouch = t;
        touchArea = tArea;
        touchArea.left -= width / 2;
        touchArea.top -= height / 2;
        touchArea.right += width / 2;
        touchArea.bottom += height / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (haveTouch) {
            float left = touchArea.left + (currentValue / 2);
            float top = touchArea.top + (currentValue / 2);
            Bitmap bitmap = Bitmap.createScaledBitmap(mFocusBitmap, width - currentValue, height - currentValue, false);
            canvas.drawBitmap(bitmap, left, top, drawingPaint);
        }
    }

    public void startAnimal() {
        setVisibility(VISIBLE);
        valueAnimator.start();
    }
}
