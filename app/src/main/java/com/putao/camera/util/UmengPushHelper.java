
package com.putao.camera.util;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.LogTag;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.IUmengUnregisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.entity.UMessage;

/**
 * Umeng推送
 *
 * @author CLEVO
 */
public class UmengPushHelper {
    private static UmengPushHelper instance;
    private PushAgent pushAgent;

    private UmengPushHelper() {
    }

    ;

    public static UmengPushHelper getInstance() {
        if (null == instance) {
            instance = new UmengPushHelper();
        }
        return instance;
    }

    /**
     * 初始化Umeng推送
     */
    public void initPushAgent() {
        if (null == pushAgent) {
            pushAgent = PushAgent.getInstance(MainApplication.getInstance());
            setCommonConfig();
        }
        enablePushAgent();
    }

    /**
     * Umeng推送参数配置
     */
    private void setCommonConfig() {
        if (null == pushAgent) {
            return;
        }
        // 调试模式 
        pushAgent.setDebugMode(false);
        // 设置免打扰时段，例如setNoDisturbMode(23, 0, 7, 0)表示23:00-07:00
        pushAgent.setNoDisturbMode(0, 0, 0, 0);
        // 消息处理
        setMessageHandler(umengMessageHandler);
        // 通知点击打开的动作
        setNotificationClickHandler(umengNotificationClickHandler);
    }

    /**
     * 通知点击打开动作,在Application调用（因某个Activity的生命结束，该Handler也会结束）
     * <<<<<<< HEAD
     * =======
     * <p/>
     * >>>>>>> dev
     *
     * @param
     */
    public void setNotificationClickHandler(UmengNotificationClickHandler umNCH) {
        if (null != umNCH) {
            pushAgent.setNotificationClickHandler(umNCH);
        }
    }

    /**
     * 消息处理，在Application调用（因某个Activity的生命结束，该Handler也会结束）
     * <<<<<<< HEAD
     * =======
     * <p/>
     * >>>>>>> dev
     *
     * @param
     */
    public void setMessageHandler(UmengMessageHandler umMH) {
        if (null != umMH) {
            pushAgent.setMessageHandler(umMH);
        }
    }

    /**
     * 开启推送
     */
    public void enablePushAgent() {
        // 统计应用启动数据
        pushAgent.onAppStart();
        // 开启Umeng推送
        pushAgent.enable(iUmengRegisterCallback);
        // 消息完全自定义处理
        /*pushAgent.setPushIntentServiceClass(UmengPushIntentService.class);*/
    }

    /**
     * 注销推送
     */
    public void disablePushAgent() {
        // 关闭Umeng推送
        pushAgent.disable(iUmengUnregisterCallback);
    }

    /**
     * 消息处理
     * 该Handler是在IntentService中被调用，如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK。
     * IntentService里的onHandleIntent方法是并不处于主线程中，因此，如果需调用到主线程，需如下所示;或者可以直接启动Service
     */
    private UmengMessageHandler umengMessageHandler = new UmengMessageHandler() {
        @Override
        public Notification getNotification(Context context, UMessage msg) {
            /*
             *根据后台设置的样式id，选择通知样式
             */
            switch (msg.builder_id) {
                case 1: {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.view_custom_notification);
                    myNotificationView.setTextViewText(R.id.notification_title, msg.title);
                    myNotificationView.setTextViewText(R.id.notification_text, msg.text);
                    myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
                    myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
                    builder.setContent(myNotificationView);
                    Notification mNotification = builder.build();
                    //由于Android v4包的bug，在2.3及以下系统，Builder创建出来的Notification，并没有设置RemoteView，故需要添加此代码
                    mNotification.contentView = myNotificationView;
                    return mNotification;
                }
                default:
                    //默认为0，若填写的builder_id并不存在，也使用默认。
                    return super.getNotification(context, msg);
            }
        }

        @Override
        public void dealWithNotificationMessage(Context arg0, UMessage arg1) {
            // TODO Auto-generated method stub
            super.dealWithNotificationMessage(arg0, arg1);

            UTrack.getInstance(MainApplication.getInstance()).trackMsgClick(arg1, false);

            ToasterHelper.showToast(arg0, arg1.custom, 0);
            /**
             * TODO 测试
             */
            {
                Vibrator vib = (Vibrator) arg0.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(1000);
            }
        }

        @Override
        public void dealWithCustomMessage(final Context context, final UMessage msg) {
            new Handler(MainApplication.getInstance().getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub

                    UTrack.getInstance(MainApplication.getInstance()).trackMsgClick(msg, false);

                    ToasterHelper.showToast(context, msg.custom, 0);
                    /**
                     * TODO 测试
                     */
                    {
                        Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                        vib.vibrate(1000);
                    }
                }
            });
        }
    };
    /**
     * 通知打开动作
     * 该Handler是在BroadcastReceiver中被调用，故如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
     */
    private UmengNotificationClickHandler umengNotificationClickHandler = new UmengNotificationClickHandler() {
        @Override
        public void dealWithCustomAction(Context context, UMessage msg) {
            ToasterHelper.showToast(context, msg.custom, 0);
            /**
             * TODO 测试
             */
            {
                Vibrator vib = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(1000);
            }
        }

        @Override
        public void launchApp(Context arg0, UMessage arg1) {
            // TODO Auto-generated method stub
            super.launchApp(arg0, arg1);
        }

        @Override
        public void openActivity(Context arg0, UMessage arg1) {
            // TODO Auto-generated method stub
            super.openActivity(arg0, arg1);
        }

        @Override
        public void openUrl(Context arg0, UMessage arg1) {
            // TODO Auto-generated method stub
            super.openUrl(arg0, arg1);
        }
    };
    /**
     * 开启推送的回调，不是运行在主线程
     */
    private IUmengRegisterCallback iUmengRegisterCallback = new IUmengRegisterCallback() {
        @Override
        public void onRegistered(String arg0) {
            // TODO Auto-generated method stub
            Loger.d(LogTag.UmengPush.toString() + "Umeng推送开启！");
            Loger.d(LogTag.UmengPush.toString() + "DeviceToken=" + UmengRegistrar.getRegistrationId(MainApplication.getInstance()));
        }
    };
    /**
     * 注销推送的回调，不是运行在主线程
     */
    private IUmengUnregisterCallback iUmengUnregisterCallback = new IUmengUnregisterCallback() {
        @Override
        public void onUnregistered(String arg0) {
            // TODO Auto-generated method stub
            Log.d(LogTag.UmengPush.toString(), "Umeng推送关闭！");
        }
    };
}
