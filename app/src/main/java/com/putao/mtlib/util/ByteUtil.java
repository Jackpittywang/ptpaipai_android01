package com.putao.mtlib.util;

/**
 * 
 * @author jidongdong
 *
 *         2015-7-24 11:35:35
 *
 */
public class ByteUtil {

	public static byte[] intToByteArray(final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));

		byte[] newbyteArray = new byte[byteArray.length];
		for (int m = 0; m < byteArray.length; m++) {
			newbyteArray[m] = byteArray[byteArray.length - 1 - m];
		}
		return (newbyteArray);
	}

	/**
	 * 
	 * @param iSource
	 * @param iArrayLen
	 * @return
	 */
	public static byte[] toByteArray(int iSource, int iArrayLen) {
		byte[] bLocalArr = new byte[iArrayLen];
		for (int i = 0; (i < 4) && (i < iArrayLen); i++) {
			bLocalArr[i] = (byte) (iSource >> 8 * i & 0xFF);
		}
		return bLocalArr;
	}

	/**
	 * 
	 * @param bRefArr
	 * @return
	 */
	public static int ByteArraytoInt(byte[] bRefArray) {
		int iOutcome = 0;
		byte bLoop;
		for (int i = 0; i < bRefArray.length; i++) {
			bLoop = bRefArray[i];
			iOutcome += (bLoop & 0xFF) << (8 * i);
		}
		return iOutcome;
	}
}
