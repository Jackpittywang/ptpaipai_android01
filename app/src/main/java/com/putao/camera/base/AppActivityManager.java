package com.putao.camera.base;

import java.util.Iterator;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

public class AppActivityManager {
    private static Stack<Activity> activityStack;
    private static AppActivityManager instance;

    private AppActivityManager() {
    }

    public static AppActivityManager getInstance() {
        if (instance == null) {
            instance = new AppActivityManager();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    public void removeActivity(Activity activity) {
        if (activityStack.contains(activity))
            activityStack.remove(activity);
    }

    public void finishActivity(Class<?> cls) {
        Activity tempActivity = getActivity(cls);
        if (tempActivity != null)
            finishActivity(tempActivity);
    }

    public Activity getActivity(Class<?> cls) {
        Activity activity = null;
        for (Iterator<Activity> iterator = activityStack.iterator(); iterator
                .hasNext(); ) {
            Activity temp = iterator.next();
            if (temp.getClass().equals(cls)) {
                activity = temp;
                break;
            }
        }
        return activity;
    }

    public boolean isExistActivity(Class<?> cls) {
        return getActivity(cls) != null;
    }

    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
            System.gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
