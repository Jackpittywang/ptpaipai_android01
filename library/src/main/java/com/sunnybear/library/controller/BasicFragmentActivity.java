package com.sunnybear.library.controller;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.R;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.controller.handler.WeakHandler;
import com.sunnybear.library.model.http.OkHttpRequestHelper;
import com.sunnybear.library.model.http.callback.RequestCallback;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.util.DiskFileCacheHelper;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.LoadingHUD;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 基础FragmentActivity
 * Created by guchenkai on 2015/11/19.
 */
public abstract class BasicFragmentActivity<App extends BasicApplication> extends AppCompatActivity {
    private static final int WHAT_ON_HOME_CLICK = 0x1;
    protected Context mContext;
    protected App mApp;
    private OkHttpClient mOkHttpClient;
    protected LoadingHUD loading;
    private boolean isRunningForeground;
    public boolean isResume;

    protected Bundle args;
    private static WeakHandler mWeakHandler;
    private HomeBroadcastReceiver mReceiver = new HomeBroadcastReceiver();//监听Home键

    protected DiskFileCacheHelper mDiskFileCacheHelper;

    private long exitTime = 0;

    /**
     * 设置布局id
     *
     * @return 布局id
     */
    protected abstract int getLayoutId();

    /**
     * 布局初始化完成的回调
     *
     * @param saveInstanceState 保存状态
     */
    protected abstract void onViewCreatedFinish(Bundle saveInstanceState);

    /**
     * 收集本Activity请求时的url
     *
     * @return url
     */
    protected abstract String[] getRequestUrls();

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int layoutId = getLayoutId();
        if (layoutId == 0)
            throw new RuntimeException("找不到Layout资源,Fragment初始化失败!");
        setContentView(layoutId);
        ButterKnife.bind(this);
        mContext = this;
        mApp = (App) getApplication();
        mOkHttpClient = mApp.getOkHttpClient();
        this.loading = LoadingHUD.getInstance(this);
        loading.setSpinnerType(LoadingHUD.FADED_ROUND_SPINNER);
        this.args = getIntent().getExtras() != null ? getIntent().getExtras() : new Bundle();
        mDiskFileCacheHelper = mApp.getDiskFileCacheHelper();
        mWeakHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case WHAT_ON_HOME_CLICK:
                        onHomeClick();
                        break;
                }
                return false;
            }
        });
        ActivityManager.getInstance().addActivity(this);//添加当前Activity到管理堆栈
        //监听Home键
        registerReceiver(mReceiver, Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        EventBusHelper.register(this);//注册EventBus
        //布局初始化完成的回调
        onViewCreatedFinish(savedInstanceState);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusHelper.unregister(this);//反注册EventBus
        unregisterReceiver(mReceiver);
    }

    @Override
    public void finish() {
//        ActivityManager.getInstance().removeCurrentActivity();
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    /**
     * 注册广播接收器
     *
     * @param receiver 广播接收器
     * @param actions  广播类型
     */
    protected void registerReceiver(BroadcastReceiver receiver, String... actions) {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : actions) {
            intentFilter.addAction(action);
        }
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 网络请求
     *
     * @param request      request主体
     * @param cacheType    缓存策略
     * @param callback     请求回调(建议使用SimpleFastJsonCallback)
     * @param interceptors 网络拦截器组
     */
    protected void networkRequest(Request request, int cacheType, RequestCallback callback, List<Interceptor> interceptors) {
        if (request == null)
            throw new NullPointerException("request为空");
        OkHttpRequestHelper helper = OkHttpRequestHelper.newInstance();
        if (interceptors != null && interceptors.size() > 0)
            helper.addInterceptors(interceptors);
        if (cacheType != -1)
            helper.cacheType(cacheType);
        helper.request(request, callback);
    }

    /**
     * 网络请求
     *
     * @param request     request主体
     * @param cacheType   缓存策略
     * @param callback    请求回调(建议使用SimpleFastJsonCallback)
     * @param interceptor 网络拦截器
     */
    protected void networkRequest(Request request, int cacheType, RequestCallback callback, Interceptor interceptor) {
        List<Interceptor> interceptors = new LinkedList<>();
        interceptors.add(interceptor);
        networkRequest(request, cacheType, callback, interceptors);
    }

    /**
     * 网络请求
     *
     * @param request   request主体
     * @param cacheType 缓存策略
     * @param callback  请求回调(建议使用SimpleFastJsonCallback)
     */
    protected void networkRequest(Request request, int cacheType, RequestCallback callback) {
        networkRequest(request, cacheType, callback, new LinkedList<Interceptor>());
    }

    /**
     * 网络请求
     *
     * @param request  request主体
     * @param callback 请求回调(建议使用SimpleFastJsonCallback)
     */
    protected void networkRequest(Request request, RequestCallback callback) {
        networkRequest(request, -1, callback);
        loading.show();
    }

    /**
     * 网络请求
     *
     * @param request     request主体
     * @param callback    请求回调(建议使用SimpleFastJsonCallback)
     * @param showLoading 是不是显示loading
     */
    protected void networkRequest(Request request, RequestCallback callback, boolean showLoading) {
        networkRequest(request, -1, callback);
        if (showLoading)
            loading.show();
    }

    /**
     * 缓存数据
     *
     * @param url    网络地址
     * @param result 数据源
     * @param <T>    数据类型
     */
    public <T extends Serializable> void cacheData(String url, T result) {
        mDiskFileCacheHelper.put(url, result);
    }

    /**
     * 添加fragment显示
     *
     * @param targetClass 目标fragment
     * @param args        传递参数
     */
    protected void addFragment(Class<? extends Fragment> targetClass, Bundle args) {
        String fragmentName = targetClass.getName();
        getSupportFragmentManager().beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .add(R.id.fragment_container, Fragment.instantiate(mContext, fragmentName, args), fragmentName).commit();
    }

    /**
     * 添加fragment显示
     *
     * @param targetClass 目标fragment
     */
    public void addFragment(Class<? extends Fragment> targetClass) {
        addFragment(targetClass, null);
    }

    /**
     * 跳转目标Activity
     *
     * @param targetClass 目标Activity类型
     */
    public void startActivity(Class<? extends Activity> targetClass) {
        startActivity(targetClass, null);
    }

    /**
     * 跳转目标Activity(传递参数)
     *
     * @param targetClass 目标Activity类型
     * @param args        传递参数
     */
    public void startActivity(Class<? extends Activity> targetClass, Bundle args) {
        Intent intent = new Intent(this, targetClass);
        if (args != null)
            intent.putExtras(args);
        startActivity(intent);
    }

    /**
     * 隐式跳转目标Activity
     *
     * @param action 隐式动作
     */
    public void startActivity(String action) {
        startActivity(action, null);
    }

    /**
     * 隐式跳转目标Activity
     *
     * @param action 隐式动作
     */
    public void startActivity(String action, Bundle args) {
        Intent intent = new Intent(action);
        if (args != null)
            intent.putExtras(args);
        startActivity(intent);
    }

    /**
     * 启动目标Service
     *
     * @param targetClass 目标Service类型
     * @param args        传递参数
     */
    public void startService(Class<? extends Service> targetClass, Bundle args) {
        Intent intent = new Intent(this, targetClass);
        if (args != null)
            intent.putExtras(args);
        startService(intent);
    }

    /**
     * 启动目标Service
     *
     * @param targetClass 目标Service类型
     */
    public void startService(Class<? extends Service> targetClass) {
        startService(targetClass, null);
    }

    /**
     * 隐式跳转目标Service
     *
     * @param action 隐式动作
     */
    public void startService(String action) {
        startService(action, null);
    }

    /**
     * 隐式跳转目标Service
     *
     * @param action 隐式动作
     */
    protected void startService(String action, Bundle args) {
        Intent intent = new Intent(action);
        if (args != null)
            intent.putExtras(args);
        startService(intent);
    }

   /* @Override
    public boolean dispatchTouchEvent(@Nullable MotionEvent ev) {
        if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (isShouldHideInput(view, ev))
                KeyboardUtils.closeKeyboard(mContext, view);
            return super.dispatchTouchEvent(ev);
        }
        //必不可少,否则所有的组件都不会有TouchEvent了
        return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
    }*/

    /**
     * 双击退出App
     *
     * @param exit 退出时间(毫秒数)
     */
    protected boolean exit(long exit) {
        if (System.currentTimeMillis() - exitTime > exit) {
            ToastUtils.showToastShort(mContext, "再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityManager.getInstance().killProcess(mContext.getApplicationContext());
        }
        return true;
    }

    /**
     * 双击退出App
     */
    protected boolean exit() {
        return exit(2000);
    }

    /**
     * 是否应该隐藏键盘
     *
     * @param view  对应的view
     * @param event 事件
     * @return 是否隐藏
     */
    private boolean isShouldHideInput(View view, MotionEvent event) {
        if (view != null && (view instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的位置
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + view.getHeight();
            int right = left + view.getWidth();
            return !(event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom);
        }
        return false;
    }

    /**
     * 点击Home键,程序退回后台
     */
    protected void onHomeClick() {

    }

    /**
     * 监听Home键广播接收器
     */
    private static class HomeBroadcastReceiver extends BroadcastReceiver {
        private String SYSTEM_REASON = "reason";
        private String SYSTEM_HOME_KEY = "homekey";
        private String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (TextUtils.equals(reason, SYSTEM_HOME_KEY))//表示按了home键,程序到了后台
                    mWeakHandler.sendEmptyMessage(WHAT_ON_HOME_CLICK);
                else if (TextUtils.equals(reason, SYSTEM_HOME_KEY_LONG))//表示长按home键,显示最近使用的程序列表
                    Logger.d("长按Home键,显示最近使用的程序列表");
            }
        }
    }

    /**
     * 网络请求
     *
     * @param request     request主体
     * @param callback    请求回调(建议使用SimpleFastJsonCallback)
     * @param interceptor 网络拦截器
     */
    @Deprecated
    public void networkRequest(Request request, Callback callback, Interceptor interceptor) {
        if (interceptor != null)
            mOkHttpClient.networkInterceptors().add(interceptor);
        if (request == null)
            throw new NullPointerException("request为空");
        if (null != loading) loading.show();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 网络请求
     *
     * @param request  request主体
     * @param callback 请求回调(建议使用SimpleFastJsonCallback)
     */
    @Deprecated
    public void networkRequest(Request request, Callback callback) {
        networkRequest(request, callback, null);
    }

    /**
     * 网络请求(首先查找文件缓存,如果缓存有就不在进行网络请求)
     *
     * @param request   request主体
     * @param callback  请求回调(建议使用SimpleFastJsonCallback)
     * @param pastTimer 过期时间阀值
     */
    @Deprecated
    protected <T extends Serializable> void networkRequestCache(Request request, SimpleFastJsonCallback<T> callback, long pastTimer) {
        String url = request.urlString();
        long currentTime = System.currentTimeMillis();//当前时间
        String past_time = mDiskFileCacheHelper.getAsString(url + "_past_time");
        //获取过期时间
        long pastTime = !StringUtils.isEmpty(past_time) ? Long.parseLong(past_time) : currentTime + pastTimer;
        if (!StringUtils.isEmpty(past_time) || currentTime < pastTime) {
            T cacheData = (T) mDiskFileCacheHelper.getAsSerializable(url);
            if (cacheData != null)
                callback.onSuccess(url, cacheData);
        } else {
            mDiskFileCacheHelper.put(url + "_past_time", String.valueOf(pastTime));//存入过期时间
            mOkHttpClient.newCall(request).enqueue(callback);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        loading.dismiss();
        isResume = false;
        //Activity停止时取消所有请求
        String[] urls = getRequestUrls();
        for (String url : urls) {
            OkHttpRequestHelper.newInstance().cancelRequest(url);
        }
        if (ActivityManager.getInstance().isAppFore()) {
            Logger.d("ActivityManager-------", "应用到后台了");
            isRunningForeground = true;
            Intent intent = new Intent("com.putao.isFore.message");
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * 注册统计
     */
    protected void onResume() {
        super.onResume();
        isResume = true;
        if (isRunningForeground) {
            Logger.d("ActivityManager", "应用恢复到前台了");
            /*Intent intent = new Intent("com.putao.mtlib.message");
            mContext.sendBroadcast(intent);*/
            isRunningForeground = false;
            Intent intent = new Intent("com.putao.isNotFore.message");
            mContext.sendBroadcast(intent);
        }
    }
}
