package com.putao.camera.share;

/**
 * 点击分享框
 * Created by guchenkai on 2015/12/30.
 */
public abstract class OnShareClickListener {

    public abstract void onWechat();

    public abstract void onWechatFriend();

    public abstract void onQQFriend();

    public abstract void onQQZone();

    public abstract void onSinaWeibo();

    public void onCollection() {

    }

    public void onCopyUrl() {

    }

    public void onCancel() {

    }
}
