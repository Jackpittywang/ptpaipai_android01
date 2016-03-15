
package com.putao.camera.logo;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.ActivityCamera;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.menu.MenuActivity;
import com.putao.camera.util.ActivityHelper;

public class LogoActivity extends BaseActivity {

    private ImageView baidu_icon_iv, image_loading;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_logo;
    }

    @Override
    public void doInitSubViews(View view) {

        baidu_icon_iv = queryViewById(R.id.baidu_icon_iv);
        image_loading = queryViewById(R.id.image_loading);
        baidu_icon_iv.setVisibility(View.INVISIBLE);
//        try {
//            //            ActivityInfo info = this.getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
//            //            String msg = info.metaData.getString("UMENG_CHANNEL");
//            String msg = getMetaDataValue("UMENG_CHANNEL");
//            if (msg.equals("channel_baidu")) {
//                baidu_icon_iv.setVisibility(View.VISIBLE);
//            } else if (msg.equals("channel_jinli")) {
//                image_loading.setImageResource(R.drawable.loading_jinli);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        EventBus.getEventBus().register(this);
    }

    @Override
    public void doInitData() {
//        Loger.i("current time:" + System.currentTimeMillis());
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                // ActivityHelper.startActivity(mActivity, CircleSwitchActivity.class);
//
//            }
//        }, 2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
                mActivity.finish();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.EVENT_FINISH_LOGO:
                finish();
                break;
        }
    }

    private String getMetaDataValue(String name, String def) {
        String value = getMetaDataValue(name);
        return (value == null) ? def : value;
    }

    private String getMetaDataValue(String name) {
        Object value = null;
        PackageManager packageManager = mContext.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 128);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                value = applicationInfo.metaData.get(name);
            }
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not read the name in the manifest file.", e);
        }
        if (value == null) {
            throw new RuntimeException("The name '" + name + "' is not defined in the manifest file's meta data.");
        }
        return value.toString();
    }
}
