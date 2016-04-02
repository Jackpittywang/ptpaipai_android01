package com.sunnybear.library.model.http.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunnybear.library.util.JsonUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * FastJson解析json
 * Created by guchenkai on 2015/10/27.
 */
abstract class FastJsonCallback<T extends Serializable> extends JSONObjectCallback {
    private Class<? extends Serializable> clazz;

    public FastJsonCallback(Class<? extends Serializable> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public final void onSuccess(String url, JSONObject result) {
        String data = result.getString("data");
        if (!StringUtils.isEmpty(data) && String.class.equals(clazz)) {
            onSuccess(url, (T) data);
            return;
        } else if (StringUtils.isEmpty(data) && StringUtils.equals(result.getString("http_code"), "200")) {
//            onSuccess(url, (T) new String(""));
            onSuccess(url, (T) null);
            return;
        } else if (StringUtils.isEmpty(data) && !StringUtils.equals(result.getString("http_code"), "200")) {
            onFinish(url, false, result.getString("msg") != null ? result.getString("msg") : "");
            return;
        }
        if (StringUtils.equals(data, "[]") && !StringUtils.equals(getGenericClassName(), ArrayList.class.getName()))
            data = null;
        JsonUtils.JsonType type = JsonUtils.getJSONType(data);
        switch (type) {
            case JSON_TYPE_OBJECT:
                onSuccess(url, (T) JSON.parseObject(data, clazz));
                break;
            case JSON_TYPE_ARRAY:
                onSuccess(url, (T) JSON.parseArray(data, clazz));
                break;
            case JSON_TYPE_ERROR:
                onFailure(url, -200, "data数据返回错误");
                Logger.e(JSONObjectCallback.TAG, "result=" + result.toJSONString());
                break;
        }
    }

    @Override
    public final void onCacheSuccess(String url, JSONObject result) {
        String data = result.getString("data");
        if (!StringUtils.isEmpty(data) && String.class.equals(clazz)) {
            onCacheSuccess(url, (T) data);
            return;
        } else if (StringUtils.isEmpty(data) && StringUtils.equals(result.getString("http_code"), "200")) {
            onCacheSuccess(url, (T) new String(""));
            return;
        } else if (StringUtils.isEmpty(data) && !StringUtils.equals(result.getString("http_code"), "200")) {
            onFinish(url, false, result.getString("msg") != null ? result.getString("msg") : "");
            return;
        }
        if (StringUtils.equals(data, "[]") && !StringUtils.equals(getGenericClassName(), ArrayList.class.getName()))
            data = null;
        JsonUtils.JsonType type = JsonUtils.getJSONType(data);
        switch (type) {
            case JSON_TYPE_OBJECT:
                onCacheSuccess(url, (T) JSON.parseObject(data, clazz));
                break;
            case JSON_TYPE_ARRAY:
                onCacheSuccess(url, (T) JSON.parseArray(data, clazz));
                break;
            case JSON_TYPE_ERROR:
                onFailure(url, -200, "data数据返回错误");
                Logger.e(JSONObjectCallback.TAG, "result=" + result.toJSONString());
                break;
        }
    }

    /**
     * 获取本类的泛型类型
     *
     * @return 泛型类型
     */
    private String getGenericClassName() {
        Type genType = this.getClass().getGenericSuperclass();
        Type generic = ((ParameterizedType) genType).getActualTypeArguments()[0];
        if (!(generic instanceof Class))
            try {
                Field mRawTypeName = generic.getClass().getDeclaredField("rawTypeName");
                mRawTypeName.setAccessible(true);
                return (String) mRawTypeName.get(generic);
            } catch (Exception e) {
                Logger.e("获取泛型类型错误.", e);
            }
        return "";
    }

    /**
     * 网络请求成功回调
     *
     * @param url    网络地址
     * @param result 请求结果
     */
    public abstract void onSuccess(String url, T result);

    /**
     * 缓存请求成功回调
     *
     * @param url    网络地址
     * @param result 请求结果
     */
    public abstract void onCacheSuccess(String url, T result);
}
