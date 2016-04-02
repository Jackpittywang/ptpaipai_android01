package com.sunnybear.library.view.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.sunnybear.library.BasicApplication;

import java.io.IOException;

/**
 * bitmap加载器
 * Created by guchenkai on 2015/11/5.
 */
public class BitmapLoader {
    private OkHttpClient mOkHttpClient;
    private Activity mActivity;

    public static BitmapLoader newInstance(Activity activity) {
        return new BitmapLoader(activity);
    }

    public BitmapLoader(Activity activity) {
        mOkHttpClient = BasicApplication.getOkHttpClient();
        mActivity = activity;
    }

    /**
     * 异步加载图片
     *
     * @param url url
     */
    public void load(String url, final BitmapCallback callback) {
        final Request request = new Request.Builder().url(url).get().build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Response response) throws IOException {
                byte[] data = response.body().bytes();
                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (callback != null)
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            callback.onResult(bitmap);
                        }
                    });
            }

            @Override
            public void onFailure(Request request, IOException e) {
                if (callback != null)
                    callback.onResult(null);
            }
        });
    }

    /**
     *
     */
    public interface BitmapCallback {

        void onResult(Bitmap bitmap);
    }
}
