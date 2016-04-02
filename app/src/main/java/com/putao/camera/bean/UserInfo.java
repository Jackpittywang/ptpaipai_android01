package com.putao.camera.bean;

import java.io.Serializable;

/**
 * 用户信息
 * Created by guchenkai on 2015/12/8.
 */
public class UserInfo implements Serializable {
    private String nick_name;//昵称
    private String head_img;//头像图片名
    private String profile;//简介

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getHead_img() {
        return head_img;
    }

    public void setHead_img(String head_img) {
        this.head_img = head_img;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "nick_name='" + nick_name + '\'' +
                ", head_img='" + head_img + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
