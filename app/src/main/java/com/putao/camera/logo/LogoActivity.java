
package com.putao.camera.logo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.welcome.CircleSwitchActivity;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends BaseActivity {

    private ImageView baidu_icon_iv, image_loading;
    public static Intent redServiceIntent;
    public static boolean isServiceClose;
    public static final String ACTION_PUSH_SERVICE = "com.putao.camera.PUSH";

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_logo;
    }

    @Override
    public void doInitSubViews(View view) {
        startRedDotService();
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

        try {
            File folder = new File(FileUtils.getARStickersPath());
            if (folder.exists() == false) folder.mkdir();
            // FileUtils.unZipInAsset(mContext, "axfl.zip", FileUtils.FILE_PARENT_NAME, false);
            String folderName = FileUtils.FILE_PARENT_NAME + File.separator + FileUtils.FILE_AR_PARENT_NAME;
            FileUtils.unZipInAsset(mContext, "cn.zip", folderName, false);

            FileUtils.unZipInAsset(mContext, "fd.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "hy.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "hz.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "icon.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "kq.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "mhl.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "xhx.zip", folderName, false);
            FileUtils.unZipInAsset(mContext, "xm.zip", folderName, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动内部推送
     */
    private void startRedDotService() {
        if (TextUtils.isEmpty(AccountHelper.getCurrentUid())) return;
        startService(MainApplication.redServiceIntent);
    }


    @Override
    public void doInitData() {
//        Loger.i("current time:" + System.currentTimeMillis());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                ActivityHelper.startActivity(mActivity, CircleSwitchActivity.class);
                finish();
            }
        }, 2000);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ActivityHelper.startActivity(mActivity, ActivityCamera.class);
//                mActivity.finish();
//            }
//        }, 2000);
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
