package com.sunnybear.library.controller;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.sunnybear.library.R;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.view.LoadingHUD;

import butterknife.ButterKnife;

/**
 * 基础PopupWindow
 * Created by guchenkai on 2015/11/29.
 */
public abstract class BasicPopupWindow extends PopupWindow implements View.OnTouchListener, View.OnKeyListener {
    protected Context mContext;
    protected View mRootView;
    private ViewGroup mMainLayout;

    protected FragmentActivity mActivity;
    protected LoadingHUD loading;

    /**
     * 设置布局
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    public BasicPopupWindow(Context context) {
        super(context);
        mContext = context;
        mActivity = (FragmentActivity) context;
        if (mActivity instanceof BasicFragmentActivity)
            this.loading = ((BasicFragmentActivity) mActivity).loading;
        int layoutId = getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Fragment初始化失败!");
        mRootView = LayoutInflater.from(context).inflate(getLayoutId(), null);
        setContentView(mRootView);//设置布局
        ButterKnife.bind(this, mRootView);
        mMainLayout = (ViewGroup) mRootView.findViewById(R.id.popup_layout);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);//设置宽
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);//设置高
        setFocusable(true);// 设置PopupWindow可获得焦点
        setTouchable(true); // 设置PopupWindow可触摸
        setOutsideTouchable(true);// 设置非PopupWindow区域可触摸
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        setBackgroundDrawable(dw);//设置背景
        //点击PopupWindow之外的区域关闭PopupWindow
        mRootView.setOnTouchListener(this);
        //响应返回键
        mRootView.setOnKeyListener(this);
        //华为手机虚拟按键挡住window
        setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int height = mMainLayout.getHeight();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_UP && y > height)
            dismiss();
        return true;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
            dismiss();
        return false;
    }

    public void networkRequest(Request Request, Callback Callback) {
        if (mActivity instanceof BasicFragmentActivity)
            ((BasicFragmentActivity) mActivity).networkRequest(Request, Callback);
    }

    /**
     * 设置layout在PopupWindow中显示的位置
     *
     * @param target 目标view
     */
    public void show(View target) {
        EventBusHelper.register(this);
        showAtLocation(target, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    @Override
    public void dismiss() {
        EventBusHelper.unregister(this);
        super.dismiss();
    }
}
