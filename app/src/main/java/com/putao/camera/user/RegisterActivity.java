package com.putao.camera.user;

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
import com.sunnybear.library.controller.ActivityManager;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.CleanableEditText;
import com.sunnybear.library.view.SwitchButton;
import com.sunnybear.library.view.TimeButton;
import com.sunnybear.library.view.image.ImageDraweeView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 注册页面
 */
public class RegisterActivity extends PTXJActivity implements View.OnClickListener, TextWatcher, SwitchButton.OnSwitchClickListener {
    public static final String REGISTER_CODE = "register_code";

    @Bind(R.id.et_mobile)
    CleanableEditText et_mobile;
    @Bind(R.id.et_graph_verify)
    CleanableEditText et_graph_verify;
    @Bind(R.id.et_sms_verify)
    CleanableEditText et_sms_verify;
    @Bind(R.id.et_password)
    CleanableEditText et_password;
    @Bind(R.id.btn_next)
    Button btn_next;
    @Bind(R.id.tb_get_verify)
    TimeButton tb_get_verify;
    @Bind(R.id.btn_is_look)
    SwitchButton btn_is_look;
    @Bind(R.id.image_graph_verify)
    ImageDraweeView image_graph_verify;

    private int mErrorCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void onViewCreatedFinish(Bundle savedInstanceState) {
        addNavigation();
        btn_next.setClickable(false);
        et_mobile.addTextChangedListener(this);
        et_password.addTextChangedListener(this);
        et_sms_verify.addTextChangedListener(this);
        btn_is_look.setOnSwitchClickListener(this);

        /**
         * 图形验证码
         * */
        AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_REGISTER);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @OnClick({R.id.tb_get_verify, R.id.btn_next, R.id.tv_user_protocol, R.id.image_graph_verify})
    @Override
    public void onClick(View v) {
        final String phone = et_mobile.getText().toString();
        String graph_verify = et_graph_verify.getText().toString();
        switch (v.getId()) {
            case R.id.tb_get_verify://获取验证码
                if (!TextUtils.isEmpty(graph_verify) || !TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(REGISTER_CODE + phone))) {
                    getVerifyCode(graph_verify);
                } else {
                    ToastUtils.showToastShort(mContext, "请输入图形验证码");
                    tb_get_verify.reset();
                }
                break;
            case R.id.btn_next://下一步
                String password = et_password.getText().toString();
                String sms_verify = et_sms_verify.getText().toString();
                networkRequest(AccountApi.register(phone, password, sms_verify, graph_verify), new AccountCallback(loading) {
                    @Override
                    public void onSuccess(JSONObject result) {
                        AccountHelper.login(result);
                        ActivityManager.getInstance().removeCurrentActivity();
                        ActivityManager.getInstance().finishCurrentActivity();
                        startActivity(PerfectActivity.class);
                        loading.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(String error_msg) {
                        loading.dismiss();
                        ToastUtils.showToastShort(mContext, error_msg);
                    }
                });
                break;
            case R.id.tv_user_protocol://用户服务协议
//                startActivity(ProtocolActivity.class);
                break;
            case R.id.image_graph_verify://改变图形验证码
                AccountApi.OnGraphVerify(image_graph_verify, AccountConstants.Action.ACTION_REGISTER);
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

    /**
     * 获取验证码
     */
    private void getVerifyCode(final String graph_verify) {
        final String mobile = et_mobile.getText().toString().trim();
        final String value = et_mobile.getText().toString();
        String regExp = "^[1]([3|7|5|8]{1}\\d{1})\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(value);
        if (!m.matches()) {
            tb_get_verify.reset();
            ToastUtils.showToastShort(mContext, "请输入正确的手机号码");
            return;
        }

        networkRequest(AccountApi.sendVerifyCode(mobile, AccountConstants.Action.ACTION_REGISTER, graph_verify), new AccountCallback(loading) {
            @Override
            public void onSuccess(JSONObject result) {
                Logger.d(result.toJSONString());
                if (!TextUtils.isEmpty(mDiskFileCacheHelper.getAsString(REGISTER_CODE + value))) {
                    mDiskFileCacheHelper.remove(REGISTER_CODE + value);
                }
                ToastUtils.showToastLong(mContext, MainApplication.isDebug ? "1234" : "验证码已发送");
            }

            @Override
            public void onError(String error_msg) {
                tb_get_verify.reset();
                ToastUtils.showToastShort(mContext, error_msg);
                mErrorCount++;
                if (mErrorCount >= 3) {
                    mDiskFileCacheHelper.put(REGISTER_CODE + value, REGISTER_CODE);
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
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (et_mobile.length() > 0 && et_password.length() > 5 && et_sms_verify.length() > 0) {
            btn_next.setClickable(true);
            btn_next.setBackgroundResource(R.drawable.btn_get_focus);
        } else {
            btn_next.setClickable(false);
            btn_next.setBackgroundResource(R.drawable.btn_los_focus);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
