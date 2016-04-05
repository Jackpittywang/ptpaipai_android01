package com.putao.camera.base;


import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.sunnybear.library.model.http.request.FormEncodingRequestBuilder;
import com.sunnybear.library.util.AppUtils;

/**
 * 继承固定请求参数
 * Created by guchenkai on 2015/11/26.
 */
public class PTWDRequestHelper {
    //===================request key================================
    public static final String REQUEST_KEY_UID = "uid";
    public static final String REQUEST_KEY_TOKEN = "token";
    public static final String REQUEST_KEY_DEVICE_ID = "master_device_id";
    public static final String REQUEST_KEY_APP_ID = "appid";

    public static final String REQUEST_KEY_START_DEVICE_ID = "deviceid";
//    public static final String REQUEST_KEY_START_DEVICE_ID = "device_id";


    /**
     * 封装固定请求参数(葡商城使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder store() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(PTWDRequestHelper.REQUEST_KEY_UID, AccountHelper.getCurrentUid())
                .addParam(PTWDRequestHelper.REQUEST_KEY_TOKEN, AccountHelper.getCurrentToken())
                .addParam(PTWDRequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id)
                .addParam(PTWDRequestHelper.REQUEST_KEY_START_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()));
    }

    /**
     * 封装固定请求参数(葡商城使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder start() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(PTWDRequestHelper.REQUEST_KEY_UID, AccountHelper.getCurrentUid())
                .addParam(PTWDRequestHelper.REQUEST_KEY_TOKEN, AccountHelper.getCurrentToken())
                .addParam(PTWDRequestHelper.REQUEST_KEY_START_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(PTWDRequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id);
    }

    /**
     * 封装固定请求参数(购物车使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder shopCar() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(PTWDRequestHelper.REQUEST_KEY_UID, AccountHelper.getCurrentUid())
                .addParam(PTWDRequestHelper.REQUEST_KEY_TOKEN, AccountHelper.getCurrentToken())
                .addParam(PTWDRequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id);
    }

    /**
     * 封装固定请求参数(探索号使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder explore() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(PTWDRequestHelper.REQUEST_KEY_UID, AccountHelper.getCurrentUid())
                .addParam(PTWDRequestHelper.REQUEST_KEY_TOKEN, AccountHelper.getCurrentToken())
                .addParam(PTWDRequestHelper.REQUEST_KEY_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(PTWDRequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id);
    }

    /**
     * 封装固定请求参数(上传使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder upload() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(REQUEST_KEY_UID, AccountHelper.getCurrentUid());
    }

    /**
     * 封装固定请求参数(用户使用)
     *
     * @return Request实例
     */
    public static FormEncodingRequestBuilder user() {
        return FormEncodingRequestBuilder.newInstance()
                .addParam(PTWDRequestHelper.REQUEST_KEY_UID, AccountHelper.getCurrentUid())
                .addParam(PTWDRequestHelper.REQUEST_KEY_TOKEN, AccountHelper.getCurrentToken())
                .addParam(PTWDRequestHelper.REQUEST_KEY_START_DEVICE_ID, AppUtils.getDeviceId(MainApplication.getInstance()))
                .addParam(PTWDRequestHelper.REQUEST_KEY_APP_ID, MainApplication.app_id);
    }
}
