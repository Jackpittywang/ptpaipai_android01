package com.sunnybear.library.view.picker.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于原版修改：可设置颜色、设置文字大小、分隔线是否可见、去掉回弹阴影、修正以便支持联动效果
 *
 * @version 2015-12-17
 * @link https://github.com/wangjiegulu/WheelView
 */
public class WheelView extends ScrollView {
    public static final int TEXT_SIZE = 20;
    public static final int TEXT_COLOR_FOCUS = 0XFF0288CE;
    public static final int TEXT_COLOR_NORMAL = 0XFFBBBBBB;
    public static final int LINE_COLOR = 0XFF83CDE6;
    public static final int OFF_SET = 1;
    private static final String TAG = WheelView.class.getSimpleName();

    private Context context;
    private LinearLayout views;
    private List<String> items = new ArrayList<String>();
    private int offset = OFF_SET; // 偏移量（需要在最前面和最后面补全）

    private int displayItemCount; // 每页显示的数量

    private int selectedIndex = OFF_SET;
    private int initialY;

    private Runnable scrollerTask;
    private int newCheck = 50;
    private int itemHeight = 0;
    private int[] selectedAreaBorder;//获取选中区域的边界
    private OnWheelViewListener onWheelViewListener;

    private Paint paint;
    private int viewWidth;
    private String text;
    private int textSize = TEXT_SIZE;
    private int textColorNormal = TEXT_COLOR_NORMAL;
    private int textColorFocus = TEXT_COLOR_FOCUS;
    private int lineColor = LINE_COLOR;
    private boolean lineVisible = true;

    private int mDefaultItem;

    public WheelView(Context context) {
        super(context);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        // FIXME: 2015/12/15 去掉ScrollView的阴影
        setFadingEdgeLength(0);
        if (Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(OVER_SCROLL_NEVER);
        }

        setVerticalScrollBarEnabled(false);

        views = new LinearLayout(context);
        views.setOrientation(LinearLayout.VERTICAL);
        addView(views);

        scrollerTask = new Runnable() {
            public void run() {
                // FIXME: 2015/12/17 java.lang.ArithmeticException: divide by zero
                if (itemHeight == 0) {
                    return;
                }
                int newY = getScrollY();
                if (initialY - newY == 0) { // stopped
                    final int remainder = initialY % itemHeight;
                    final int divided = initialY / itemHeight;
                    Log.d(TAG, "initialY: " + initialY + ", remainder: " + remainder + ", divided: " + divided);
                    if (remainder == 0) {
                        selectedIndex = divided + offset;
                        onSelectedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    smoothScrollTo(0, initialY - remainder + itemHeight);
                                    selectedIndex = divided + offset + 1;
                                    onSelectedCallBack();
                                }
                            });
                        } else {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    smoothScrollTo(0, initialY - remainder);
                                    selectedIndex = divided + offset;
                                    onSelectedCallBack();
                                }
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    postDelayed(scrollerTask, newCheck);
                }
            }
        };
        // FIXME: 2015/12/17 默认选中第一项
        postDelayed(scrollerTask, newCheck);
    }

    private void initData(int defaultItem) {
        setSelection(defaultItem);

        displayItemCount = offset * 2 + 1;

        // FIXME: 2015/12/15 添加此句才可以支持联动效果
        views.removeAllViews();

        for (String item : items) {
            views.addView(createView(item));
        }

        refreshItemView(0);
    }

    private void initData() {
        // FIXME: 2015/12/21 默认选择第一项
        initData(0);
    }

    private TextView createView(String item) {
        TextView tv = new TextView(context);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setText(item);
        tv.setTextSize(textSize);
        tv.setGravity(Gravity.CENTER);
        int padding = dip2px(15);
        tv.setPadding(padding, padding, padding, padding);
        if (0 == itemHeight) {
            itemHeight = getViewMeasuredHeight(tv);
            Log.d(TAG, "itemHeight: " + itemHeight);
            views.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight * displayItemCount));
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = views.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) views.getChildAt(i);
            if (null == itemView) {
                return;
            }
            // FIXME: 2015/12/15 颜色常量
            if (position == i) {
                itemView.setTextColor(textColorFocus);
            } else {
                itemView.setTextColor(textColorNormal);
            }
        }
    }

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    /**
     * 选中回调
     */
    private void onSelectedCallBack() {
        if (null != onWheelViewListener) {
            onWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }
    }

    private int dip2px(float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    @Override
    public void setBackground(Drawable background) {
        setBackgroundDrawable(background);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setBackgroundDrawable(Drawable background) {
        if (viewWidth == 0) {
            viewWidth = ((Activity) context).getWindowManager().getDefaultDisplay().getWidth();
            Log.d(TAG, "viewWidth: " + viewWidth);
        }

        // FIXME: 2015/12/22 可设置分隔线是否可见
        if (!lineVisible) {
            return;
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(lineColor);
            paint.setStrokeWidth(dip2px(1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                int[] areaBorder = obtainSelectedAreaBorder();
                canvas.drawLine(viewWidth / 6, areaBorder[0], viewWidth * 5 / 6, areaBorder[0], paint);
                canvas.drawLine(viewWidth / 6, areaBorder[1], viewWidth * 5 / 6, areaBorder[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        };
        super.setBackgroundDrawable(background);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "w: " + w + ", h: " + h + ", oldw: " + oldw + ", oldh: " + oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    public void startScrollerTask() {
        initialY = getScrollY();
        postDelayed(scrollerTask, newCheck);
    }

    public void setItems(List<String> list) {
        setItems(list, 0);
    }

    public void setItems(List<String> list, int defaultItem) {
        items.clear();
        items.addAll(list);

        // 前面和后面补全
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData(defaultItem);
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColorFocus;
    }

    public void setTextColor(@ColorInt int textColorNormal, @ColorInt int textColorFocus) {
        this.textColorNormal = textColorNormal;
        this.textColorFocus = textColorFocus;
    }

    public void setTextColor(@ColorInt int textColor) {
        this.textColorFocus = textColor;
    }

    public boolean isLineVisible() {
        return lineVisible;
    }

    public void setLineVisible(boolean lineVisible) {
        this.lineVisible = lineVisible;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(@ColorInt int lineColor) {
        this.lineColor = lineColor;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setSelection(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(0, p * itemHeight);
            }
        });
    }

    public String getSeletedItem() {
        return items.get(selectedIndex);
    }

    public int getSeletedIndex() {
        return selectedIndex - offset;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public void setOnWheelViewListener(OnWheelViewListener onWheelViewListener) {
        this.onWheelViewListener = onWheelViewListener;
    }

    public interface OnWheelViewListener {
        void onSelected(int selectedIndex, String item);
    }

}
