package com.putao.mtlib.tcp;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.putao.mtlib.jni.MsgpackJNI;
import com.putao.mtlib.util.MD5Util;
import com.putao.mtlib.util.NetManager;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月27日 下午6:20:01
 *
 */
public class PTSenderManager {

	private static PTSenderManager s_SocketManager = null;

	private PTSocketInputThread mInputThread = null;

	private PTSocketOutputThread mOutThread = null;

	private PTSocketHeartThread mHeartThread = null;

	private PTMessageConfig mConfig = new PTMessageConfig();

	private PTSendMsgHandle mHandler;

	// 获取单例
	public static PTSenderManager sharedInstance() {
		if (s_SocketManager == null) {
			s_SocketManager = new PTSenderManager();
		}
		return s_SocketManager;
	}

	private PTSenderManager() {
		mHandler = new PTSendMsgHandle();
	}

	/**
	 * init network and socket threads
	 * 
	 * @param context
	 */
	public void init(Context context) {
		String host = mConfig.getHost();
		int port = mConfig.getPort();
		if (TextUtils.isEmpty(host) || host.equalsIgnoreCase("null") || port <= 0) {
			throw new IllegalArgumentException("the host must can't be null and the port must be >0");
		}
		NetManager.instance().init(context);
		mHeartThread = new PTSocketHeartThread();
		mInputThread = new PTSocketInputThread(context);
		mOutThread = new PTSocketOutputThread();
		s_SocketManager.startThreads();
	}

	/**
	 * 启动线程
	 */

	private void startThreads() {
		mHeartThread.start();
		mInputThread.start();
		mInputThread.setStart(true);
		mOutThread.start();
		mOutThread.setStart(true);
	}

	/**
	 * stop线程
	 */
	public void stopThreads() {
		mHeartThread.stopThread();
		mInputThread.setStart(false);
		mOutThread.setStart(false);
	}

	public static void releaseInstance() {
		if (s_SocketManager != null) {
			s_SocketManager.stopThreads();
			s_SocketManager = null;
		}
	}

	public void setConfig(PTMessageConfig config) {
		mConfig = config;
		if (mConfig != null && mHeartThread != null) {
			if (!mConfig.getHeartEnable()) {
				mHeartThread.stopThread();
			}
		}
	}

	/**
	 * set receive message listener
	 *
	 * @param listener
	 */
	public void setReceiveMessageListener(OnReceiveMessageListener listener) {
		mInputThread.setOnSocketResponseListener(listener);
	}

	/**
	 * set send message listener
	 * 
	 * @param listener
	 */
	public void setSendMessageListener(OnSendMessageListener listener) {
		if (mHandler != null)
			mHandler.setOnSendMessageListener(listener);
	}

	public boolean IsConnected() {
		return PTTCPClient.instance().isConnect();
	}

	/**
	 * 发送连接验证
	 * 
	 * @param uid
	 * @param appid
	 * @param secret
	 */
	public void sendConnectValidate(int uid, int appid, String secret) {
		String valivate_data = MD5Util.getMD5Str(uid + "" + appid + "" + secret).toUpperCase();
		byte[] data = MsgpackJNI.PackMessage(uid, appid, valivate_data);
		byte[] buff = PTMessageUtil.getMesageByteArray(PTMessageType.CS_CONNECT, data);
		PTSenderManager.sharedInstance().sendMsg(buff, mHandler);
	}

	/**
	 * 
	 * @param buffer
	 */
	public void sendMsg(byte[] buffer) {
		sendMsg(buffer, mHandler);
	}

	void sendMsg(byte[] buffer, Handler handler) {
		Log.i("ptl", "send socket message     !!!!!!:" + buffer[0]);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < buffer.length; i++) {
			sb.append(i + 1 + "=" + buffer[i] + " ");
		}
		MsgEntity entity = new MsgEntity(buffer, mHandler);
		mOutThread.addMsgToSendList(entity);
	}

	public PTMessageConfig getConfig() {
		return mConfig;
	}

}
