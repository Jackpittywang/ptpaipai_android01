package com.sunnybear.library.controller;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;

/**
 * Activity管理工具类,将activity放入栈统一管理
 * Created by guchenkai on 2015/10/22.
 */
public class ActivityManager {
    //activity管理栈
    private volatile Stack<BasicFragmentActivity> activityStack;
    //全局单例
    private static volatile ActivityManager instance;

    public ActivityManager() {
        activityStack = new Stack<>();
    }

    /**
     * 单例
     *
     * @return ActivityManager实例
     */
    public static ActivityManager getInstance() {
        if (instance == null)
            instance = new ActivityManager();
        return instance;
    }

    /**
     * 添加activity到管理栈
     *
     * @param activity activity
     */
    public void addActivity(BasicFragmentActivity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取管理栈顶的activity
     *
     * @return 栈顶的activity
     */
    public BasicFragmentActivity getCurrentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 将指定的activity移出管理堆栈
     */
    public void removeCurrentActivity() {
        BasicFragmentActivity activity = getCurrentActivity();
        if (activity != null)
            activityStack.remove(activity);
    }


    /**
     * 结束当前的activity
     */
    public void finishCurrentActivity() {
        getCurrentActivity().finish();
    }

    /**
     * 结束指定的activity
     *
     * @param activity activity
     */
    public void finishActivity(BasicFragmentActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定的activity(class方式)
     *
     * @param activityClass activityClass
     */
    public void finishActivity(Class<? extends BasicFragmentActivity> activityClass) {
        for (BasicFragmentActivity activity : activityStack) {
            if (activityClass.equals(activity.getClass()))
                finishActivity(activity);
        }
    }

    /**
     * 程序是否进入后台
     *
     */
    public boolean isAppFore() {
        for (BasicFragmentActivity activity : activityStack) {
            if (activity.isResume)
                return false;
        }
        return true;
    }

    /**
     * 结束所有的activity
     */
    public void finishAllActivity() {
        while (getCurrentActivity() != null) {
            finishActivity(getCurrentActivity());
        }
        activityStack.clear();
    }

    /**
     * 退出栈中其他所有Activity
     *
     * @param cls activityClass
     */
    public void popOtherActivity(Class<? extends BasicFragmentActivity> cls) {
        if (null == cls) return;
        for (BasicFragmentActivity activity : activityStack) {
            if (null == activity || activity.getClass().equals(cls))
                continue;
            activity.finish();
        }
    }

    /**
     * 退出应用程序
     */
    public void killProcess(Context context) {
        try {
            finishAllActivity();
            android.app.ActivityManager activityMgr = (android.app.ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
//            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
