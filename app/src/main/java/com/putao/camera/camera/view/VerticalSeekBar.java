package com.putao.camera.camera.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    /**
     * Instantiates a new vertical seek bar.
     *
     * @param context the context
     */
    public VerticalSeekBar(Context context) {

        super(context);
    }

    /**
     * Instantiates a new vertical seek bar.
     *
     * @param context  the context
     * @param attrs    the attrs
     * @param defStyle the def style
     */
    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);
    }

    /**
     * Instantiates a new vertical seek bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public VerticalSeekBar(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.AbsSeekBar#onSizeChanged(int, int, int, int)
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(h, w, oldh, oldw);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.AbsSeekBar#onMeasure(int, int)
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {

        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.AbsSeekBar#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas c) {

        c.rotate(-90);
        c.translate(-getHeight(), 0);

        super.onDraw(c);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.AbsSeekBar#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                setProgress(getMax()
                        - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}