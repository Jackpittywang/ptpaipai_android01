package com.putao.camera.setting;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.umengfb.UmengFeedbackActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.UmengUpdateHelper;

/**
 * Created by jidongdong on 15/2/28.
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout btn_dl_manager, btn_feedback, btn_check_update, btn_about_us;
    private Button back_btn;
    private TextView title_tv;
    private Button right_btn;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_setting;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("设置");
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);

        btn_about_us = (LinearLayout) findViewById(R.id.btn_about_us);
        btn_check_update = (LinearLayout) findViewById(R.id.btn_check_update);
        btn_dl_manager = (LinearLayout) findViewById(R.id.btn_dl_manager);
        btn_feedback = (LinearLayout) findViewById(R.id.btn_feedback);
        back_btn = (Button) findViewById(R.id.back_btn);
        addOnClickListener(btn_about_us, btn_check_update, btn_dl_manager, btn_feedback, back_btn);
    }

    @Override
    public void doInitData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_about_us:
            case R.id.btn_feedback:
            case R.id.btn_dl_manager:
                startSettingItem(v.getId());
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_check_update:
                UmengUpdateHelper.getInstance().setShowTip(true).autoUpdate(this);
                break;
        }
    }

    void startSettingItem(int id) {
        Class<?> clazz = null;
        switch (id) {
            case R.id.btn_about_us:
                clazz = AboutActivity.class;
                break;
            case R.id.btn_dl_manager:
                clazz = DLManagerActivity.class;
                break;
            case R.id.btn_feedback:
                clazz = UmengFeedbackActivity.class;
                break;
        }
        if (clazz != null)
            ActivityHelper.startActivity(mActivity, clazz);
    }
}
