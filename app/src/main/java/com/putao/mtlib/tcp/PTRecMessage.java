package com.putao.mtlib.tcp;

import java.io.Serializable;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月27日 下午6:41:52
 *
 */
public class PTRecMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private int type;
	private String data;

	public PTRecMessage(int type, String data) {
		this.type = type;
		this.data = data;
	}

	public String getMessage() {
		return this.data;
	}

	public int getType() {
		return this.type;
	}
}
