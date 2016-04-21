package com.putao.mtlib.util;

import com.putao.mtlib.jni.MsgpackJNI;
import com.putao.mtlib.model.CS_CONNECT;
import com.putao.mtlib.model.CS_NOTICEACK;
import com.putao.mtlib.tcp.PTMessageType;
import com.putao.mtlib.tcp.PTMessageUtil;

/**
 * 消息打包
 *
 * @author jidongdong
 *         <p/>
 *         2015年8月11日 下午5:22:11
 */
public class MsgPackUtil {

    /**
     * @param object
     * @param type
     * @return
     */
    public static byte[] Pack(Object object, int type) {
        byte[] body = null;
        if (type == PTMessageType.CS_CONNECT) {
            if (object instanceof CS_CONNECT) {
                CS_CONNECT item = (CS_CONNECT) object;
                PTLoger.d("device id::" + item.getDeviceid());
                PTLoger.d("app id::" + item.getAppid());
                PTLoger.d("sign::" + item.getSign());
                body = MsgpackJNI.PackMessageWeidu(item.getDeviceid(), item.getAppid(), item.getSign());
            }
        }
//		} else if (type == PTMessageType.CS_LOGGER_ACTION) {
//			if (object instanceof CS_LOGACTION) {
//				CS_LOGACTION action = (CS_LOGACTION) object;
//				body = MsgpackJNI.PackLoggerActionMsg(action.getActionid(), action.getTime(), action.getInfo1(),
//						action.getInfo2(), action.getInfo3(), action.getInfo4(), action.getInfo5());
//			}

        else if(type == PTMessageType.CS_NOTICEACK){
            CS_NOTICEACK item = (CS_NOTICEACK) object;
            body = MsgpackJNI.PackMessageWeiduBack(item.getMsgId());
        }
        byte[] data = PTMessageUtil.getMesageByteArray(type, body);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            sb.append(data[i] & 0xff);
            if (i < data.length - 1) {
                sb.append(",");
            }
        }
        PTLoger.d("send data::" + sb.toString());
        return data;
    }
}
