package com.putao.camera.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.putao.account.AccountApi;
import com.putao.account.AccountCallback;
import com.putao.account.AccountConstants;
import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.PTXJActivity;
import com.putao.camera.bean.UserInfo;
import com.putao.camera.constants.UserApi;
import com.sunnybear.library.controller.eventbus.EventBusHelper;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.util.StringUtils;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.CleanableEditText;
import com.sunnybear.library.view.SwitchButton;
import com.sunnybear.library.view.TimeButton;
import com.sunnybear.library.view.image.ImageDraweeView;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends PTXJActivity implements View.OnClickListener, TextWatcher, SwitchButton.OnSwitchClickListener {

    public static final String FORGET_CODE = "forget_code";
    @Bind(R.id.btn_nextstep)
    Button btn_nextstep;//下一步
    @Bind(R.id.et_mobile)
    CleanableEditText et_mobile;//手机号
    @Bind(R.id.et_sms_verify)
    CleanableEditText et_sms_verify;//获取验证码
    @Bind(R.id.et_password)
    CleanableEditText et_password;//密码
    @Bind(R.id.btn_lock)
    SwitchButton btn_lock;
    @Bind(R.id.tb_get_verify)
    TimeButton tb_get_verify;
    @Bind(R.id.image_graph_verify)
    ImageDraweeView image_graph_verify;
    @Bind(R.id.et_graph_verify)
    CleanableEditText et_graph_verify;

    private int mErrorCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    public void onViewCreatedFinish(Bundle savedInstanceState) {
        addNavigation();
        et_mobile.addTextChangedListener(this);
        //et_password.addTextChangedListener(this);
        btn_lock.setOnSwitchClickListener(this);

        /**
         * 图形验证码
         * */
        AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_FORGET);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @OnClick({R.id.btn_nextstep, R.id.tb_get_verify, R.id.image_graph_verify})
    @Override
    public void onClick(View v) {
        final String mobile = et_mobile.getText().toString();
        final String verify = et_graph_verify.getText().toString();
        switch (v.getId()) {
            case R.id.btn_nextstep://下一步
                if (11 != et_mobile.getText().toString().length() || "" == et_mobile.getText().toString().trim()) {
                    ToastUtils.showToastLong(mContext, "请输入正确手机号码");
                } else {
                    String smsCode = et_sms_verify.getText().toString();
                    final String password = et_password.getText().toString();
                    networkRequest(AccountApi.forget(mobile, smsCode, password, verify), new AccountCallback(loading) {

                        @Override
                        public void onSuccess(JSONObject result) {
                            networkRequest(AccountApi.safeLogin(mobile, password, verify),
                                    new AccountCallback(loading) {
                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            AccountHelper.setCurrentUid(result.getString("uid"));
                                            AccountHelper.setCurrentToken(result.getString("token"));
                                            checkLogin();
                                        }

                                        @Override
                                        public void onError(String error_msg) {
                                        }
                                    });
                        }

                        @Override
                        public void onError(String error_msg) {
                            ToastUtils.showToastLong(mContext, error_msg);
                        }
                    });
                }
//                    startActivity(ResetPasswordAcitivity.class);
                break;
            case R.id.tb_get_verify://获取验证码
                System.out.println("============" + mDiskFileCacheHelper.getAsString(FORGET_CODE + mobile) + "  " + verify + "  " + mErrorCount);
                if (StringUtils.isEmpty(mobile)) {
                    tb_get_verify.reset();
                    ToastUtils.showToastLong(mContext, "请输入手机号码");
                    return;
                }
                if (!TextUtils.isEmpty(verify) || !TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(FORGET_CODE + mobile))) {
                    getSendCode(mobile, verify);
                } else {
                    ToastUtils.showToastShort(mContext, "请输入图形验证码");
                    tb_get_verify.reset();
                }

                break;
            case R.id.image_graph_verify:
                AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_FORGET);
                break;
        }
    }


    @Override
    public void onSwitchClick(View v, boolean isSelect) {
        if (!isSelect) //加密
            et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        else //不加密
            et_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 0) {
            btn_nextstep.setClickable(true);
            btn_nextstep.setBackgroundResource(R.drawable.btn_get_focus);
        } else {
            btn_nextstep.setClickable(false);
            btn_nextstep.setBackgroundResource(R.drawable.btn_los_focus);
        }
    }

    public void getSendCode(final String mobile, String verify) {
        networkRequest(AccountApi.sendVerifyCode(mobile, AccountConstants.Action.ACTION_FORGET, verify),
                new AccountCallback(loading) {
                    @Override
                    public void onSuccess(JSONObject result) {
                        ToastUtils.showToastLong(mContext, "验证码发送成功");
                        if (!TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(FORGET_CODE + mobile))) {
                            mDiskFileCacheHelper.remove(FORGET_CODE + mobile);
                        }
                    }

                    @Override
                    public void onError(String error_msg) {
                        tb_get_verify.reset();
                        ToastUtils.showToastLong(mContext, error_msg);
                        mErrorCount++;
                        if (mErrorCount >= 2) {
                            mDiskFileCacheHelper.put(FORGET_CODE + mobile, FORGET_CODE);
                        }
                        AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_LOGIN);
                        et_graph_verify.setText("");
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 验证登录
     */
    private void checkLogin() {
        networkRequest(UserApi.getUserInfo(), new SimpleFastJsonCallback<UserInfo>(UserInfo.class, loading) {
            @Override
            public void onSuccess(String url, UserInfo result) {
                AccountHelper.setUserInfo(result);
                EventBusHelper.post(LoginActivity.EVENT_LOGIN, LoginActivity.EVENT_LOGIN);
//                startActivity(IndexActivity.class);
                //启动红点推送
//                sendBroadcast(new Intent(MainApplication.IN_FORE_MESSAGE));
                finish();
                loading.dismiss();
            }
        });
    }

}