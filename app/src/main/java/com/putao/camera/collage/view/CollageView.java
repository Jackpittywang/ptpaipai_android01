package com.putao.camera.collage.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.putao.camera.R;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.JNIFUN;
import com.putao.camera.editor.view.TextWaterMarkView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jidongdong on 15/1/28.
 */
public class CollageView extends View {
    private final static String TAG = "CollageView";
    private Bitmap img_sample = null;
    private List<CollagePhotoSet> mPhotoList;
    private ArrayList<CollageConfigInfo.CollageText> mTextList;
    private float down_x, down_y;
    private int touchIndex = -1;
    private int touchIndex2 = -1;
    private Paint mPaint = new Paint();
    private int mViewWidth = Integer.MAX_VALUE;
    private int mViewHeight = Integer.MAX_VALUE;
    PointF mid = new PointF();
    private OperateMode currentMode = OperateMode.NONE;
    float oldDistacne = 1f;
    float zoomRate = 1.0f;
    private Matrix currentMatrix = new Matrix();
    private boolean isDraged = false;
    private boolean isZoomed = false;
    private float border_move_x;
    private float border_move_y;

    private OnPhotoItemOnClick mOnPhotoItemOnClick;
    private int mCounter;
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            mCounter--;
            if (mCounter > 0 || currentMode == OperateMode.NONE || isDraged || isZoomed)
                return;
            currentMode = OperateMode.SWAP;
            invalidate();
        }
    };

    public CollageView(Context context) {
        super(context);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * 计算两个点的中点坐标
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    private void init(Context context) {
        mPaint.setAntiAlias(true);
        img_sample = getBitmapFromRes(context, R.drawable.module);
        mPhotoList = new ArrayList<CollagePhotoSet>();
        mTextList = new ArrayList<CollageConfigInfo.CollageText>();
    }

    /**
     * 设置模板遮罩图片
     *
     * @param sampleImage
     */
    public void setSampleImage(Bitmap sampleImage) {
        if (sampleImage == null) {
            throw new IllegalArgumentException("mask image is null");
        }
        img_sample = sampleImage;
        if (img_sample != null) {
            mViewWidth = img_sample.getWidth();
            mViewHeight = img_sample.getHeight();
        }
        requestLayout();
        invalidate();
    }

    /**
     * 设置模板照片列表
     *
     * @param photolist
     */
    public void setImageList(ArrayList<CollagePhotoSet> photolist) {
        mPhotoList = photolist;
        invalidate();
    }

    public List<CollagePhotoSet> getPhotoList() {
        return mPhotoList;
    }


    public void changeSourcePhotoSet(Bitmap bitmap, int index) {
        mPhotoList.get(index).setSourcePhoto(bitmap);
        invalidate();
    }

    /**
     * 设置拼图上的文字信息
     *
     * @param textlist
     */
    public void setTextList(ArrayList<CollageConfigInfo.CollageText> textlist) {
        mTextList = textlist;
    }

    public void setOnPhotoItemOnClick(OnPhotoItemOnClick listener) {
        mOnPhotoItemOnClick = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (mViewWidth != Integer.MAX_VALUE) ? mViewWidth : widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (mViewHeight != Integer.MAX_VALUE) ? mViewHeight
                    : heightSize;
        }
        setMeasuredDimension(width, height);
    }

    /**
     * 小到大的排序
     *
     * @param targetArr
     */
    public void sort(float[] targetArr) {
        float temp;
        for (int i = 0; i < targetArr.length; i++) {
            for (int j = i; j < targetArr.length; j++) {
                if (targetArr[i] > targetArr[j]) {
                    temp = targetArr[i];
                    targetArr[i] = targetArr[j];
                    targetArr[j] = temp;
                }
            }
        }
    }

    private Bitmap clipPolygonBitmap(CollagePhotoSet photo, int index) {
        Bitmap bit = clipPolygonBitmap(photo.Photo, photo.Points, photo.matrix, photo.ShowRect);
        if (mPhotoList.get(index).ClipPhoto != null) {
            mPhotoList.get(index).ClipPhoto.recycle();
            mPhotoList.get(index).ClipPhoto = bit;
        }
        return bit;
    }

    private Bitmap clipPolygonBitmap(Bitmap bitmap, Area area, Matrix matrix, Rect rect) {
        Bitmap bit = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(bit);
        canvas.drawARGB(0, 0, 0, 0);
        Path path = new Path();
        path.reset();
        path.moveTo(area.PloyX[0] - rect.left, area.PloyY[0] - rect.top);
        for (int i = 1; i < area.PloyX.length; i++) {
            path.lineTo(area.PloyX[i] - rect.left, area.PloyY[i] - rect.top);
        }
        path.close();
        canvas.clipPath(path, Region.Op.INTERSECT);
        canvas.drawBitmap(bitmap, matrix, paint);
        return bit;
    }


    private void drawCollagePath(Canvas canvas, Path path) {
        Paint borderpaint = new Paint();
        borderpaint.setStyle(Paint.Style.STROKE);
        borderpaint.setAntiAlias(true);
        borderpaint.setColor(Color.RED);
        borderpaint.setStrokeWidth(2);
        canvas.drawPath(path, borderpaint);
    }

    private Bitmap getBitmapFromRes(Context context, int resId) {
        return ((BitmapDrawable) context.getResources().getDrawable(resId))
                .getBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (img_sample != null && canvas != null) {
            canvas.drawARGB(0, 0, 0, 0);
            try {
                canvas.save();
                for (int j = 0; j < mPhotoList.size(); j++) {
                    CollagePhotoSet set = mPhotoList.get(j);
                    Bitmap bitmap = set.ClipPhoto;
                    if (touchIndex == j && touchIndex > -1) {
                        bitmap = clipPolygonBitmap(set, j);
                    }
                    if (bitmap != null) {
                        canvas.drawBitmap(bitmap, set.ShowRect.left, set.ShowRect.top, mPaint);
                    }
                }
                canvas.restore();
                canvas.drawBitmap(img_sample, 0, 0, null);
                if (currentMode == OperateMode.SWAP) {
                    DrawRectBorder(canvas);
                }
                DrawText(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                currentMode = OperateMode.DRAG;
                isZoomed = false;
                isDraged = false;
                down_x = event.getX();
                down_y = event.getY();
                touchIndex = getTouchAreaIndex(down_x, down_y);
                if (touchIndex > -1) {
                    currentMatrix.set(mPhotoList.get(touchIndex).matrix);
                }
                mCounter++;
                postDelayed(mLongPressRunnable, 1000);
                break;
            case MotionEvent.ACTION_MOVE:
                if (currentMode == OperateMode.DRAG) {
                    float move_x = event.getX() - down_x;
                    float move_y = event.getY() - down_y;
                    float move_distance = FloatMath.sqrt(move_x * move_x + move_y
                            * move_y);
                    if (touchIndex > -1 && move_distance > 10f) {
                        isDraged = true;
                        down_x = event.getX();
                        down_y = event.getY();
                        float[] values = new float[9];
                        currentMatrix.getValues(values);
                        float dx = checkDxBound(mPhotoList.get(touchIndex), values, move_x);
                        float dy = checkDyBound(mPhotoList.get(touchIndex), values, move_y);
                        currentMatrix.postTranslate(dx, dy);
                        mPhotoList.get(touchIndex).matrix.set(currentMatrix);
                        invalidate();
                    }
                } else if (currentMode == OperateMode.ZOOM) {
                    float newDistance = spacing(event);
                    zoomRate = newDistance / oldDistacne;
                    if ((touchIndex == touchIndex2) && (touchIndex > -1)) {
                        isZoomed = true;
                        Rect sr = mPhotoList.get(touchIndex).ShowRect;
                        currentMatrix.postScale(zoomRate, zoomRate, sr.width() / 2, sr.height() / 2);
                        float[] curren_values = new float[9];
                        currentMatrix.getValues(curren_values);
                        float scale_x = curren_values[Matrix.MSCALE_X];
                        oldDistacne = newDistance;
                        if (scale_x < 1.0f) {
                            float zoom_ratio = 1.0f / scale_x;
                            currentMatrix.postScale(zoom_ratio, zoom_ratio, sr.width() / 2, sr.height() / 2);
                        }
                        mPhotoList.get(touchIndex).matrix.set(currentMatrix);
                        invalidate();
                    }
                } else if (currentMode == OperateMode.SWAP) {
                    float move_x = event.getX() - down_x;
                    float move_y = event.getY() - down_y;
                    float move_distance = FloatMath.sqrt(move_x * move_x + move_y
                            * move_y);
                    if (touchIndex > -1 && move_distance > 10f) {
                        down_x = event.getX();
                        down_y = event.getY();
                        border_move_x += move_x;
                        border_move_y += move_y;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                currentMode = OperateMode.ZOOM;
                touchIndex2 = getTouchAreaIndex(event.getX(1), event.getY(1));
                oldDistacne = spacing(event);
                if ((touchIndex == touchIndex2) && (touchIndex > -1)) {
                    mPhotoList.get(touchIndex).matrix.set(currentMatrix);
                    midPoint(mid, event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == OperateMode.SWAP) {
                    swapPhoto(getTouchAreaIndex(event.getX(), event.getY()));
                    border_move_x = 0;
                    border_move_y = 0;
                } else {
                    if (touchIndex > -1 && !isZoomed && !isDraged && mOnPhotoItemOnClick != null)
                        mOnPhotoItemOnClick.onClicked(touchIndex);
                }
                currentMode = OperateMode.NONE;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    void swapPhoto(int target) {
        if (target < 0 || target == touchIndex)
            return;
        Bitmap src = mPhotoList.get(touchIndex).Photo;
        Bitmap tar = mPhotoList.get(target).Photo;
        changeSourcePhotoSet(src, target);
        changeSourcePhotoSet(tar, touchIndex);
    }

    public interface OnPhotoItemOnClick {
        void onClicked(int index);
    }

    void DrawRectBorder(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(5);
        if (touchIndex > -1) {
            Area area = mPhotoList.get(touchIndex).Points;
            float[] x = area.PloyX;
            float[] y = area.PloyY;
            canvas.save();
            canvas.translate(border_move_x, border_move_y);
            canvas.drawRoundRect(new RectF(x[0], y[0], x[1], y[3]), 10, 10, paint);
            canvas.restore();
        }
    }

    void DrawText(Canvas canvas) {
        for (int i = 0; i < mTextList.size(); i++) {
            CollageConfigInfo.CollageText textobj = mTextList.get(i);
            // Loger.d(textobj.text + "|" + textobj.left + "," + textobj.top +
            // ","
            // + textobj.right + "," + textobj.bottom + "|"
            // + textobj.textSize);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(textobj.textSize);
            paint.setColor(Color.parseColor(textobj.textColor));
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.DEFAULT);
            Paint.FontMetrics fm = paint.getFontMetrics();

            RectF targetRect = new RectF(textobj.left, textobj.top,
                    textobj.right, textobj.bottom);
            float baseline = targetRect.top
                    + (targetRect.bottom - targetRect.top - fm.bottom + fm.top)
                    / 2 - fm.top;
            float text_width = paint.measureText(textobj.text);
            float draw_text_left = targetRect.left;
            if (textobj.textAlign
                    .equals(TextWaterMarkView.WaterTextAlign.CENTER)) {
                draw_text_left = targetRect.centerX() - text_width / 2;
            } else if (textobj.textAlign
                    .equals(TextWaterMarkView.WaterTextAlign.RIGHT)) {
                draw_text_left = targetRect.right - text_width;
            }
            canvas.drawText(textobj.text, draw_text_left, baseline, paint);
        }
    }

    private int getTouchAreaIndex(float x, float y) {
        int index = -1;
        for (int i = mPhotoList.size() - 1; i > -1; i--) {
            Area points = mPhotoList.get(i).Points;
            int r = JNIFUN.pointInPolygon(points.PloyX.length, points.PloyX,
                    points.PloyY, x, y);
            if (r > 0) {
                index = i;
                break;
            }
        }
        return index;
    }

    private float checkDxBound(CollagePhotoSet set, float[] values, float dx) {
        float width = set.ShowRect.width();
        float image_width = set.Photo.getWidth();
        if (image_width * values[Matrix.MSCALE_X] < width)
            return 0;
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(image_width * values[Matrix.MSCALE_X] - width))
            dx = -(image_width * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        return dx;
    }

    private float checkDyBound(CollagePhotoSet set, float[] values, float dy) {
        float height = set.ShowRect.height();
        float image_height = set.Photo.getHeight();
        if (image_height * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(image_height * values[Matrix.MSCALE_Y] - height))
            dy = -(image_height * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }

    public float[] getScalePloyX(float[] ployx, float scale) {
        float[] new_ployX = new float[ployx.length];
        StringBuilder new_y = new StringBuilder();
        for (int i = 0; i < ployx.length; i++) {
            new_ployX[i] = ployx[i] * scale;
            new_y.append(new_ployX[i] + ",");
        }
        return new_ployX;
    }

    public float[] getScalePloyY(float[] ployy, float scale) {
        float[] new_ployY = new float[ployy.length];
        StringBuilder new_y = new StringBuilder();
        for (int i = 0; i < ployy.length; i++) {
            new_ployY[i] = ployy[i] * scale;
            new_y.append(new_ployY[i] + ",");
        }
        return new_ployY;
    }

    enum OperateMode {
        NONE,
        DRAG,
        ZOOM,
        SWAP
    }

    public class CollageTextType {
        public final static String CURRENT_CITY = "CURRENT_CITY";
        public final static String CURRENT_TIME = "CURRENT_TIME";
    }

    /**
     * 模板源图片
     */
    public class CollagePhotoSet {
        public Bitmap Photo;
        public Bitmap ClipPhoto;
        public Matrix matrix = new Matrix();
        // 显示区域坐标点
        public Area Points;

        public Rect ShowRect = null;

        public CollagePhotoSet(Bitmap photo, Area points) {
            Points = points;
            initshowRect();
            setSourcePhoto(photo);
        }

        void setSourcePhoto(Bitmap bitmap) {
            Photo = bitmap;
            float bit_w = ShowRect.width();
            float bit_h = ShowRect.height();
            Matrix matrix_scale = new Matrix();
            float scale = 1f;
            if ((Photo.getHeight() / bit_h) >= (Photo.getWidth() / bit_w)) {
                scale = bit_w / Photo.getWidth();
            } else {
                scale = bit_h / Photo.getHeight();
            }
            matrix_scale.postScale(scale, scale);
            Photo = Bitmap.createBitmap(Photo, 0, 0, Photo.getWidth(), Photo.getHeight(), matrix_scale, false);
            setClipPhoto();
        }

        void setClipPhoto() {
            ClipPhoto = clipPolygonBitmap(Photo, Points, matrix, ShowRect);
        }

        // 计算图片显示区域
        void initshowRect() {
            float[] area_x = Points.PloyX.clone();
            float[] area_y = Points.PloyY.clone();
            sort(area_x);
            sort(area_y);
            ShowRect = new Rect((int) area_x[0], (int) area_y[0], (int) area_x[area_x.length - 1], (int) area_y[area_y.length - 1]);
        }

    }

    /**
     * 表示一张拼图上的单个显示图片区的坐标点（顺时针）
     */
    public class Area {
        public float[] PloyX;
        public float[] PloyY;

        public Area(float[] ployX, float[] ployY) {
            PloyX = ployX;
            PloyY = ployY;
        }
    }
}
