package com.putao.camera.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.util.Log;

import com.putao.camera.camera.model.AnimationModel;
import com.putao.camera.camera.model.FaceModel;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BitmapToVideoUtil {
    private static final String TAG = BitmapToVideoUtil.class.getSimpleName();

    /**
     * 根据贴纸和人脸识别的结果得到合成视频的图片
     *
     * @param faceModel
     * @param currentModel
     * @param bgBmp
     * @param eyeBmps
     * @param mouthBmps
     * @param bottomBmps
     * @return
     */
    public static List<byte[]> getCombineData(FaceModel faceModel, AnimationModel currentModel, Bitmap bgBmp, List<Bitmap> eyeBmps, List<Bitmap> mouthBmps, List<Bitmap> bottomBmps) {
        long time = System.currentTimeMillis();
        List<byte[]> combineBmps = new ArrayList<byte[]>();

        if (faceModel == null || currentModel == null || bgBmp == null) return combineBmps;
        if (eyeBmps == null && mouthBmps == null && bottomBmps == null) return combineBmps;

        int eyeCount = eyeBmps == null ? 0 : eyeBmps.size();
        int mouthCount = mouthBmps == null ? 0 : mouthBmps.size();
        int bottomCount = bottomBmps == null ? 0 : bottomBmps.size();
        int count = Math.max(eyeCount, Math.max(mouthCount, bottomCount));


        //根据识别到的人脸算出各个部位动画的平移，缩放，旋转，等比例
        float angle;
        float[] points = faceModel.landmarks;
        float leftEyeX = (points[19 * 2] + points[20 * 2] + points[21 * 2] + points[22 * 2] + points[23 * 2] + points[24 * 2]) / 6;
        float leftEyeY = (points[19 * 2 + 1] + points[20 * 2 + 1] + points[21 * 2 + 1] + points[22 * 2 + 1] + points[23 * 2 + 1] + points[24 * 2 + 1]) / 6;
        float rightEyeX = (points[25 * 2] + points[26 * 2] + points[27 * 2] + points[28 * 2] + points[29 * 2] + points[30 * 2]) / 6;
        float rightEyeY = (points[25 * 2 + 1] + points[26 * 2 + 1] + points[27 * 2 + 1] + points[28 * 2 + 1] + points[29 * 2 + 1] + points[30 * 2 + 1]) / 6;

        //这个版本的动态贴纸中的mouth其实是nouse，所以此利用nouse的坐标
        float mouthX = (points[10 * 2] + points[10 * 2]) / 2;
        float mouthY = (points[10 * 2 + 1] + points[10 * 2 + 1]) / 2;

        float centerX = (leftEyeX + rightEyeX) / 2;
        float centerY = (leftEyeY + rightEyeY) / 2;

        float eyeDistance = calDistance(leftEyeX, leftEyeY, rightEyeX, rightEyeY);

        // 计算旋转角度
        if (leftEyeX == leftEyeY)
            angle = (float) Math.PI / 2;
        else
            angle = (float) Math.atan((rightEyeY - leftEyeY) / (rightEyeX - leftEyeX));

        float scale = eyeDistance / currentModel.getDistance();

        Matrix matrix;
        Matrix matrixTran;
        Matrix matrixScale;
        Matrix matrixRote;

        //将动态贴纸绘制到人脸图片上
        for (int position = 0; position < count; position++) {
            final Bitmap canvasBmp = Bitmap.createBitmap(bgBmp.getWidth(), bgBmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(canvasBmp);
            canvas.drawBitmap(bgBmp, 0, 0, null);

            if (currentModel.getEye() != null && position < eyeCount) {
                matrix = new Matrix();
                matrixTran = new Matrix();
                matrixScale = new Matrix();
                matrixRote = new Matrix();


                matrixTran.setTranslate(centerX - scale * Float.valueOf(currentModel.getCenterX()), centerY - scale * Float.valueOf(currentModel.getCenterY()));
                matrixScale.setScale(scale, scale);
                // 此处旋转用的是角度 不是弧度
                matrixRote.setRotate((float) (angle * 180f / Math.PI), centerX, centerY);
                matrix.setConcat(matrixRote, matrixTran);
                matrix.setConcat(matrix, matrixScale);

                canvas.drawBitmap(eyeBmps.get(position), matrix, null);
            }
            if (currentModel.getMouth() != null && position < mouthCount) {
                matrix = new Matrix();
                matrixTran = new Matrix();
                matrixScale = new Matrix();
                matrixRote = new Matrix();


                matrixTran.setTranslate(mouthX - scale * Float.valueOf(currentModel.getCenterX()), mouthY - scale * Float.valueOf(currentModel.getCenterY()));
                matrixScale.setScale(scale, scale);
                // 此处旋转用的是角度 不是弧度
                matrixRote.setRotate((float) (angle * 180f / Math.PI), centerX, centerY);
                matrix.setConcat(matrixRote, matrixTran);
                matrix.setConcat(matrix, matrixScale);

                canvas.drawBitmap(mouthBmps.get(position), matrix, null);
            }
            if (currentModel.getBottom() != null && position < bottomCount) {
                matrix = new Matrix();
                matrixTran = new Matrix();
                matrixScale = new Matrix();
                matrixRote = new Matrix();

                float tempScale = (float) bgBmp.getWidth() / (float) bottomBmps.get(position).getWidth();
                matrixTran.setTranslate(0, 0);
                matrixScale.setScale(tempScale, tempScale);
                matrix.setConcat(matrixScale, matrixTran);

                canvas.drawBitmap(bottomBmps.get(position), matrix, null);
            }

            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.restore();

            combineBmps.add(BitmapToVideoUtil.getYUV420sp(canvasBmp.getWidth(), canvasBmp.getHeight(), canvasBmp));
            canvasBmp.recycle();
        }
        Log.d(TAG, "getCombineData:生成数据耗时 " + (System.currentTimeMillis() - time));
        return combineBmps;
    }

    private static float calDistance(float fromX, float fromY, float toX, float toY) {
        return (float) Math.sqrt((toX - fromX) * (toX - fromX) + (toY - fromY) * (toY - fromY));
    }


    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(Bitmap bgBmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bgBmp.compress(Bitmap.CompressFormat.PNG, 90, baos);
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, options);
//        options.inSampleSize = calculateInSampleSize(options, 480, 640);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 将RGB格式的bitmap装换成YUV格式
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] getYUV420sp(int inputWidth, int inputHeight,
                                     Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
//        scaled.recycle();
        return yuv;
    }

    /**
     * RGBתYUV420sp
     *
     * @param yuv420sp inputWidth * inputHeight * 3 / 2
     * @param argb     inputWidth * inputHeight
     * @param width
     * @param height
     */
    private static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width,
                                       int height) {
        final int frameSize = width * height;
        int Y, U, V;
        int yIndex = 0;
        int uvIndex = frameSize;
        int a, R, G, B;
        int argbIndex = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                a = (argb[argbIndex] & 0xff000000) >> 24;
                R = (argb[argbIndex] & 0xff0000) >> 16;
                G = (argb[argbIndex] & 0xff00) >> 8;
                B = (argb[argbIndex] & 0xff);
                argbIndex++;
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                Y = Math.max(0, Math.min(Y, 255));
                U = Math.max(0, Math.min(U, 255));
                V = Math.max(0, Math.min(V, 255));
                yuv420sp[yIndex++] = (byte) Y;
                if ((j % 2 == 0) && (i % 2 == 0)) {
                    yuv420sp[uvIndex++] = (byte) V;
                    yuv420sp[uvIndex++] = (byte) U;
                }
            }
        }
    }

    /**
     * 将YUV格式数据存为bitmap
     *
     * @param data
     * @param width
     * @param height
     * @return
     */
    public static Bitmap yuv2bitmap(byte[] data, int width, int height) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, width, height,
                null);
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, width, height),
                100, out);
        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0,
                imageBytes.length);
        return bitmap;
    }

}
