package com.putao.camera.setting;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.SharedPreferencesHelper;

/**
 * Created by jidongdong on 15/3/3.
 */
public class DLManagerActivity extends BaseActivity implements View.OnClickListener {
    private Button back_btn, btn_mmcc_set, btn_wifi_set;
    private boolean wifi_dl_setting = true;
    private boolean mmcc_dl_setting = false;
    private TextView title_tv;
    private Button right_btn;
    private Button btn_camera_sound_set;
    private Button btn_camera_enter_set;
    private Button btn_camera_water_mark_set;
    private boolean camera_sound_setting;
    private boolean camera_enter_setting;
    private boolean camera_watermark_setting;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_dl_setting;
    }

    @Override
    public void doInitSubViews(View view) {
        title_tv = (TextView) this.findViewById(R.id.title_tv);
        title_tv.setText("相机设置");
        right_btn = (Button) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.INVISIBLE);

        back_btn = (Button) findViewById(R.id.back_btn);
        btn_mmcc_set = (Button) findViewById(R.id.btn_mmcc_set);
        btn_wifi_set = (Button) findViewById(R.id.btn_wifi_set);

        btn_camera_sound_set = (Button) findViewById(R.id.btn_camera_sound_set);
        btn_camera_enter_set = (Button) findViewById(R.id.btn_camera_enter_set);
        btn_camera_water_mark_set = (Button) findViewById(R.id.btn_camera_water_mark_set);

        addOnClickListener(btn_mmcc_set, btn_wifi_set, back_btn, btn_camera_sound_set, btn_camera_enter_set, btn_camera_water_mark_set);
    }

    @Override
    public void doInitData() {
        wifi_dl_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_WIFI_AUTO_DOWNLOAD_SETTING, true);
        mmcc_dl_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_MMCC_AUTO_DOWNLOAD_SETTING, false);

        camera_sound_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_SOUND_SETTING, true);
        camera_enter_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, false);
        camera_watermark_setting = SharedPreferencesHelper.readBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_WATER_MARK_SETTING, false);

        changeSwitchState(btn_wifi_set, wifi_dl_setting);
        changeSwitchState(btn_mmcc_set, mmcc_dl_setting);

        changeSwitchState(btn_camera_sound_set, camera_sound_setting);
        changeSwitchState(btn_camera_enter_set, camera_enter_setting);
        changeSwitchState(btn_camera_water_mark_set, camera_watermark_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.btn_wifi_set:
                wifi_dl_setting = !wifi_dl_setting;
                changeSwitchState(btn_wifi_set, wifi_dl_setting);
                SharedPreferencesHelper.saveBooleanValue(mActivity, PuTaoConstants.PREFERENC_WIFI_AUTO_DOWNLOAD_SETTING, wifi_dl_setting);
                break;
            case R.id.btn_mmcc_set:
                mmcc_dl_setting = !mmcc_dl_setting;
                changeSwitchState(btn_mmcc_set, mmcc_dl_setting);
                SharedPreferencesHelper.saveBooleanValue(mActivity, PuTaoConstants.PREFERENC_MMCC_AUTO_DOWNLOAD_SETTING, mmcc_dl_setting);
                break;
            case R.id.btn_camera_sound_set:
                camera_sound_setting = !camera_sound_setting;
                changeSwitchState(btn_camera_sound_set, camera_sound_setting);
                SharedPreferencesHelper.saveBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_SOUND_SETTING, camera_sound_setting);
                break;
            case R.id.btn_camera_enter_set:
                camera_enter_setting = !camera_enter_setting;
                changeSwitchState(btn_camera_enter_set, camera_enter_setting);
                SharedPreferencesHelper.saveBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_ENTER_SETTING, camera_enter_setting);
                break;
            case R.id.btn_camera_water_mark_set:
                camera_watermark_setting = !camera_watermark_setting;
                changeSwitchState(btn_camera_water_mark_set, camera_watermark_setting);
                SharedPreferencesHelper.saveBooleanValue(mActivity, PuTaoConstants.PREFERENC_CAMERA_WATER_MARK_SETTING, camera_watermark_setting);
                break;
        }
    }

    void changeSwitchState(Button view, boolean enable) {
        view.setBackgroundDrawable(getResources().getDrawable(enable ? R.drawable.set_button_on : R.drawable.set_button_off));
    }
}
