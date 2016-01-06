
package com.putao.camera.util;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

public class CameraManager {
    private static CameraManager instance = new CameraManager();
    private int mFrontCameraNo = -1;
    private int mBackCameraNo = -1;

    private CameraManager() {
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            CameraInfo ci = new CameraInfo();
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
                mFrontCameraNo = i;
            } else if (ci.facing == CameraInfo.CAMERA_FACING_BACK) {
                mBackCameraNo = i;
            } else {
            }
        }
    }

    public static CameraManager getInstance() {
        return instance;
    }

    /**
     * 检查是否有前置摄像头
     *
     * @return
     */
    public boolean hasFrontCamera() {
        return mFrontCameraNo == -1 ? false : true;
    }

    /**
     * 获得前置Camera Id, 如果没有则返回-1
     *
     * @return
     */
    public int getFrontCameraNo() {
        return mFrontCameraNo;
    }

    /**
     * 获得后置Camera Id, 如果没有则返回-1
     *
     * @return
     */
    public int getBackCameraNo() {
        return mBackCameraNo;
    }
}
