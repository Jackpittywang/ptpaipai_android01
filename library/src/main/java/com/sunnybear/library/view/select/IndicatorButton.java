package com.sunnybear.library.view.select;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunnybear.library.R;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.view.BadgeView;

/**
 * 指示按钮
 * Created by guchenkai on 2015/12/3.
 */
public class IndicatorButton extends LinearLayout {
    private View mRootView;

    private ImageView mIndicatorIcon;
    private TextView mTvNumber;
    private TextView mTitle;
    private RelativeLayout mMainView;
    /*private RelativeLayout mIndicatorLayout;*/

    private Drawable mIndicatorDrawable;
    private String mTitleText;

    private int mTitleColor;
    private int mTitleSize;

    private int mIndicatorTextColor;

    private BadgeView mIndicator;

    public IndicatorButton(Context context) {
        this(context, null, 0);
    }

    public IndicatorButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyleable(context, attrs);
        initView(context);
        init();
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IndicatorButton);
        mIndicatorDrawable = array.getDrawable(R.styleable.IndicatorButton_indicator_drawable);
        mTitleText = array.getString(R.styleable.IndicatorButton_indicator_title);
        mTitleColor = array.getColor(R.styleable.IndicatorButton_indicator_title_color, -1);
        mTitleSize = array.getDimensionPixelSize(R.styleable.IndicatorButton_indicator_title_size, -1);
        mIndicatorTextColor = array.getColor(R.styleable.IndicatorButton_indicator_text_color, -1);
        array.recycle();
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.widget_indicator_button, this);
        mIndicatorIcon = (ImageView) mRootView.findViewById(R.id.iv_icon);
        mTvNumber = (TextView) mRootView.findViewById(R.id.tv_number);
        mTitle = (TextView) mRootView.findViewById(R.id.tv_title);
        /*mIndicatorLayout = (RelativeLayout) mRootView.findViewById(R.id.rl_indicator);*/
        mMainView = (RelativeLayout) mRootView.findViewById(R.id.ll_main);
    }

    private void init() {
        if (!StringUtils.isEmpty(mTitleText))
            mTitle.setText(mTitleText);
        if (mIndicatorDrawable != null)
            mIndicatorIcon.setImageDrawable(mIndicatorDrawable);
        if (mTitleColor != -1)
            mTitle.setTextColor(mTitleColor);
        if (mTitleSize != -1)
            mTitle.setTextSize(mTitleSize);
    }

    public void show(int count) {
        if (count == 0) {
            hide();
            return;
        }
        if (mTvNumber.getVisibility() == GONE) mTvNumber.setVisibility(VISIBLE);

        mTvNumber.setText(count + "");
       /* if(mIndicator == null) {
            mIndicator = new BadgeView(getContext(), mMainView);
            mIndicator.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            mIndicator.setBackgroundResource(R.drawable.indicator_background);
        }
        mIndicator.setText(String.valueOf(count));
        if (mIndicatorTextColor != -1)
            mIndicator.setTextColor(mIndicatorTextColor);
        mIndicator.setBadgeMargin(8, 8);
        mIndicator.show();*/
    }
    public void hide() {
        mTvNumber.setVisibility(GONE);
        /*if (mIndicator != null && mIndicator.isShown())
            mIndicator.hide();*/
    }
}
