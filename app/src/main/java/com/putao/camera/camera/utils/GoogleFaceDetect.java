package com.putao.camera.camera.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Handler;
import android.os.Message;


/**
 * 脸部识别实现类
 */

@Deprecated
public class GoogleFaceDetect implements FaceDetectionListener {

    private static final String TAG = "GoogleFaceDetect";

    public static final int UPDATE_FACE_RECT = 0;
    public static final int CAMERA_HAS_STARTED_PREVIEW = 1;

    private Context mContext;
    private Handler mHander;

    public GoogleFaceDetect(Context context, Handler handler) {
        mContext = context;
        mHander = handler;
    }

    @Override
    public void onFaceDetection(Face[] faces, Camera camera) {

        // Log.i(TAG, "onFaceDetection...");
        if (faces != null) {
            Message m = mHander.obtainMessage();
            m.what = UPDATE_FACE_RECT;
            m.obj = faces;
            m.sendToTarget();
        }
    }

}
