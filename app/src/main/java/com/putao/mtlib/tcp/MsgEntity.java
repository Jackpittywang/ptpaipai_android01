package com.putao.mtlib.tcp;

import android.os.Handler;

/**
 * 
 * @author
 *
 */
public class MsgEntity {
	private byte[] bytes;
	private Handler mHandler;

	public MsgEntity(byte[] bytes, PTSendMsgHandle handler) {
		this.bytes = bytes;
		mHandler = handler;
	}

	public byte[] getBytes() {
		return this.bytes;
	}

	public Handler getHandler() {
		return mHandler;
	}

}
