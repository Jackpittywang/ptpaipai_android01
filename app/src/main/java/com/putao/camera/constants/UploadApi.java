package com.putao.camera.constants;

import com.putao.account.AccountHelper;
import com.putao.camera.application.MainApplication;
import com.squareup.okhttp.Request;
import com.sunnybear.library.model.http.UploadFileTask;
import com.sunnybear.library.model.http.request.MultiPartRequestBuilder;

import java.io.File;

/**
 * 上传文件接口
 * Created by guchenkai on 2015/12/14.
 */
public class UploadApi {
    public static final String REQUEST_UID = "x:uid";
    public static final String REQUEST_FILENAME = "filename";
    public static final String REQUEST_SHA1 = "sha1";
    public static final String REQUEST_TYPE = "type";
    public static final String REQUEST_APP_ID = "appid";
    public static final String REQUEST_UPLOAD_TOKEN = "uploadToken";
    public static final String REQUEST_FILE = "file";

    private static final String BASE_URL = MainApplication.isDebug ? "http://upload.dev.putaocloud.com/" : "http://upload.putaocloud.com/";//基础url
    private static final String UPLOAD_APP_ID = "1003";//上传使用的app_id

    public static void install(String base_url) {
//        BASE_URL = base_url;
    }

    /**
     * 校检sha1
     */
    public static final String URL_CHECK_SHA1 = BASE_URL + "fileinfo";

    /**
     * 校检sha1
     *
     * @param sha1 sha1
     */
    public static Request checkSha1(String sha1) {
        return MultiPartRequestBuilder.newInstance()
                .addParam(REQUEST_SHA1, sha1)
                .build(URL_CHECK_SHA1);
    }

    /**
     * 上传文件
     */
    public static final String URL_UPLOAD_FILE = BASE_URL + "upload";

    /**
     * 上传文件
     *
     * @param uploadToken 上传token
     * @param file        上传文件
     * @return
     */
    public static void uploadFile(String uploadToken, String sha1, File file, UploadFileTask.UploadCallback callback) {
        UploadFileTask.newInstance()
                .addParam(REQUEST_APP_ID, UPLOAD_APP_ID)
                .addParam(REQUEST_UPLOAD_TOKEN, uploadToken)
                .addParam(REQUEST_UID, AccountHelper.getCurrentUid())
                .addParam(REQUEST_FILENAME, sha1)
                .addParam(REQUEST_SHA1, sha1)
                .addParam(REQUEST_FILE, file)
                .build(URL_UPLOAD_FILE, callback);
    }
}
