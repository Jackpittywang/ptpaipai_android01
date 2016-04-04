package com.putao.mtlib.tcp;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月29日 下午5:19:09
 *
 */

public class PTMessage {
	int type;
	int length;
	byte[] data;

	public PTMessage(int type, int len, byte[] data) {
		this.type = type;
		this.length = len;
		this.data = data;
	}
}
