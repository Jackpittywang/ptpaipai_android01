package com.sunnybear.library.view.select;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunnybear.library.R;
import com.sunnybear.library.util.StringUtils;

/**
 * 标题指示器元素
 * Created by guchenkai on 2015/12/2.
 */
public class TitleItem extends LinearLayout {
    private View mRootView;
    private TextView mTitleView;
    private View mIndicator;
    private View mRedDot;

    private String mTitleText;
    private boolean isActive;

    private int mActiveColor;
    private int mInActiveColor;

    private int mIndicatorColor;

    public TitleItem(Context context) {
        this(context, null, 0);
    }

    public TitleItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyleable(context, attrs);
        initView(context);
        init();
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.TitleItem);
        isActive = array.getBoolean(R.styleable.TitleItem_is_active, false);
        mActiveColor = array.getColor(R.styleable.TitleItem_active_color, -1);
        mInActiveColor = array.getColor(R.styleable.TitleItem_inactive_color, -1);
        mTitleText = array.getString(R.styleable.TitleItem_title_text);
        mIndicatorColor = array.getColor(R.styleable.TitleItem_indicator_color, -1);
        array.recycle();
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.widget_title_item, this);
        mTitleView = (TextView) mRootView.findViewById(R.id.tv_title);
        mRedDot = mRootView.findViewById(R.id.red_dot);
        mIndicator = mRootView.findViewById(R.id.indicator);
    }

    private void init() {
        if (!StringUtils.isEmpty(mTitleText))
            mTitleView.setText(mTitleText);
        if (mIndicatorColor != -1)
            mIndicator.setBackgroundColor(mIndicatorColor);
        setActive(isActive);
    }

    public void setActive(boolean isActive) {
        mTitleView.setTextColor(isActive ? mActiveColor : mInActiveColor);
        mIndicator.setVisibility(isActive ? VISIBLE : INVISIBLE);
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * 设置title
     *
     * @param title title文字
     */
    public void setTitle(String title) {
        mTitleView.setText(title);
    }

    /**
     * 获得title
     *
     * @return title文字
     */
    public String getTitle() {
        return mTitleView.getText().toString();
    }

    public void setActiveColor(int activeColor) {
        mActiveColor = activeColor;
    }

    public void setInActiveColor(int inActiveColor) {
        mInActiveColor = inActiveColor;
    }

    public void setIndicatorColor(int indicatorColor) {
        mIndicatorColor = indicatorColor;
    }

    /**
     * 显示指示
     */
    public void show() {
        mRedDot.setVisibility(VISIBLE);
        postInvalidate();

    }

    /**
     * 隐藏指示
     */
    public void hide() {
        mRedDot.setVisibility(GONE);
    }
}
