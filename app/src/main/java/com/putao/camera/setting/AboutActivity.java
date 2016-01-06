package com.putao.camera.setting;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.welcome.CircleSwitchActivity;

/**
 * Created by jidongdong on 15/2/28.
 */
public class AboutActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn;
    private LinearLayout btn_go_web, btn_feedback;
    private TextView tv_version;
    private TextView title_tv;
    private Button right_btn;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_about;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("关于我们");
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);

        back_btn = (Button) findViewById(R.id.back_btn);
        btn_go_web = (LinearLayout) findViewById(R.id.btn_go_web);
        btn_feedback = (LinearLayout) findViewById(R.id.btn_feedback);
        tv_version = (TextView) findViewById(R.id.tv_version);
        addOnClickListener(back_btn, btn_feedback, btn_go_web);
    }

    @Override
    public void doInitData() {
        tv_version.setText(getVersion());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_feedback:
                Bundle bundle = new Bundle();
                bundle.putBoolean("fromAbout", true);
                ActivityHelper.startActivity(mActivity, CircleSwitchActivity.class, bundle);
                break;
            case R.id.btn_go_web:
                Uri uri = Uri.parse(PuTaoConstants.ORG_WEBSITE_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return "版本:" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
