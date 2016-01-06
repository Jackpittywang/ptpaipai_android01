package com.putao.camera.editor.filtereffect;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by jidongdong on 15/3/17.
 */
public class EffectImageTask extends AsyncTask<Void, Void, Bitmap> {
    private Bitmap mBitmap;
    private String mEffect;
    private FilterEffectListener filterEffectListener;

    public EffectImageTask(Bitmap bitmap, String effect, FilterEffectListener listener) {
        mBitmap = bitmap;
        mEffect = effect;
        filterEffectListener = listener;
    }

    public interface FilterEffectListener {
        void rendered(Bitmap bitmap);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            return getRenderedBitmap();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getRenderedBitmap() {
        GLEffectRender renderer = new GLEffectRender();
        PixelBuffer buffer = new PixelBuffer(mBitmap.getWidth(), mBitmap.getHeight());
        buffer.setRenderer(renderer);
        renderer.setOriginBitmap(mBitmap);
        renderer.setCurrentEffect(mEffect);
        Bitmap result = buffer.getBitmap();
        buffer.destroy();
        return result;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (filterEffectListener != null)
            filterEffectListener.rendered(bitmap);
    }
}
