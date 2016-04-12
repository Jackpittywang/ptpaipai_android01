package com.putao.camera.constants;

import android.content.Context;

import com.putao.camera.application.MainApplication;
import com.putao.camera.base.PTWDRequestHelper;
import com.squareup.okhttp.Request;
import com.sunnybear.library.model.http.request.FormEncodingRequestBuilder;
import com.sunnybear.library.model.http.request.RequestMethod;
import com.sunnybear.library.util.AppUtils;
import com.sunnybear.library.util.PreferenceUtils;

/**
 * 用户接口
 */
public class UserApi {
    private static final String REQUEST_NICK_NAME = "nick_name";//昵称
    private static final String REQUEST_PROFILE = "profile";//简介
    private static final String REQUEST_EXT = "ext";
    private static final String REQUEST_MEDIA = "media_type";

    private static final String REQUEST_FILENAME = "file_name";
    private static final String REQUEST_FILEHASH = "hash";
    private static final String REQUEST_HEAD_ICON = "userProfilePhoto";//头像
    private static final String REQUEST_PAGE = "page";//页码
    private static final String REQUEST_TYPE = "type";//类型
    private static final String REQUEST_MSG = "message";//提问问题
    public static final String HAS_DEVICE = "has_device";//是否已经添加设备

    /*private static final String BABY_ID = "baby_id";//孩子ID
    private static final String BABY_NAME = "baby_name";//孩子昵称
    private static final String RELATION = "relation";//与孩子关系
    private static final String SEX = "sex";//孩子性别
    private static final String BIRTHDAY = "birthday";//孩子生日*/


    private static final String REQUEST_NICKNAME = "nickName";
    private static final String BASE_URL = MainApplication.isDebug ? "http://api-paipai.ptdev.cn/" : "http://api.camera.putao.com/";

    //    private static final String BASE_URL = MainApplication.isDebug ? "http://api-weidu.ptdev.cn/" : "http://api-weidu.putao.com/";//基础url
    private static final String BASE_ACTION_URL = MainApplication.isDebug ? "http://api-weidu.ptdev.cn/" : "http://api-event-dev.putao.com/";//活动,消息,提问使用的地址

    public static void install(String base_url) {
//        BASE_URL = base_url;
    }

    /**
     * 登录接口（查询）
     */
    @Deprecated
    public static final String URL_LOGIN = BASE_URL + "login/verification";

    /**
     * 登录接口（查询）
     */
    @Deprecated
    public static Request login() {
        return PTWDRequestHelper.explore()
                .build(RequestMethod.POST, URL_LOGIN);
    }

    /**
     * 完善用户信息（更新）
     */
    public static final String URL_USER_ADD = BASE_URL + "user/add";
    public static final String URL_USER_EDIT = BASE_URL + "user/edit";
    public static final String URL_USER_MEDIA = BASE_URL + "relation/media";

    public static Request userMedia(String ext, String filename, String filehash, String nick_name, String media_type) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_EXT, ext)
                .addParam(REQUEST_FILENAME, filename)
                .addParam(REQUEST_FILEHASH, filehash)
                .addParam(REQUEST_NICK_NAME, nick_name)
                .addParam(REQUEST_MEDIA, media_type)
                .build(RequestMethod.POST, URL_USER_MEDIA);
    }


    public static Request userAdd(String ext, String filename, String filehash, String nick_name, String user_info) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_EXT, ext)
                .addParam(REQUEST_FILENAME, filename)
                .addParam(REQUEST_FILEHASH, filehash)
                .addParam(REQUEST_NICK_NAME, nick_name)
                .addParam(REQUEST_PROFILE, user_info)
                .build(RequestMethod.POST, URL_USER_ADD);
    }

    /**
     * 完善用户信息（更新）
     *
     * @param ext      图片后缀
     * @param filename 文件名
     * @param filehash 文件hash
     */
    public static Request userEdit(String ext, String filename, String filehash) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_EXT, ext)
                .addParam(REQUEST_FILENAME, filename)
                .addParam(REQUEST_FILEHASH, filehash)
                .build(RequestMethod.POST, URL_USER_EDIT);
    }

    /**
     * 保存昵称
     *
     * @param nick_name 昵称
     */
    public static Request userNick(String nick_name) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_NICK_NAME, nick_name)
                .build(RequestMethod.POST, URL_USER_EDIT);
    }

    /**
     * 保存简介
     *
     * @param user_info 用户简介
     */
    public static Request userInfo(String user_info) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_PROFILE, user_info)
                .build(RequestMethod.POST, URL_USER_EDIT);
    }

    /**
     * 保存昵称
     *
     * @param user_info 用户简介
     */
    public static Request perfectUserInfo(String ext, String filename, String filehash, String nick_name, String user_info) {
        return PTWDRequestHelper.explore()
                .addParam(REQUEST_EXT, ext)
                .addParam(REQUEST_FILENAME, filename)
                .addParam(REQUEST_FILEHASH, filehash)
                .addParam(REQUEST_NICK_NAME, nick_name)
                .addParam(REQUEST_PROFILE, user_info)
                .build(RequestMethod.POST, URL_USER_EDIT);
    }

    /**
     * 完善用户信息（查询）
     */
    public static final String URL_USER_INFO = BASE_URL + "user/info";

    /**
     * 完善用户信息（查询）
     */
    public static Request getUserInfo() {
        return PTWDRequestHelper.explore()
                .build(RequestMethod.POST, URL_USER_INFO);
    }

    /**
     * 获取上传UploadToken
     */
    public static final String URL_UPLOAD_TOKEN = BASE_URL + "get/upload/token";

    /**
     * 获取uploadToken
     */
    public static Request getUploadToken() {
        return PTWDRequestHelper.upload()
                .addParam(REQUEST_TYPE, "uploadPhotos")
                .build(RequestMethod.GET, URL_UPLOAD_TOKEN);
    }

    /**
     * 我参与的活动
     */
    public static final String URL_GET_ME_ACTIONS = BASE_ACTION_URL + "user/event/list";

    /**
     * 我参与的活动
     *
     * @param nick_name 昵称
     * @param head_icon 头像
     * @param page      页码
     */
    public static Request getMeActions(String nick_name, String head_icon, String page) {
        return PTWDRequestHelper.user()
                .addParam(REQUEST_NICKNAME, nick_name)
                .addParam(REQUEST_HEAD_ICON, head_icon)
                .addParam(REQUEST_PAGE, page)
                .build(RequestMethod.GET, URL_GET_ME_ACTIONS);
    }

    /**
     * 提交葡萄籽问题
     */
//    public static final String URL_QUESTION_ADD = BASE_ACTION_URL + "question/add";
    public static final String URL_QUESTION_ADD = BASE_URL + "qa/add";

    /**
     * 提交葡萄籽问题
     *
     * @param nickName         昵称
     * @param userProfilePhoto 头像
     * @param msg              问题
     */
    public static Request questionAdd(String msg, String nickName, String userProfilePhoto) {
        return PTWDRequestHelper.user()
                .addParam(REQUEST_MSG, msg).addParam(REQUEST_NICKNAME, nickName)
                .addParam(REQUEST_HEAD_ICON, userProfilePhoto).
                        build(RequestMethod.POST, URL_QUESTION_ADD);
    }

    /**
     * 获取提问
     */
//    public static final String URL_QUESTION_LIST = BASE_ACTION_URL + "user/question/list";
    /**
     * 获取提问
     */
    public static final String URL_QUESTION_LIST = BASE_URL + "qa/list";

    /**
     * 获取问题列表
     *
     * @param nickName         昵称
     * @param userProfilePhoto 头像
     */
    public static Request getQuestionList(String nickName, String userProfilePhoto) {
        return PTWDRequestHelper.user()
                .addParam(REQUEST_NICKNAME, nickName)
                .addParam(REQUEST_HEAD_ICON, userProfilePhoto)
                .build(RequestMethod.POST, URL_QUESTION_LIST);
    }

    /**
     * 查询孩子信息
     */
    public static final String URL_CHILD_GET = BASE_URL + "kids/getinfo";

    /**
     * 查询孩子信息
     */
    public static Request getChildInfo() {
        return PTWDRequestHelper.user()
                .build(RequestMethod.POST, URL_CHILD_GET);
    }

    /**
     * 保存孩子信息
     */
    public static final String URL_CHILD_SET = BASE_URL + "kids/setinfo";

   /* *//**
     * 保存孩子信息
     *//*
    public static Request setChildInfo(String baby_id, String baby_name, String relation, String sex, String birthday) {
        return PTWDRequestHelper.user()
                .addParam(BABY_ID, baby_id)
                .addParam(BABY_NAME, baby_name)
                .addParam(RELATION, relation)
                .addParam(SEX, sex)
                .addParam(BIRTHDAY, birthday)
                .build(RequestMethod.POST, URL_CHILD_SET);
    }*/

    /**
     * 资源更新
     *
     * @param context 上下文
     */
    public static Request resourceUpload(Context context) {
        return FormEncodingRequestBuilder.newInstance()
                .addParam("appid", MainApplication.app_id)
                .addParam("client_id", "1")
                .addParam("client_secret", "d3159867d3525452773206e189ef6966")
                .addParam("op_id", "1")
                .addParam("resource_version", PreferenceUtils.getValue("resource_version", "10000"))
                .addParam("game_id", "game_id")
                .addParam("app_version", AppUtils.getVersionName(context).substring(1))
                .build(RequestMethod.GET, "http://source.start.wang/client/resource");
    }
}
