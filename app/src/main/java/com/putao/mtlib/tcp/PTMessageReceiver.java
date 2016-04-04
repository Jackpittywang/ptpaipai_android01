package com.putao.mtlib.tcp;

import android.content.BroadcastReceiver;

/**
 * 继承该类用于接收收到的消息
 * 
 * @author jidongdong
 *
 *         2015年7月29日 下午5:28:13
 *
 */
public abstract class PTMessageReceiver extends BroadcastReceiver {

	public final static String RedAction = "com.putao.mtlib.message";
	public final static String KeyMessage = "message";

}
