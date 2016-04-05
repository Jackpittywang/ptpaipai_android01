package com.putao.camera.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.putao.account.AccountApi;
import com.putao.account.AccountCallback;
import com.putao.account.AccountConstants;
import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.PTXJActivity;
import com.putao.camera.util.NetManager;
import com.putao.jpush.JPushHeaper;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.CleanableEditText;
import com.sunnybear.library.view.image.ImageDraweeView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 登录模块
 */
public class LoginActivity extends PTXJActivity implements View.OnClickListener, TextWatcher {
    public static final String EVENT_LOGIN = "login";
    public static final String EVENT_CANCEL_LOGIN = "cancel_login";

    public static final String TERMINAL_ACTIVITY = "terminal";
    public static final String NEED_CODE = "need_code";

    @Bind(R.id.et_mobile)
    CleanableEditText et_mobile;
    @Bind(R.id.et_password)
    CleanableEditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.rl_graph_verify)
    RelativeLayout rl_graph_verify;//图形验证码
    @Bind(R.id.et_graph_verify)
    CleanableEditText et_graph_verify;
    @Bind(R.id.image_graph_verify)
    ImageDraweeView image_graph_verify;

    private int mErrorCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        addNavigation();
        et_mobile.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        btn_login.setClickable(false);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @OnClick({R.id.btn_login, R.id.tv_register, R.id.tv_forget, R.id.image_graph_verify})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login://登录
                loading.show();
                btn_login.setClickable(false);
                final String mobile = et_mobile.getText().toString();
                final String passWord = et_password.getText().toString();
                final String verify = et_graph_verify.getText().toString();
                if (NetManager.isNetworkAvailable(LoginActivity.this) == true) {//没有网络连接
                    ToastUtils.showToastLong(mContext, "您的网络不给力");
                    btn_login.setClickable(true);
                    loading.dismiss();
                } else {
                    if (!TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(NEED_CODE + mobile)) && rl_graph_verify.getVisibility() == View.GONE) {
                        rl_graph_verify.setVisibility(View.VISIBLE);
                        AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_LOGIN);
                        btn_login.setClickable(true);
                        loading.dismiss();
                    } else
                        networkRequest(AccountApi.safeLogin(mobile, passWord, verify),
                                new AccountCallback(loading) {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        AccountHelper.setCurrentUid(result.getString("uid"));
                                        AccountHelper.setCurrentToken(result.getString("token"));
                                        new JPushHeaper().setAlias(mContext, result.getString("uid"));
                                        mContext.sendBroadcast(new Intent(MainApplication.Not_Fore_Message));
                                        EventBusHelper.post(EVENT_LOGIN, EVENT_LOGIN);
//                                        startActivity((Class) args.getSerializable(TERMINAL_ACTIVITY), args);
                                        startActivity(CompleteActivity.class);
//                                        startActivity(PerfectActivity.class);
                                        if (!TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(NEED_CODE + mobile))) {
                                            mDiskFileCacheHelper.remove(NEED_CODE + mobile);
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onError(String error_msg) {
                                        ToastUtils.showToastShort(mContext, error_msg);
                                        mErrorCount++;
                                        if (mErrorCount == 3) {
                                            rl_graph_verify.setVisibility(View.VISIBLE);
                                            AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_LOGIN);
                                            mDiskFileCacheHelper.put(NEED_CODE + mobile, NEED_CODE);
                                        }
                                        AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_LOGIN);
                                        et_graph_verify.setText("");
                                    }

                                    @Override
                                    public void onFinish(String url, boolean isSuccess, String msg) {
                                        super.onFinish(url, isSuccess, msg);
                                        btn_login.setClickable(true);
                                    }
                                });
                }
                break;
            case R.id.tv_register://注册新用户
                startActivity(RegisterActivity.class);
                break;
            case R.id.tv_forget://忘记密码
                startActivity(ForgetPasswordActivity.class);
                break;
            case R.id.image_graph_verify:
                AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_LOGIN);
                break;
        }
    }

    /**
     * 验证登录
     *//*
    private void checkLogin() {
        EventBusHelper.post(EVENT_LOGIN, EVENT_LOGIN);
        networkRequest(UserApi.getUserInfo(),
                new SimpleFastJsonCallback<UserInfo>(UserInfo.class, loading) {
                    @Override
                    public void onSuccess(String url, UserInfo result) {
                        AccountHelper.setUserInfo(result);
                        startActivity((Class) args.getSerializable(TERMINAL_ACTIVITY), args);
                        loading.dismiss();
                        finish();
                    }

                    @Override
                    public void onFailure(String url, int statusCode, String msg) {
                        super.onFailure(url, statusCode, msg);
//                        ToastUtils.showToastLong(mContext, "登录失败请重新登录");
                    }

                    @Override
                    public void onFinish(String url, boolean isSuccess, String msg) {
                        super.onFinish(url, isSuccess, msg);
                        btn_login.setClickable(true);
                    }
                });
    }*/
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (et_mobile.length() == 11 && et_password.length() >= 6) {
            btn_login.setClickable(true);
            btn_login.setBackgroundResource(R.drawable.btn_get_focus);
        } else {
            btn_login.setClickable(false);
            btn_login.setBackgroundResource(R.drawable.btn_los_focus);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onLeftAction() {
        EventBusHelper.post(EVENT_CANCEL_LOGIN, EVENT_CANCEL_LOGIN);
        super.onLeftAction();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            EventBusHelper.post(EVENT_CANCEL_LOGIN, EVENT_CANCEL_LOGIN);
        return super.dispatchKeyEvent(event);
    }


}
