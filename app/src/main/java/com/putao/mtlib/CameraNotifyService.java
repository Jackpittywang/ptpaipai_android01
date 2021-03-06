package com.putao.mtlib;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.putao.mtlib.model.CS_CONNECT;
import com.putao.mtlib.tcp.OnReceiveMessageListener;
import com.putao.mtlib.tcp.PTMessageConfig;
import com.putao.mtlib.tcp.PTMessageType;
import com.putao.mtlib.tcp.PTRecMessage;
import com.putao.mtlib.tcp.PTSenderManager;
import com.putao.mtlib.tcp.PTSocketOutputThread;
import com.putao.mtlib.tcp.PTTCPClient;
import com.putao.mtlib.util.MD5Util;
import com.putao.mtlib.util.MsgPackUtil;
import com.putao.mtlib.util.PTLoger;
import com.sunnybear.library.util.AppUtils;
import com.sunnybear.library.util.Logger;

import java.net.InetAddress;

/**
 * 通知服务
 * Created by Administrator on 2015/12/28.
 */
public class CameraNotifyService extends Service {
    private static String HOST = MainApplication.isDebug ? "10.1.11.31" : "notice.putao.com";
    private static final int PORT = MainApplication.isDebug ? 8083 : 8040;
    private static final String secret = "499478a81030bb177e578f86410cda8641a22799";
    public static final int appid = 613;

    private Context mContext;
    private HandlerThread mStartThread;
    private Handler mHandler;
    private String mThreadName = CameraNotifyService.class.getSimpleName();
    private PTSenderManager mPTSenderManager;
    private Thread thread;
    private boolean isAlive = true;

    private Handler mThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mContext = CameraNotifyService.this;
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
                            PTSocketOutputThread.isConnected = true;
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
    };

    @Override
    public void onCreate() {
        super.onCreate();
        if (!MainApplication.isDebug) {
            if (null == thread)
                thread = new Thread("realhost") {
                    @Override
                    public void run() {
                        super.run();
                        while (isAlive) {
                            InetAddress ip = null;
                            try {
                                PTLoger.d("尝试获取ip");
                                ip = InetAddress.getByName(HOST);
                                if (null != ip) {
                                    HOST = ip.getHostAddress();
                                    mThreadHandler.sendEmptyMessage(0);
                                    PTLoger.d("获取ip成功");
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                PTLoger.d("获取ip失败----" + e);
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
            thread.start();
        } else {
            mThreadHandler.sendEmptyMessage(0);
        }
    }

    /**
     * 发送连接验证
     */
    public static void sendConnectValidate() {
        CS_CONNECT connect = new CS_CONNECT();
        connect.setDeviceid(AppUtils.getDeviceId(MainApplication.getInstance()));
        connect.setAppid(appid);
        connect.setSign(getSign(connect.getDeviceid(), secret));
        PTSenderManager.sharedInstance().sendMsg(MsgPackUtil.Pack(connect, PTMessageType.CS_CONNECT));
    }

    public static String getSign(String deviceid, String secret) {
        return MD5Util.getMD5Str(deviceid + appid + secret).toUpperCase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPTSenderManager.stopThreads();
        PTTCPClient.setS_Tcp(null);
        isAlive = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
