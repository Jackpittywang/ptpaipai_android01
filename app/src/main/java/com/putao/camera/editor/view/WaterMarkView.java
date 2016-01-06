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
public abstract class WaterMarkView extends ImageView {
    Context mContext;
    float x_down, y_down, x_down_convert, y_down_convert;
    PointF mid = new PointF();
    float old_dist = 1f;
    float old_rotation = 0;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    enum TransformMode {
        NONE,
        DRAG,
        ROTATION,
        STRETCH
    }

    TransformMode mTransform = TransformMode.NONE;

    /**
     * record last operate mode
     */
    TransformMode last_mode = TransformMode.NONE;
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
    Bitmap mWaterBitmap, mWaterBitmapEdit, mWaterBitmapNormal, removeWater, stretchButton, rotationButton;
    private RectF mWaterRect, removeRect, stretchButtonRect, rotationButtonRect;
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
    int wmb_width, wmb_height;
    /**
     * represent whether the water mark is in editing
     */
    boolean isEditState = false;

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
     * 默认图片缩放系数
     */
    private float size_scale = 1.0f;


    boolean mIsCanRemove = false;

    /**
     * 屏幕的方向角度
     */
    float rotationDegree = 0;

    float CENTER_X, CENTER_Y;


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
    public WaterMarkView(Context context, Bitmap watermark) {
        super(context);
        mContext = context;
        mWaterBitmap = watermark;
        init();
    }

    public WaterMarkView(Context context, Bitmap watermark, boolean isCanRemove) {
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
        mWaterBitmapNormal = getEditNormalBitmap();
        mWaterBitmapEdit = getEditStateBitmap();
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
                    WaterMarkClicked(x_down_convert - removeRect.width() / 2, y_down_convert - removeRect.height() / 2);
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
        Matrix matrix_init = new Matrix();
        if (size_scale != 1.0f) {
            matrix_init.postScale(size_scale, size_scale);
        }
        return matrix_init;
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
        canvas.drawBitmap((isEditState ? mWaterBitmapEdit : mWaterBitmapNormal), matrix, MARK_PAINT);
        canvas.restore();
    }

    /**
     * @param
     */
    public void setWaterMarkRemoveIcon() {
        removeWater = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_button_delete);
        removeRect = new RectF(0.0f, 0.0f, (float) removeWater.getWidth(), (float) removeWater.getHeight());
        stretchButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_button_rotation);
        stretchButtonRect = new RectF(0, 0, (float) stretchButton.getWidth(), (float) stretchButton.getHeight());
        rotationButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_button_stretch);
        rotationButtonRect = new RectF(0, 0, (float) rotationButton.getWidth(), (float) rotationButton.getHeight());
    }

    public void setWaterMarkBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("water mark bitmap is null");
        }
//        Loger.d("change the water mark bitmap");
        mWaterBitmap = bitmap;
        mWaterBitmapNormal = getEditNormalBitmap();
        mWaterBitmapEdit = getEditStateBitmap();
    }


    protected Bitmap getEditNormalBitmap() {
        float extra_width = 0;
        float extra_height = 0;
        if (removeRect != null) {
            extra_width = (removeRect.width() + stretchButtonRect.width()) / 2;
            extra_height = (removeRect.height() + stretchButtonRect.height()) / 2;
        }
        wmb_width = mWaterBitmap.getWidth();
        wmb_height = mWaterBitmap.getHeight();
        mWaterRect = new RectF(removeRect.width() / 2, removeRect.height() / 2, (float) wmb_width + removeRect.width() / 2, (float) wmb_height + removeRect.height() / 2);
        Bitmap bit = Bitmap.createBitmap(wmb_width + (int) extra_width, wmb_height + (int) extra_height, Config.ARGB_8888);
        Canvas bitcanvas = new Canvas(bit);
        bitcanvas.save();
        bitcanvas.translate(removeRect.width() / 2, removeRect.height() / 2);
        bitcanvas.drawBitmap(mWaterBitmap, 0, 0, MARK_PAINT);
        bitcanvas.restore();
        CENTER_X = bit.getWidth() / 2;
        CENTER_Y = bit.getHeight() / 2;
        return bit;
    }

    /**
     * create the water mark bitmap when editing state
     */
    protected Bitmap getEditStateBitmap() {
        float extra_width = 0;
        float extra_height = 0;
        if (removeRect != null) {
            extra_width = (removeRect.width() + stretchButton.getWidth()) / 2;
            extra_height = (removeRect.height() + stretchButton.getHeight()) / 2;
        }
        Bitmap bit = Bitmap.createBitmap(wmb_width + (int) extra_width, wmb_height + (int) extra_height, Config.ARGB_8888);
        Canvas bitcanvas = new Canvas(bit);

        bitcanvas.drawBitmap(mWaterBitmap, removeRect.width() / 2, removeRect.height() / 2, MARK_PAINT);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStrokeWidth(2);
        bitcanvas.drawRect(mWaterRect, borderPaint);
        if (mIsCanRemove) {
            bitcanvas.drawBitmap(removeWater, 0, 0, MARK_PAINT);
        }
        bitcanvas.save();
        bitcanvas.drawBitmap(stretchButton, bit.getWidth() - stretchButton.getWidth(), bit.getHeight() - stretchButton.getHeight(), MARK_PAINT);
//        bitcanvas.drawBitmap(rotationButton, bit.getWidth() - rotationButton.getWidth(), 0, MARK_PAINT);
        bitcanvas.restore();
        stretchButtonRect = new RectF((bit.getWidth() - stretchButton.getWidth()), (bit.getHeight() - stretchButton.getHeight()), bit.getWidth(), bit.getHeight());
        rotationButtonRect = new RectF(bit.getWidth() - rotationButton.getWidth(), 0, bit.getWidth(), rotationButton.getHeight());
        return bit;
    }

    /**
     * a interface to remove event this view from it's parent view
     */
    public interface OnRemoveWaterListener {
        void onRemoveClick(WaterMarkView view);
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
                mTransform = TransformMode.DRAG;
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
                } else {
                    if (checkClickStretchButton(x_down_convert, y_down_convert)) {
                        mTransform = TransformMode.STRETCH;
                        midPoint(mid);
                        old_dist = spacing(event);
                        old_rotation = rotation(event);
                    }
//                    else if (checkClickRotationButton(x_down_convert, y_down_convert)) {
//                        mTransform = TransformMode.ROTATION;
//                        midPoint(mid);
//                        old_rotation = rotation(event);
//                    }
                }
                mCounter++;
                isMoved = false;
                isReleased = false;
                lastOptIsZoom = false;
                postDelayed(mLongPressRunnable, 3000);
                break;
            case MotionEvent.ACTION_MOVE:
                isMoved = true;
                if (mTransform == TransformMode.STRETCH) {
                    last_mode = TransformMode.STRETCH;
                    lastOptIsZoom = true;
                    stretchWaterMark(event);
                } else if (mTransform == TransformMode.DRAG) {
                    dragWaterMark(event);
                } else if (mTransform == TransformMode.ROTATION) {
                    rotationWaterMark(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                isEditState = checkClickIsvalidity(new float[]{x_down_convert, y_down_convert});
                invalidate();
                if (last_mode == TransformMode.NONE && !lastOptIsZoom) {
                    performClick();
                }
                last_mode = TransformMode.NONE;
                mTransform = TransformMode.NONE;
                isReleased = true;
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
        Matrix drag_matrix = new Matrix();
        drag_matrix.set(savedMatrix);
        float[] values = new float[9];
        drag_matrix.getValues(values);
        float m_x = event.getX() - x_down;
        float m_y = event.getY() - y_down;
        RectF rect = new RectF(mWaterRect.left, mWaterRect.top, mWaterRect.right, mWaterRect.bottom);
        drag_matrix.mapRect(rect);
        if (rect.width() < DisplayHelper.getScreenWidth()) {
            if (rect.right + m_x > DisplayHelper.getScreenWidth()) {
                m_x = DisplayHelper.getScreenWidth() - rect.right;
            } else if (rect.left + m_x < 0) {
                m_x = -rect.left;
            }
        } else {
//            m_x = checkDxBound(values, rect.width(), m_x);
        }
        if (rect.height() < DisplayHelper.getScreenHeight()) {
            if (rect.bottom + 250 + m_y > DisplayHelper.getScreenHeight()) {
                m_y = DisplayHelper.getScreenHeight() - rect.bottom - 250;
            } else if (rect.top + m_y < 0) {
                m_y = -rect.top;
            }
        } else {
//            m_y = checkDyBound(values, rect.height(), m_y);
        }
        drag_matrix.postTranslate(m_x, m_y);
        if (Math.abs(m_x) > 0 || Math.abs(m_y) > 0) {
            last_mode = TransformMode.DRAG;
        }
        if (isEditState) {
            isPositonChanged = true;
            matrix.set(drag_matrix);
            invalidate();
        }
    }

    private float checkDxBound(float[] values, float img_w, float dx) {
        float width = DisplayHelper.getScreenWidth();
        float image_width = img_w;
        if (image_width * values[Matrix.MSCALE_X] < width)
            return 0;
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(image_width * values[Matrix.MSCALE_X] - width))
            dx = -(image_width * values[Matrix.MSCALE_X] - width) - values[Matrix.MTRANS_X];
        return dx;
    }

    private float checkDyBound(float[] values, float img_h, float dy) {
        float height = DisplayHelper.getScreenHeight() - 250;
        float image_height = img_h;
        if (image_height * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(image_height * values[Matrix.MSCALE_Y] - height))
            dy = -(image_height * values[Matrix.MSCALE_Y] - height) - values[Matrix.MTRANS_Y];
        return dy;
    }


    /**
     * zoom this water mark bitmap
     *
     * @param event
     */
    private void stretchWaterMark(MotionEvent event) {
        Matrix stretch_matrix = new Matrix();
        stretch_matrix.set(savedMatrix);
        float scale = spacing(event) / old_dist;
        stretch_matrix.postScale(scale, scale, mid.x, mid.y);
        float rotation = rotation(event) - old_rotation;
        stretch_matrix.postRotate(rotation, mid.x, mid.y);
//        float[] values = new float[9];
//        stretch_matrix.getValues(values);

//        if (isEditState && values[Matrix.MSCALE_X] >= 0.5) {
        if (isEditState) {
            matrix.set(stretch_matrix);
            invalidate();
        }
    }

    private void rotationWaterMark(MotionEvent event) {
        Matrix rotation_matrix = new Matrix();
        rotation_matrix.set(savedMatrix);
        float rotation = rotation(event) - old_rotation;
        rotation_matrix.postRotate(rotation, mid.x, mid.y);
        if (isEditState) {
            matrix.set(rotation_matrix);
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
        invalidate();
    }

    /**
     * check the click point is in the water mark area or not
     *
     * @param point
     * @return
     */
    boolean checkClickIsvalidity(float[] point) {
        float tx = point[0];
        float ty = point[1];
        boolean isInWaterRect = mWaterRect.contains(tx, ty);
        if (mIsCanRemove) {
            return isInWaterRect || checkClickIsRemove(tx, ty) || checkClickStretchButton(tx, ty) || checkClickRotationButton(tx, ty);
        } else {
            return isInWaterRect || checkClickStretchButton(tx, ty) || checkClickRotationButton(tx, ty);
        }
    }

    /**
     * @param x
     * @param y
     * @return
     */
    boolean checkClickIsRemove(float x, float y) {
        return removeRect.contains(x, y);
    }

    boolean checkClickStretchButton(float x, float y) {
        return stretchButtonRect.contains(x, y);
    }

    boolean checkClickRotationButton(float x, float y) {
        return rotationButtonRect.contains(x, y);
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
     * 计算触摸位置距离中心点的距离
     *
     * @param event
     * @return
     */
    private float spacing(MotionEvent event) {
        float x = event.getX() - mid.x;
        float y = event.getY() - mid.y;
        return FloatMath.sqrt(x * x + y * y);
    }


    private void midPoint(PointF point) {
        float[] points = new float[]{CENTER_X, CENTER_Y};
        savedMatrix.mapPoints(points);
        point.set(points[0], points[1]);
    }


    /**
     * 计算触摸点到中心点的角度
     *
     * @param event
     * @return
     */
    private float rotation(MotionEvent event) {
        double delta_x = (event.getX() - mid.x);
        double delta_y = (event.getY() - mid.y);
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
    private WaterMarkView getIntance() {
        return (this);
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
     * return the watermark is whether zoomed in
     *
     * @return
     */
    public boolean isZoomedIn() {
        float[] values = new float[9];
        matrix.getValues(values);
        return (values[Matrix.MSCALE_X] < 1);
    }

    /**
     * return the watermark is whether zoomed out
     *
     * @return
     */
    public boolean isZoomedOut() {
        float[] values = new float[9];
        matrix.getValues(values);
        return (values[Matrix.MSCALE_X] > 1);
    }
}
