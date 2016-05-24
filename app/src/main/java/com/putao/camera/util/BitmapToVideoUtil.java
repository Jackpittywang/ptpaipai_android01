package com.putao.camera.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;
import android.view.View;

import com.putao.camera.camera.model.AnimationModel;
import com.putao.camera.camera.model.FaceModel;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    public static int[] decodeYUV420SPrgb565(int[] rgb, byte[] yuv420sp, int width,
                                             int height) {
        final int frameSize = width * height;
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);

            }
        }
        return rgb;
    }

    private static int R = 0;
    private static int G = 1;
    private static int B = 2;

    //I420是yuv420格式，是3个plane，排列方式为(Y)(U)(V)
    public static int[] I420ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int positionOfU = numOfPixel / 4 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];
        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = (i / 2) * (width / 2);
            int startU = positionOfV + step;
            int startV = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = startV + j / 2;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }

        return rgb;
    }

    private static class RGB {
        public int r, g, b;
    }

    private static RGB yuvTorgb(byte Y, byte U, byte V) {
        RGB rgb = new RGB();
        rgb.r = (int) ((Y & 0xff) + 1.4075 * ((V & 0xff) - 128));
        rgb.g = (int) ((Y & 0xff) - 0.3455 * ((U & 0xff) - 128) - 0.7169 * ((V & 0xff) - 128));
        rgb.b = (int) ((Y & 0xff) + 1.779 * ((U & 0xff) - 128));
        rgb.r = (rgb.r < 0 ? 0 : rgb.r > 255 ? 255 : rgb.r);
        rgb.g = (rgb.g < 0 ? 0 : rgb.g > 255 ? 255 : rgb.g);
        rgb.b = (rgb.b < 0 ? 0 : rgb.b > 255 ? 255 : rgb.b);
        return rgb;
    }

    //YV16是yuv422格式，是三个plane，(Y)(U)(V)
    public static int[] YV16ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int positionOfV = numOfPixel / 2 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];
        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width / 2;
            int startU = positionOfU + step;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = startV + j / 2;
                int index = Y * 3;
                //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YV12是yuv420格式，是3个plane，排列方式为(Y)(V)(U)
    public static int[] YV12ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int positionOfU = numOfPixel / 4 + numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = (i / 2) * (width / 2);
            int startV = positionOfV + step;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = startU + j / 2;
                int index = Y * 3;

                //rgb[index+R] = (int)((src[Y]&0xff) + 1.4075 * ((src[V]&0xff)-128));
                //rgb[index+G] = (int)((src[Y]&0xff) - 0.3455 * ((src[U]&0xff)-128) - 0.7169*((src[V]&0xff)-128));
                //rgb[index+B] = (int)((src[Y]&0xff) + 1.779 * ((src[U]&0xff)-128));
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YUY2是YUV422格式，排列是(YUYV)，是1 plane
    public static int[] YUY2ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startY = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int Y1 = j + startY;
                int Y2 = Y1 + 2;
                int U = Y1 + 1;
                int V = Y1 + 3;
                int index = (Y1 >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //UYVY是YUV422格式，排列是(UYVY)，是1 plane
    public static int[] UYVYToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startU = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int U = j + startU;
                int Y1 = U + 1;
                int Y2 = U + 3;
                int V = U + 2;
                int index = (U >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV21是YUV420格式，排列是(Y), (VU)，是2 plane
    public static int[] NV21ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i / 2 * width;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = V + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV12是YUV420格式，排列是(Y), (UV)，是2 plane
    public static int[] NV12ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i / 2 * width;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = U + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV16是YUV422格式，排列是(Y), (UV)，是2 plane
    public static int[] NV16ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfU = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width;
            int startU = positionOfU + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int U = startU + j / 2;
                int V = U + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //NV61是YUV422格式，排列是(Y), (VU)，是2 plane
    public static int[] NV61ToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int positionOfV = numOfPixel;
        int[] rgb = new int[numOfPixel * 3];

        for (int i = 0; i < height; i++) {
            int startY = i * width;
            int step = i * width;
            int startV = positionOfV + step;
            for (int j = 0; j < width; j++) {
                int Y = startY + j;
                int V = startV + j / 2;
                int U = V + 1;
                int index = Y * 3;
                RGB tmp = yuvTorgb(src[Y], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //YVYU是YUV422格式，排列是(YVYU)，是1 plane
    public static int[] YVYUToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startY = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int Y1 = j + startY;
                int Y2 = Y1 + 2;
                int V = Y1 + 1;
                int U = Y1 + 3;
                int index = (Y1 >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //VYUY是YUV422格式，排列是(VYUY)，是1 plane
    public static int[] VYUYToRGB(byte[] src, int width, int height) {
        int numOfPixel = width * height;
        int[] rgb = new int[numOfPixel * 3];
        int lineWidth = 2 * width;
        for (int i = 0; i < height; i++) {
            int startV = i * lineWidth;
            for (int j = 0; j < lineWidth; j += 4) {
                int V = j + startV;
                int Y1 = V + 1;
                int Y2 = V + 3;
                int U = V + 2;
                int index = (U >> 1) * 3;
                RGB tmp = yuvTorgb(src[Y1], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
                index += 3;
                tmp = yuvTorgb(src[Y2], src[U], src[V]);
                rgb[index + R] = tmp.r;
                rgb[index + G] = tmp.g;
                rgb[index + B] = tmp.b;
            }
        }
        return rgb;
    }

    //截取屏幕画面
    public static Bitmap takeScreenShot(Activity activity) {

//View是你需要截图的View
//        View view =activity.getWindow().getCurrentFocus();
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap b1 = view.getDrawingCache();
        //获取状态栏高度
        Rect frame = new Rect();

        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;

        System.out.println(statusBarHeight);//获取屏幕长和高

        int width = activity.getWindowManager().getDefaultDisplay().getWidth();

        int height = activity.getWindowManager().getDefaultDisplay().getHeight();//去掉标题栏

        //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);

        view.destroyDrawingCache();

        return b;

    }//保存到sdcard

    //保存图片
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos)
            {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
