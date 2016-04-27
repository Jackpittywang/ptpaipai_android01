package com.putao.mtlib.tcp;

import android.content.Intent;

import com.putao.mtlib.util.PTLoger;
import com.putao.wd.GlobalApplication;

/**
 * @author jidongdong
 *         <p>
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
//        isStop = false;
        while (!isStop) {
            try {
                Thread.sleep(PTSenderManager.sharedInstance().getConfig().getHeartSecond() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (PTSocketOutputThread.isConnected && PTTCPClient.instance().isConnect()) {
                PTLoger.d("SocketConnect--is---------true, send heart message/");
                PTSenderManager.sharedInstance().sendMsg(PingBytes, null);
            } else {
                PTLoger.d("SocketConnect--is---------false, no send /");
//                reConnect();
                GlobalApplication.getInstance().sendBroadcast(new Intent(GlobalApplication.RESTART_MESSAGE));
            }
        }
    }
}
