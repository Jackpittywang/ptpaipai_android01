package com.putao.mtlib.tcp;

import android.os.Handler;

import com.putao.mtlib.util.PTLoger;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月27日 下午6:20:53
 *
 */
public class PTSocketOutputThread extends Thread {
	private boolean isStart = true;
	private List<MsgEntity> sendMsgList;

	public PTSocketOutputThread() {

		sendMsgList = new CopyOnWriteArrayList<MsgEntity>();
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
		synchronized (this) {
			notify();
		}
	}

	public boolean sendMsg(byte[] msg) throws Exception {
		if (msg == null) {
			return false;
		}

		try {
			PTTCPClient.instance().sendMsg(msg);
		} catch (Exception e) {
			throw (e);
		}
		return true;
	}

	public void addMsgToSendList(MsgEntity msg) {

		synchronized (this) {
			this.sendMsgList.add(msg);
			notify();
		}
	}

	@Override
	public void run() {
		while (isStart) {
			synchronized (sendMsgList) {
				for (MsgEntity msg : sendMsgList) {
					Handler handler = msg.getHandler();
					try {
						sendMsg(msg.getBytes());
						sendMsgList.remove(msg);
						if (handler != null) {
							handler.sendMessage(handler.obtainMessage(PTSendMsgHandle.SEND_SUCCESS));
						}
					} catch (IOException e) {
						PTTCPClient.instance().setConnectState(false);
						PTLoger.e("server is disconnect");
					} catch (Exception e) {
						e.printStackTrace();
						if (handler != null) {
							handler.sendMessage(handler.obtainMessage(PTSendMsgHandle.SEND_FAILED));
						}
					}
				}
			}

			synchronized (this) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
