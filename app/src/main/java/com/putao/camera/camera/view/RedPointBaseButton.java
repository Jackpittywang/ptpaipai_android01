
package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RedPointBaseButton extends ImageView {
    private boolean bShowRedPoint = false;
    private Paint paint;

    public RedPointBaseButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF0000);
//        this.doInitWaterMarkBtn();
    }

    public void setShowRedPoint(boolean aShowRedPoint) {
        bShowRedPoint = aShowRedPoint;
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bShowRedPoint) {
            int width = this.getWidth();
            canvas.drawCircle(width - 8, 8, 8, paint);
        }
    }


}
