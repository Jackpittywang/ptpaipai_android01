package com.putao.camera.editor.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 滤镜效果缩略图
 */
public class FilterEffectThumbnailView extends ImageView {
    private Paint paint;
    private float scalePro = 1f;
    public float cornerValue = 20.0f;
    private boolean mSelected = false;

    public FilterEffectThumbnailView(Context context) {
        super(context);
        init();
    }

    public FilterEffectThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        paint = new Paint();
    }

    /**
     * 绘制圆角矩形图片
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = getRoundBitmap(bitmap, cornerValue);
            final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
            final Rect rectDest = new Rect(0, 0, getWidth(), getHeight());
            paint.reset();
//            if (mSelected) {
//                paint.setColorFilter(getChangeLight(-80));
//            } else {
//                paint.setColorFilter(getChangeLight(0));
//            }
            canvas.drawBitmap(b, rectSrc, rectDest, paint);
        } else {
            super.onDraw(canvas);
        }
    }

    public void setPhotoSelected(boolean bSelected) {
        mSelected = bSelected;
        invalidate();
    }

    private ColorMatrixColorFilter getChangeLight(int brightness) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        return new ColorMatrixColorFilter(matrix);
    }

    /**
     * 获取圆角矩形图片
     *
     * @param bitmap
     * @param roundPx
     * @return
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, float roundPx) {
        paint.reset();
        Bitmap output = Bitmap.createBitmap((int) (bitmap.getWidth() * scalePro), (int) (bitmap.getHeight() * scalePro), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, (int) (bitmap.getWidth() * scalePro), (int) (bitmap.getHeight() * scalePro));
        final RectF rectF = new RectF(rect);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (mSelected) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5);
            paint.setColor(Color.RED);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        }
        return output;
    }
}