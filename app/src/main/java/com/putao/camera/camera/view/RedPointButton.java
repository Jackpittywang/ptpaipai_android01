
package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.util.WaterMarkHelper;

public class RedPointButton extends ImageView {
    private boolean bShowRedPoint = false;
    private Paint paint;

    public RedPointButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFF0000);
        this.doInitWaterMarkBtn();
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

    public void doInitWaterMarkBtn() {
        WaterMarkConfigInfo info = WaterMarkHelper.getWaterMarkConfigInfoFromDB(getContext());
        boolean bShowRed = false;
        if (info != null) {
            if (info.content != null) {
                for (int i = 0; i < info.content.photo_watermark.size(); i++) {
                    if (info.content.photo_watermark.get(i).updated.equals("1")) {
                        bShowRed = true;
                        break;
                    }
                }
            }
        }
        this.setShowRedPoint(bShowRed);
    }
}
