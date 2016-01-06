
package com.putao.camera.camera.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class DrawingFocusView2 extends View {
    boolean haveFace;
    Paint drawingPaint;
    boolean haveTouch;
    Rect touchArea;
    ValueAnimator valueAnimator;
    int currentValue = 0;
    int width = 220, height = 220;

    public DrawingFocusView2(Context context) {
        super(context);
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
        valueAnimator.setDuration(300);
    }

    public void setHaveFace(boolean h) {
        haveFace = h;
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
            drawingPaint.setColor(Color.GREEN);
            //            canvas.drawRect(touchArea.left + currentValue, touchArea.top + currentValue, touchArea.right - currentValue, touchArea.bottom
            //                    - currentValue, drawingPaint);
            RectF r2 = new RectF();
            r2.left = touchArea.left + currentValue; //左边  
            r2.top = touchArea.top + currentValue; //上边  
            r2.right = touchArea.right - currentValue; //右边  
            r2.bottom = touchArea.bottom - currentValue; //下边  
            //            canvas.drawRoundRect(touchArea.left + currentValue, touchArea.top + currentValue, touchArea.right - currentValue, touchArea.bottom
            //                    - currentValue, drawingPaint);
            canvas.drawRoundRect(r2, 10, 10, drawingPaint);
        }
    }

    public void startAnimal() {
        valueAnimator.start();
    }
}
