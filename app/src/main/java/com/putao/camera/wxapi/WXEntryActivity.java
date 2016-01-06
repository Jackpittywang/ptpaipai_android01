
package com.putao.camera.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.thirdshare.dialog.ThirdShareDialog;
import com.putao.camera.util.ToasterHelper;
import com.putao.camera.voice.VoicePhotoShareActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (ThirdShareDialog.mWXAPIFactory != null) {
            ThirdShareDialog.mWXAPIFactory.handleIntent(getIntent(), this);
        }
        if (VoicePhotoShareActivity.mWXAPIFactory != null) {
            VoicePhotoShareActivity.mWXAPIFactory.handleIntent(getIntent(), this);
        }
        if (PhotoShareActivity.mShareTools != null) {
            PhotoShareActivity.mShareTools.mWXAPIFactory.handleIntent(getIntent(), this);
        }

    }

    @Override
    public void onReq(BaseReq arg0) {
        // TODO Auto-generated method stub
    }

    public void onResp(BaseResp resp) {
        //          
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                ToasterHelper.showShort(this, "分享成功", ToasterHelper.IMG_INFO);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                ToasterHelper.showShort(this, "取消分享", ToasterHelper.IMG_INFO);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
        finish();
    }
}