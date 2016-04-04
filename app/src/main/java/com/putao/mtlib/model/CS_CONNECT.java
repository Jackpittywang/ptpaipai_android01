package com.putao.mtlib.model;

import java.io.Serializable;

/**
 * 客户端发起连接服务端
 * Created by guchenkai on 2015/12/28.
 */
public class CS_CONNECT implements Serializable {
    private String deviceid;
    private int appid;
    private String sign;

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getAppid() {
        return appid;
    }

    public void setAppid(int appid) {
        this.appid = appid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "CS_CONNECT{" +
                "deviceid='" + deviceid + '\'' +
                ", appid=" + appid +
                ", sign='" + sign + '\'' +
                '}';
    }
}
