package com.putao.camera.share;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.sunnybear.library.util.ToastUtils;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import cn.sharesdk.wechat.utils.WechatHelper;
import onekeyshare.OnekeyShare;

/**
 * 分享工具
 * Created by guchenkai on 2015/11/27.
 */
public class ShareTools {
    private OnekeyShare mOnekeyShare;
    private String platfom;

    private ShareTools(String platfom) {
        mOnekeyShare = new OnekeyShare();
        mOnekeyShare.setPlatform(platfom);
    }

    public static ShareTools newInstance(String platfom) {
        return new ShareTools(platfom);
    }

    /**
     * title标题，在印象笔记、邮箱、信息、微信（包括好友、朋友圈和收藏）、
     * 易信（包括好友、朋友圈）、人人网和QQ空间使用，否则可以不提供
     *
     * @param title
     */
    public ShareTools setTitle(String title) {
        mOnekeyShare.setTitle(title);
        return this;
    }

    /**
     * text是分享文本，所有平台都需要这个字段(该字段必须设置,否则分享失败)
     *
     * @param text
     */
    public ShareTools setText(String text) {
        mOnekeyShare.setText(text);
        return this;
    }

    /**
     * imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段
     *
     * @param imagePath
     */
    public ShareTools setImagePath(String imagePath) {
        mOnekeyShare.setImagePath(imagePath);
        return this;
    }

    /**
     * imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段
     *
     * @param imageUrl
     */
    public ShareTools setImageUrl(String imageUrl) {
        mOnekeyShare.setImageUrl(imageUrl);
        return this;
    }

    /**
     * url在微信（包括好友、朋友圈收藏）和易信（包括好友和朋友圈）中使用，否则可以不提供
     *
     * @param url
     */
    public ShareTools setUrl(String url) {
        mOnekeyShare.setUrl(url);
        return this;
    }

    /**
     * filePath是待分享应用程序的本地路劲，仅在微信（易信）好友和Dropbox中使用，否则可以不提供
     *
     * @param filePath
     */
    public ShareTools setFilePath(String filePath) {
        mOnekeyShare.setFilePath(filePath);
        return this;
    }

    /**
     * 执行分享
     *
     * @param context
     */
    public void execute(Context context) {
        mOnekeyShare.setDialogMode();
        mOnekeyShare.disableSSOWhenAuthorize();
//        mOnekeyShare.setShareContentCustomizeCallback(new ReflectableShareContentCustomizeCallback());
        mOnekeyShare.show(context);
    }

    /**
     * 微信网页分享
     */
    public static void wechatWebShare(Context context, boolean isWechat, String title, String text, String imageUrl, String url) {
        WechatHelper.ShareParams params = null;
        if (isWechat)
            params = new Wechat.ShareParams();
        else
            params = new WechatFavorite.ShareParams();
        params.title = title;
        params.text = text;
//        params.imageUrl = imageUrl;
        params.url = url;
        params.imagePath=imageUrl;
        params.setShareType(Platform.SHARE_WEBPAGE);

        Platform plat = null;
        if (isWechat)
            plat = ShareSDK.getPlatform(Wechat.NAME);
        else
            plat = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调
        plat.setPlatformActionListener(new MyPlatformActionListener(context));
        plat.share(params);
    }

    /**
     * QQ、空间分享
     */
    public static void OnQQZShare(final Context context, boolean isWebQQ, String title, String text, String imageUrl, String url) {
        QQ.ShareParams params = new QQ.ShareParams();
        params.title = title;
        params.text = text;
        params.imagePath=imageUrl;
        params.titleUrl = url;
        params.setShareType(Platform.SHARE_TEXT);

        Platform plat = null;
        if (isWebQQ) {
            plat = ShareSDK.getPlatform(QQ.NAME);
        } else {
            plat = ShareSDK.getPlatform(QZone.NAME);
        }
        // 设置分享事件回调
        plat.setPlatformActionListener(new MyPlatformActionListener(context));
        // 执行图文分享
        plat.share(params);
    }

    /**
     * 微博的分享
     */
    public static void OnWeiboShare(final Context context, String text, String imagePath, String url) {
        if (!checkApkExist(context, "com.sina.weibo")) {
            return;
        }
        SinaWeibo.ShareParams params = new SinaWeibo.ShareParams();
        params.setText(text + url);
        params.setImagePath(imagePath);
        Platform plat = ShareSDK.getPlatform(SinaWeibo.NAME);
        // 设置分享事件回调
        plat.setPlatformActionListener(new MyPlatformActionListener(context));
        plat.SSOSetting(false);
        // 执行图`文分享
        plat.share(params);
    }


    static class MyPlatformActionListener implements PlatformActionListener {
        private Context mContext;
        private Handler mHandler;

        public MyPlatformActionListener(Context context) {
            mContext = context;
            mHandler = new Handler();
        }

        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            ToastUtils.showToastShort(mContext, "分享成功");
        }

        /**
         * 此方法在子线程中执行
         *
         * @param platform
         * @param i
         * @param throwable
         */
        @Override
        public void onError(Platform platform, int i, final Throwable throwable) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                        if (throwable.getClass().getName().contains("NotExistException"))
                        ToastUtils.showToastShort(mContext, "您未安装该应用");
                    else
                        ToastUtils.showToastShort(mContext, "分享失败");
                }
            });
        }

        @Override
        public void onCancel(Platform platform, int i) {
            ToastUtils.showToastShort(mContext, "取消分享");
        }

    }

    public static boolean checkApkExist(Context context, String packageName) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(packageName,
                            PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            ToastUtils.showToastShort(context, "您未安装该应用");
            return false;
        }
    }

}
