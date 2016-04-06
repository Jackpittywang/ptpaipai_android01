package com.putao.camera.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.PTXJActivity;
import com.sunnybear.library.util.ResourcesUtils;

import butterknife.Bind;

/**
 * 用户服务协议
 * Created by guchenkai on 2015/11/29.
 */
public class ProtocolActivity extends PTXJActivity implements View.OnClickListener {
    @Bind(R.id.tv_protocol)
    TextView tv_protocol;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_protocol;
    }

    @Override
    public void onViewCreatedFinish(Bundle savedInstanceState) {
        addNavigation();
        String protocol = ResourcesUtils.getAssetsTextFile(mContext, "protocol.txt");
        tv_protocol.setText(protocol);
    }

    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }

    @Override
    public void onClick(View v) {

    }
}