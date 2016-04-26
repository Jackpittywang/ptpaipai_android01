package com.putao.mtlib.tcp;

import android.content.Intent;

import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.putao.camera.util.AppUtils;
import com.putao.mtlib.CameraNotifyService;
import com.putao.mtlib.model.CS_CONNECT;
import com.putao.mtlib.util.MsgPackUtil;
import com.putao.mtlib.util.PTLoger;

/**
 * @author jidongdong
 *         <p/>
 *         2015年7月27日 下午6:20:08
 */
class PTSocketHeartThread extends Thread {
    boolean isStop = false;
    boolean mIsConnectSocketSuccess = false;
    static PTSocketHeartThread s_instance;

    private PTTCPClient mTcpClient = null;
    private static byte[] PingBytes;

    public static synchronized PTSocketHeartThread instance() {
        if (s_instance == null) {
            s_instance = new PTSocketHeartThread();
        }
        return s_instance;
    }

    public PTSocketHeartThread() {
        PTLoger.i("init socket heart thread");
        PingBytes = PTMessageUtil.getMesageByteArray(PTMessageType.CS_PINGREQ, null);
    }

    public void stopThread() {
        isStop = true;
        PTLoger.d("heart thread isStop:" + isStop);
    }

    /**
     * 连接socket到服务器, 并发送初始化的Socket信息
     *
     * @return
     */

    private boolean reConnect() {
        return PTTCPClient.instance().reConnect();
    }


    public void run() {
        while (!isStop) {
            boolean canConnectToServer = false;
            if (AppUtils.isApplicationInBackground(MainApplication.getInstance())) {
                MainApplication.getInstance().sendBroadcast(new Intent(MainApplication.OUT_FORE_MESSAGE));
            }/* else {
                MainApplication.getInstance().sendBroadcast(new Intent(MainApplication.IN_FORE_MESSAGE));
            }*/
            if (PTSocketOutputThread.isConnected && PTTCPClient.instance().isConnect()) {

                PTLoger.d("SocketConnect--is---------true, send heart message/");
                PTSenderManager.sharedInstance().sendMsg(PingBytes, null);
                canConnectToServer = true;
            } else {
                PTLoger.d("SocketConnect--is---------false, no send /");
            }
            if (!canConnectToServer && PTSocketOutputThread.isConnected) {
                if (reConnect()) CameraNotifyService.sendConnectValidate();
            }
            try {
                Thread.sleep(PTSenderManager.sharedInstance().getConfig().getHeartSecond() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
