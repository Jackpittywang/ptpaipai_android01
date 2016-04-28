package com.putao.camera.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.putao.camera.base.interfaces.IActivityInterface;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends FragmentActivity implements IActivityInterface {
    public Context mContext;
    public Activity mActivity;
    private boolean isFullScreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBefore();
        View containerView = getLayoutInflater().inflate(doGetContentViewId(), null);
        setContentView(containerView);
        init();
        doInitSubViews(containerView);
        doInitData();
        fullScreen(true);
        setMobclickAgent();
        EventBusHelper.register(this);//注册EventBus
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
    }

    /**
     * 设置统计配置
     */
    void setMobclickAgent() {
        MobclickAgent.openActivityDurationTrack(true);
        MobclickAgent.setDebugMode(false);
        MobclickAgent.setSessionContinueMillis(30 * 1000);
        MobclickAgent.setCatchUncaughtExceptions(true);
    }

    private void init() {
        mActivity = this;
        mContext = this;
    }

    protected <T extends View> T queryViewById(int viewId) {
        if (viewId > 0) {
            return (T) findViewById(viewId);
        }
        return null;
    }

    protected <T extends View> T queryViewById(View parent, int viewId) {
        if (viewId > 0 && parent != null) {
            return (T) parent.findViewById(viewId);
        }
        return null;
    }


    /**
     * set OnClickListener for every View
     *
     * @param
     * @param views
     */
    protected void addOnClickListener(View... views) {
        OnClickListener listener = (OnClickListener) mActivity;
        if (listener != null)
            for (int i = 0; i < views.length; i++)
                views[i].setOnClickListener(listener);
    }

    public void doBefore() {
    }

    protected void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 友盟统计
     *
     * @param eventCode
     */
    protected void doUmengEventAnalysis(String eventCode) {
        //UmengAnalysisHelper.onEvent(mActivity, eventCode);
        MobclickAgent.onEvent(this, eventCode);
    }

    public boolean getFullScreenState() {
        return isFullScreen;
    }

    protected void fullScreen(boolean enable) {
        if (enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        isFullScreen = enable;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (BasicApplication.isInBack) {
            sendBroadcast(new Intent("camera_in_fore_message"));
            BasicApplication.isInBack = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);//反注册EventBus
    }
}
