package com.putao.camera.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by jidongdong on 15/3/6.
 */
public class MyTextView extends TextView {
    public int mIndex;
    private Paint paint;
    private boolean bShowRedPoint = false;

    public MyTextView(Context context) {
        super(context);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF0000);
    }

    public void setShowRedPoint(boolean aShowRedPoint) {
        bShowRedPoint = aShowRedPoint;
        invalidate();
    }


    public int getIndex() {
        return mIndex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bShowRedPoint) {
            int width = this.getWidth();
            canvas.drawCircle(width - 15, 15, 15, paint);
        }
    }

}
