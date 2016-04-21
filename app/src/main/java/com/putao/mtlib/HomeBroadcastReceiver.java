/*
package com.putao.mtlib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.putao.camera.application.MainApplication;
import com.sunnybear.library.util.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

*/
/**
 * 程序进入后台恢复前台监听
 *//*

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
            case MainApplication.Fore_Message:
//                inFore();
                if (null != timer) {
                    timer.cancel();
                    timer = null;
                }
                if (MainApplication.isServiceClose && !isServiceStart) {
                    context.startService(MainApplication.redServiceIntent);
                    MainApplication.isServiceClose = true;
                }
                break;
            case MainApplication.Not_Fore_Message:
//                outFore();
                if (null == timer)
                    timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MainApplication.isServiceClose = true;
                        Logger.d("ptl-----------", "停止服务");
                        if (isServiceStart) {
                            context.stopService(MainApplication.redServiceIntent);
                            MainApplication.isServiceClose = true;
                        }
//                        stopSelf();
                    }
                }, 60 * 1000);
                break;
        }
    }

    private boolean isServiceStart(Context context) {
        android.app.ActivityManager systemService = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<android.app.ActivityManager.RunningServiceInfo> runningServices = systemService.getRunningServices(100);
        for (android.app.ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if ("com.putao.mtlib.NotifyService".equals(runningServiceInfo.service.getClassName().toString())) {
                return true;
            }
        }
        return false;
    }

//    abstract void inFore();
//
//    abstract void outFore();
}
*/
