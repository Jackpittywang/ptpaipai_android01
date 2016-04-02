package com.putao.account;

import com.alibaba.fastjson.JSONObject;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.UserInfo;
import com.sunnybear.library.util.PreferenceUtils;
import com.sunnybear.library.util.StringUtils;

/**
 * 通行证助手
 * Created by guchenkai on 2015/11/25.
 */
public final class AccountHelper {

    /**
     * 登录
     *
     * @param jsonObject jsonObject
     */
    public static void login(JSONObject jsonObject) {
        String uid = jsonObject.getString("uid");
        String token = jsonObject.getString("token");
        String nickname = jsonObject.getString("nickname");
        String expire_time = jsonObject.getString("expire_time");
        String refresh_token = jsonObject.getString("refresh_token");

        if (!StringUtils.isEmpty(uid))
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_UID, uid);
        if (!StringUtils.isEmpty(token))
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_TOKEN, token);
        if (!StringUtils.isEmpty(nickname))
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_NICKNAME, nickname);
        if (!StringUtils.isEmpty(expire_time))
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_EXPIRE_TIME, expire_time);
        if (!StringUtils.isEmpty(uid))
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_REFRESH_TOKEN, refresh_token);
    }

    /**
     * 登出
     */
    public static void logout() {
//        PreferenceUtils.clear();
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_UID);
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_TOKEN);
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_NICKNAME);
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_EXPIRE_TIME);
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_REFRESH_TOKEN);
        PreferenceUtils.remove(MainApplication.PREFERENCE_KEY_USER_INFO);
    }

    /**
     * 获取当前登录的Uid
     *
     * @return 当前登录的Uid
     */
    public static String getCurrentUid() {
        return PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_UID, "");
    }

    /**
     * 保存当前Uid
     *
     * @param uid
     */
    public static void setCurrentUid(String uid) {
        PreferenceUtils.save(MainApplication.PREFERENCE_KEY_UID, uid);
    }

    /**
     * 获取当前的token
     *
     * @return 当前登录的Uid
     */
    public static String getCurrentToken() {
        return PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_TOKEN, "");
    }

    /**
     * 保存当前Token
     *
     * @param token
     */
    public static void setCurrentToken(String token) {
        PreferenceUtils.save(MainApplication.PREFERENCE_KEY_TOKEN, token);
    }

    /**
     * 是否在登录状态
     *
     * @return
     */
    public static boolean isLogin() {
        return !StringUtils.isEmpty(getCurrentUid()) && !StringUtils.isEmpty(getCurrentToken()) && getCurrentUserInfo() != null;
    }

    /**
     * 设置当前userInfo
     *
     * @param userInfo
     */
    public static void setUserInfo(UserInfo userInfo) {
        if (userInfo != null)
            PreferenceUtils.save(MainApplication.PREFERENCE_KEY_USER_INFO, userInfo);
    }

    /**
     * 获取当前userInfo
     *
     * @return
     */
    public static UserInfo getCurrentUserInfo() {
        return PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_USER_INFO, null);
    }

    /**
     * 获取当前昵称
     *
     * @return
     */
    public static String getUserNickName() {
        return PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_NICKNAME, "");
    }
}
