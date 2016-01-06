package com.putao.camera.movie.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.putao.camera.application.MainApplication;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.http.CacheRequest;

public class MovieCaption {

    static MovieCaptionConfig movieCaptionConfig;

    public static MovieCaption newInstance() {
        MovieCaption caption = new MovieCaption();
        if (movieCaptionConfig == null) {
            queryCaptionList();
        }
        return caption;
    }

    public static String readAssertResource(Context context, String strAssertFileName) {
        AssetManager assetManager = context.getAssets();
        String strResponse = "";
        try {
            InputStream ims = assetManager.open(strAssertFileName);
            strResponse = getStringFromInputStream(ims);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strResponse;
    }

    private static String getStringFromInputStream(InputStream a_is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            br = new BufferedReader(new InputStreamReader(a_is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
        }
        return sb.toString();
    }

    static void queryCaptionList() {
        CacheRequest.ICacheRequestCallBack translateRequest = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                setCaptionList(json + "");
            }

            private void setCaptionList(String string) {
                Gson gson = new Gson();
                movieCaptionConfig = gson.fromJson(string, MovieCaptionConfig.class);

            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                setCaptionList(readAssertResource(MainApplication.getInstance().getApplicationContext(), "movelines.json"));
            }
        };

        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest("", map, translateRequest) {
            @Override
            public String getUrlString() {
                return PuTaoConstants.MOVIE_DEFAULT_CAPTION_URL;
            }
        };
        mCacheRequest.startGetRequest();
    }

    public MovieCaptionConfig getMovieCaptionConfig() {
        return movieCaptionConfig;
    }

    /**
     * 随机取num个元素
     *
     * @param num
     * @return
     */
    public MovieCaptionConfig getMovieCaptionConfig(int num) {
        if (movieCaptionConfig == null)
            return null;

        MovieCaptionConfig tmp = new MovieCaptionConfig();
        tmp.version = movieCaptionConfig.version;
        tmp.movieLines = new ArrayList<MovieCaptionConfig.MovieCaptionItem>();
        for (int i = 0; i < num; i++) {
            Random r = new Random();
            int index = r.nextInt(movieCaptionConfig.movieLines.size());
            tmp.movieLines.add(movieCaptionConfig.movieLines.get(index));
            movieCaptionConfig.movieLines.remove(index);
        }

        return tmp;
    }

}
