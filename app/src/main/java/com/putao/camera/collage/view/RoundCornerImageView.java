package com.putao.camera.collage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by jidongdong on 15/4/14.
 * <p/>
 * 小米 MI-ONE Plus:RoundCornerImageView.onDraw  java.lang.UnsupportedOperationException
 */
public class RoundCornerImageView extends ImageView {

    private Paint roundPaint = new Paint();


    public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RoundCornerImageView(Context context) {
        super(context);
        init();
    }

    public RoundCornerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
//        roundPaint.setColor(getResources().getColor(R.color.color_bfbfbf));
//        roundPaint.setStrokeWidth(2);
//        roundPaint.setStyle(Paint.Style.STROKE);
        roundPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            Path clipPath = new Path();
            int w = this.getWidth();
            int h = this.getHeight();
            clipPath.addRoundRect(new RectF(0, 0, w, h), 10.0f, 10.0f, Path.Direction.CW);
            canvas.clipPath(clipPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDraw(canvas);
//        canvas.drawRect(new RectF(0, h - 8, w, 8), roundPaint);
    }
}
