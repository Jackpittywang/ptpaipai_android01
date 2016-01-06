
package com.putao.camera.thirdshare.dialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseDialog;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.thirdshare.view.ThirdShareItemView;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.ToasterHelper;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXImageObject;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ThirdShareDialog extends BaseDialog implements View.OnClickListener {
    public ThirdShareItemView third_share_weixin_btn;
    public ThirdShareItemView third_share_pengyouquan_btn;
    // public ThirdShareItemView third_share_qq_zone_btn;
    public ThirdShareItemView third_share_sina_btn;
    public ThirdShareItemView third_share_qzone_btn;
    /**
     * 微博微博分享接口实例
     */
    public static IWeiboShareAPI mWeiboShareAPI = null;
    public static IWXAPI mWXAPIFactory = null;
    public static Tencent mTencent;
    private QzoneShare mQzoneShare;
    private String mCurrentUrl;
    private ThirdShareDialogProcessListener mThirdShareDialogProcessListener;
    private ArrayList<String> mArrayText;
    private TextView mTitle_tv;

    @Override
    protected void initView(Dialog dlg) {
        mTitle_tv = (TextView) dlg.findViewById(R.id.dialog_title_tv);
        third_share_weixin_btn = (ThirdShareItemView) dlg.findViewById(R.id.third_share_weixin_btn);
        third_share_pengyouquan_btn = (ThirdShareItemView) dlg.findViewById(R.id.third_share_pengyouquan_btn);
        // third_share_qq_zone_btn = (ThirdShareItemView)
        // dlg.findViewById(R.id.third_share_qq_zone_btn);
        third_share_sina_btn = (ThirdShareItemView) dlg.findViewById(R.id.third_share_sina_btn);
        third_share_qzone_btn = (ThirdShareItemView) dlg.findViewById(R.id.third_share_qzone_btn);
        third_share_weixin_btn.setOnClickListener(this);
        third_share_pengyouquan_btn.setOnClickListener(this);
        // third_share_qq_zone_btn.setOnClickListener(this);
        third_share_sina_btn.setOnClickListener(this);
        third_share_qzone_btn.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        Bundle bundle = this.getArguments();
        String title = bundle.getString("title");
        if (title != null && !title.equals("")) {
            mTitle_tv.setText(title);
            mTitle_tv.setVisibility(View.VISIBLE);
        } else {
            mTitle_tv.setVisibility(View.GONE);
        }
        mCurrentUrl = bundle.getString("url");
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this.getActivity(), PuTaoConstants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
        // acquire wxapi
        mWXAPIFactory = WXAPIFactory.createWXAPI(this.getActivity(), PuTaoConstants.WEIXIN_APP_KEY);
        mWXAPIFactory.registerApp(PuTaoConstants.WEIXIN_APP_KEY);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(PuTaoConstants.QQ_APP_KEY, getActivity());
        }
        mQzoneShare = new QzoneShare(getActivity(), mTencent.getQQToken());
        mArrayText = new ArrayList<String>();
        mArrayText.add("再不拍，孩子就长大了！");
        mArrayText.add("用镜头记录孩子成长点滴，满满的都是爱啊！");
        mArrayText.add("我对你的喜爱有如滔滔江水，连绵不绝，又如黄河泛滥，一发不可收拾。");
        mArrayText.add("萌主驾到，速来围观！");
        mArrayText.add("家有萌娃，可爱，就是这么任性！");
    }

    @Override
    public void onClick(View v) {
        // 图像处理接口
        if (this.mThirdShareDialogProcessListener != null) {
            File file = mThirdShareDialogProcessListener.onSave();
            mCurrentUrl = file.getPath();
        }
        switch (v.getId()) {
            case R.id.third_share_weixin_btn:
                // 发送微信
                sendBitmapToWeixin(false);
                this.dismiss();
                break;
            case R.id.third_share_pengyouquan_btn:
                // 发送朋友圈
                sendBitmapToWeixin(true);
                this.dismiss();
                break;
            case R.id.third_share_sina_btn:
                doShareToWeibo();
                this.dismiss();
                break;
            case R.id.third_share_qzone_btn:
                doShareToQzone();
                this.dismiss();
                break;
        }
    }

    @Override
    protected int initContentView() {
        return R.layout.layout_third_share_dialog;
    }

    public void doShareToQzone() {
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
        // mParams.getString(IMAGE_URL));
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, " ");// 必填
        int random = new Random().nextInt(mArrayText.size()) + 1;
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mArrayText.get(random));// 选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "http://putao.im/forum.php");// 必填
        // 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (mCurrentUrl != null && !mCurrentUrl.equals("")) {
            imageUrls.add(mCurrentUrl);
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mQzoneShare.shareToQzone(getActivity(), params, new IUiListener() {
            @Override
            public void onCancel() {
                dismiss();
                ToasterHelper.showShort(getActivity(), "取消分享", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onComplete(Object arg0) {
                dismiss();
                ToasterHelper.showShort(getActivity(), "分享成功", ToasterHelper.IMG_INFO);
                // if (callBack != null)
                // {
                // callBack.onCallBack();
                // }
            }

            @Override
            public void onError(UiError arg0) {
                // B5MToaster.showShort(mActivity, arg0.errorMessage,
                // B5MToaster.IMG_INFO);
            }
        });
    }

    // 微博分享------------
    private void doShareToWeibo() {
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
        mWeiboShareAPI.sendRequest(this.getActivity(), request); // 发送请求消息到微博,唤起微博分享界面
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

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int quality = 100;
        bmp.compress(CompressFormat.JPEG, quality, output);
        byte[] result = output.toByteArray();
        while (result.length / 1024 > 20) { // 循环判断如果压缩后图片是否大于30kb,大于继续压缩
            output.reset();// 重置baos即清空baos
            quality -= 10;// 每次都减少10
            bmp.compress(CompressFormat.JPEG, quality, output);// 这里压缩options%，把压缩后的数据存放到baos中
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

    public interface ThirdShareDialogProcessListener {
        public File onSave();
    }

    public void setThirdShareDialogProcessListener(ThirdShareDialogProcessListener aThirdShareDialogProcessListener) {
        this.mThirdShareDialogProcessListener = aThirdShareDialogProcessListener;
    }
}
