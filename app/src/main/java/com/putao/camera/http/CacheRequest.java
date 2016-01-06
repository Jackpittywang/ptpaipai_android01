
package com.putao.camera.http;

import android.widget.ProgressBar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.putao.camera.application.MainApplication;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.Loger;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CacheRequest implements IBaseRequest {
    // private B5MBaseRequest<?> mB5MBaseRequest;
    private String mHostName;
    private String mMethodName;
    protected HashMap<String, String> mRequestParamsMap;
    protected RequestParams mRequestParams;
    protected JSONObject mRequestJsonParams;
    private String urlString;
    private AsyncHttpClient asyncHttpClient;
    private int mTimeout = 30000;
    private ICacheRequestCallBack mCallback;
    private int whatCode = -1;
    public static final String SUCCESS = "succ";
    public static final String MESSAGE = "msg";
    public static final String ERROR = "errorCode";
    public static final String RESULT = "result";
    public static final int OTHER_FLAG = 0, SUCCESS_FLAG = 1, MULTI_LOGIN = 2, DEFAULT_FLAG = Integer.MAX_VALUE;
    public int succCode, errorCode;
    public String msg;
    public boolean catchEnable = false;
    private boolean mForceFeatchEnable = false;

    public CacheRequest(String methodName, ICacheRequestCallBack callback) {
        mMethodName = methodName;
        mCallback = callback;
        init();
        addDataToRequestParamsFirst();
    }

    public CacheRequest(String hostName, String methodName, RequestParams requestParams, ICacheRequestCallBack callback) {
        this(methodName, callback);
        this.mHostName = hostName;
        this.mRequestParams = requestParams;
    }

    public CacheRequest(String hostName, String methodName, HashMap<String, String> requestParams, ICacheRequestCallBack callback) {
        this(methodName, callback);
        this.mHostName = hostName;
        this.mRequestParamsMap = requestParams;
    }

    public CacheRequest(String methodName, HashMap<String, String> requestParams, ICacheRequestCallBack callback) {
        this(methodName, callback);
        this.mRequestParamsMap = requestParams;
    }

    // public CacheRequest(String methodName, JSONObject requestJsonParams,
    // ICacheRequestCallBack callback)
    // {
    // this(methodName, callback);
    // mRequestJsonParams = requestJsonParams;
    // }
    // public CacheRequest(CustomProgressBar progressBar, String methodName,
    // ICacheRequestCallBack callback)
    // {
    // this(methodName, callback);
    // this.progressBar = progressBar;
    // }
    //
    // public CacheRequest(CustomProgressBar progressBar, String methodName,
    // HashMap<String, String> requestParams, ICacheRequestCallBack callback)
    // {
    // this(methodName, requestParams, callback);
    // this.progressBar = progressBar;
    // }
    protected void customRequestParams() {
        // TODO Auto-generated method stub
    }

    private void addDataToRequestParamsFirst() {
        // asyncHttpClient.addHeader("appVersion",
        // MainApplication.getInstance().getString(R.string.version));
        // asyncHttpClient.addHeader("appOs", "android");
        // String userId =
        // B5MPreferenceHelper.readStringValue(MainApplication.getInstance(),
        // Constants.PREFERENCE_USER_USERID);
        // asyncHttpClient.addHeader("appUserId", userId);
        // asyncHttpClient.addHeader("appImei",
        // B5MHeader.getInstance().getDeviceID(MainApplication.getInstance()));
        // asyncHttpClient.addHeader("appChannel",
        // B5MHeader.getInstance().chnl);
        // asyncHttpClient.addHeader("appDevice", B5MHeader.getInstance().dev);
    }

    @Override
    public void start() {
        // if (catchEnable)
        // {
        // String result = DbUtil.queryCatchByUrl(this.getUrlString() +
        // getRequestParams());
        // if (!B5MStringHelper.isEmpty(result))
        // {
        // try
        // {
        // Object json = new JSONTokener(result).nextValue();
        // if (json instanceof JSONObject)
        // {
        // parase(new JSONObject(result));
        // }
        // else if (json instanceof JSONArray)
        // {
        // parserJSONArray(new JSONArray(result));
        // }
        // if (mForceFeatchEnable == true)
        // {
        // startRequest();
        // }
        // return;
        // }
        // catch (JSONException e)
        // {
        // e.printStackTrace();
        // }
        // }
        // }
        startPostRequest();
    }

    private void init() {
        asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(mTimeout);
    }

    @Override
    public void startPostRequest() {
        showProgress();
        try {
            JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    hideProgress();
                    Loger.d("response = " + response);
                    parase(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    hideProgress();
                    if (catchEnable) {
                        String urlKey = getUrlString() + getRequestParams();
                        // if (DbUtil.queryCatchByUrl(urlKey) != null)
                        // {
                        // DbUtil.updateCatch(urlKey,
                        // response.toString());
                        // }
                        // else
                        // {
                        // DbUtil.insertCatch(urlKey,
                        // response.toString());
                        // }
                    }
                    Loger.d("response = " + response);
                    mCallback.onSuccess(whatCode == -1 ? this.hashCode() : whatCode, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    hideProgress();
                    try {
                        parase(new JSONObject(responseString));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Loger.d("response = " + responseString);
                    mCallback.onFail(statusCode, statusCode, responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    hideProgress();
                    if (errorResponse != null) {
                        parase(errorResponse);
                    }
                    Loger.d("response = " + errorResponse);
                    mCallback.onFail(statusCode, statusCode, errorResponse == null ? null : errorResponse.toString());
                }
            };
            if (mRequestParams != null) {
                asyncHttpClient.post(getUrlString(), mRequestParams, jsonHttpResponseHandler);
            } else {
                asyncHttpClient.post(MainApplication.getInstance(), getUrlString(), new StringEntity(getRequestParams()),
                        "application/json; charset=UTF-8", jsonHttpResponseHandler);
            }
            Loger.d("url = " + getUrlString() + "\nrequest = " + getRequestParams());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startGetRequest() {
        try {
            JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    hideProgress();
                    Loger.d("response = " + response);
                    parase(response);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    super.onSuccess(statusCode, headers, response);
                    hideProgress();
                    if (catchEnable) {
                        String urlKey = getUrlString() + getRequestParams();
                    }
                    Loger.d("response = " + response);
                    mCallback.onSuccess(whatCode == -1 ? this.hashCode() : whatCode, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    hideProgress();
                    try {
                        parase(new JSONObject(responseString));
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    Loger.d("statusCode=" + statusCode + "response = " + responseString);
                    mCallback.onFail(statusCode, statusCode, responseString);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    hideProgress();
                    if (errorResponse != null) {
                        parase(errorResponse);
                    }
                    Loger.d("response = " + errorResponse);
                    mCallback.onFail(statusCode, statusCode, errorResponse == null ? null : errorResponse.toString());
                }
            };
            Loger.d("url = " + getUrlString() + "\nrequest = " + getRequestParams());
            asyncHttpClient.get(MainApplication.getInstance(), getUrlString(), jsonHttpResponseHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getWhatCode() {
        return whatCode;
    }

    public void setWhatCode(int whatCode) {
        this.whatCode = whatCode;
    }

    public void parase(JSONObject json) {
        try {
            parserHead(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void parserJSONArray(JSONArray jsonArray) throws JSONException {
        mCallback.onSuccess(whatCode == -1 ? this.hashCode() : whatCode, jsonArray);
    }

    public void parserHead(JSONObject json) throws JSONException {
        if (json != null) {
            if (json.has(SUCCESS)) {
                this.succCode = json.optInt(SUCCESS, DEFAULT_FLAG);
                this.errorCode = json.optInt(ERROR, DEFAULT_FLAG);
                this.msg = json.optString(MESSAGE, "");
                if (succCode == SUCCESS_FLAG || json.optBoolean(SUCCESS)) {
                    mCallback.onSuccess(whatCode == -1 ? this.hashCode() : whatCode, json.has(RESULT) ? json.optJSONObject(RESULT) : json);
                }
            } else {
                if (!json.has(ERROR)) {
                    mCallback.onSuccess(whatCode == -1 ? this.hashCode() : whatCode, json.has(RESULT) ? json.optJSONObject(RESULT) : json);
                }
            }
        }
    }

    public String getUrlString() {
        return (null == urlString) ? (getHost() + getMethod()) : this.urlString;
    }

    private String getRequestParams() {
        if (mRequestJsonParams != null) {
            return mRequestJsonParams.toString();
        } else if (mRequestParamsMap != null && !mRequestParamsMap.isEmpty()) {
            customRequestParams();
            JSONObject jsonObject = new JSONObject(mRequestParamsMap);
            return jsonObject.toString();
        }
        return "";
    }

    @Override
    public String getHost() {
        // return "http://7u2jvu.com1.z0.glb.clouddn.com/";
        if (mHostName == null) {
            return PuTaoConstants.PAIPAI_SERVER_HOST;
        } else {
            return mHostName;
        }
    }

    @Override
    public String getMethod() {
        return mMethodName;
    }

    public void setCatchEnable(boolean catchEnable) {
        this.catchEnable = catchEnable;
    }

    /**
     * 是否需要缓存
     */
    public boolean needCacheEnable() {
        return this.catchEnable;
    }

    /**
     * 在缓存模式是否需要请求
     */
    public void setForceFeatchEnable(boolean forceFeatchEnable) {
        this.mForceFeatchEnable = forceFeatchEnable;
    }

    public static class ICacheRequestCallBack {
        public void onSuccess(int whatCode, JSONObject json) {
        }

        public void onSuccess(int whatCode, JSONArray jsonArray) {
        }

        public void onFail(int whatCode, int statusCode, String responseString) {
        }
    }

    ;
    protected ProgressBar progressBar;

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private boolean isShow = true;

    public void setShowProgress(boolean isShow) {
        this.isShow = isShow;
    }

    public void showProgress() {
        if (isShow) {
            // ViewHelp.setContentShown(progressBar, true);
        }
    }

    public void hideProgress() {
        if (isShow) {
            // ViewHelp.setContentShown(progressBar, false);
        }
    }

    public void cancelsRequests() {
        if (asyncHttpClient != null) {
            asyncHttpClient.cancelAllRequests(true);
        }
    }
}
