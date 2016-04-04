package com.putao.mtlib.jni;

/**
 * @author jidongdong
 *         <p/>
 *         2015年7月29日 下午5:27:46
 */
public class MsgpackJNI {

    /**
     * @param uid
     * @param appid
     * @param data
     * @return
     */
    public static native byte[] PackMessage(int uid, int appid, String data);

    public static native byte[] PackMessageWeidu(String device, int appid, String sign);

    public static native byte[] PackMessageWeiduBack(int msgId);

    /**
     * @param bytes
     * @param length
     * @return
     */
    public static native String UnPackMessage(byte[] bytes, int length);

    /**
     * @param data
     * @return
     */
    public static native String UnpackMessageData(byte[] data, int length);

    /**
     * @param uid
     * @param appid
     * @param sign
     * @param deviceid
     * @param token
     * @return
     */
    public static native byte[] PackLoggerConnectMsg(int uid, int appid, String sign, String deviceid, String token);

    /**
     * @param actionid
     * @param time
     * @param info1
     * @param info2
     * @param info3
     * @param info4
     * @param info5
     * @return
     */
    public static native byte[] PackLoggerActionMsg(int actionid, int time, String info1, String info2, String info3,
                                                    String info4, String info5);

    static {
        System.loadLibrary("putaomt");
    }
}
