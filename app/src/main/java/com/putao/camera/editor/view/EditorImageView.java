
package com.putao.camera.editor.view;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.putao.camera.util.Loger;

public class EditorImageView extends ImageView {
    public int actW;
    public int actH;
    public float scaleX;
    public float scaleY;

    public EditorImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        getImageMatrix().getValues(f);
        // Extract the scale values using the constants (if aspect ratio
        // maintained, scaleX == scaleY)
        scaleX = f[Matrix.MSCALE_X];
        scaleY = f[Matrix.MSCALE_Y];
        // Get the drawable (could also get the bitmap behind the drawable and
        // getWidth/getHeight)
        final Drawable d = getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();
        // Calculate the actual dimensions
        actW = Math.round(origW * scaleX);
        actH = Math.round(origH * scaleY);
        Loger.d("EditorImageView" + "[" + origW + "," + origH + "] -> [" + actW + "," + actH + "] & scales: x=" + scaleX + " y=" + scaleY);
    }
}
