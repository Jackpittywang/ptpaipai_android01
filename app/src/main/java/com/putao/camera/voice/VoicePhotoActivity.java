
package com.putao.camera.voice;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.putao.camera.R;
import com.putao.camera.album.AlbumProcessDialog;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.voice.util.AudioRecorder;

public class VoicePhotoActivity extends BaseActivity implements View.OnClickListener {
    private Button choice_photo_btn, take_photo_btn, voice_record_btn, save_btn, back_btn;
    private ImageView voice_play_state_iv, voice_photo_iv;
    private Dialog mRecordDialog;
    private static final int MAX_RECORD_TIME = 10; // 最长录制时间，单位秒，0为无时间限制
    private static final int MIN_RECORD_TIME = 1; // 最短录制时间，单位秒，0为无时间限制
    private int recordState = 0; // 录音状态
    private float recodeTime = 0.0f; // 录音时长
    private double voiceValue = 0.0; // 录音的音量值
    private boolean playState = false; // 录音的播放状态
    private boolean moveState = false; // 手指是否移动
    private MediaPlayer mMediaPlayer;
    private float downY;
    private ProgressBar record_voice_left_time_pb;
    private TextView mTvRecordDialogTxt;
    private ImageView mIvRecVolume;
    private static final int RECORD_OFF = 0; // 不在录音
    private static final int RECORD_ON = 1; // 正在录音
    private AudioRecorder mAudioRecorder;
    private final String RECORD_FILENAME = "record_file"; // 录音文件名
    private final String PHOTO_FILENAME = "photo_file"; // 录音文件名
    private Thread mRecordThread;
    private static final int VOICE_PROCESS_STATE_FIRST_INIT = 0;
    private static final int VOICE_PROCESS_STATE_CHOICE_PHOTO_INIT = 1;
    private static final int VOICE_PROCESS_STATE_FIRST_TOCUH_DOWN = 2;
    private static final int VOICE_PROCESS_STATE_RECORD_EIDTOR = 3;
    private RelativeLayout void_photo_rl;

    // private static final int VOICE_PROCRESS_STATE_OPEN = 2;
    // private static final int VOICE_PROCRESS_STATE_OPEN = 3;
    // private static final int VOICE_PROCRESS_STATE_OPEN = 4;
    @Override
    public int doGetContentViewId() {
        // TODO Auto-generated method stub
        return R.layout.activity_voice_photo;
    }

    @Override
    public void doInitSubViews(View view) {
        // TODO Auto-generated method stub
        choice_photo_btn = (Button) this.findViewById(R.id.choice_photo_btn);
        take_photo_btn = (Button) this.findViewById(R.id.take_photo_btn);
        voice_photo_iv = (ImageView) this.findViewById(R.id.voice_photo_iv);
        voice_play_state_iv = (ImageView) this.findViewById(R.id.voice_play_state_iv);
        voice_record_btn = (Button) this.findViewById(R.id.voice_record_btn);
        save_btn = (Button) this.findViewById(R.id.save_btn);
        back_btn = (Button) this.findViewById(R.id.back_btn);
        void_photo_rl = (RelativeLayout) this.findViewById(R.id.void_photo_rl);
        voice_record_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 按下按钮
                        if (recordState != RECORD_ON) {
                            doProcessState(VOICE_PROCESS_STATE_FIRST_TOCUH_DOWN);
                            downY = event.getY();
                            deleteOldFile();
                            mAudioRecorder = new AudioRecorder(RECORD_FILENAME);
                            recordState = RECORD_ON;
                            try {
                                mAudioRecorder.start();
                                recordTimethread();
                                showVoiceDialog(0);
                                voice_record_btn.setText("松开结束");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE: // 滑动手指
                        if (recordState == RECORD_ON) {
                            float moveY = event.getY();
                            if (moveY - downY > 50) {
                                moveState = true;
                                showVoiceDialog(1);
                            }
                            if (moveY - downY < 20) {
                                moveState = false;
                                showVoiceDialog(0);
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: // 松开手指
                        if (recordState == RECORD_ON) {
                            recordState = RECORD_OFF;
                            if (mRecordDialog.isShowing()) {
                                mRecordDialog.dismiss();
                            }
                            try {
                                mAudioRecorder.stop();
                                mRecordThread.interrupt();
                                voiceValue = 0.0;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (!moveState) {
                                if (recodeTime < MIN_RECORD_TIME) {
                                    showToast("时间太短  录音失败");
                                } else {
                                    doProcessState(VOICE_PROCESS_STATE_RECORD_EIDTOR);
                                    // mTvRecordTxt.setText("录音时间：" + ((int)
                                    // recodeTime));
                                    //
                                    // mTvRecordPath.setText("文件路径：" +
                                    // getAmrPath());
                                }
                            }
                            voice_record_btn.setText("按住说话");
                            moveState = false;
                        } else {
                            //录音超过最长时间后重置
                            doProcessState(VOICE_PROCESS_STATE_RECORD_EIDTOR);
                            voice_record_btn.setText("按住说话");
                            moveState = false;
                        }
                        break;
                }
                return false;
            }
        });
        addOnClickListener(choice_photo_btn, take_photo_btn, voice_record_btn, save_btn, voice_play_state_iv, back_btn);
    }

    @Override
    public void doInitData() {
        // TODO Auto-generated method stub
        EventBus.getEventBus().register(this);
        deleteOldFile();
    }

    // 删除老文件
    void deleteOldFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
        if (file.exists()) {
            file.delete();
        }
    }

    // 获取文件手机路径
    private String getAmrPath() {
        File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
        return file.getAbsolutePath();
    }

    private File getPhotoFile() {
        File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + PHOTO_FILENAME + ".jpg");
        return file;
    }

    // 获取文件手机路径
    private File getAmr() {
        File file = new File(Environment.getExternalStorageDirectory(), "WifiChat/voiceRecord/" + RECORD_FILENAME + ".amr");
        if (file.exists()) {
            return file;
        }
        return null;
    }

    // 录音计时线程
    void recordTimethread() {
        mRecordThread = new Thread(recordThread);
        mRecordThread.start();
    }

    // 录音Dialog图片随声音大小切换
    void setDialogImage() {
        record_voice_left_time_pb.setProgress((int) (recodeTime * 10));
        if (voiceValue < 600.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume01);
        } else if (voiceValue > 600.0 && voiceValue < 1000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume02);
        } else if (voiceValue > 1000.0 && voiceValue < 1200.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume03);
        } else if (voiceValue > 1200.0 && voiceValue < 1400.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume04);
        } else if (voiceValue > 1400.0 && voiceValue < 1600.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume05);
        } else if (voiceValue > 1600.0 && voiceValue < 1800.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume06);
        } else if (voiceValue > 1800.0 && voiceValue < 2000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume07);
        } else if (voiceValue > 2000.0 && voiceValue < 3000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume08);
        } else if (voiceValue > 3000.0 && voiceValue < 4000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume09);
        } else if (voiceValue > 4000.0 && voiceValue < 6000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume10);
        } else if (voiceValue > 6000.0 && voiceValue < 8000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume11);
        } else if (voiceValue > 8000.0 && voiceValue < 10000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume12);
        } else if (voiceValue > 10000.0 && voiceValue < 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume13);
        } else if (voiceValue > 12000.0) {
            mIvRecVolume.setImageResource(R.drawable.voice_tips_volume14);
        }
    }

    public Handler recordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            setDialogImage();
        }
    };
    // 录音线程
    private Runnable recordThread = new Runnable() {
        @Override
        public void run() {
            recodeTime = 0.0f;
            while (recordState == RECORD_ON) {
                // 限制录音时长
                if (recodeTime >= MAX_RECORD_TIME && MAX_RECORD_TIME != 0) {
                    recordState = RECORD_OFF;
                    if (mRecordDialog.isShowing()) {
                        mRecordDialog.dismiss();
                    }
                    try {
                        mAudioRecorder.stop();
                        mRecordThread.interrupt();
                        voiceValue = 0.0;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(150);
                        recodeTime += 0.15;
                        // 获取音量，更新dialog
                        if (!moveState) {
                            voiceValue = mAudioRecorder.getAmplitude();
                            recordHandler.sendEmptyMessage(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    // 录音时显示Dialog
    void showVoiceDialog(int flag) {
        if (mRecordDialog == null) {
            mRecordDialog = new Dialog(this, R.style.dialog_style);
            mRecordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mRecordDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecordDialog.setContentView(R.layout.layout_record_dialog);
            mIvRecVolume = (ImageView) mRecordDialog.findViewById(R.id.record_dialog_img);
            mTvRecordDialogTxt = (TextView) mRecordDialog.findViewById(R.id.record_dialog_txt);
            record_voice_left_time_pb = (ProgressBar) mRecordDialog.findViewById(R.id.record_voice_left_time_pb);
        }
        switch (flag) {
            case 1:
                mIvRecVolume.setImageResource(R.drawable.voice_button_stop);
                mTvRecordDialogTxt.setText("松开手指可取消录音");
                break;
            default:
                mIvRecVolume.setImageResource(R.drawable.voice_tips_volume01);
                mTvRecordDialogTxt.setText("向下滑动可取消录音");
                break;
        }
        mTvRecordDialogTxt.setTextSize(14);
        mRecordDialog.show();
    }

    public void playRecord() {
        if (!playState) {
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setDataSource(getAmrPath());
                mMediaPlayer.prepare();
                // 正在播放
                voice_play_state_iv.setImageResource(R.drawable.voice_button_stop);
                playState = true;
                mMediaPlayer.start();
                // 设置播放结束时监听
                mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (playState) {
                            // mBtnPlayRecord.setText("播放声音");
                            voice_play_state_iv.setImageResource(R.drawable.voice_button_play);
                            playState = false;
                        }
                    }
                });
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                playState = false;
            } else {
                playState = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choice_photo_btn:
                ActivityHelper.startActivity(this, VoiceCategoryPhotoListActivity.class);
                break;
            case R.id.take_photo_btn:
                ActivityHelper.startActivity(this, VoiceCameraActivity.class);
                break;
            case R.id.voice_record_btn:
                break;
            case R.id.voice_play_state_iv:
                playRecord();
                break;
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.save_btn:
                //                showSaveBtnConfirm();
                doSaveBtn();
                break;
        }
    }

    private void doSaveBtn() {
        final ProgressDialog dialog = new AlbumProcessDialog(mContext, "加载中...").Get();
        dialog.show();
        copyPhoto();
        CacheRequest.ICacheRequestCallBack aCacheRequestCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                if (json.has("card_url")) {
                    try {
                        String card_url = json.getString("card_url");
                        Bundle bundle = new Bundle();
                        bundle.putString("card_url", card_url);
                        bundle.putString("local_photo_path", getPhotoFile().getPath());
                        ActivityHelper.startActivity(VoicePhotoActivity.this, VoicePhotoShareActivity.class, bundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                dialog.dismiss();
            }
        };
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put("picture", getPhotoFile());
            File amrFile = getAmr();
            if (amrFile != null) {
                requestParams.put("audio", getAmr());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        CacheRequest request = new CacheRequest(PuTaoConstants.PAIPAI_SERVER_HOST_VOICE_UPLOAD, "/card/card/share/", requestParams,
                aCacheRequestCallback);
        request.startPostRequest();
    }

    private void showSaveBtnConfirm() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("提示");
        dialog.setMessage("确认贺卡编辑完成进行分享吗");
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
                doSaveBtn();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonId) {
            }
        });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.VOICE_PHOTO_CONTENT_PROVIDER_BACK: {
                Bundle bundle = event.bundle;
                if (bundle != null) {
                    PhotoInfo photoinfo = (PhotoInfo) bundle.getSerializable("PhotoInfo");
                    if (photoinfo != null) {
                        Bitmap originImageBitmap = rotateBitmap(photoinfo._DATA);
                        voice_photo_iv.setImageBitmap(originImageBitmap);
                        doProcessState(VOICE_PROCESS_STATE_CHOICE_PHOTO_INIT);
                    }
                }
            }
            break;
            case PuTaoConstants.VOICE_PHOTO_SHARE_FINISH:
                this.finish();
                break;
            case PuTaoConstants.VOICE_PHOTO_TAKE_PHOTO_BACK: {
                Bundle bundle = event.bundle;
                Bitmap originImageBitmap = (Bitmap) bundle.getParcelable("PhotoInfo");
                voice_photo_iv.setImageBitmap(originImageBitmap);
                doProcessState(VOICE_PROCESS_STATE_CHOICE_PHOTO_INIT);
            }
            break;
            default:
                break;
        }
    }

    private Bitmap rotateBitmap(String file_path) {
        Bitmap bitmap = BitmapHelper.getInstance().getBitmapFromPath(file_path);
        try {
            ExifInterface exif = new ExifInterface(file_path);
            int result = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
            int rotate = 0;
            switch (result) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    break;
            }
            Matrix matrix = new Matrix();
            if (rotate != 0) {
                matrix.postRotate(rotate);
            }
            if (bitmap.getWidth() > DisplayHelper.getScreenWidth()) {
                float scale = (float) DisplayHelper.getScreenWidth() / bitmap.getWidth();
                matrix.postScale(scale, scale);
            }
            Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            if (rotateBitmap != null) {
                bitmap = rotateBitmap;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void copyPhoto() {
        Bitmap bitmap = Bitmap.createBitmap(void_photo_rl.getWidth(), void_photo_rl.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        void_photo_rl.draw(canvas);
        byte[] bytes = BitmapHelper.Bitmap2Bytes(bitmap);
        saveImage(bytes);
    }

    private void saveImage(byte[] image) {
        File photo = getPhotoFile();
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(photo.getPath());
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            bos.write(image);
            bos.flush();
            fos.getFD().sync();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public String getVoicePhotoPath() {
    // return FileOperationHelper.getExternalFilePath()
    // + PuTaoConstants.PAIPAI_VOICE_PHOTO_FLODER_NAME;
    // }
    private void doProcessState(int recordState) {
        switch (recordState) {
            case VOICE_PROCESS_STATE_FIRST_INIT:
                voice_record_btn.setText("按住说话");
                voice_record_btn.setEnabled(true);
                choice_photo_btn.setVisibility(View.VISIBLE);
                take_photo_btn.setVisibility(View.VISIBLE);
                save_btn.setVisibility(View.INVISIBLE);
                break;
            case VOICE_PROCESS_STATE_CHOICE_PHOTO_INIT:
                voice_record_btn.setText("按住说话");
                voice_record_btn.setEnabled(true);
                choice_photo_btn.setText("重新挑一张");
                take_photo_btn.setText("重新拍一张");
                choice_photo_btn.setVisibility(View.VISIBLE);
                take_photo_btn.setVisibility(View.VISIBLE);
                save_btn.setVisibility(View.VISIBLE);
                break;
            case VOICE_PROCESS_STATE_RECORD_EIDTOR:
                choice_photo_btn.setVisibility(View.INVISIBLE);
                take_photo_btn.setVisibility(View.INVISIBLE);
                save_btn.setVisibility(View.VISIBLE);
                voice_play_state_iv.setVisibility(View.VISIBLE);
                break;
            case VOICE_PROCESS_STATE_FIRST_TOCUH_DOWN:
                choice_photo_btn.setVisibility(View.INVISIBLE);
                take_photo_btn.setVisibility(View.INVISIBLE);
                voice_play_state_iv.setVisibility(View.INVISIBLE);
                // save_btn.setVisibility(View.VISIBLE);
                // voice_play_state_iv.setVisibility(View.VISIBLE);
                break;
        }
    }
}
