
package com.putao.camera.camera.utils;

public class OrientationUtil {
    private static int mOrientation = 0;

    public static int getOrientation() {
        return mOrientation;
    }

    public static void setOrientation(int ori) {
        OrientationUtil.mOrientation = ori;
    }
}
