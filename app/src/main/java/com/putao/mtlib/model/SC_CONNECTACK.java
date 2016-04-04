package com.putao.mtlib.model;

import java.io.Serializable;

/**
 * 服务端给客户端的应答
 * Created by guchenkai on 2015/12/28.
 */
public class SC_CONNECTACK implements Serializable {
    private byte retCode;

    public byte getRetCode() {
        return retCode;
    }

    public void setRetCode(byte retCode) {
        this.retCode = retCode;
    }

    @Override
    public String toString() {
        return "SC_CONNECT{" +
                "retCode=" + retCode +
                '}';
    }
}
