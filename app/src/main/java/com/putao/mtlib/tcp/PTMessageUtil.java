package com.putao.mtlib.tcp;

import com.putao.mtlib.util.ByteUtil;

/**
 * 
 * @author jidongdong
 *
 * 
 */
public class PTMessageUtil {

	/**
	 * make socket send byteArray one time
	 * 
	 * @param type
	 *            message type
	 * @param data
	 *            send user data
	 * @return union message header and body,return byte array data
	 */
	public static byte[] getMesageByteArray(int type, byte[] data) {
		int data_length = data != null ? data.length : 0;
		int buff_len = data_length + 6;
		byte[] buff = new byte[buff_len];
		buff[0] = ByteUtil.intToByteArray(type)[0];
		byte[] message_length = ByteUtil.intToByteArray(data_length);
		for (int j = 0; j < message_length.length; j++) {
			buff[j + 2] = message_length[j];
		}
		if (null != data) {
			for (int i = 0; i < data.length; i++) {
				buff[6 + i] = data[i];
			}
		}
		return buff;
	}

	/**
	 * analysis message type from header
	 * 
	 * @param type
	 * 
	 * @return convert message type from byte[] to int
	 */
	public static int getMessageType(byte[] header) {
		if (header == null || header.length < 2)
			return -1;
		return ByteUtil.ByteArraytoInt(new byte[] { header[0], header[1] });
	}

	/**
	 * get message packet body length from header
	 * 
	 * @param header
	 * @return
	 */
	public static int getPacketBodySize(byte[] header) {
		if (header.length < 6)
			return -1;
		return ByteUtil.ByteArraytoInt(new byte[] { header[2], header[3], header[4], header[5] });
	}
	
}
