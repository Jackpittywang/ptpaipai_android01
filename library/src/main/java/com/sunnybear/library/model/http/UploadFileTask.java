package com.sunnybear.library.model.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cz.msebera.android.httpclient.Consts;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.entity.mime.content.StringBody;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * 上传任务
 * Created by guchenkai on 2015/12/15.
 */
public class UploadFileTask {
    private Map<String, String> headers;//请求头参数
    private Map<String, Object> params;//请求参数

    private MultipartEntityBuilder builder;

    public UploadFileTask() {
        headers = new ConcurrentHashMap<>();
        params = new ConcurrentHashMap<>();

//        entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    }

    /**
     * 创建FileRequestHelper实例
     *
     * @return
     */
    public static UploadFileTask newInstance() {
        return new UploadFileTask();
    }

    /**
     * 添加请求头
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public UploadFileTask addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    /**
     * 添加请求参数
     *
     * @param name  name
     * @param value value
     * @return RequestHelper实例
     */
    public UploadFileTask addParam(String name, Object value) {
        params.put(name, value);
        return this;
    }

    public void build(String url, UploadCallback callback) {
        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof String)
                    builder.addPart(name, new StringBody((String) value, Consts.UTF_8));
                else if (value instanceof File)
                    builder.addPart(name, new FileBody((File) value, ContentType.create("image/*"), ((File) value).getName()));
            }

            HttpPost post = new HttpPost(url);
            builder.setCharset(Consts.UTF_8);
            post.setEntity(builder.build());

            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String responseBody = EntityUtils.toString(response.getEntity());
                if (callback != null)
                    callback.onSuccess(JSON.parseObject(responseBody));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public interface UploadCallback {

        void onSuccess(JSONObject result);
    }
}
