package com.putao.mtlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.sunnybear.library.util.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 程序进入后台恢复前台监听
 */
public class HomeBroadcastReceiver extends BroadcastReceiver {
    Timer timer;
    private static HomeBroadcastReceiver mHomeBroadcastReceiver;

    public static HomeBroadcastReceiver getInstance() {
        if (null == mHomeBroadcastReceiver) {
            mHomeBroadcastReceiver = new HomeBroadcastReceiver();
        }
        return mHomeBroadcastReceiver;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final boolean isServiceStart = isServiceStart(context);
        switch (intent.getAction()) {
            case MainApplication.IN_FORE_MESSAGE:
//                inFore();
                Logger.d("ptl---------------", "应用恢复到前台了");
                if (!AccountHelper.isLogin()) return;

                if (null != timer) {
                    timer.cancel();
                    timer = null;
                }
                if (!isServiceStart) {
                    context.startService(MainApplication.redServiceIntent);
                    Logger.d("ptl-----------", "启动服务");
                }
                break;
            case MainApplication.OUT_FORE_MESSAGE:
//                outFore();
                if (null == timer)
                    timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (isServiceStart) {
                            context.stopService(MainApplication.redServiceIntent);
                            Logger.d("ptl-----------", "停止服务");
                        }
                    }
                }, 60 * 1000);
                break;
            case MainApplication.OUT_FORE_MESSAGE_SOON:
                context.stopService(MainApplication.redServiceIntent);
                Logger.d("ptl---------------", "停止服务");
                break;
        }
    }

    private boolean isServiceStart(Context context) {
        android.app.ActivityManager systemService = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningServiceInfo> runningServices = systemService.getRunningServices(100);
        for (android.app.ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            Logger.d("service-----", runningServiceInfo.service.getClassName().toString());
            if ("com.putao.mtlib.CameraNotifyService".equals(runningServiceInfo.service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

//    abstract void inFore();
//
//    abstract void outFore();
}
