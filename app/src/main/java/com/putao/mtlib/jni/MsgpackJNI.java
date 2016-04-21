package com.putao.mtlib.jni;

import com.putao.mtlib.model.MessagePackData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    public static native int UnpackNoticeData(byte[] bytes, int length);

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


    /**
     * messagepack 转成messagepackdata
     *
     * @param bytes
     * @param length
     * @return
     */
    public static MessagePackData unpackMessageData(byte[] bytes, int length) {
        MessagePackData msgData = new MessagePackData();
        try {
            msgData.setMsgId(UnpackNoticeData(bytes, length));
            String data = new String(bytes, "UTF-8");
//            Pattern p1 = Pattern.compile(data.endsWith("]}") ? "\\{.+?\\]\\}" : "\\{.+?null\\}");
            Pattern p1 = Pattern.compile("\\{.+?\\}\\}");
            Matcher match = p1.matcher(data);
            if (match.find()) {
                msgData.setMsg(match.group(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msgData;
    }


}
