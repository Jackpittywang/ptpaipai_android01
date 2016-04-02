package com.sunnybear.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunnybear.library.R;

/**
 * 数量选择控件
 * Created by guchenkai on 2015/11/30.
 */
public class AmountSelectLayout extends RelativeLayout {
    private View mRootView;
    private LinearLayout mMinusBtn, mPlusBtn;
    private TextView mEditCount;

    private int mCurrentCount = 1;//当前数量

    private int mMaxCount;
    private Drawable mBtnBackground;//按钮颜色
    private Drawable mEditBackground;//数字区背景
    private boolean isEditable;//是否可编辑
    private int mEditTextColor;//数字颜色

    private int mDisColor;//
    private int mNorColor;//
    private TextView tv_minus, tv_plus;

    private OnAmountSelectedListener mOnAmountSelectedListener;

    public void setOnAmountSelectedListener(OnAmountSelectedListener onAmountSelectedListener) {
        mOnAmountSelectedListener = onAmountSelectedListener;
    }

    public AmountSelectLayout(Context context) {
        this(context, null, 0);
    }

    public AmountSelectLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmountSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyleable(context, attrs);
        initView(context);
        init();
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AmountSelectLayout);
        mBtnBackground = array.getDrawable(R.styleable.AmountSelectLayout_btn_background);
        mEditBackground = array.getDrawable(R.styleable.AmountSelectLayout_edit_background);
        isEditable = array.getBoolean(R.styleable.AmountSelectLayout_isEditable, false);
        mEditTextColor = array.getColor(R.styleable.AmountSelectLayout_edit_text_colot, -1);
        mDisColor = array.getColor(R.styleable.AmountSelectLayout_dis_color, -1);
        mNorColor = array.getColor(R.styleable.AmountSelectLayout_nor_color, -1);
        array.recycle();
    }

    private void initView(Context context) {
        mRootView = LayoutInflater.from(context).inflate(R.layout.widget_amount_select, this);
        mMinusBtn = (LinearLayout) mRootView.findViewById(R.id.btn_minus);
        mPlusBtn = (LinearLayout) mRootView.findViewById(R.id.btn_plus);
        mEditCount = (TextView) mRootView.findViewById(R.id.tv_count);
        mEditCount.setText(String.valueOf(mCurrentCount));
        tv_minus = (TextView) mRootView.findViewById(R.id.tv_minus);
        tv_plus = (TextView) mRootView.findViewById(R.id.tv_minus);

        mMinusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentCount > 1)
                    mCurrentCount--;
                else if (mCurrentCount == 1)
                    tv_minus.setTextColor(mDisColor);
                mEditCount.setText(String.valueOf(mCurrentCount));
                if (mOnAmountSelectedListener != null)
                    mOnAmountSelectedListener.onAmountSelected(mCurrentCount, false);
            }
        });
        mPlusBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentCount++;
                tv_minus.setTextColor(mNorColor);
                mEditCount.setText(String.valueOf(mCurrentCount));
                if (mOnAmountSelectedListener != null)
                    mOnAmountSelectedListener.onAmountSelected(mCurrentCount, true);
            }
        });
    }

    private void init() {
        if (mBtnBackground != null) {
            mMinusBtn.setBackground(mBtnBackground);
            mPlusBtn.setBackground(mBtnBackground);
        }
        if (mEditBackground != null)
            mEditCount.setBackground(mEditBackground);
        mEditCount.setFocusable(isEditable);
        mEditCount.setEnabled(isEditable);
        if (mEditTextColor != -1) mEditCount.setText(mEditTextColor);
        if (mDisColor != -1) tv_minus.setTextColor(mDisColor);
        if (mNorColor != -1) tv_plus.setTextColor(mNorColor);
    }

    /**
     * 设置数量
     *
     * @param count
     */
    public void setCount(int count) {
        mCurrentCount = count;
        mEditCount.setText(String.valueOf(count));
    }

    /**
     * 获取当前数量
     *
     * @return
     */
    public int getCurrentCount() {
        return mCurrentCount;
    }

    /**
     * 重置
     */
    public void reset() {
        setCount(1);
        tv_minus.setTextColor(mDisColor);
    }

    /**
     *
     */
    public interface OnAmountSelectedListener {

        void onAmountSelected(int count, boolean isPlus);
    }
}
