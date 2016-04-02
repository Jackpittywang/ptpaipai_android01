package com.sunnybear.library.model.http.interceptor;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;
import com.sunnybear.library.model.http.progress.ProgressResponseBody;
import com.sunnybear.library.model.http.progress.ProgressResponseListener;

import java.io.IOException;

/**
 * 进度拦截器
 * Created by guchenkai on 2015/10/26.
 */
public class ProgressInterceptor implements Interceptor {
    private ProgressResponseListener progressListener;

    public ProgressInterceptor(ProgressResponseListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), progressListener)).build();
    }
}
