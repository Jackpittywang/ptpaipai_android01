package com.sunnybear.library.model.http.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.R;
import com.sunnybear.library.util.JsonUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ResourcesUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 回调转换成json
 * Created by guchenkai on 2015/10/27.
 */
public abstract class JSONObjectCallback extends RequestCallback {
    public static final String TAG = JSONObjectCallback.class.getSimpleName();

    private static final int RESULT_NETWORK_SUCCESS = 0x01;//网络回调成功
    private static final int RESULT_CACHE_SUCCESS = 0x02;//缓存回调成功
    private static final int RESULT_FAILURE = 0x03;//失败

    private static final String KEY_URL = "url";
    private static final String KEY_JSON = "json";
    private static final String KEY_STATUS_CODE = "statusCode";
    private static final String KEY_FAILURE_MSG = "errorMsg";

    private Handler mHandler;//主线程回调

    public JSONObjectCallback() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RESULT_NETWORK_SUCCESS://网络回调成功
                        Bundle success = (Bundle) msg.obj;
                        String url_success = success.getString(KEY_URL);
                        String json = success.getString(KEY_JSON);
                        try {
                            onSuccess(url_success, JSON.parseObject(json));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //请求完成
                        onFinish(url_success, true, "");
                        break;
                    case RESULT_CACHE_SUCCESS://缓存回调成功
                        Bundle cache_success = (Bundle) msg.obj;
                        String cache_url_success = cache_success.getString(KEY_URL);
                        String cache_json = cache_success.getString(KEY_JSON);
                        onCacheSuccess(cache_url_success, JSON.parseObject(cache_json));
                        //请求完成
                        onFinish(cache_url_success, true, "");
                        break;
                    case RESULT_FAILURE://失败
                        Bundle failure = (Bundle) msg.obj;
                        String url_failure = failure.getString(KEY_URL);
                        int statusCode = failure.getInt(KEY_STATUS_CODE);
                        String failure_msg = failure.getString(KEY_FAILURE_MSG);
                        onFailure(url_failure, statusCode, failure_msg);
                        //请求完成
                        onFinish(url_failure, false, failure_msg);
                        break;
                }
            }
        };
    }

    /**
     * 网络请求成功回调
     *
     * @param response 响应
     * @throws IOException
     */
    @Override
    public final void onResponse(Response response) throws IOException {
        processResponse(response, true);
    }

    /**
     * 缓存请求成功回调
     *
     * @param response 响应
     * @throws IOException
     */
    @Override
    public final void onCacheResponse(Response response) throws IOException {
        processResponse(response, false);
    }

    /**
     * 处理响应
     *
     * @param response  响应
     * @param isNetwork 是否是网络请求
     */
    private void processResponse(Response response, boolean isNetwork) throws IOException {
        if (response == null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, "");
            bundle.putInt(KEY_STATUS_CODE, 404);
            bundle.putString(KEY_FAILURE_MSG,
                    isNetwork ? ResourcesUtils.getString(BasicApplication.getInstance(), R.string.not_network)
                            : ResourcesUtils.getString(BasicApplication.getInstance(), R.string.fail_request));
            mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
            return;
        }
        String url = response.request().urlString();
        String json = response.body().string();
        int statusCode = response.code();
        Logger.d(TAG, "url=" + url + ",状态码=" + statusCode);
        if (response.isSuccessful()) {
           /* if (isNetwork)
                Logger.d(TAG, "网络请求url:" + url + "\n" + "网络请求成功,请求结果=" + JsonUtils.jsonFormatter(json));
            else
                Logger.d(TAG, "缓存请求url:" + url + "\n" + "缓存请求成功,请求结果=" + JsonUtils.jsonFormatter(json));*/
            if (!TextUtils.isEmpty(json)) {
                Bundle bundle = new Bundle();
                bundle.putString(KEY_URL, url);
                bundle.putString(KEY_JSON, json);
                mHandler.sendMessage(Message.obtain(mHandler, isNetwork ? RESULT_NETWORK_SUCCESS : RESULT_CACHE_SUCCESS, bundle));
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, url);
            bundle.putInt(KEY_STATUS_CODE, statusCode);
            bundle.putString(KEY_FAILURE_MSG, response.message());
            mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
        }
    }

    /**
     * 请求失败回调
     *
     * @param request request
     * @param e       异常
     */
    @Override
    public final void onFailure(Request request, IOException e) {
        String url = request.urlString();
        Logger.e(TAG, "url=" + url + "\n", e);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putInt(KEY_STATUS_CODE, 500);
        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException)
            bundle.putString(KEY_FAILURE_MSG, "请检查网络后重新尝试");
        else
            bundle.putString(KEY_FAILURE_MSG, e.getMessage());
        mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
    }

    /**
     * 网络请求成功回调
     *
     * @param url    网络地址
     * @param result 请求结果
     */
    public abstract void onSuccess(String url, JSONObject result);

    /**
     * 缓存请求成功回调
     *
     * @param url    网络地址
     * @param result 请求结果
     */
    public abstract void onCacheSuccess(String url, JSONObject result);

    /**
     * 请求失败回调
     *
     * @param url        网络地址
     * @param statusCode 状态码
     * @param msg        失败错误信息
     */
    public abstract void onFailure(String url, int statusCode, String msg);
}
