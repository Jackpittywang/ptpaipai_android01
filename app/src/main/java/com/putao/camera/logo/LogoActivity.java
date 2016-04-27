
package com.putao.camera.logo;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.putao.account.AccountHelper;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.welcome.CircleSwitchActivity;
import com.sunnybear.library.controller.eventbus.Subcriber;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends BaseActivity {

    private ImageView baidu_icon_iv;
    private TextView tvTip;
    public static Intent redServiceIntent;
    public static boolean isServiceClose;
    public static final String ACTION_PUSH_SERVICE = "com.putao.camera.PUSH";
    private ProgressBar pbInit;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_logo;
    }

    @Override
    public void doInitSubViews(View view) {
        EventBus.getEventBus().register(this);
        startRedDotService();
        baidu_icon_iv = queryViewById(R.id.baidu_icon_iv);
        pbInit = queryViewById(R.id.pbInit);
        tvTip = queryViewById(R.id.tvTip);
        baidu_icon_iv.setVisibility(View.INVISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File folder = new File(FileUtils.getARStickersPath());
                    if (folder.exists() == false) folder.mkdir();
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
        }).start();

    }

    /**
     * 启动内部推送
     */
    private void startRedDotService() {
        sendBroadcast(new Intent(MainApplication.IN_FORE_MESSAGE));
    }

    private final long WAIT_TIME = 2 * 1000;

    @Override
    public void doInitData() {
        boolean isFristUse = SharedPreferencesHelper.readBooleanValue(this, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);
        if (isFristUse) {
            pbInit.setVisibility(View.VISIBLE);
            tvTip.setVisibility(View.VISIBLE);
        } else {
            pbInit.setVisibility(View.GONE);
            tvTip.setVisibility(View.GONE);
        }
        new AsyncTask<Void, Integer, Void>() {
            int count = 0;

            @Override
            protected Void doInBackground(Void... params) {
                while (count <= 100) {
                    count++;
                    try {
                        Thread.sleep(WAIT_TIME / 100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(count);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (values[0] <= 100) {
                    pbInit.setProgress(values[0]);
                } else {
                    pbInit.setProgress(values[0]);
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                ActivityHelper.startActivity(mActivity, CircleSwitchActivity.class);
                finish();
            }
        }.execute();
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
