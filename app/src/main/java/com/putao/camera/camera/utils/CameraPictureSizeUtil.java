package com.putao.camera.camera.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

public class CameraPictureSizeUtil {
    private static final String tag = "yan";
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();
    private static CameraPictureSizeUtil mCameraPictureSizeUtil = null;

    private CameraPictureSizeUtil() {
    }

    public static CameraPictureSizeUtil getInstance() {
        if (mCameraPictureSizeUtil == null) {
            mCameraPictureSizeUtil = new CameraPictureSizeUtil();
            return mCameraPictureSizeUtil;
        } else {
            return mCameraPictureSizeUtil;
        }
    }

    public Size getPreviewSize(List<Size> list, int th) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Size s : list) {
            if ((s.width > th) && equalRate(s, 1.33f)) {
                Log.i(tag, "最终设置预览尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        return list.get(i);
    }

    public Size getPictureSize(List<Size> list, int th) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Size s : list) {
            if ((s.width > th) && equalRate(s, 1.77f)) {
                Log.i(tag, "最终设置图片尺寸:w = " + s.width + "h = " + s.height);
                break;
            }
            i++;
        }
        if (i >= list.size()) {
            i = list.size() - 1;
        }
        return list.get(i);
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }

    public class CameraSizeComparator implements Comparator<Size> {
        //按升序排列  
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub  
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}