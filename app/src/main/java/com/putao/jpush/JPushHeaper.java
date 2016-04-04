package com.putao.jpush;

import android.content.Context;
import android.os.Handler;

import com.putao.camera.application.MainApplication;
import com.sunnybear.library.util.Logger;

import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by Administrator on 2016/3/22.
 */
public class JPushHeaper {
    private Context mContext;

    public void setAlias(Context context, String alias) {
        mContext = context;
        // 调用 Handler 来异步设置别名
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }
            Logger.d("TagAliasCallback------------", logs);
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    Set<String> set = new HashSet<String>();
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    set.add(MainApplication.isDebug ? "dev" : "pro");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(mContext,
                            (String) msg.obj, set
                            ,
                            mAliasCallback);
                    break;
            }
        }
    };

}
