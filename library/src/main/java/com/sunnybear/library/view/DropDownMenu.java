package com.sunnybear.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sunnybear.library.R;
import com.sunnybear.library.util.DensityUtil;
import com.sunnybear.library.util.Logger;

import java.util.List;

/**
 * 多条件筛选菜单
 * Created by guchenkai on 2016/1/21.
 */
public class DropDownMenu extends LinearLayout {
    private LinearLayout mTabMenuView;//顶部菜单布局
    private FrameLayout mContainerView;//底部容器,包含popupViews,maskView
    private FrameLayout mPopupMenuViews;//弹出菜单父布局
    private View mMaskView;//遮罩半透明View，点击可关闭DropDownMenu
    //mTabMenuView里面选中的tab位置，-1表示未选中
    private int current_tab_position = -1;

    //分割线颜色
    private int mDividerColor;
    //tab选中颜色
    private int mTextSelectedColor;
    //tab未选中颜色
    private int mTextUnselectedColor;
    //遮罩颜色
    private int mMaskColor;
    //tab字体大小
    private int mMenuTextSize = 14;
    private int mMenuBackgroundColor;
    private int mUnderlineColor;

    private int mMenuSelectedIcon; //tab选中图标
    private int mMenuUnselectedIcon;//tab未选中图标

    public DropDownMenu(Context context) {
        this(context, null, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        initStyleable(context, attrs);
        initView(context);
    }

    private void initStyleable(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.DropDownMenu);
        mUnderlineColor = array.getColor(R.styleable.DropDownMenu_underlineColor, 0xFFCCCCCC);
        mDividerColor = array.getColor(R.styleable.DropDownMenu_dividerColor, 0xFFCCCCCC);
        mTextSelectedColor = array.getColor(R.styleable.DropDownMenu_textSelectedColor, 0xFF890C85);
        mTextUnselectedColor = array.getColor(R.styleable.DropDownMenu_textUnselectedColor, 0xFF111111);
        mMaskColor = array.getColor(R.styleable.DropDownMenu_maskColor, 0x88888888);
        mMenuBackgroundColor = array.getColor(R.styleable.DropDownMenu_menuBackgroundColor, 0xFFFFFFFF);
        mMenuTextSize = array.getDimensionPixelSize(R.styleable.DropDownMenu_menuTextSize, 14);
        mMenuSelectedIcon = array.getResourceId(R.styleable.DropDownMenu_menuSelectedIcon, 0xFFFFFFFF);
        mMenuUnselectedIcon = array.getResourceId(R.styleable.DropDownMenu_menuUnselectedIcon, 0xFFCCCCCC);
        array.recycle();
    }

    private void initView(Context context) {
        //初始化tabMenuView并添加到tabMenuView
        mTabMenuView = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mTabMenuView.setOrientation(HORIZONTAL);
        mTabMenuView.setBackgroundColor(mMenuBackgroundColor);
        mTabMenuView.setLayoutParams(params);
        addView(mTabMenuView, 0);
        //为tabMenuView添加下划线
        View underLine = new View(context);
        underLine.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(context, 1.0F)));
        underLine.setBackgroundColor(mUnderlineColor);
        addView(underLine, 1);
        //初始化containerView并将其添加到DropDownMenu
        mContainerView = new FrameLayout(context);
        mContainerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        addView(mContainerView, 2);
    }

    /**
     * 初始化DropDownMenu
     *
     * @param tabTexts    标题
     * @param popupViews  弹出框
     * @param contentView 内容
     */
    public void setDropDownMenu(@NonNull List<String> tabTexts, @NonNull List<View> popupViews, @NonNull View contentView) {
        if (tabTexts.size() != popupViews.size())
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        for (int i = 0; i < tabTexts.size(); i++) {
            addTab(tabTexts, i);
        }
        mContainerView.addView(contentView, 0);

        mMaskView = new View(getContext());
        mMaskView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        mMaskView.setBackgroundColor(mMaskColor);
        mMaskView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu();
            }
        });
        mContainerView.addView(mMaskView, 1);
        mMaskView.setVisibility(GONE);

        mPopupMenuViews = new FrameLayout(getContext());
        mPopupMenuViews.setVisibility(GONE);
        mContainerView.addView(mPopupMenuViews, 2);

        for (int i = 0; i < popupViews.size(); i++) {
            View popupView = popupViews.get(i);
            popupView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            mPopupMenuViews.addView(popupView, i);
        }
    }

    private void addTab(List<String> tabTexts, int index) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mMenuTextSize);
        tab.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0F));
        tab.setTextColor(mTextUnselectedColor);
        tab.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(mMenuUnselectedIcon), null);
        tab.setText(tabTexts.get(index));
        tab.setPadding(DensityUtil.dp2px(getContext(), 5.0F), DensityUtil.dp2px(getContext(), 12.0F),
                DensityUtil.dp2px(getContext(), 5.0F), DensityUtil.dp2px(getContext(), 12.0F));
        //添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMenu(v);
            }
        });
        mTabMenuView.addView(tab);
        //添加分隔线
        if (index < tabTexts.size() - 1) {
            View view = new View(getContext());
            view.setLayoutParams(new LayoutParams(DensityUtil.dp2px(getContext(), 1.0f), ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(mDividerColor);
            mTabMenuView.addView(view);
        }
    }

    /**
     * 切换菜单
     *
     * @param target 目标view
     */
    private void switchMenu(View target) {
        Logger.d(current_tab_position + "");
        for (int i = 0; i < mTabMenuView.getChildCount(); i += 2) {
            if (mTabMenuView.getChildAt(i) == target) {
                if (current_tab_position == i) {
                    closeMenu();
                } else {
                    if (current_tab_position == -1) {
                        mPopupMenuViews.setVisibility(View.VISIBLE);
                        mPopupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_in));
                        mMaskView.setVisibility(VISIBLE);
                        mMaskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_in));
                        mPopupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    } else {
                        mPopupMenuViews.getChildAt(i / 2).setVisibility(View.VISIBLE);
                    }
                    current_tab_position = i;
                    ((TextView) mTabMenuView.getChildAt(i)).setTextColor(mTextSelectedColor);
                    ((TextView) mTabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                            getResources().getDrawable(mMenuSelectedIcon), null);
                }
            } else {
                ((TextView) mTabMenuView.getChildAt(i)).setTextColor(mTextUnselectedColor);
                ((TextView) mTabMenuView.getChildAt(i)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                        getResources().getDrawable(mMenuUnselectedIcon), null);
                mPopupMenuViews.getChildAt(i / 2).setVisibility(View.GONE);
            }
        }
    }

    /**
     * 关闭菜单
     */
    public void closeMenu() {
        if (current_tab_position != -1) {
            ((TextView) mTabMenuView.getChildAt(current_tab_position)).setTextColor(mTextUnselectedColor);
            ((TextView) mTabMenuView.getChildAt(current_tab_position)).setCompoundDrawablesWithIntrinsicBounds(null, null,
                    getResources().getDrawable(mMenuUnselectedIcon), null);
            mPopupMenuViews.setVisibility(View.GONE);
            mPopupMenuViews.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_menu_out));
            mMaskView.setVisibility(GONE);
            mMaskView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.dd_mask_out));
            current_tab_position = -1;
        }
    }

    /**
     * 改变tab文字
     *
     * @param text
     */
    public void setTabText(String text) {
        if (current_tab_position != -1)
            ((TextView) mTabMenuView.getChildAt(current_tab_position)).setText(text);
    }

    public void setTabClickable(boolean clickable) {
        for (int i = 0; i < mTabMenuView.getChildCount(); i = i + 2) {
            mTabMenuView.getChildAt(i).setClickable(clickable);
        }
    }

    /**
     * DropDownMenu是否处于可见状态
     *
     * @return
     */
    public boolean isShowing() {
        return current_tab_position != -1;
    }
}
