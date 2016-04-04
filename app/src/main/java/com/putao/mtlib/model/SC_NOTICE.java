package com.putao.mtlib.model;

import java.io.Serializable;

/**
 * 服务器给客户端推送消息
 * Created by Administrator on 2015/12/28.
 */
public class SC_NOTICE implements Serializable {
    private int msgId;
    private String data;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SC_NOTICE{" +
                "msgId=" + msgId +
                ", data='" + data + '\'' +
                '}';
    }
}
