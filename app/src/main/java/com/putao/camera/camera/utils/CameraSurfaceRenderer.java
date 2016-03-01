package com.putao.camera.camera.utils;

import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;

import com.putao.camera.camera.gles.FullFrameRect;
import com.putao.camera.camera.gles.Texture2dProgram;
import com.putao.camera.util.Loger;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jidongdong on 15/5/25.
 */
public class CameraSurfaceRenderer implements GLSurfaceView.Renderer {
    GlSurfacePreviewStrategy.CameraHandler mCameraHandler;
    private FullFrameRect mFullScreen;
    private final float[] mSTMatrix = new float[16];
    private int mTextureId;

    private int mIncomingWidth;
    private int mIncomingHeight;

    private SurfaceTexture mSurfaceTexture;


    public CameraSurfaceRenderer(GlSurfacePreviewStrategy.CameraHandler handler) {
        mCameraHandler = handler;
        mIncomingWidth = mIncomingHeight = -1;
        mTextureId = -1;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        // Set up the texture blitter that will be used for on-screen display.  This
        // is *not* applied to the recording, because that uses a separate shader.
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));

        mTextureId = mFullScreen.createTextureObject();

        // Create a SurfaceTexture, with an external texture, in this EGL context.  We don't
        // have a Looper in this thread -- GLSurfaceView doesn't create one -- so the frame
        // available messages will arrive on the main thread.
        mSurfaceTexture = new SurfaceTexture(mTextureId);

        // Tell the UI thread to enable the camera preview.
        mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                GlSurfacePreviewStrategy.CameraHandler.MSG_SET_SURFACE_TEXTURE, mSurfaceTexture));
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Loger.d("on surfaceChanged width:" + width + " , height:" + height);
        mCameraHandler.sendMessage(mCameraHandler.obtainMessage(
                GlSurfacePreviewStrategy.CameraHandler.MSG_SET_SURFACE_TEXTURE_SIZE,
                new GlSurfacePreviewStrategy.PtTextureSize(width, height)));
        mIncomingWidth = width;
        mIncomingHeight = height;
//        onDrawFrame(gl);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Loger.d("begin drawing texture-----1");
        if (mSurfaceTexture == null || mFullScreen == null)
            return;
        try {
            mSurfaceTexture.updateTexImage();
        }
        catch (Exception e){

        }
        // Loger.d("begin drawing texture-----2::" + mIncomingWidth + ",  " + mIncomingHeight);
        if (mIncomingWidth <= 0 || mIncomingHeight <= 0) {
            // Texture size isn't set yet.  This is only used for the filters, but to be
            // safe we can just skip drawing while we wait for the various races to resolve.
            // (This seems to happen if you toggle the screen off/on with power button.)
            Loger.w("Drawing before incoming texture size set; skipping");
            return;
        }
        // Loger.d("begin drawing texture..........");
        try {
            mFullScreen.changeProgram(new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT));
            mFullScreen.getProgram().setTexSize(mIncomingWidth, mIncomingHeight);
            // Draw the frame.
            mSurfaceTexture.getTransformMatrix(mSTMatrix);
            mFullScreen.drawFrame(mTextureId, mSTMatrix);
        } catch (Exception e) {
        }

    }

    /**
     * Notifies the renderer thread that the activity is pausing.
     * <p/>
     * For best results, call this *after* disabling Camera preview.
     */
    public void notifyPausing() {
        if (mSurfaceTexture != null) {
            Loger.d("renderer pausing -- releasing SurfaceTexture");
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mFullScreen != null) {
            mFullScreen.release(false);     // assume the GLSurfaceView EGL context is about
            mFullScreen = null;             //  to be destroyed
        }
        mIncomingWidth = mIncomingHeight = -1;
    }

    /**
     * Records the size of the incoming camera preview frames.
     * <p/>
     * It's not clear whether this is guaranteed to execute before or after onSurfaceCreated(),
     * so we assume it could go either way.  (Fortunately they both run on the same thread,
     * so we at least know that they won't execute concurrently.)
     */
    public void setCameraPreviewSize(int width, int height) {
        Loger.d("setCameraPreviewSize");
        mIncomingWidth = width;
        mIncomingHeight = height;
    }

}
