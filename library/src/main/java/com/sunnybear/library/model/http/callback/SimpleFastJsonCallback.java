package com.sunnybear.library.model.http.callback;

import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.LoadingHUD;

import java.io.Serializable;

/**
 * 简单封装FastJsonCallback
 * Created by guchenkai on 2015/11/17.
 */
public abstract class SimpleFastJsonCallback<T extends Serializable> extends FastJsonCallback<T> {
    private LoadingHUD loading;

    public SimpleFastJsonCallback(Class<? extends Serializable> clazz, LoadingHUD loading) {
        super(clazz);
        this.loading = loading;
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onCacheSuccess(String url, T result) {

    }

    @Override
    public void onFailure(String url, int statusCode, String msg) {
        Logger.e("请求错误:url=" + url + ",statusCode=" + statusCode + ",错误信息=" + msg);
        if (!StringUtils.isEmpty(msg) && statusCode != -200)
            ToastUtils.showToastLong(BasicApplication.getInstance(), msg);
        if (loading != null) loading.dismiss();
    }

    @Override
    public void onFinish(String url, boolean isSuccess, String msg) {
        if (!isSuccess)
            Logger.w("服务器消息:" + msg);
        if (loading != null) loading.dismiss();
    }
}
