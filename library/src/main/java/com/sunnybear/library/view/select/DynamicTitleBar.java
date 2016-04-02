package com.sunnybear.library.view.select;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.sunnybear.library.R;

import java.util.List;

/**
 * 动态标题指示器
 * Created by guchenkai on 2016/1/12.
 */
public class DynamicTitleBar extends LinearLayout implements View.OnClickListener {
    private TitleItem mLastSelectedItem = null;
    //    private SparseIntArray array;
//    private SparseArray<TitleItem> mTitleItemArray;
    private ArrayMap<String, Integer> array;
    private TitleBar.OnTitleItemSelectedListener mOnTitleItemSelectedListener;

    public void setOnTitleItemSelectedListener(TitleBar.OnTitleItemSelectedListener onTitleItemSelectedListener) {
        mOnTitleItemSelectedListener = onTitleItemSelectedListener;
    }

    public DynamicTitleBar(Context context) {
        this(context, null, 0);
    }

    public DynamicTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        array = new SparseIntArray();
//        mTitleItemArray = new SparseArray<>();
        array = new ArrayMap<>();
    }

    @Override
    public void onClick(View v) {
        selectTagItem((TitleItem) v);
    }

    /**
     * 选择TagItem
     *
     * @param item 选中的TagItem
     */
    public void selectTagItem(TitleItem item) {
        if (mLastSelectedItem == item) {
            mLastSelectedItem.setActive(true);
            return;
        } else {
            if (mLastSelectedItem != null)
                mLastSelectedItem.setActive(false);
            item.setActive(true);
            mLastSelectedItem = item;
        }
        int position = array.get(item.getTitle());
        if (mOnTitleItemSelectedListener != null)
            mOnTitleItemSelectedListener.onTitleItemSelected(item, position);
    }

    /**
     * 添加TitleItem
     *
     * @param titles          title文字集合
     * @param defaultPosition 默认选中项
     */
    public void addTitles(List<String> titles, int defaultPosition) {
        for (int i = 0; i < titles.size(); i++) {
            TitleItem titleItem = (TitleItem) View.inflate(getContext(), R.layout.widget_title, null);
            titleItem.setTitle(titles.get(i));
            LinearLayout.LayoutParams params = new LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            params.gravity = Gravity.CENTER;
            titleItem.setLayoutParams(params);
            addView(titleItem);

            array.put(titles.get(i), i);
        }
        if (defaultPosition >= titles.size()) return;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (!(child instanceof TitleItem))
                throw new RuntimeException("TitleBar's child must be subclass of TitleItem!");
            if (i == defaultPosition) {
                ((TitleItem) child).setActive(true);
                mLastSelectedItem = (TitleItem) child;
            }
//            array.put(child.getId(), i);
//            mTitleItemArray.put(child.getId(), (TitleItem) child);
//            if (((TitleItem) child).isActive())
//                mLastSelectedItem = (TitleItem) child;
            child.setOnClickListener(this);
        }
    }
}
