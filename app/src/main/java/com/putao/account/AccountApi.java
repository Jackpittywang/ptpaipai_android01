package com.putao.account;

import com.putao.base.RequestHelper;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.PTWDRequestHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.squareup.okhttp.Request;
import com.sunnybear.library.model.http.request.FormEncodingRequestBuilder;
import com.sunnybear.library.model.http.request.RequestMethod;
import com.sunnybear.library.util.AppUtils;
import com.sunnybear.library.util.PreferenceUtils;
import com.sunnybear.library.view.image.ImageDraweeView;

import java.security.MessageDigest;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * 通行证api
 * Created by guchenkai on 2015/11/2.
 */
public class AccountApi {
    public static final String PLATFORM_ID = "1";   //平台id
    public static final String CLIENT_TYPE = "2";  //设备类型

    public static final String BASE_URL = MainApplication.isDebug ? "https://account-api-dev.putao.com/" : "https://account-api.putao.com/";

    public static String APP_ID;//app_id
    public static String VERSION;//版本号
    public static String SECRETKEY;


    public static void install(String version, String appid, String secretkey) {
        VERSION = version;
        APP_ID = appid;
        SECRETKEY = secretkey;
    }

    /**
     * 注册
     */
    public static final String URL_REGISTER = BASE_URL + AccountConstants.Url.URL_REGISTER;

    /**
     * 注册
     *
     * @param mobile   账号
     * @param password 密码
     * @param code     验证码
     */
    public static Request register(String mobile, String password, String code, String verfiy) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_ONCE, password)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_TWICE, password)
                .addParam(AccountConstants.Parameter.PARAM_CLIENT_TYPE, AccountApi.CLIENT_TYPE)
                .addParam(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION)
                .addParam(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(AccountConstants.Parameter.PARAM_CODE, code)
                .addParam(AccountConstants.Parameter.PARAM_GRAPH_CODE, verfiy)
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .build(RequestMethod.POST, URL_REGISTER);
    }

    /**
     * 手机注册与否检测
     */
    public static final String URL_CHECK_MOBILE = BASE_URL + AccountConstants.Url.URL_CHECK_MOBILE;

    /**
     * 手机注册与否检测
     *
     * @param mobile 账号
     */
    public static Request checkMobile(String mobile) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID)
                .build(RequestMethod.POST, URL_CHECK_MOBILE);
    }

    /**
     * 发送验证码
     */
    public static final String URL_SEND_VERIFY_CODE = BASE_URL + AccountConstants.Url.URL_SEND_VERIFY_CODE;

    /**
     * 发送验证码
     *
     * @param mobile 账号
     * @param action 验证码发送原因
     */
    public static Request sendVerifyCode(String mobile, String action, String verify) {
        Map<String, String> params = new HashMap<>();
        params.put(AccountConstants.Parameter.PARAM_MOBILE, mobile);
        params.put(AccountConstants.Parameter.PARAM_ACTION, action);
        params.put(AccountConstants.Parameter.PARAM_GRAPH_CODE, verify);
        params.put(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()));
        params.put(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID);
        String sign = generateSign(params, SECRETKEY);
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_ACTION, action)
                .addParam(AccountConstants.Parameter.PARAM_GRAPH_CODE, verify)
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_SIGN, sign)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .build(RequestMethod.POST, URL_SEND_VERIFY_CODE);
    }


    /**
     * 发送图形验证码
     */
    public static final String URL_SEND_PHOTO_CODE = BASE_URL + AccountConstants.Url.URL_SEND_PHOTO_CODE;

    /**
     * 发送图形验证码
     *
     * @param action 验证码发送原因
     */
    public static Request sendPhotoCode(String action) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_ACTION, action)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .build(RequestMethod.POST, URL_SEND_PHOTO_CODE);
    }


    /**
     * 忘记密码
     */
    public static final String URL_FORGET = BASE_URL + AccountConstants.Url.URL_FORGET;

    /**
     * 忘记密码
     *
     * @param mobile   账号
     * @param password 密码
     * @param code     验证码
     */
    public static Request forget(String mobile, String code, String password, String verify) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_CODE, code)
                .addParam(AccountConstants.Parameter.PARAM_GRAPH_CODE, verify)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_ONCE, password)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_TWICE, password)
                .build(RequestMethod.POST, URL_FORGET);
    }

    /**
     * 登录
     */
    public static final String URL_LOGIN = BASE_URL + AccountConstants.Url.URL_LOGIN;

    /**
     * 登录
     *
     * @param mobile   账号
     * @param password 密码
     */
    public static Request login(String mobile, String password) {
        Map<String, String> params = new HashMap<>();
        params.put(AccountConstants.Parameter.PARAM_MOBILE, mobile);
        params.put(AccountConstants.Parameter.PARAM_PASSWD, password);
        params.put(AccountConstants.Parameter.PARAM_CLIENT_TYPE, AccountApi.CLIENT_TYPE);
        params.put(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION);
        params.put(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID);
        params.put(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()));
        params.put(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID);
        String sign = generateSign(params, SECRETKEY);
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD, password)
                .addParam(AccountConstants.Parameter.PARAM_CLIENT_TYPE, AccountApi.CLIENT_TYPE)
                .addParam(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION)
                .addParam(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_SIGN, sign)
                .build(RequestMethod.POST, URL_LOGIN);
    }

    /**
     * 登陆后验证
     */
    public static final String URL_LOGIN_AFTER = PuTaoConstants.PAIPAI_SERVER_HOST + AccountConstants.Url.URL_LOGIN_CHECK;

    /**
     * 登录后的验证
     */
    public static Request login() {
        return PTWDRequestHelper.start()
                .build(RequestMethod.POST, URL_LOGIN_AFTER);

    }

    /**
     * 图形验证码的登录
     */
    public static final String URL_SAFELOGIN = BASE_URL + AccountConstants.Url.URL_SAFELOGIN;

    /**
     * 图形验证码的登录
     *
     * @param mobile   账号
     * @param password 密码
     * @param verify   图形验证码
     */
    public static Request safeLogin(String mobile, String password, String verify) {
        Map<String, String> params = new HashMap<>();
        params.put(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID);
        params.put(AccountConstants.Parameter.PARAM_CLIENT_TYPE, AccountApi.CLIENT_TYPE);
        params.put(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()));
        params.put(AccountConstants.Parameter.PARAM_MOBILE, mobile);
        params.put(AccountConstants.Parameter.PARAM_PASSWD, password);
        params.put(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION);
        params.put(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID);
        params.put(AccountConstants.Parameter.PARAM_GRAPH_CODE, verify);
        String sign = generateSign(params, SECRETKEY);
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_MOBILE, mobile)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD, password)
                .addParam(AccountConstants.Parameter.PARAM_CLIENT_TYPE, AccountApi.CLIENT_TYPE)
                .addParam(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION)
                .addParam(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_GRAPH_CODE, verify)
                .addParam(AccountConstants.Parameter.PARAM_SIGN, sign)
                .build(RequestMethod.POST, URL_SAFELOGIN);
    }

    /**
     * 修改密码
     */
    public static final String URL_UPDATE_PASSWORD = BASE_URL + AccountConstants.Url.URL_UPDATE_PASSWORD;

    /**
     * 修改密码
     *
     * @param oldPassword    旧密码
     * @param newPassword    新密码
     * @param repeatPassword 新密码确认
     */
    public static Request updatePassword(String oldPassword, String newPassword, String repeatPassword) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(RequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id)
                .addParam(RequestHelper.REQUEST_KEY_UID, PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_UID, ""))
                .addParam(RequestHelper.REQUEST_KEY_TOKEN, PreferenceUtils.getValue(MainApplication.PREFERENCE_KEY_TOKEN, ""))
                .addParam(RequestHelper.REQUEST_KEY_START_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(AccountConstants.Parameter.PARAM_VERSION, AccountApi.VERSION)
                .addParam(AccountConstants.Parameter.PARAM_PLATFORM_ID, AccountApi.PLATFORM_ID)
                .addParam(AccountConstants.Parameter.PARAM_OLD_PASSWD, oldPassword)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_ONCE, newPassword)
                .addParam(AccountConstants.Parameter.PARAM_PASSWD_TWICE, repeatPassword)
                .build(RequestMethod.POST, URL_UPDATE_PASSWORD);
    }

    /**
     * 刷新token
     */
    public static final String URL_UPDATE_TOKEN = BASE_URL + AccountConstants.Url.URL_UPDATE_TOKEN;

    /**
     * 刷新token
     *
     * @param token         即将过期的token
     * @param refresh_token 刷新的token
     */
    public static Request updateToken(String token, String refresh_token) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_TOKEN, token)
                .addParam(AccountConstants.Parameter.PARAM_REFRESH_TOKEN, refresh_token)
                .build(RequestMethod.POST, URL_UPDATE_TOKEN);
    }

    /**
     * 验证token
     */
    public static final String URL_CHECK_TOKEN = BASE_URL + AccountConstants.Url.URL_CHECK_TOKEN;

    /**
     * 验证token
     *
     * @param token 令牌
     */
    public static Request checkToken(String token) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_TOKEN, token)
                .build(RequestMethod.POST, URL_CHECK_TOKEN);
    }

    /**
     * 获取昵称
     */
    public static final String URL_GET_NICK_NAME = BASE_URL + AccountConstants.Url.URL_GET_NICK_NAME;

    /**
     * 获取昵称
     *
     * @param token 令牌
     * @param uid   用户id
     */
    public static Request getNickName(String token, String uid) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_TOKEN, token)
                .addParam(AccountConstants.Parameter.PARAM_UID, uid)
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .build(RequestMethod.POST, URL_GET_NICK_NAME);
    }

    /**
     * 设置昵称
     */
    public static final String URL_SET_NICK_NAME = BASE_URL + AccountConstants.Url.URL_SET_NICK_NAME;

    /**
     * 设置昵称
     *
     * @param token     令牌
     * @param uid       用户id
     * @param nick_name 昵称
     */
    public static Request setNickName(String token, String uid, String nick_name) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(AccountConstants.Parameter.PARAM_TOKEN, token)
                .addParam(AccountConstants.Parameter.PARAM_UID, uid)
                .addParam(AccountConstants.Parameter.PARAM_NICK_NAME, nick_name)
                .build(RequestMethod.POST, URL_SET_NICK_NAME);
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    private static Map<String, String> sortMapByKey(Map<String, String> map) {
        if (map == null || map.isEmpty())
            return null;
        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());
        sortMap.putAll(map);
        return sortMap;
    }

    /**
     * 排序器
     */
    private static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

    private static String generateSign(Map<String, String> param, String secretkey) {
        Map<String, String> map = sortMapByKey(param);
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        String result = sb.delete(sb.length() - 1, sb.length()).append(secretkey).toString();
        return getMD5Str(result);
    }

    private static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (Exception e) {
            // TODO: handle exception
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            } else {
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
            }
        }
        return md5StrBuff.toString().toLowerCase();
    }

    public static void OnGraphVerify(ImageDraweeView image_graph_verify, String action) {
        Random random = new Random();
        //图形验证码
        image_graph_verify.setImageURL(RequestHelper.user()
                .addParam("action", action)
                .addParam(AccountConstants.Parameter.PARAM_APPID, AccountApi.APP_ID)
                .addParam(AccountConstants.Parameter.PARAM_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam("r", random.nextInt() + "")
                .joinURL(AccountApi.URL_SEND_PHOTO_CODE));
    }

}
