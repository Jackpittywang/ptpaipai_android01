package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.putao.camera.R;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;

public class AlbumButton extends RelativeLayout {
    private Context mContext;
    private ImageView photo_body_iv, photo_anim_iv;
    private Bitmap mCurrentBitmap;

    public AlbumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_album_button, this);
        photo_body_iv = (ImageView) this.findViewById(R.id.photo_body_iv);
        photo_anim_iv = (ImageView) this.findViewById(R.id.photo_anim_iv);
    }

    public void setImageBitmap(final Bitmap bitmap, boolean bAnim) {
        if (bAnim == false) {
            photo_anim_iv.setImageBitmap(clipCircleBitmap(bitmap));
            photo_body_iv.setImageBitmap(clipCircleBitmap(bitmap));
            return;
        }
        photo_anim_iv.setImageBitmap(clipCircleBitmap(bitmap));
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0.1f, 1.0f);
        mAlphaAnimation.setDuration(1000);
        mAlphaAnimation.setFillAfter(true);
        mAlphaAnimation.setFillEnabled(true);
        mAlphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                photo_body_iv.setImageBitmap(clipCircleBitmap(bitmap));
            }
        });
        photo_anim_iv.startAnimation(mAlphaAnimation);
    }

    public Bitmap clipCircleBitmap(Bitmap aSourceBitmap) {
        int targetWidth = DisplayHelper.dipTopx(45);
        int targetHeight = DisplayHelper.dipTopx(45);
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2, ((float) targetHeight - 1) / 2, (Math.min(((float) targetWidth), ((float) targetHeight)) / 2),
                Path.Direction.CCW);
        canvas.clipPath(path);
        Matrix matrix = new Matrix();

        float scale = 1.0f;
        if (aSourceBitmap.getWidth() > aSourceBitmap.getHeight()) {
            scale = (float) targetHeight / aSourceBitmap.getHeight();
        } else {
            scale = (float) targetWidth / aSourceBitmap.getWidth();
        }
        matrix.postScale(scale, scale);
        Bitmap sourceBitmap = Bitmap.createBitmap(aSourceBitmap, 0, 0, aSourceBitmap.getWidth(), aSourceBitmap.getHeight(), matrix, false);
        int cut_x = 0, cut_y = 0;
        if (sourceBitmap.getHeight() > targetHeight) {
            cut_y = (sourceBitmap.getHeight() - targetHeight) / 2;
        }
        if (sourceBitmap.getWidth() > targetWidth) {
            cut_x = (sourceBitmap.getWidth() - targetWidth) / 2;
        }
        canvas.drawBitmap(sourceBitmap, -cut_x, -cut_y, null);
        return targetBitmap;
    }
}
