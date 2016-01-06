
package com.putao.camera.thirdshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.thirdshare.dialog.ThirdShareDialog;
import com.putao.camera.util.ToasterHelper;
import com.putao.camera.voice.VoicePhotoShareActivity;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

public class WeiboEntryActivity extends Activity implements IWeiboHandler.Response {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (ThirdShareDialog.mWeiboShareAPI != null) {
            ThirdShareDialog.mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        if (VoicePhotoShareActivity.mWeiboShareAPI != null) {
            VoicePhotoShareActivity.mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        if (PhotoShareActivity.mShareTools != null) {
            PhotoShareActivity.mShareTools.mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
    }

    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ToasterHelper.showShort(this, "分享成功", ToasterHelper.IMG_INFO);
                //                B5MThirdShareHelper.getInstance(this).doPopupViewDismiss();
                //                ICallBack callBack = B5MThirdShareHelper.getInstance(this).getCallBack();
                //                if (callBack != null)
                //                {
                //                    callBack.onCallBack();
                //                }
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToasterHelper.showShort(this, "取消分享", ToasterHelper.IMG_INFO);
                //                B5MThirdShareHelper.getInstance(this).doPopupViewDismiss();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                //                B5MToaster.showShort(this, baseResp.errMsg, B5MToaster.IMG_INFO);
                ToasterHelper.showShort(this, baseResp.errMsg, ToasterHelper.IMG_INFO);
                break;
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ThirdShareDialog.mWeiboShareAPI.handleWeiboResponse(intent, this);
    }
}
