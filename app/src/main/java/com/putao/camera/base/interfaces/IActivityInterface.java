package com.putao.camera.base.interfaces;

import android.view.View;

/**
 * @author ronj2d
 */
public interface IActivityInterface {
    /**
     * 初始化父视图
     *
     * @return
     */
    public int doGetContentViewId();

    /**
     * 初始化子视图
     *
     * @param view
     */
    public void doInitSubViews(View view);

    /**
     * 初始化视图数据
     */
    public void doInitData();
}
