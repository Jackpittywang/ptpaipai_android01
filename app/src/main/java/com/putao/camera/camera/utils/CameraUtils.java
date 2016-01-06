/**
 * Copyright (c) 2013 CommonsWare, LLC
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.putao.camera.camera.utils;

import android.hardware.Camera;
import android.hardware.Camera.Size;

import com.putao.camera.util.DisplayHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {

    public static Size getBestAspectPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters) {
        return (getBestAspectPreviewSize(displayOrientation, width, height, parameters, 0.0d));
    }

    public static Size getBestAspectPreviewSize(int displayOrientation, int width, int height, Camera.Parameters parameters,
                                                       double closeEnough) {
        double targetRatio = (double) width / height;
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        if (displayOrientation == 90 || displayOrientation == 270) {
            targetRatio = (double) height / width;
        }
        List<Size> sizes = parameters.getSupportedPreviewSizes();
        Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(ratio - targetRatio);
            }
            if (minDiff < closeEnough) {
                break;
            }
        }
        return (optimalSize);
    }

    public static Size getBestPictureSize(Camera.Parameters parameters) {
        Size result;
        int maxsize = DisplayHelper.getScreenHeight();
        List<Size> list = parameters.getSupportedPictureSizes();
        result = CameraPictureSizeUtil.getInstance().getPictureSize(list, maxsize);
        return (result);
    }


    private static class SizeComparator implements Comparator<Size> {
        @Override
        public int compare(Size lhs, Size rhs) {
            int left = lhs.width * lhs.height;
            int right = rhs.width * rhs.height;
            if (left < right) {
                return (-1);
            } else if (left > right) {
                return (1);
            }
            return (0);
        }
    }
}
