package com.putao.camera.editor.filtereffect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jidongdong on 14/12/29.
 */
public class GLEffectRender implements GLSurfaceView.Renderer {
    private TextureRenderer mTexRenderer = new TextureRenderer();
    public static String DEFAULT_EFFECT_ID = EffectCollection.none;
    private boolean mInitialized = false;
    private EffectContext mEffectContext;
    private Effect mEffect;
    private List<Effect> mEffectList = new ArrayList<Effect>();
    private String mCurrentEffect = DEFAULT_EFFECT_ID;
    private int[] mTextures = new int[2];
    private int mImageWidth;
    private int mImageHeight;
    private Bitmap originImageBitmap;
//    private Bitmap newImageBitmap;
//    private int w_surface, h_surface;


    public GLEffectRender() {

    }


    public void setOriginBitmap(Bitmap bitmap) {
        originImageBitmap = bitmap;
        loadTextures();
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        if (mTexRenderer != null) {
//            Loger.d("on surface changed===========>" + width + "," + height);
            mTexRenderer.updateViewSize(width, height);
//            w_surface = width;
//            h_surface = height;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!mInitialized) {
            //Only need to do this once
            mEffectContext = EffectContext.createWithCurrentGlContext();
            mTexRenderer.init();
            loadTextures();
            mInitialized = true;
        }
        if (!mCurrentEffect.equals(EffectCollection.none)) {
            //if an effect is chosen initialize it and apply it to the texture
            initEffect();
            applyEffect();
        }
        renderResult();
    }


    public void setCurrentEffect(String effect) {
        mCurrentEffect = effect;
    }


    private void loadTextures() {
        // Generate textures
        GLES20.glGenTextures(2, mTextures, 0);
        // Load input bitmap
        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.water_mark_list_thumbnail_wz_choice07);*/
        if (null == originImageBitmap) {
            originImageBitmap = BitmapFactory.decodeResource(MainApplication.getInstance().getResources(),
                    R.drawable.water_mark_list_thumbnail_wz_choice07);
        }
        mImageWidth = originImageBitmap.getWidth();
        mImageHeight = originImageBitmap.getHeight();
//        Loger.d("mImageWidth,mImageHeight======>" + mImageWidth + "," + mImageHeight);
        mTexRenderer.updateTextureSize(mImageWidth, mImageHeight);
        // Upload to texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, originImageBitmap, 0);
        // Set texture parameters
        GLToolbox.initTexParams();
    }


    private void initEffect() {
        EffectFactory effectFactory = mEffectContext.getFactory();
//        if (mEffect != null) {
//            mEffect.release();
//        }
        if (mEffectList.size() > 0) {
            for (int i = 0; i < mEffectList.size(); i++) {
                mEffectList.get(i).release();
            }
            mEffectList.clear();
        }
        if (mCurrentEffect.equals(EffectCollection.crossprocess)) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_CROSSPROCESS);
            mEffectList.add(mEffect);
            Effect saturate_effect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
            saturate_effect.setParameter("scale", -0.3f);
            mEffectList.add(saturate_effect);
        } else if (mCurrentEffect.equals(EffectCollection.filllight)) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
            mEffect.setParameter("strength", .4f);
            mEffectList.add(mEffect);
            Effect bwEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BLACKWHITE);
            bwEffect.setParameter("black", 0.2f);
            bwEffect.setParameter("white", 0.9f);
            mEffectList.add(bwEffect);
        } else if (mCurrentEffect.equals(EffectCollection.saturate)) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_SATURATE);
            mEffect.setParameter("scale", -0.4f);
            mEffectList.add(mEffect);
            Effect temp_effect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
            temp_effect.setParameter("scale", .6f);
            mEffectList.add(temp_effect);
            Effect bwEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BLACKWHITE);
            bwEffect.setParameter("black", 0.1f);
            bwEffect.setParameter("white", 0.9f);
            mEffectList.add(bwEffect);
        } else if (mCurrentEffect.equals(EffectCollection.temperature)) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
            mEffect.setParameter("scale", .7f);
            mEffectList.add(mEffect);
            Effect effect_brightness = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            effect_brightness.setParameter("brightness", 1.1f);
            mEffectList.add(effect_brightness);
            Effect effect_fill = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
            effect_fill.setParameter("strength", .3f);
            mEffectList.add(effect_fill);
        } else if (mCurrentEffect.equals(EffectCollection.vignette)) {
            mEffect = effectFactory.createEffect(EffectFactory.EFFECT_VIGNETTE);
            mEffect.setParameter("scale", .5f);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.documentary)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_DOCUMENTARY);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.grayscale)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_GRAYSCALE);
            mEffectList.add(mEffect);
            Effect bwEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BLACKWHITE);
            bwEffect.setParameter("black", 0.1f);
            bwEffect.setParameter("white", 0.8f);
            mEffectList.add(bwEffect);
        } else if (mCurrentEffect.equals(EffectCollection.autofix)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_AUTOFIX);
            mEffect.setParameter("scale", 0.9f);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.fisheye)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_FISHEYE);
            mEffect.setParameter("scale", .5f);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.sepia)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_SEPIA);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.lomoish)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_LOMOISH);
            mEffectList.add(mEffect);
        } else if (mCurrentEffect.equals(EffectCollection.brightness)) {
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 1.2f);
            mEffectList.add(mEffect);
            Effect temp_effect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
            temp_effect.setParameter("scale", .3f);
            mEffectList.add(temp_effect);
            Effect effect_fill = effectFactory.createEffect(EffectFactory.EFFECT_FILLLIGHT);
            effect_fill.setParameter("strength", .3f);
            mEffectList.add(effect_fill);

        } else if (mCurrentEffect.equals(EffectCollection.tint)) {
            Effect tint_effect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TINT);
            tint_effect.setParameter("tint", Color.argb(0, 204, 204, 255));
            mEffectList.add(tint_effect);
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 1.2f);
            mEffectList.add(mEffect);
            Effect temp_effect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
            temp_effect.setParameter("scale", .3f);
            mEffectList.add(temp_effect);
        } else if (mCurrentEffect.equals(EffectCollection.none)) {

        } else if (mCurrentEffect.equals(EffectCollection.sketch)) {
            Effect tint_effect = effectFactory.createEffect(
                    EffectFactory.EFFECT_TINT);
            tint_effect.setParameter("tint", Color.argb(0, 204, 204, 255));
            mEffectList.add(tint_effect);
            mEffect = effectFactory.createEffect(
                    EffectFactory.EFFECT_BRIGHTNESS);
            mEffect.setParameter("brightness", 1.2f);
            mEffectList.add(mEffect);
            Effect temp_effect = effectFactory.createEffect(EffectFactory.EFFECT_TEMPERATURE);
            temp_effect.setParameter("scale", .3f);
            mEffectList.add(temp_effect);

        }
        else {

        }
    }

    private void applyEffect() {
        //mEffect.apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);

        if (mEffectList.size() > 0) {
            for (int i = 0; i < mEffectList.size(); i++) {
                if (i == 0) {
                    mEffectList.get(i).apply(mTextures[0], mImageWidth, mImageHeight, mTextures[1]);
                } else {
                    mEffectList.get(i).apply(mTextures[1], mImageWidth, mImageHeight, mTextures[1]);
                }
            }
        }
    }

    private void renderResult() {
        if (!mCurrentEffect.equals(DEFAULT_EFFECT_ID)) {
            // if no effect is chosen, just render the original bitmap
            mTexRenderer.renderTexture(mTextures[1]);
        } else {
            // render the result of applyEffect()
            mTexRenderer.renderTexture(mTextures[0]);
        }
    }
}
