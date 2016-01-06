
package com.putao.camera.voice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.thirdshare.view.ThirdShareItemView;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.ToasterHelper;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class VoicePhotoShareActivity extends BaseActivity implements View.OnClickListener {
    private WebView voice_photo_body_wv;
    private Button cancel_btn;
    private WebSettings webSettings;
    /**
     * 微博微博分享接口实例
     */
    public static IWeiboShareAPI mWeiboShareAPI = null;
    public static IWXAPI mWXAPIFactory = null;
    public static Tencent mTencent;
    private QzoneShare mQzoneShare;
    public ThirdShareItemView third_share_weixin_btn;
    public ThirdShareItemView third_share_pengyouquan_btn;
    public ThirdShareItemView third_share_sina_btn;
    public ThirdShareItemView third_share_qzone_btn;
    public String card_url, local_photo_path;
    private final static String _title = "新年有声贺卡";
    private final static String _description = "今年过节不收礼，收礼只收萌娃娃！我家宝贝给您拜年啦，快来围观呀";
    private Bitmap scaledThumb = null;
    private ArrayList<String> mArrayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initData();
    }

    protected void initData() {
        // TODO Auto-generated method stub
        // Bundle bundle = this.getArguments();
        // mCurrentUrl = bundle.getString("url");
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, PuTaoConstants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
        // acquire wxapi
        mWXAPIFactory = WXAPIFactory.createWXAPI(this, PuTaoConstants.WEIXIN_APP_KEY);
        mWXAPIFactory.registerApp(PuTaoConstants.WEIXIN_APP_KEY);
        if (mTencent == null) {
            mTencent = Tencent.createInstance(PuTaoConstants.QQ_APP_KEY, this);
        }
        mQzoneShare = new QzoneShare(mActivity, mTencent.getQQToken());
        mArrayText = new ArrayList<String>();
        mArrayText.add("再不拍，孩子就长大了！");
        mArrayText.add("用镜头记录孩子成长点滴，满满的都是爱啊！");
        mArrayText.add("我对你的喜爱有如滔滔江水，连绵不绝，又如黄河泛滥，一发不可收拾。");
        mArrayText.add("萌主驾到，速来围观！");
        mArrayText.add("家有萌娃，可爱，就是这么任性！");
    }

    @Override
    public int doGetContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_voice_photo_share;
    }

    @Override
    public void doInitSubViews(View view) {
        voice_photo_body_wv = (WebView) this.findViewById(R.id.voice_photo_body_wv);
        cancel_btn = (Button) this.findViewById(R.id.cancel_btn);
        third_share_weixin_btn = (ThirdShareItemView) this.findViewById(R.id.third_share_weixin_btn);
        third_share_pengyouquan_btn = (ThirdShareItemView) this.findViewById(R.id.third_share_pengyouquan_btn);
        third_share_sina_btn = (ThirdShareItemView) this.findViewById(R.id.third_share_sina_btn);
        third_share_qzone_btn = (ThirdShareItemView) this.findViewById(R.id.third_share_qzone_btn);
    }

    @Override
    public void doInitData() {
        addOnClickListener(cancel_btn, third_share_weixin_btn, third_share_pengyouquan_btn, third_share_sina_btn, third_share_qzone_btn);
        voice_photo_body_wv.getSettings().setJavaScriptEnabled(true);
        voice_photo_body_wv.setWebViewClient(new MyWebViewClient());
        voice_photo_body_wv.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                super.onProgressChanged(view, progress);
            }
        });
        webSettings = voice_photo_body_wv.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        card_url = this.getIntent().getStringExtra("card_url");
        local_photo_path = this.getIntent().getStringExtra("local_photo_path");
        voice_photo_body_wv.loadUrl(card_url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                showCancelBtnConfirm();
                break;
            case R.id.third_share_weixin_btn:
                // 发送微信
                sendToWeixin(false);
                break;
            case R.id.third_share_pengyouquan_btn:
                // 发送朋友圈
                sendToWeixin(true);
                break;
            case R.id.third_share_sina_btn:
                // doShareToWeibo();
                // this.dismiss();
                sendMultiMessage();
                break;
            case R.id.third_share_qzone_btn:
                doShareToQzone();
                break;
        }
    }

    public void doShareToQzone() {
        // params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,
        // mParams.getString(IMAGE_URL));
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, _title);// 必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, _description);// 选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, card_url);// 必填
        // 分享的图片, 以ArrayList<String>的类型传入，以便支持多张图片（注：图片最多支持9张图片，多余的图片会被丢弃）。
        ArrayList<String> imageUrls = new ArrayList<String>();
        imageUrls.add(getPhotoFile().getPath());
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        mQzoneShare.shareToQzone(mActivity, params, new IUiListener() {
            @Override
            public void onCancel() {
                // pop.dismiss();
                ToasterHelper.showShort(mActivity, "取消分享", ToasterHelper.IMG_INFO);
            }

            @Override
            public void onComplete(Object arg0) {
                // pop.dismiss();
                ToasterHelper.showShort(mActivity, "分享成功", ToasterHelper.IMG_INFO);
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

    public void sendToWeixin(boolean isTimelineCb) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = card_url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = _title;
        msg.description = _description;
        if (scaledThumb == null) {
            Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(getPhotoFile().getPath());
            byte[] b = bmpToByteArray(bitmap, false, 30);
            Bitmap thumb = BitmapFactory.decodeByteArray(b, 0, b.length);
            scaledThumb = Bitmap.createScaledBitmap(thumb, 120, 120, false);
        }
        msg.thumbData = bmpToByteArray(scaledThumb, false, 30);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimelineCb ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        mWXAPIFactory.sendReq(req);
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle, int limitSize) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int quality = 100;
        bmp.compress(CompressFormat.JPEG, quality, output);
        byte[] result = output.toByteArray();
        while (result.length / 1024 > limitSize) {
            // 循环判断如果压缩后图片是否大于30kb,大于继续压缩
            // Loger.i("quality:" + quality + "result.length / 1024:" +
            // result.length / 1024);
            output.reset();// 重置baos即清空baos
            quality -= 5;// 每次都减少-5
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

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    /**
     * 分享网页控件
     */
    private void sendMultiMessage() {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();// 初始化微博的分享消息
        weiboMessage.textObject = getTextObj();
        weiboMessage.mediaObject = getWebpageObj();
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(this, request); // 发送请求消息到微博,唤起微博分享界面
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        int random = new Random().nextInt(mArrayText.size()) + 1;
        textObject.text = mArrayText.get(random);
        return textObject;
    }

    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        try {
            mediaObject.identify = Utility.generateGUID();
            mediaObject.title = _title;
            mediaObject.description = _description;
            // 设置 Bitmap 类型的图片到视频对象里
            if (scaledThumb == null) {
                Bitmap bitmap = BitmapHelper.getInstance().loadBitmap(getPhotoFile().getPath());
                byte[] b = bmpToByteArray(bitmap, false, 30);
                Bitmap thumb = BitmapFactory.decodeByteArray(b, 0, b.length);
                scaledThumb = Bitmap.createScaledBitmap(thumb, 120, 120, false);
            }
            mediaObject.setThumbImage(scaledThumb);
            mediaObject.actionUrl = card_url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaObject;
    }

    private void showCancelBtnConfirm() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("确认放弃当前编辑吗？");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "狠心关闭", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                doCancelBtn();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "继续分享", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    private void doCancelBtn() {
        Bundle bundle = new Bundle();
        EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.VOICE_PHOTO_SHARE_FINISH, bundle));
        this.finish();
    }

    private final String PHOTO_FILENAME = "photo_file"; // 录音文件名

    private File getPhotoFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + PHOTO_FILENAME + ".jpg");
        return file;
    }
}
