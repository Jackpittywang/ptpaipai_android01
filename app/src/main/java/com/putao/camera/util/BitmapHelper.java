package com.putao.camera.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.LruCache;

import com.putao.camera.application.MainApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

public class BitmapHelper {
//    Canvas mCanvas;

    public BitmapHelper() {
//        mCanvas = new Canvas();
    }


    ActivityManager am = (ActivityManager) MainApplication.getInstance().getSystemService(MainApplication.getInstance().ACTIVITY_SERVICE);
    int memClassBytes = am.getMemoryClass() * 1024 * 1024;
    int cacheSize = memClassBytes / 4;
    LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getByteCount();
        }
    };

    public Bitmap loadBitmapByThumbiId(String id) {
        Bitmap bitmap = getBitmapFromMemoryCache(id);
        if (bitmap == null || bitmap.isRecycled()) {
            bitmap = PhotoLoaderHelper.getThumbnailLocalBitmap(id);
            addBitmapToMemoryCache(id, bitmap);
        } else {
        }
        return bitmap;
    }

    public Bitmap loadBitmap(String url) {
        return loadBitmap(url, DisplayHelper.getScreenWidth(), DisplayHelper.getScreenHeight());
    }

    public Bitmap loadBitmap(String url, int width, int height) {
        Bitmap bitmap = getBitmapFromMemoryCache(url);
        if (bitmap == null || bitmap.isRecycled()) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(url, options);
            int sampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            bitmap = PhotoLoaderHelper.getLocalBitmap(url, sampleSize);
            if (url != null && bitmap != null) {
                addBitmapToMemoryCache(url, bitmap);
            }
        }
        return bitmap;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return mMemoryCache.get(key);
    }

    private static BitmapHelper instance;

    public static BitmapHelper getInstance() {
        if (instance == null) {
            instance = new BitmapHelper();
        }
        return instance;
    }

    //    // 从Resources中加载图片
    //    public static Bitmap decodeSampledBitmapFromResource(Resources res,
    //                                                         int resId, int reqWidth, int reqHeight) {
    //        final BitmapFactory.Options options = new BitmapFactory.Options();
    //        options.inJustDecodeBounds = true;
    //        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
    //        options.inSampleSize = calculateInSampleSize(options, reqWidth,
    //                reqHeight); // 计算inSampleSize
    //        options.inJustDecodeBounds = false;
    //        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
    //        return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
    //    }
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = (int) (height / (float) reqHeight);
            int widthRatio = (int) (width / (float) reqWidth);
            inSampleSize = (heightRatio < widthRatio) ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    private static int calculateInSampleSizebak(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public Bitmap getBitmapFromPath(String path) {
        try {
            return BitmapFactory.decodeFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setImageFileOrientarionMatrix(String filePath, Matrix matrix) {
        int ori = BitmapHelper.getImageFileOrientation(filePath);
        if (ori != ExifInterface.ORIENTATION_NORMAL) {
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.preScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.preScale(1, -1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.preScale(1, -1);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(270);
                    matrix.preScale(1, -1);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        }
    }

    public Bitmap getBitmapFromPathWithSize(String path, int targetwidth, int targetheight) {
        try {
            Bitmap result = BitmapFactory.decodeFile(path, null);
//            if (result.getWidth() > targetwidth || result.getHeight() > targetheight)
            {
                Matrix matrix = new Matrix();
                float ratio_current = result.getWidth() / (float) result.getHeight();
                float ratio_target = targetwidth / (float) targetheight;
                float scale = 1.0f;
                if (ratio_current < ratio_target) {//以高为准压缩
                    scale = (float) targetheight / result.getHeight();
                    matrix.postScale(scale, scale);
                } else {//以宽为准压缩
                    scale = (float) targetwidth / result.getWidth();
                    matrix.postScale(scale, scale);
                }
                setImageFileOrientarionMatrix(path, matrix);
                result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, false);
            }
            Loger.d("result size----->" + result.getWidth() + "," + result.getHeight());
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public Bitmap getCenterCropBitmap(String path, int targetwidth, int targetheight) {
        try {
            Bitmap result = BitmapFactory.decodeFile(path, null);
//            if (result.getWidth() > targetwidth || result.getHeight() > targetheight) 
            {
                Matrix matrix = new Matrix();
                float ratio_current = result.getWidth() / (float) result.getHeight();
                float ratio_target = targetwidth / (float) targetheight;
                float scale = 1.0f;
                if (ratio_current >= ratio_target) {//以高为准压缩
                    scale = (float) targetheight / result.getHeight();
                    matrix.postScale(scale, scale);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, false);
                    result = Bitmap.createBitmap(result, (result.getWidth() - targetwidth) / 2, 0, targetwidth, targetheight);
                } else {//以宽为准压缩
                    scale = (float) targetwidth / result.getWidth();
                    matrix.postScale(scale, scale);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, false);
                    result = Bitmap.createBitmap(result, 0, (result.getHeight() - targetheight) / 2, targetwidth, targetheight);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap drawableToBitmap(Context context, Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        if (DisplayHelper.getDensity() < 3) {
            w = drawable.getIntrinsicWidth();
            h = drawable.getIntrinsicHeight();
        } else if (DisplayHelper.getDensity() >= 3) {
            w = drawable.getIntrinsicWidth() + 100;
            h = drawable.getIntrinsicHeight() + 100;
        }
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap;
        bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getLoadingDrawable(int w, int h) {
        return new BitmapDrawable(getLoadingBitmap(w, h));
    }

    public static Drawable getLoadingDrawable() {
        return new BitmapDrawable(getLoadingBitmap());
    }

    public static Bitmap getLoadingBitmap() {
        int bmpWith = DisplayHelper.dipTopx(100);
        int bmpHeight = bmpWith;

        return getLoadingBitmap(bmpWith, bmpHeight);
    }

    public static Bitmap getLoadingBitmap(int bmpWith, int bmpHeight) {
        int[] colors = {0xff7a4e6d, 0xffb9cc7d, 0xffce9e8b, 0xff758c4d, 0xffeae989, 0xff9ab9bb, 0xff79bbff, 0xffdd9095, 0xff2b304a, 0xffc7bcb4, 0xffd7d7ce,
                0xffb0eccb, 0xffdabe8e, 0xffad9882, 0xff979eb4, 0xfff6e6db, 0xffe29b3a, 0xff546ea2};

        Bitmap bitmap = Bitmap.createBitmap(bmpWith, bmpHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        int randomIndex = new Random(System.currentTimeMillis()).nextInt(colors.length);
        int color = colors[randomIndex];
        canvas.drawColor(color);

//        String text = "Loading...";
//        paint.setColor(Color.WHITE);
//        float textSize = DisplayHelper.dipTopx(12);
//        paint.setTextSize(textSize);
//        int textLength = (int) paint.measureText(text);
//        int text_y;
//        if (bmpHeight > textSize * 3) {
//            text_y = (int) (bmpHeight - textSize * 3);
//        } else {
//            text_y = (int) ((bmpHeight - textSize) / 2);
//        }
//        canvas.drawText(text, 0, text.length(), (bmpWith - textLength) >> 1, text_y, paint);

        return bitmap;
    }

    /**
     * 以radius为半径,以color为颜色的实心圆
     *
     * @param color
     * @param radius
     * @return bitmap
     */
    public static Bitmap getCircleBitmap(int color, int radius) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawColor(Color.TRANSPARENT);
        paint.setColor(color);
        canvas.drawCircle(radius, radius, radius, paint);
        return bitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * ARGB8888 CONVERT TO RGBA8888
     *
     * @param pix
     */
    public static int[] ARGBTORGBA(int[] pix) {
        int[] piexs = pix.clone();
        for (int i = 0; i < piexs.length; i++) {
            int pixel = piexs[i];
            piexs[i] = (pixel << 8) | ((pixel >> 24) & 0xFF);
        }
        return piexs;
    }

    /**
     * 获取图片的旋转角度
     *
     * @param filePath
     * @return
     */
    public static int getImageFileOrientation(String filePath) {
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(filePath);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            return orientation;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ExifInterface.ORIENTATION_NORMAL;
    }


    /**
     * Takes an orientation and a bitmap, and returns the bitmap transformed
     * to that orientation.
     */
    public static Bitmap orientBitmap(Bitmap bitmap, int ori) {
        Matrix matrix = new Matrix();
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (ori == ExifInterface.ORIENTATION_ROTATE_90 ||
                ori == ExifInterface.ORIENTATION_ROTATE_270 ||
                ori == ExifInterface.ORIENTATION_TRANSPOSE ||
                ori == ExifInterface.ORIENTATION_TRANSVERSE) {
            int tmp = w;
            w = h;
            h = tmp;
        }
        switch (ori) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270, w / 2f, h / 2f);
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.preScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.preScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90, w / 2f, h / 2f);
                matrix.preScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(270, w / 2f, h / 2f);
                matrix.preScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }
}
