package com.putao.camera.util;

import android.content.Context;
import android.widget.Toast;

import com.putao.camera.bean.LogTag;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengDownloadListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * Umeng自动更新
 *
 * @author CLEVO
 */
public class UmengUpdateHelper {
    private static UmengUpdateHelper instance;
    private Boolean isReset;
    private boolean isTip = false;

    private UmengUpdateHelper() {
    }

    ;

    public static UmengUpdateHelper getInstance() {
        if (null == instance) {
            instance = new UmengUpdateHelper();
        }
        return instance;
    }

    /**
     * 自动更新参数恢复Umeng默认
     */
    private void setDefaultConfig() {
        UmengUpdateAgent.setDefault();
    }

    public UmengUpdateHelper setShowTip(boolean show) {
        isTip = show;
        return instance;
    }

    /**
     * 自动更新通用参数设置
     */
    private void setInnerConfig() {
        // 自动更新日志输出
        UpdateConfig.setDebug(true);
        // 检查程序关于Umeng自动更新的本地配置
        UmengUpdateAgent.setUpdateCheckConfig(true);
        // 是否仅wifi条件下允许更新
        UmengUpdateAgent.setUpdateOnlyWifi(true);
        // 自动更新弹框
        UmengUpdateAgent.setUpdateAutoPopup(false);
        // 全量更新或者增量更新
        UmengUpdateAgent.setDeltaUpdate(true);
        // 更新下载通知栏是否显示高级模式
        UmengUpdateAgent.setRichNotification(true);
        // 更新提示方式
        UmengUpdateAgent.setUpdateUIStyle(UpdateStatus.STYLE_DIALOG);
    }

    /**
     * Umeng更新的参数配置
     */
    public void setCommonConfig() {
        if (null == isReset || !isReset) {
            setDefaultConfig();
            setInnerConfig();
            isReset = true;
        }
    }

    /**
     * 自动更新
     */
    /**
     * @param ctx
     */
    public void autoUpdate(final Context ctx) {
        Loger.d("do autoupdate.........");
        // 自动更新api調用
        UmengUpdateAgent.update(ctx);
        setUpdateListener(new PTUmengUpdateListener(ctx));
    }

    /**
     * 手动更新
     */
    public void forceUpdate(final Context ctx) {
        // 手动更新
        UmengUpdateAgent.forceUpdate(ctx);
        setUpdateListener(new PTUmengUpdateListener(ctx));
    }

    /**
     * 静默更新
     */
    public void silentUpdate(final Context ctx) {
        // 静默更新
        UmengUpdateAgent.silentUpdate(ctx);
        setUpdateListener(new PTUmengUpdateListener(ctx));
    }

    /**
     * 显示更新弹框
     *
     * @param ctx
     * @param updateResponse
     */
    public void showUpdateDialog(Context ctx, UpdateResponse updateResponse) {
        UmengUpdateAgent.showUpdateDialog(ctx, updateResponse);
    }

    /**
     * 监听获取更新结果
     *
     * @param
     */
    public void setUpdateListener(UmengUpdateListener umUL) {
        if (null != umUL) {
            UmengUpdateAgent.setUpdateListener(umUL);
        }
    }

    /**
     * 监听按键操作
     *
     * @param
     */
    public void setDialogListener(UmengDialogButtonListener umDBL) {
        if (null != umDBL) {
            UmengUpdateAgent.setDialogListener(umDBL);
        }
    }

    /**
     * 监听下载
     *
     * @param
     */
    public void setDownloadListener(UmengDownloadListener umDL) {
        if (null != umDL) {
            UmengUpdateAgent.setDownloadListener(umDL);
        }
    }

    private class PTUmengUpdateListener implements UmengUpdateListener {
        private Context context;

        public PTUmengUpdateListener(Context ctx) {
            this.context = ctx;
        }

        @Override
        public void onUpdateReturned(int arg0, UpdateResponse arg1) {
            // TODO Auto-generated method stub
//            Loger.d(LogTag.UmengUpdate.toString() + "Umeng" + "UpdateStatus" + arg0 + (arg0 == UpdateStatus.Yes ? "有更新!" : "没有更新!"));
            if (arg0 == UpdateStatus.Yes) {
                showUpdateDialog(context, arg1);
                setDialogListener(new PTUmengDialogButtonListener(context, arg1));
            } else if (arg0 == UpdateStatus.No) {
                if (isTip)
                    Toast.makeText(context, "已是最新版本", Toast.LENGTH_LONG).show();
            } else if (arg0 == UpdateStatus.NoneWifi) {
                if (isTip)
                    Toast.makeText(context, "当前环境不是Wifi网络", Toast.LENGTH_LONG).show();
            } else if (arg0 == UpdateStatus.Timeout) {
                if (isTip)
                    Toast.makeText(context, "请求网络超时", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class PTUmengDialogButtonListener implements UmengDialogButtonListener {
        private Context context;
        private UpdateResponse updateResponse;

        public PTUmengDialogButtonListener(Context ctx, UpdateResponse ur) {
            this.context = ctx;
            this.updateResponse = ur;
        }

        @Override
        public void onClick(int arg0) {
            // TODO Auto-generated method stub
            Loger.d(LogTag.UmengUpdate.toString() + "Umeng弹框按鍵=" + arg0);
            // 弹框一次，忽略更新。6=取消，5=下载更新
            switch (arg0) {
                case UpdateStatus.Ignore: {
                    UmengUpdateAgent.ignoreUpdate(context, updateResponse);
                }
                break;
                case UpdateStatus.Update:
                case UpdateStatus.NotNow: {
                    setDownloadListener(new PTUmengDownloadListener(context, updateResponse));
                }
                break;
            }
            // 不管用户怎么选择事件，只会弹框一次
            //UmengUpdateAgent.ignoreUpdate(context, updateResponse);
            //setDownloadListener(new PTUmengDownloadListener(context, updateResponse));
        }
    }

    private class PTUmengDownloadListener implements UmengDownloadListener {
        private Context context;
        private UpdateResponse updateResponse;

        public PTUmengDownloadListener(Context ctx, UpdateResponse ur) {
            this.context = ctx;
            this.updateResponse = ur;
        }

        @Override
        public void OnDownloadUpdate(int arg0) {
            // TODO Auto-generated method stub
            Loger.d(LogTag.UmengUpdate.toString() + "Umeng下载更新中=" + arg0);
        }

        @Override
        public void OnDownloadStart() {
            // TODO Auto-generated method stub
            Loger.d(LogTag.UmengUpdate.toString() + "Umeng下载更新开始！");
        }

        @Override
        public void OnDownloadEnd(int arg0, String arg1) {
            // TODO Auto-generated method stub
            Loger.d(LogTag.UmengUpdate.toString() + "Umeng下载更新结束！");
            // 下载完毕，可能用户未选择“安装”，避免下次再次弹框，故忽略更新
            UmengUpdateAgent.ignoreUpdate(context, updateResponse);
        }
    }

    ;
}
