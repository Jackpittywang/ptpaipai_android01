package com.putao.camera.base;

import com.putao.camera.R;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.view.NavigationBar;

import butterknife.Bind;

/**
 * 基础FragmentActivity
 * Created by guchenkai on 2015/11/25.
 */
public abstract class PTXJActivity<App extends BasicApplication> extends BasicFragmentActivity<App> implements NavigationBar.ActionsListener {
    @Bind(R.id.navigation_bar)
    public NavigationBar navigation_bar;

    /**
     * 添加标题栏
     */
    protected void addNavigation() {
        navigation_bar.setActionListener(this);
    }

    /**
     * 设置主标题文字
     *
     * @param text 标题文字
     */
    protected void setMainTitle(String text) {
        navigation_bar.setMainTitle(text);
    }

    /**
     * 设置主标题文字颜色
     *
     * @param color
     */
    protected void setMainTitleColor(int color) {
        navigation_bar.setMainTitleColor(color);
    }

    /**
     * 设置左标题文字
     *
     * @param text 标题文字
     */
    protected void setLeftTitle(String text) {
        navigation_bar.setLeftTitle(text);
    }

    /**
     * 设置左标题文字颜色
     *
     * @param color 颜色id
     */
    protected void setLeftTitleColor(int color) {
        navigation_bar.setLeftTitleColor(color);
    }

    /**
     * 设置左标题文字是否可以点击
     *
     * @param isClick 是否可以点击
     */
    protected void setLeftClickable(boolean isClick) {
        navigation_bar.setLeftClickable(isClick);
    }

    /**
     * 设置右标题文字
     *
     * @param text 标题文字
     */
    protected void setRightTitle(String text) {
        navigation_bar.setRightTitle(text);
    }

    /**
     * 设置右标题文字颜色
     *
     * @param color 颜色id
     */
    protected void setRightTitleColor(int color) {
        navigation_bar.setRightTitleColor(color);
    }

    /**
     * 设置右标题文字是否可以点击
     *
     * @param isClick 是否可以点击
     */
    protected void setRightClickable(boolean isClick) {
        navigation_bar.setRightClickable(isClick);
    }

    @Override
    public void onLeftAction() {
        onBackPressed();
    }

    @Override
    public void onRightAction() {

    }

    @Override
    public void onMainAction() {

    }
}
