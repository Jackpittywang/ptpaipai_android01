package com.sunnybear.library.model.http.interceptor;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sunnybear.library.util.Logger;

import java.io.IOException;

/**
 * reponse信息拦截器
 * Created by guchenkai on 2016/1/22.
 */
public class ResponseInfoInterceptor implements Interceptor {
    private static final String TAG = ResponseInfoInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.urlString();
        Response response = chain.proceed(request);
        Headers responseHeaders = response.headers();
        if (null != chain.connection()) {
            Logger.i(TAG + "-" + url, "------request信息------\n"
                    + chain.connection().toString().replace("{", ":").replace("}", "") + "\n"
                    + request.headers().toString());
        }
        Logger.i(TAG + "-" + url, "------response头信息------\n"
                + responseHeaders.toString()
                + "请求用时:"
                + (Long.parseLong(responseHeaders.get("OkHttp-Received-Millis"))
                - Long.parseLong(responseHeaders.get("OkHttp-Sent-Millis"))) + "ms");
        return response;
    }
}
