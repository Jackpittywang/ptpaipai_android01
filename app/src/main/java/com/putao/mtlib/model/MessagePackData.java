package com.putao.mtlib.model;

/**
 * Created by yanguoqiang on 16/4/15.
 */
public class MessagePackData {
    // 消息id
    private int msgId;
    // 消息数据
    private String msg;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
