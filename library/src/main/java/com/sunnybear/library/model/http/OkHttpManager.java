package com.sunnybear.library.model.http;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

/**
 * OkHttp管理
 * Created by guchenkai on 2015/10/26.
 */
public final class OkHttpManager {
    private static final int CONNECT_TIMEOUT_MILLIS = 10 * 1000;//连接时间超时
    private static final int WRITE_TIMEOUT_MILLIS = 10 * 1000;//写入时间超时
    private static final int READ_TIMEOUT_MILLIS = 10 * 1000;//读取时间超时

    private static OkHttpManager instance;
    private static List<Interceptor> mInterceptors;

    private int mCacheSize;
    private String mCacheDirectoryPath;

    public OkHttpManager(String cacheDirectoryPath, int cacheSize) {
        mCacheDirectoryPath = cacheDirectoryPath;
        mCacheSize = cacheSize;
        mInterceptors = new LinkedList<>();
    }

    /**
     * 单例实例
     *
     * @return OkHttpHelper实例
     */
    public static OkHttpManager getInstance(String cacheDirectoryPath, int cacheSize) {
        if (instance == null)
            instance = new OkHttpManager(cacheDirectoryPath, cacheSize);
        return instance;
    }

    /**
     * 构建OkHttpClient
     *
     * @return OkHttpClient
     */
    public OkHttpClient build() {
        return generateOkHttpClient(mInterceptors);
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     * @return OkHttpHelper
     */
    public OkHttpManager addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    /**
     * 获得OkHttp客户端
     *
     * @return OkHttp客户端
     */
    public OkHttpClient generateOkHttpClient(List<Interceptor> interceptors) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setWriteTimeout(WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        okHttpClient.setReadTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        if (interceptors != null && interceptors.size() > 0)
            okHttpClient.networkInterceptors().addAll(interceptors);//拦截器
        try {
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{new SSLTrustManager()}, new SecureRandom());
            okHttpClient.setSslSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
//        setCache(okHttpClient);
        return okHttpClient;
    }

    /**
     * 获得缓存器
     *
     * @param okHttpClient OkHttpClient
     */
    private void setCache(OkHttpClient okHttpClient) {
        File cacheDirectory = new File(mCacheDirectoryPath);
        if (!cacheDirectory.exists())
            cacheDirectory.mkdirs();
        okHttpClient.setCache(new Cache(cacheDirectory, mCacheSize));
    }
}
