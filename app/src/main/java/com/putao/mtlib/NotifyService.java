package com.putao.mtlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.putao.mtlib.model.CS_CONNECT;
import com.putao.mtlib.tcp.OnReceiveMessageListener;
import com.putao.mtlib.tcp.PTMessageConfig;
import com.putao.mtlib.tcp.PTMessageType;
import com.putao.mtlib.tcp.PTRecMessage;
import com.putao.mtlib.tcp.PTSenderManager;
import com.putao.mtlib.util.MD5Util;
import com.putao.mtlib.util.MsgPackUtil;
import com.sunnybear.library.util.Logger;

/**
 * 通知服务
 * Created by Administrator on 2015/12/28.
 */
public class NotifyService extends Service {
    private static final String HOST = MainApplication.isDebug ? "10.1.11.31" : "122.226.100.152";
    private static final int PORT = MainApplication.isDebug ? 8083 : 8040;
    private static final String secret = "499478a81030bb177e578f86410cda8641a22799";
    private static final int appid = 613;

    private Context mContext;
    private HandlerThread mStartThread;
    private Handler mHandler;
    private String mThreadName = NotifyService.class.getSimpleName();
    private PTSenderManager mPTSenderManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = NotifyService.this;
        mStartThread = new HandlerThread(mThreadName);
        mStartThread.start();
        mHandler = new Handler(mStartThread.getLooper());

        mPTSenderManager = PTSenderManager.sharedInstance();
        mPTSenderManager.setConfig(new PTMessageConfig.Builder()
                .setHost(HOST).setPort(PORT).setHeartSecond(1 * 60).build());
        mPTSenderManager.init(getApplicationContext());
        mPTSenderManager.setReceiveMessageListener(new OnReceiveMessageListener() {
            @Override
            public void onResponse(PTRecMessage response) {
                Logger.d("ptl-----------Message", response.getMessage());
                Logger.d("ptl-----------Type", response.getType() + "");
                switch (response.getType()) {
                    case 2:
                        Logger.d("ptl-----------", "连接成功");
                        break;
                   /* case 3:
                        String message = response.getMessage();
                        String result = message.substring(message.indexOf("{"), message.length());
                        Logger.d(mThreadName, result);
                        break;*/
                }
            }
        });
        sendConnectValidate();

    }

    /**
     * 发送连接验证
     */
    public void sendConnectValidate() {
        CS_CONNECT connect = new CS_CONNECT();
        connect.setDeviceid(AccountHelper.getCurrentUid());
        connect.setAppid(appid);
        connect.setSign(getSign(connect.getDeviceid(), secret));
        PTSenderManager.sharedInstance().sendMsg(MsgPackUtil.Pack(connect, PTMessageType.CS_CONNECT));
    }

    public String getSign(String deviceid, String secret) {
        return MD5Util.getMD5Str(deviceid + appid + secret).toUpperCase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPTSenderManager.stopThreads();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
