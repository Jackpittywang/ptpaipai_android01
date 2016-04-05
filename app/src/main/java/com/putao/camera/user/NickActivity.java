package com.putao.camera.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.putao.camera.R;
import com.putao.camera.base.PTXJActivity;
import com.putao.camera.constants.UserApi;
import com.sunnybear.library.model.http.callback.SimpleFastJsonCallback;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.CleanableEditText;

import butterknife.Bind;

/**
 * 保存昵称
 * Created by guchenkai on 2015/11/29.
 */
public class NickActivity extends PTXJActivity {

    @Bind(R.id.et_intro)
    CleanableEditText et_intro;

    private String mNickName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_nick;
    }

    @Override
    public void onViewCreatedFinish(Bundle savedInstanceState) {
        addNavigation();
        initData();
        initView();
    }

    private void initView() {
        et_intro.setText(mNickName);
        et_intro.setSelection(mNickName.length());
    }

    private void initData() {
        Bundle bundle = args.getBundle(CompleteActivity.NICK_NAME);
        mNickName = bundle.getString(CompleteActivity.NICK_NAME);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    /**
     * 保存用户信息
     * by yanghx
     * 请求参数 String类型 昵称、图片url、个人简介
     */
    @Override
    public void onRightAction() {
        String nickName = et_intro.getText().toString();
        if (mNickName.equals(nickName)) {
            ToastUtils.showToastShort(mContext, "没有更改无需保存");
            return;
        }
        if (nickName.length() < 2 || nickName.length() > 24) {
            ToastUtils.showToastShort(mContext, "设置2-24字内的昵称");
            return;
        }
        upload();
    }

    /**
     * 上传PHP服务器
     */
    private void upload() {
        final String etIntro = et_intro.getText().toString();
        networkRequest(UserApi.userNick(etIntro),
                new SimpleFastJsonCallback<String>(String.class, loading) {
                    @Override
                    public void onSuccess(String url, String result) {
                        Logger.i("保存用户信息");
                        Intent intent = new Intent();
                        intent.putExtra(CompleteActivity.NICK_NAME, etIntro);
                        setResult(1, intent);
                        finish();
                    }

                    @Override
                    public void onFinish(String url, boolean isSuccess, String msg) {
                        loading.dismiss();
                        if (!TextUtils.isEmpty(msg))
                            ToastUtils.showToastShort(mContext, msg);
                    }
                });
    }
}
