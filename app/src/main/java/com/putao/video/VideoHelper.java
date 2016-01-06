package com.putao.video;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.putao.camera.R;

/**
 * 视频助手
 * Created by guchenkai on 2016/1/6.
 */
public class VideoHelper {
    private static VideoHelper mInstance;
    private FFmpeg mFFmpeg;

    private VideoHelper(final Context context) {
        try {
            if (mFFmpeg == null)
                mFFmpeg = FFmpeg.getInstance(context);
            mFFmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
                    showUnsupportedExceptionDialog((Activity) context);
                }
            });
        } catch (FFmpegNotSupportedException e) {
            showUnsupportedExceptionDialog((Activity) context);
        }
    }

    /**
     * ffmpeg异常警告弹窗
     *
     * @param context Activity
     */
    private void showUnsupportedExceptionDialog(final Activity context) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(context.getString(R.string.device_not_supported))
                .setMessage(context.getString(R.string.device_not_supported_message))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.finish();
                    }
                })
                .create()
                .show();
    }

    /**
     * 单例VideoHelper实例
     *
     * @param context context
     * @return VideoHelper实例
     */
    public static VideoHelper getInstance(Context context) {
        return new VideoHelper(context);
    }

    /**
     * 执行FFmpeg命令
     *
     * @param command 命令
     */
    public void exectueFFmpegCommand(String[] command, FFmpegExecuteResponseHandler handler) {
        try {
            mFFmpeg.execute(command, handler);
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }
}
