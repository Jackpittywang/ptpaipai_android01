package com.putao.mtlib.model;

import java.io.Serializable;

/**
 * 服务器在推送后，客户端发的确认
 * Created by guchenkai on 2015/12/28.
 */
public class CS_NOTICEACK implements Serializable {
    private int msgId;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    @Override
    public String toString() {
        return "CS_NOTICEACK{" +
                "msgId=" + msgId +
                '}';
    }
}
