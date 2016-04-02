package com.sunnybear.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunnybear.library.util.Logger;

import java.net.URLDecoder;

/**
 * 基础WebView
 * Created by guchenkai on 2015/11/30.
 */
public class BasicWebView extends WebView {
    private boolean isLoaderFinish = false;
    private OnWebViewLoadUrlCallback mOnWebViewLoadUrlCallback;

    public void setOnWebViewLoadUrlCallback(OnWebViewLoadUrlCallback onWebViewLoadUrlCallback) {
        mOnWebViewLoadUrlCallback = onWebViewLoadUrlCallback;
    }

    public BasicWebView(Context context) {
        this(context, null, 0);
    }

    public BasicWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化WebView
     */
    private void initView() {
        setWebSettings();
        setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);//滚动条风格，为0指滚动条不占用空间，直接覆盖在网页上

        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String protocolHeader = getProtocolHeader(url);
                switch (protocolHeader) {
                    case ProtocolHeader.PROTOCOL_HEADER_HTTP:
                        view.loadUrl(url);
                        break;
                    case ProtocolHeader.PROTOCOL_HEADER_HTTPS:
                        view.loadUrl(url);
                        break;
                    case ProtocolHeader.PROTOCOL_HEADER_PUTAO:
                        String scheme = getScheme(url);
                        Logger.d(scheme);
                        String content = getContentUrl(url);
                        if (mOnWebViewLoadUrlCallback != null)
                            mOnWebViewLoadUrlCallback.onParsePutaoUrl(scheme, JSON.parseObject(content));
                        break;
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mOnWebViewLoadUrlCallback != null && isLoaderFinish)
                    mOnWebViewLoadUrlCallback.onWebPageLoaderFinish(url);
                isLoaderFinish = true;
            }
        });
    }

    /**
     * 设置WebSettings
     */
    private void setWebSettings() {
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);//开启对JavaScript的支持
        settings.setDefaultTextEncodingName("UTF-8");//设置字符编码
    }

    /**
     * 获得协议头
     *
     * @param url url
     * @return 协议头
     */
    private String getProtocolHeader(String url) {
        return url.substring(0, url.indexOf(":"));
    }


    /**
     * 获得Scheme
     *
     * @param url url
     * @return Scheme
     */
    private String getScheme(String url) {
        return url.substring(url.indexOf(":") + 3, url.indexOf("{") - 1);
    }

    /**
     * 获得真实url内容
     *
     * @param url url
     * @return 真实url内容
     */
    public String getContentUrl(String url) {
        return URLDecoder.decode(url.substring(url.indexOf("{"), url.length()));
    }

    /**
     * WebView加载URL回调
     */
    public interface OnWebViewLoadUrlCallback {

        void onParsePutaoUrl(String scheme, JSONObject result);

        void onWebPageLoaderFinish(String url);
    }

    /**
     * 协议头定义
     */
    public static final class ProtocolHeader {
        public static final String PROTOCOL_HEADER_HTTP = "http";
        public static final String PROTOCOL_HEADER_HTTPS = "https";
        public static final String PROTOCOL_HEADER_PUTAO = "putao";
    }
}
