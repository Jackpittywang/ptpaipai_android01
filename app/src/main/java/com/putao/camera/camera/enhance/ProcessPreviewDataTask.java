package com.putao.camera.camera.enhance;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.putao.camera.JNIFUN;
import com.putao.camera.util.Loger;

/**
 * Created by jidongdong on 15/5/22.
 */
public class ProcessPreviewDataTask extends AsyncTask<byte[], Void, Boolean> {

    Camera mCamera;
    int width, height;
    boolean mProcessInProgress = false;

    public ProcessPreviewDataTask(Camera camera, int w, int h) {
        mCamera = camera;
        width = w;
        height = h;
    }

    @Override
    protected Boolean doInBackground(byte[]... params) {
        byte[] data = params[0];
        if (data != null) {
            byte[] reslut = JNIFUN.PTEnhanceCameraPreview(data, width, height);
            Loger.d("end:" + SystemClock.currentThreadTimeMillis());
            mCamera.addCallbackBuffer(reslut);
            Loger.d("begin:" + SystemClock.currentThreadTimeMillis());
            mProcessInProgress = false;
        }
        return true;
    }
}
