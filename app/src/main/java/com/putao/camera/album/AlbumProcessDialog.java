
package com.putao.camera.album;

import android.app.ProgressDialog;
import android.content.Context;

public class AlbumProcessDialog {
    private ProgressDialog mDialog;
    private Context mcContext;
    private String mMessage;

    public AlbumProcessDialog(Context context, String message) {
        mcContext = context;
        mMessage = message;
    }

    public ProgressDialog Get() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mcContext);
            mDialog.setMessage(mMessage);
        }
        return mDialog;
    }
}
