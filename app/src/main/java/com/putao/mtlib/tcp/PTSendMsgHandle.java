package com.putao.mtlib.tcp;

import android.os.Handler;
import android.os.Message;

public class PTSendMsgHandle extends Handler {
	public final static int SEND_SUCCESS = 1;
	public final static int SEND_FAILED = 2;

	private OnSendMessageListener mOnSendMessageListener;

	public void setOnSendMessageListener(OnSendMessageListener listener) {
		mOnSendMessageListener = listener;
	}

	public PTSendMsgHandle() {
		mOnSendMessageListener = new OnSendMessageListenerImpl();
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		switch (msg.what) {
		case SEND_SUCCESS:
		case SEND_FAILED:
			if (mOnSendMessageListener != null)
				mOnSendMessageListener.onSend(msg.what);
			break;
		default:
			break;
		}
	}

}
