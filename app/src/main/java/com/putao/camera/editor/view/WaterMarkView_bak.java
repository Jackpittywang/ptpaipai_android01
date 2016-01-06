package com.putao.camera.editor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.putao.camera.R;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.Loger;

/**
 * Created by ji dong dong
 */
public abstract class WaterMarkView_bak extends ImageView {
    Context mContext;
    /**
     * point click down pos x
     */
    float x_down = 0;
    /**
     * point click down pos y
     */
    float y_down = 0;
    /**
     * point click down pos invert x
     */
    float x_down_convert = 0;
    /**
     * point click down pos invert x
     */
    float y_down_convert = 0;
    /**
     *
     */
    PointF mid = new PointF();
    /**
     *
     */
    float oldDist = 1f;
    /**
     * global zoom scale
     */
    float zoomScale = 1f;
    /**
     *
     */
    float oldRotation = 0;
    /**
     * the water mark finally matrix
     */
    Matrix matrix = new Matrix();
    /**
     *
     */
    Matrix matrix1 = new Matrix();
    /**
     *
     */
    Matrix savedMatrix = new Matrix();
    /**
     * represent operation modes
     */
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    /**
     * represent current operation mode
     */
    int mode = NONE;
    /**
     * record last operate mode
     */
    int last_mode = NONE;
    boolean lastOptIsZoom = false;
    /**
     * the screen width
     */
    int widthScreen;
    /**
     * the screen height
     */
    int heightScreen;
    /**
     *
     */
    Bitmap mWaterBitmap, mWaterBitmapEdit, mWaterBitmapNormal, removeWater;
    /**
     * record click times,use for performLongClick
     */
    private int mCounter;
    /**
     * define long press runnable
     */
    private Runnable mLongPressRunnable;
    /**
     * represent whether the water mark is moved
     */
    private boolean isMoved;
    /**
     * represent whether the point is away screen
     */
    private boolean isReleased;
    /**
     * the water mark image width
     */
    int wmb_width;
    /**
     * the water mark image height
     */
    int wmb_height;
    /**
     * represent whether the water mark is in editing
     */
    boolean isEditState = false;
    /**
     *
     */
    private RectF mWaterRect, removeRect;
    /**
     * remove the water event listener
     */
    private OnRemoveWaterListener mOnRemoveWaterListener;
    /**
     * represent the click point is validity
     */
    boolean isvalidity = false;
    /**
     * the paint use for painting the water mark image
     */
    Paint MARK_PAINT = new Paint();
    /**
     * water mark position weather changed
     */
    private boolean isPositonChanged = false;
    /**
     * water mark is zoomed
     */
    private boolean isZoomed = false;
    /**
     * wheather watermark is zoomed in
     */
    private boolean isZoomedIn = false;
    /**
     * wheather watermark is zoomed out
     */
    private boolean isZoomedOut = false;
    /**
     * 默认图片缩放系数
     */
    private float size_scale = 1.0f;
    //移除按钮图片超出的宽度和高度
    float extra_width = 25;
    float extra_height = 25;

    boolean mIsCanRemove = false;

    /**
     * 屏幕的方向角度
     */
    float rotationDegree = 0;


    public float getRotationDegree() {
        return rotationDegree;
    }

    public void setRotationDegree(float rotationDegree) {
        this.rotationDegree = rotationDegree;
    }

    /**
     * Construct a WaterMarkView with context and a bitmap
     *
     * @param context
     */
    public WaterMarkView_bak(Context context, Bitmap watermark) {
        super(context);
        mContext = context;
        mWaterBitmap = watermark;
        init();
    }

    public WaterMarkView_bak(Context context, Bitmap watermark, boolean isCanRemove) {
        super(context);
        mContext = context;
        mWaterBitmap = watermark;
        mIsCanRemove = isCanRemove;
        init();
    }

    /**
     * init params
     */
    public void init() {
        setBitSizeScale(DisplayHelper.getDensity() / 2);
        widthScreen = DisplayHelper.getScreenWidth();
        heightScreen = DisplayHelper.getScreenHeight();
        setWaterMarkRemoveIcon();
        setEditNormalBitmap();
        setEditStateBitmap();
        matrix = initMatrix();
        matrix.postTranslate((widthScreen - mWaterBitmapNormal.getWidth() * size_scale) / 2, (heightScreen - mWaterBitmapNormal.getHeight()
                * size_scale) / 2);
        MARK_PAINT.setAntiAlias(true);
        MARK_PAINT.setFilterBitmap(true);
        mLongPressRunnable = new Runnable() {
            @Override
            public void run() {
                mCounter--;
                if (mCounter > 0 || isReleased || isMoved)
                    return;
                try {
                    performLongClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Loger.d("watermark view is clicked!!!");
                if (mIsCanRemove && checkClickIsRemove(x_down_convert, y_down_convert)) {
                    if (mOnRemoveWaterListener != null)
                        mOnRemoveWaterListener.onRemoveClick(getIntance());
                } else {
                    WaterMarkClicked(x_down_convert - extra_width, y_down_convert - extra_height);
                }
            }
        });
    }

    public void setEditState(boolean editState) {
        isEditState = editState;
        invalidate();
    }

    protected void setBitSizeScale(float scale) {
        size_scale = scale;
    }

    /**
     * init matrix
     *
     * @return
     */
    protected Matrix initMatrix() {
        Matrix matrix2 = new Matrix();
        if (size_scale != 1.0f) {
            matrix2.postScale(size_scale, size_scale);
        }
        return matrix2;
    }

    /**
     * set the remove event listener
     *
     * @param removeListener
     */
    public void setOnRemoveWaterListener(OnRemoveWaterListener removeListener) {
        mOnRemoveWaterListener = removeListener;
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        canvas.save();
        /*
        if (getWidth() > 0 && getHeight() > 0) {
            Paint paint = new Paint();
            Rect r = new Rect(0, 0, getWidth(), getHeight());
            paint.setColor(0x88ff0000);
            canvas.drawRect(r, paint);
            Loger.d("isEditstate::::" + isEditState);
        }
        */
        canvas.drawBitmap((isEditState ? mWaterBitmapEdit : mWaterBitmapNormal), matrix, MARK_PAINT);
        canvas.restore();
    }

    /**
     * @param
     */
    public void setWaterMarkRemoveIcon() {
        if (mIsCanRemove) {
            removeWater = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_button_delete);
            removeRect = new RectF(0.0f, 0.0f, (float) removeWater.getWidth() + 20, (float) removeWater.getHeight() + 20);
        }
    }

    public void setWaterMarkBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("water mark bitmap is null");
        }
        Loger.d("change the water mark bitmap");
        mWaterBitmap = bitmap;
        setEditNormalBitmap();
        setEditStateBitmap();
    }

    private void setEditNormalBitmap() {
        if (mWaterBitmapNormal != null) {
            mWaterBitmapNormal.recycle();
        }
        if (removeRect != null) {
            extra_width = removeRect.width() / 2;
            extra_height = removeRect.height() / 2;
        }
        wmb_width = mWaterBitmap.getWidth();
        wmb_height = mWaterBitmap.getHeight();
        mWaterRect = new RectF(0.0f, 0.0f, (float) wmb_width, (float) wmb_height);
        Bitmap bit = Bitmap.createBitmap(wmb_width + (int) extra_width, wmb_height + (int) extra_height, Config.ARGB_8888);
        Canvas bitcanvas = new Canvas(bit);
        bitcanvas.drawBitmap(mWaterBitmap, extra_width, extra_height, MARK_PAINT);
        mWaterBitmapNormal = bit;
    }

    /**
     * create the water mark bitmap when editing state
     */
    protected void setEditStateBitmap() {
        if (mWaterBitmapEdit != null) {
            mWaterBitmapEdit.recycle();
        }
        float extra_width = 25;
        float extra_height = 25;
        if (removeRect != null) {
            extra_width = removeRect.width() / 2;
            extra_height = removeRect.height() / 2;
        }
        Bitmap bit = Bitmap.createBitmap(wmb_width + (int) extra_width, wmb_height + (int) extra_height, Config.ARGB_8888);
        Canvas bitcanvas = new Canvas(bit);
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(2);
        bitcanvas.drawBitmap(mWaterBitmap, extra_width, extra_height, MARK_PAINT);
        if (mIsCanRemove) {
            bitcanvas.drawBitmap(removeWater, 10, 10, MARK_PAINT);
            //bitcanvas.drawRect(removeRect.left, removeRect.top, removeRect.right, removeRect.bottom, borderPaint);
        }
        bitcanvas.drawRect(mWaterRect.left + extra_width, mWaterRect.top + extra_height, mWaterRect.right + extra_width, mWaterRect.bottom
                + extra_height, borderPaint);
        mWaterBitmapEdit = bit;
    }

    /**
     * a interface to remove event this view from it's parent view
     */
    public static interface OnRemoveWaterListener {
        void onRemoveClick(WaterMarkView_bak view);
    }


    /**
     * water mark click in other area(except remove area)
     *
     * @param x invert down_x
     * @param y invert down_y
     */
    abstract void WaterMarkClicked(float x, float y);

    public void cancelMarkEdit() {
        Loger.d("cancel mark edit....");
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                x_down = event.getX();
                y_down = event.getY();
                savedMatrix.set(matrix);
                float[] pts = ConvertPoint(x_down, y_down);
                x_down_convert = (int) pts[0];
                y_down_convert = (int) pts[1];
                isvalidity = checkClickIsvalidity(pts);
                if (!isvalidity) {
                    cancelMarkEdit();
                    isEditState = false;
                    invalidate();
                }
                mCounter++;
                isMoved = false;
                isReleased = false;
                lastOptIsZoom = false;
                postDelayed(mLongPressRunnable, 3000);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                savedMatrix.set(matrix);
                midPoint(mid, event);
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                if (mode == ZOOM) {
                    last_mode = ZOOM;
                    lastOptIsZoom = true;
                    zoomWaterMark(event);
                } else if (mode == DRAG) {
                    dragWaterMark(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isEditState = checkClickIsvalidity(new float[]{x_down_convert, y_down_convert});
                invalidate();
                if (last_mode == NONE && !lastOptIsZoom) {
                    performClick();
                }
                last_mode = NONE;
                mode = NONE;
                isReleased = true;
                isZoomedIn = (zoomScale > 1) ? false : true;
                isZoomedOut = !isZoomedIn;
                break;
        }
        return isvalidity;
    }

    /**
     * drag this water mark bitmap
     *
     * @param event
     */
    private void dragWaterMark(MotionEvent event) {
        matrix1.set(savedMatrix);
        float m_x = event.getX() - x_down;
        float m_y = event.getY() - y_down;
        matrix1.postTranslate(m_x, m_y);
        if (Math.abs(m_x) > 0 || Math.abs(m_y) > 0) {
            last_mode = DRAG;
        }
        if (isEditState && checkClickIsvalidity(new float[]{x_down_convert, y_down_convert})) {
            isPositonChanged = true;
            matrix.set(matrix1);
            invalidate();
        }
    }

    /**
     * zoom this water mark bitmap
     *
     * @param event
     */
    private void zoomWaterMark(MotionEvent event) {
        matrix1.set(savedMatrix);
        float rotation = rotation(event) - oldRotation;
        float newDist = spacing(event);
        float scale = newDist / oldDist;
        matrix1.postScale(scale, scale, mid.x, mid.y);
        matrix1.postRotate(rotation, mid.x, mid.y);
        if (isEditState && checkClickIsvalidity(ConvertPoint(event.getX(0), event.getY(0)))
                && checkClickIsvalidity(ConvertPoint(event.getX(1), event.getY(1)))) {
            matrix.set(matrix1);
            isZoomed = true;
            zoomScale = scale;
            invalidate();
        }
    }

    /**
     * 旋转水印
     *
     * @param degree
     */
    public void rotateWaterMark(float degree) {
        float[] pts = new float[]{wmb_width / 2, wmb_height / 2};
        setRotationDegree(degree);

        matrix.mapPoints(pts);
        matrix.postRotate(degree, pts[0], pts[1]);
//        matrix.setRotate(degree, pts[0], pts[1]);
        invalidate();
    }

    /**
     * check the click point is in the water mark area or not
     *
     * @param point
     * @return
     */
    boolean checkClickIsvalidity(float[] point) {

        float tx = point[0] - extra_width;
        float ty = point[1] - extra_height;
        if (mIsCanRemove) {
            Matrix transfer_matrix = new Matrix();
            transfer_matrix.postTranslate(-(removeRect.width() / 2), -(removeRect.height() / 2));
            RectF rectF = new RectF(removeRect);
            transfer_matrix.mapRect(rectF);
            return checkPointInRect(mWaterRect, tx, ty) || rectF.contains(tx, ty);
        } else {
            return checkPointInRect(mWaterRect, tx, ty);
        }
    }

    /**
     * @param x
     * @param y
     * @return
     */
    boolean checkClickIsRemove(float x, float y) {
        return checkPointInRect(removeRect, x, y);
    }

    /**
     * check a point is in a RECTF or not
     *
     * @param rect
     * @param x
     * @param y
     * @return
     */
    protected boolean checkPointInRect(RectF rect, float x, float y) {
        return rect.contains(x, y);
    }

    /**
     * invert a point(x,y) using the matrix
     *
     * @param x
     * @param y
     * @return
     */
    private float[] ConvertPoint(float x, float y) {
        Matrix m = new Matrix();
        savedMatrix.invert(m);
        float[] pts = {x, y};
        m.mapPoints(pts);
        return pts;
    }

    /**
     * calculate the two point space
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
     * calculate  the two point's mid point
     *
     * @param point
     * @param event
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    /**
     * calculate the water bitmap rotation degree
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 水印类型
     */
    public class WaterType {
        /**
         * 纯图片
         */
        public final static String TYPE_Normal = "NORMAL";
        /**
         * 有文字，城市距离相关
         */
        public final static String TYPE_DISTANCE = "DISTANCE";
        /**
         * 有文字节日相关
         */
        public final static String TYPE_FESTIVAL = "FESTIVAL";
        /**
         * 文字编辑类型
         */
        public final static String TYPE_TEXTEDIT = "TEXTEDIT";
    }

    /**
     * return this view object
     *
     * @return
     */
    private WaterMarkView_bak getIntance() {
        return (this);
    }

    /**
     * return the water bitmap
     *
     * @return
     */
    public Bitmap getWaterMarkBitmap() {
        return mWaterBitmapNormal;
    }

    /**
     * return the watermark matrix
     *
     * @return
     */
    public Matrix getWaterMarkMatrix() {
        return matrix;
    }

    /**
     * clear matrix
     *
     * @return
     */

    public void cleartWaterMarkMatrix() {
        matrix = null;
    }

    /**
     * return the water mark's position whether changed ever
     *
     * @return
     */
    public boolean getIsMoved() {
        return isPositonChanged;
    }

    /**
     * return the water mark whether zoomed
     *
     * @return
     */
    public boolean getIsZoomed() {
        return isZoomed;
    }

    /**
     * return the watermark is whether zoomed in
     *
     * @return
     */
    public boolean isZoomedIn() {
        return isZoomedIn;
    }

    /**
     * return the watermark is whether zoomed out
     *
     * @return
     */
    public boolean isZoomedOut() {
        return isZoomedOut;
    }
}
