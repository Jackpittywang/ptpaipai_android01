package com.putao.mtlib.tcp;

/**
 * 
 * @author jidongdong
 *
 *         2015年7月27日 下午6:19:56
 *
 */
public class PTMessageType {
	public static final int Reserved = 0;
	/**
	 * 客户端发起连接服务端
	 */
	public static final int CS_CONNECT = 1;
	/**
	 * 服务端给客户端的应答
	 */
	public static final int SC_CONNECTACK = 2;
	/**
	 * 服务器给客户端推送消息
	 */
	public static final int SC_NOTICE = 3;
	/**
	 * 服务器在推送后，客户端发的确认
	 */
	public static final int CS_NOTICEACK = 4;
	/**
	 * 客户端主动给服务器ping
	 */
	public static final int CS_PINGREQ = 5;
	/**
	 * 服务端给客户端的ping的应答
	 */
	public static final int SC_PINGRESP = 6;
	/**
	 * 消息发布(APP端暂时不需要使用此字段)
	 */
	public static final int CS_PUBLISH = 7;
	/**
	 * 消息发布回应(APP端暂时不需要使用此字段)
	 */
	public static final int CS_PUBLISACK = 8;

	/**
	 * 打点统计连接
	 */
	public static final int CS_LOGGER_CONNECT = 1;

	/**
	 *服务端给客户端的应答
	 */
	public static final int SC_LOGGER_CONNECTACK = 2;

	/**
	 * 客户端给服务器发送日志消息
	 */
	public static final int CS_LOGGER_ACTION = 3;
}
