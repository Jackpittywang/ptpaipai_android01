package com.sunnybear.library.view.select;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 标题指示器
 * Created by guchenkai on 2015/12/2.
 */
public class TitleBar extends LinearLayout implements View.OnClickListener {
    private OnTitleItemSelectedListener mTitleItemSelectedListener;
    private TitleItem mLastSelectedItem = null;

    private SparseIntArray array;
    private SparseArray<TitleItem> mTitleItemArray;

    public void setOnTitleItemSelectedListener(OnTitleItemSelectedListener titleItemSelectedListener) {
        mTitleItemSelectedListener = titleItemSelectedListener;
    }

    public TitleBar(Context context) {
        this(context, null, 0);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        array = new SparseIntArray();
        mTitleItemArray = new SparseArray<>();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            array.put(child.getId(), i);
            mTitleItemArray.put(child.getId(), (TitleItem) child);
            if (!(child instanceof TitleItem))
                throw new RuntimeException("TitleBar's child must be subclass of TitleItem!");
            if (((TitleItem) child).isActive())
                mLastSelectedItem = (TitleItem) child;
            child.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        selectTitleItem((TitleItem) v);
    }

    /**
     * 选择TabItem
     *
     * @param item 选中的TabItem
     */
    private void selectTitleItem(TitleItem item) {
        if (mLastSelectedItem == item) {
            mLastSelectedItem.setActive(true);
            return;
        } else {
            if (mLastSelectedItem != null)
                mLastSelectedItem.setActive(false);
            item.setActive(true);
            mLastSelectedItem = item;
        }
        int position = array.get(item.getId());
        if (mTitleItemSelectedListener != null)
            mTitleItemSelectedListener.onTitleItemSelected(item, position);
    }

    /**
     * 选择TabItem
     *
     * @param resId 資源id
     */
    public void selectTitleItem(int resId) {
        selectTitleItem(mTitleItemArray.get(resId));
    }

    /**
     * TabItem选择监听器
     */
    public interface OnTitleItemSelectedListener {

        void onTitleItemSelected(TitleItem item, int position);
    }
}
