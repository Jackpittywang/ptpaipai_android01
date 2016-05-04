package com.sunnybear.library.model.http.request;

import android.view.Menu;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.sunnybear.library.util.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * form请求构建类
 * Created by guchenkai on 2015/10/27.
 */
public final class FormEncodingRequestBuilder {
    private static final String TAG = FormEncodingRequestBuilder.class.getSimpleName();
    private Map<String, String> headers;//请求头参数
    private Map<String, String> params;//请求参数
    private Map<Menu, String> menus;//请求参数

    private Request.Builder builder;

    public FormEncodingRequestBuilder() {
        headers = new ConcurrentHashMap<>();
        params = new ConcurrentHashMap<>();

        builder = new Request.Builder();
//        builder.addHeader("Cache-Control", String.format("max-age=%d", BasicApplication.getMaxAge()));//缓存数据验证是否有效
//        builder.cacheControl(new CacheControl.Builder().maxAge(BasicApplication.getMaxAge(), TimeUnit.SECONDS).build());
    }

    /**
     * 创建CommonRequestHelper实例
     *
     * @return RequestHelper实例
     */
    public static FormEncodingRequestBuilder newInstance() {
        return new FormEncodingRequestBuilder();
    }

    /**
     * 添加请求头
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public FormEncodingRequestBuilder addHeader(String name, String value) {
        try {
            headers.put(name, value);
        } catch (NullPointerException e) {
            Logger.e(TAG, "设置头参数为空,参数名:" + name + ",参数值:" + value);
        }
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public FormEncodingRequestBuilder addParam(String name, String value) {
        try {
            params.put(name, value);
        } catch (NullPointerException e) {
            Logger.e(TAG, "设置参数为空,参数名:" + name + ",参数值:" + value);
        }
        return this;
    }

    public FormEncodingRequestBuilder addMenuParam(Menu name, String value) {
        try {
            menus.put(name, value);
        } catch (NullPointerException e) {
            Logger.e(TAG, "设置参数为空,参数名:" + name + ",参数值:" + value);
        }
        return this;
    }


    /**
     * 构建Request实例
     *
     * @param method 请求类型
     * @param url
     * @return
     */
    public Request build(int method, String url) {
        Request request = null;
        switch (method) {
            case RequestMethod.GET:
                for (String name : headers.keySet()) {
                    builder.addHeader(name, headers.get(name));
                }
                url = jointUrl(url, params);
                Logger.d(TAG, "get请求,url=" + url);
                /*if (url.contains("?"))
                    url = url.substring(0, url.indexOf("?"));*/
                request = builder.url(url).get().tag(url).build();
                break;
            case RequestMethod.POST:
                for (String name : headers.keySet()) {
                    builder.addHeader(name, headers.get(name));
                }
               /* builder.addHeader("Content-Type", "application/x-www-form-urlencoded");
                builder.addHeader("Connection", "Keep-Alive");
                builder.addHeader("User-Agent", "okhttp/2.7.2");
                builder.addHeader("Accept-Encoding", "gzip");
                builder.addHeader("Host", "10.1.11.31:9084");
                builder.addHeader("Content-Length", "143");*/
                FormEncodingBuilder param = new FormEncodingBuilder();
                for (String name : params.keySet()) {
                    param.add(name, params.get(name));
                }
                Logger.d(TAG, "post请求,url=" + jointUrl(url, params) + "\n" + "请求 参数:" + "\n" + formatParams(params));
                request = builder.url(url).post(param.build()).tag(url).build();
                break;
        }
        return request;
    }


    /**
     * 构建url
     *
     * @return
     */
    public String joinURL(String url) {
        return jointUrl(url, params).toString();
    }
    /**
     * 拼接参数
     *
     * @param url
     * @param params
     * @return
     */
    private String jointUrl(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer(url + "?");
        for (String name : params.keySet()) {
            sb.append(name).append("=").append(params.get(name)).append("&");
        }
        url = sb.delete(sb.length() - 1, sb.length()).toString();
        return url;
    }

    /**
     * 格式化显示参数信息
     *
     * @return 参数信息
     */
    private String formatParams(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        for (String name : params.keySet()) {
            sb.append(name).append("=").append(params.get(name)).append("\n");
        }
        return sb.delete(sb.length() - 1, sb.length()).toString();
    }
}
