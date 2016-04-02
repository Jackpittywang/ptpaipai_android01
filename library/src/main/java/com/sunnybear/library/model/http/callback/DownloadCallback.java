package com.sunnybear.library.model.http.callback;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.R;
import com.sunnybear.library.util.FileUtils;
import com.sunnybear.library.util.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 下载回调
 * Created by guchenkai on 2016/1/25.
 */
public abstract class DownloadCallback implements Callback {
    private static final String TAG = DownloadCallback.class.getSimpleName();

    private static final int RESULT_SUCCESS = 0x01;//成功
    private static final int RESULT_FAILURE = 0x02;//失败

    private static final String KEY_URL = "url";
    private static final String KEY_STATUS_CODE = "statusCode";
    private static final String KEY_FAILURE_MSG = "errorMsg";
    private static final String KEY_SAVE_FILE = "save_file";

    private String mFilePath;//文件保存路径

    private Handler mHandler;//主线程回调

    public DownloadCallback() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RESULT_SUCCESS://成功
                        Bundle success = (Bundle) msg.obj;
                        String url_success = success.getString(KEY_URL);
                        File file = (File) success.getSerializable(KEY_SAVE_FILE);
                        onDownloadSuccess(url_success, file);
                        //请求完成
                        onFinish(url_success, true, "");
                        break;
                    case RESULT_FAILURE://失败
                        Bundle failure = (Bundle) msg.obj;
                        String url_failure = failure.getString(KEY_URL);
                        int statusCode = failure.getInt(KEY_STATUS_CODE);
                        String failure_msg = failure.getString(KEY_FAILURE_MSG);
                        onDownloadFailure(url_failure, statusCode, failure_msg);
                        //请求完成
                        onFinish(url_failure, false, failure_msg);
                        break;
                }
            }
        };
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    @Override
    public final void onResponse(Response response) throws IOException {
        if (response == null) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, "");
            bundle.putInt(KEY_STATUS_CODE, 404);
            bundle.putString(KEY_FAILURE_MSG, BasicApplication.getInstance().getResources().getString(R.string.not_network));
            mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
        }
        String url = response.request().urlString();
        int statusCode = response.code();
        Logger.d(TAG, "url=" + url + ",状态码=" + statusCode);
        if (response.isSuccessful()) {
            InputStream is = response.body().byteStream();
            if (is != null) {
                File file = FileUtils.saveFile(is, mFilePath);
                Bundle bundle = new Bundle();
                bundle.putString(KEY_URL, url);
                bundle.putSerializable(KEY_SAVE_FILE, file);
                mHandler.sendMessage(Message.obtain(mHandler, RESULT_SUCCESS, bundle));
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_URL, url);
            bundle.putInt(KEY_STATUS_CODE, statusCode);
            bundle.putString(KEY_FAILURE_MSG, "");
            mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
        }
    }

    @Override
    public final void onFailure(Request request, IOException e) {
        String url = request.urlString();
        Logger.e(TAG, "下载url=" + url + "\n", e);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_URL, url);
        bundle.putInt(KEY_STATUS_CODE, 500);
        if (e instanceof SocketTimeoutException || e instanceof UnknownHostException)
//            bundle.putString(KEY_FAILURE_MSG, "请检查网络后重新尝试");
            bundle.putString(KEY_FAILURE_MSG, "");
        else
            bundle.putString(KEY_FAILURE_MSG, e.getMessage());
        mHandler.sendMessage(Message.obtain(mHandler, RESULT_FAILURE, bundle));
    }

    /**
     * 下载成功回调
     *
     * @param url  下载地址
     * @param file 下载文件
     */
    public abstract void onDownloadSuccess(String url, File file);

    /**
     * 下载失败回调
     *
     * @param url        网络地址
     * @param statusCode 状态码
     * @param msg        失败错误信息
     */
    public void onDownloadFailure(String url, int statusCode, String msg) {

    }


    /**
     * 开始请求
     */
    public void onStart() {

    }

    /**
     * 完成请求
     *
     * @param url       url
     * @param isSuccess 请求是否成功
     * @param msg       请求完成的消息
     */
    public void onFinish(String url, boolean isSuccess, String msg) {

    }
}
