
package com.putao.camera.thirdshare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.ToasterHelper;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jidongdong on 15/3/5.
 */
public class ShareTools {
    /**
     * 微博微博分享接口实例
     */
    public IWeiboShareAPI mWeiboShareAPI = null;
    public IWXAPI mWXAPIFactory = null;
    public Tencent mTencent;
    private QzoneShare mQzoneShare;
    private String mCurrentUrl;
    private ArrayList<String> mArrayText;
    private Activity mActivity;
    private static String APP_NAME;

    public ShareTools(Activity activity, String fileurl) {
        mActivity = activity;
        mCurrentUrl = fileurl;
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity, PuTaoConstants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
        mWXAPIFactory = WXAPIFactory.createWXAPI(mActivity, PuTaoConstants.WEIXIN_APP_KEY);
        mWXAPIFactory.registerApp(PuTaoConstants.WEIXIN_APP_KEY);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(PuTaoConstants.QQ_APP_KEY, mActivity);
        }
        mQzoneShare = new QzoneShare(mActivity, mTencent.getQQToken());
        mArrayText = new ArrayList<String>();
        mArrayText.add("再不拍，孩子就长大了！");
        mArrayText.add("用镜头记录孩子成长点滴，满满的都是爱啊！");
        mArrayText.add("我对你的喜爱有如滔滔江水，连绵不绝，又如黄河泛滥，一发不可收拾。");
        mArrayText.add("萌主驾到，速来围观！");
        mArrayText.add("家有萌娃，可爱，就是这么任性！");
        APP_NAME = "葡萄相机";
    }

    //QZone分享
    public void doShareToQzone() {
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
        // mParams.getString(IMAGE_URL));
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, " ");// 必填
        int random = new Random().nextInt(mArrayText.size());
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mArrayText.get(random));// 选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://putao.im/forum.php");// 必填
        // 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (mCurrentUrl != null && !mCurrentUrl.equals("")) {
            imageUrls.add(mCurrentUrl);
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mQzoneShare.shareToQzone(mActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                ToasterHelper.showShort(mActivity, "取消分享", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onComplete(Object arg0) {
                ToasterHelper.showShort(mActivity, "分享成功", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onError(UiError arg0) {
            }
        });
    }

    //QQ分享
    public void doShareToQQ() {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, mCurrentUrl);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, APP_NAME);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mTencent.shareToQQ(mActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                ToasterHelper.showShort(mActivity, "取消分享", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onComplete(Object arg0) {
                ToasterHelper.showShort(mActivity, "分享成功", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onError(UiError arg0) {
                ToasterHelper.showShort(mActivity, "分享错误", ToasterHelper.IMG_INFO);
            }
        });
    }

    // 微博分享------------
    public void doShareToWeibo() {
        sendMultiMessage(true, true);
    }

    private void sendMultiMessage(boolean hasText, boolean hasImage) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();// 初始化微博的分享消息
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasImage) {
            weiboMessage.imageObject = getImageObj();
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(mActivity, request); // 发送请求消息到微博,唤起微博分享界面
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        int random = new Random().nextInt(mArrayText.size()) + 1;
        textObject.text = mArrayText.get(random);
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj() {
        ImageObject imageObject = new ImageObject();
        // BitmapDrawable bitmapDrawable = (BitmapDrawable)
        // mImageView.getDrawable();
        Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(mCurrentUrl);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    // 微信分享------------
    private static final int THUMB_SIZE = 150;

    public void sendBitmapToWeixin(boolean isTimelineCb) {
        Bitmap bmp = BitmapHelper.getInstance().loadBitmap(mCurrentUrl);
        WXImageObject imgObj = new WXImageObject(bmp);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, (int) ((float) bmp.getHeight() / (float) bmp.getWidth() * THUMB_SIZE), true);
        // bmp.recycle();
        msg.thumbData = bmpToByteArray(thumbBmp, true); // 设置缩略图
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        mWXAPIFactory.sendReq(req);
    }

    /**
     * 微信网页分享
     */
    /*public static void wechatWebShare(Context context, boolean isWechat, String title, String text, String imageUrl, String url) {
        WechatHelper.ShareParams params = null;
        if (isWechat)
            params = new Wechat.ShareParams();
        else
            params = new WechatFavorite.ShareParams();
        params.title = title;
        params.text = text;
        params.imageUrl = imageUrl;
        params.url = url;
        params.setShareType(Platform.SHARE_WEBPAGE);

        Platform plat = null;
        if (isWechat)
            plat = ShareSDK.getPlatform(Wechat.NAME);
        else
            plat = ShareSDK.getPlatform(WechatMoments.NAME);
        // 设置分享事件回调
        plat.setPlatformActionListener(new MyPlatformActionListener(context));
        plat.share(params);
    }*/


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int quality = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, output);
        byte[] result = output.toByteArray();
        while (result.length / 1024 > 20) { // 循环判断如果压缩后图片是否大于30kb,大于继续压缩
            output.reset();// 重置baos即清空baos
            quality -= 10;// 每次都减少10
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, output);// 这里压缩options%，把压缩后的数据存放到baos中
            result = output.toByteArray();
        }
        if (needRecycle) {
            bmp.recycle();
        }
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
