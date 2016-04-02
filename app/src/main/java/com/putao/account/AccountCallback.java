package com.putao.account;

import com.alibaba.fastjson.JSONObject;
import com.sunnybear.library.model.http.callback.JSONObjectCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.view.LoadingHUD;

/**
 * 通信证回调
 * Created by guchenkai on 2015/11/25.
 */
public abstract class AccountCallback extends JSONObjectCallback {
    private LoadingHUD loading;

    public AccountCallback(LoadingHUD loading) {
        this.loading = loading;
    }

    /**
     * 成功回调
     *
     * @param result 返回结果
     */
    public abstract void onSuccess(JSONObject result);

    @Override
    public void onCacheSuccess(String url, JSONObject result) {

    }

    /**
     * 错误信息回调
     *
     * @param error_msg 错误信息
     */
    public abstract void onError(String error_msg);

    @Override
    public final void onSuccess(String url, JSONObject result) {
        loading.dismiss();
        String error_code = result.getString("error_code");
        if ("0".equals(error_code))
            onSuccess(result);
        else
            onError(result.getString("msg"));
    }

    @Override
    public void onFailure(String url, int statusCode, String msg) {
        Logger.e("请求错误:url=" + url + ",statusCode=" + statusCode + ",错误信息=" + msg);
        loading.dismiss();
    }
}
