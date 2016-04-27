package com.putao.account;

/**
 * 常量
 * Created by guchenkai on 2015/10/16.
 */
public final class AccountConstants {

    /**
     * 参数常量
     */
    public static final class Parameter {
        public static final String PARAM_MOBILE = "mobile";                 //账户
        public static final String PARAM_PASSWD = "passwd";                 //登陆密码
        public static final String PARAM_PASSWD_ONCE = "passwd_once";       //密码
        public static final String PARAM_PASSWD_TWICE = "passwd_twice";     //密码确认
        public static final String PARAM_OLD_PASSWD = "old_passwd";         //旧密码
        public static final String PARAM_CLIENT_TYPE = "client_type";       //设备类型(1:ios,2:andriod,3:wp,4:other)
        public static final String PARAM_VERSION = "version";               //version
        public static final String PARAM_PLATFORM_ID = "platform_id";       //平台(putao:1)
        public static final String PARAM_DEVICE_ID = "device_id";           //设备id(IMEI)
        public static final String PARAM_CODE = "code";                     //手机验证码
        public static final String PARAM_GRAPH_CODE = "verify";             //图形验证码
        public static final String PARAM_APPID = "appid";                   //平台ID(1101:ios,1102:Android)
        public static final String PARAM_ACTION = "action";                 //验证码发送原因(register|forget|changephone|checkoldphone|login)
        public static final String PARAM_TOKEN = "token";                   //令牌
        public static final String PARAM_REFRESH_TOKEN = "refresh_token";   //刷新用的token
        public static final String PARAM_UID = "uid";                       //用户ID
        public static final String PARAM_NICK_NAME = "nick_name";           //昵称
        public static final String PARAM_SIGN = "sign";                     //签名
    }

    /**
     * Url常量
     */
    public static final class Url {
        public static final String URL_REGISTER = "api/register";           //注册
        public static final String URL_CHECK_MOBILE = "api/checkMobile";     //手机注册与否检测
        //        public static final String URL_SEND_VERIFY_CODE = "api/sendMsg";     //发送验证码
        public static final String URL_SEND_VERIFY_CODE = "/api/safeSendMsg";//发送验证码安全接口
        public static final String URL_FORGET = "api/forget";               //忘记密码(手机)
        public static final String URL_LOGIN = "api/login";                 //登录
        public static final String URL_LOGIN_CHECK = "/user/login";                 //登录后的连接传送
        public static final String URL_SAFELOGIN = "api/safeLogin";         //安全登录
        public static final String URL_UPDATE_PASSWORD = "api/changePasswd";  //修改密码
        public static final String URL_UPDATE_TOKEN = "api/updateToken";    //更新token
        public static final String URL_CHECK_TOKEN = "api/checkToken";      //验证token
        public static final String URL_GET_NICK_NAME = "api/getNickName";   //获取昵称
        public static final String URL_SET_NICK_NAME = "api/setNickName";   //设置昵称
        public static final String URL_SEND_PHOTO_CODE = "api/verification";//发送图形验证码
    }

    /**
     * 验证码发送原因
     */
    public static final class Action {
        public static final String ACTION_REGISTER = "register";            //注册
        public static final String ACTION_FORGET = "forget";                //忘记密码
        public static final String ACTION_LOGIN = "login";                  //登录
        public static final String ACTION_CHANGEPHONE = "changephone";
        public static final String ACTION_CHECKOLDPHONE = "checkoldphone";
    }

    /**
     * 验证错误信息
     */
    public static final class ErrorMessage {
        public static final String check_password_error_length_msg = "密码长度必须大于6位且小于18位";
        public static final String check_password_error_diff_msg = "两次输入密码不一致";
        public static final String check_nick_name_error_length_msg = "昵称长度必须大于4位且小于24位";
        public static final String check_nick_name_error_format_msg = "昵称仅支持中英文和数字";
    }
}
