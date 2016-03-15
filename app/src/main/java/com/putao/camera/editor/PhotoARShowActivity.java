package com.putao.camera.editor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.ToasterHelper;
import com.putao.video.VideoHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;

public class PhotoARShowActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = PhotoARShowActivity.class.getName();
    private Button backBtn, saveBtn;
    private ImageView show_image;
    private Bitmap originImageBitmap;
    private String imagePath = "";
    private int faceCenterX = 0;
    private int faceCenterY = 0;
    private float faceScale = 1;
    private float faceAngle = 0;
    private int mouthX = 0;
    private int mouthY = 0;
    private String animationName = "";
    private String videoImagePath = "";
    // 保存视频时候图片的张数
    private int imageCount = 36;

    private AnimationImageView animation_view;

    private ProgressDialog progressDialog;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo_ar_show;
    }

    @Override
    public void doInitSubViews(View view) {
        backBtn = queryViewById(R.id.back_btn);
        saveBtn = queryViewById(R.id.btn_save);
        show_image = queryViewById(R.id.show_image);
        animation_view = queryViewById(R.id.animation_view);
        animation_view.setImageFolder(FileUtils.getARStickersPath());
        addOnClickListener(saveBtn, backBtn);
        EventBus.getEventBus().register(this);
    }

    @Override
    public void doInitData() {
        Intent intent = this.getIntent();
        if (intent == null) return;
        imagePath = intent.getStringExtra("imagePath");
        faceCenterX = intent.getIntExtra("faceCenterX", 0);
        faceCenterY = intent.getIntExtra("faceCenterY", 0);
        mouthX = intent.getIntExtra("mouthX", 0);
        mouthY = intent.getIntExtra("mouthY", 0);
        faceScale = intent.getFloatExtra("faceScale", 1);
        faceAngle = intent.getFloatExtra("faceAngle", 0);

        animationName = intent.getStringExtra("animationName");

        if (StringHelper.isEmpty(imagePath)) return;
        try {

            // 把图片缩放成屏幕的大小1:1，方便视频合成的时候调用
            Bitmap bgImageBitmap = originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(imagePath, DisplayHelper.getScreenWidth(),
                    DisplayHelper.getScreenHeight());
            // 背景脸图片在屏幕上的缩放
            int screenW = DisplayHelper.getScreenWidth();
            int screenH = DisplayHelper.getScreenHeight();
            float imageScaleW = (float) screenW / (float) originImageBitmap.getWidth();
            float imageScaleH = (float) screenH / (float) originImageBitmap.getHeight();
            float imageScale = imageScaleW;
            if (imageScaleH < imageScaleW) imageScale = imageScaleH;

            int bgImageOffsetX = (int) (DisplayHelper.getScreenWidth() - imageScale * originImageBitmap.getWidth()) / 2;
            int bgImageOffsetY = (int) (DisplayHelper.getScreenHeight() - imageScale * originImageBitmap.getHeight()) / 2;
            originImageBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
            Bitmap resizedBgImage = BitmapHelper.resizeBitmap(bgImageBitmap, imageScale);
            originImageBitmap = BitmapHelper.combineBitmap(originImageBitmap, resizedBgImage, bgImageOffsetX, bgImageOffsetY);

            animation_view.setData(animationName, false);

            show_image.setImageBitmap(originImageBitmap);

            float animationScale = imageScale * faceScale;
            // 图片居中显示后，图片的位置会发生变化
            faceCenterX = bgImageOffsetX + (int) (imageScale * faceCenterX);
            faceCenterY = bgImageOffsetY + (int) (imageScale * faceCenterY);

//            YMDetector
//                    mDetector = new YMDetector(this, YMDetector.Config.FACE_270, YMDetector.Config.RESIZE_WIDTH_640);
//            YMFace face = mDetector.onDetector(originImageBitmap);
//            if (face != null) {
//                float[] landmarks = face.getLandmarks();
//                mouthX = (int) ((landmarks[10 * 2] + landmarks[10 * 2]) / 2);
//                mouthY = (int) (landmarks[10 * 2 + 1] + landmarks[10 * 2 + 1]) / 2;
//            }
            animation_view.setPositionAndStartAnimation(faceCenterX, faceCenterY, animationScale, faceAngle, mouthX, mouthY);
            bgImageBitmap.recycle();
            resizedBgImage.recycle();

        } catch (Exception e) {

        }

    }

//    // 此方法因为获取不到脸的角度，未使用
//    private void getFaceFromBitmap(Bitmap bitmap) {
//        // 检测脸
//        FaceDetector fd;
//        FaceDetector.Face[] faces = new FaceDetector.Face[2];
//        int count = 0;
//        try {
//            fd = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 2);
//            count = fd.findFaces(bitmap, faces);
//            if (count < 1) return;
//            animation_view.setPositionAndStartAnimation(faces[0], bitmap.getWidth(), bitmap.getHeight(), DisplayHelper.getScreenWidth(), DisplayHelper.getScreenHeight());
//
//        } catch (Exception e) {
//            Log.e(TAG, "find face error");
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animation_view.clearData();
        animation_view = null;
        EventBus.getEventBus().unregister(this);
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE:
                imagesToVideo();
                break;
        }
    }


    public void save() {

        videoImagePath = Environment.getExternalStorageDirectory() + File.separator + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "/temp/";
        File file = new File(videoImagePath);
        if (file.exists() == false) file.mkdir();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存视频请稍后...");
        progressDialog.show();

        animation_view.setSave(originImageBitmap, videoImagePath, imageCount);

//        // 先处理成图片存到sd卡
//        AnimationModel animationModel = animation_view.getAnimationModel();
//        final BitmapFactory.Options option = new BitmapFactory.Options();
//        option.inScaled = false;
//        for (int i = 0; i < animationModel.getAnimationImageSize(); i++) {
//            Bitmap frameBitmap = originImageBitmap.copy(originImageBitmap.getConfig(), true);
//            if (animationModel.getEye() != null) {
//                Bitmap stickBitmap = BitmapHelper.getBitmapFromPath(animation_view.getImageFolder() + animationName + File.separator + animationModel.getEye().getImageList().get(i), option);
//                BitmapHelper.combineBitmap(frameBitmap, stickBitmap);
//            }
//            if (animationModel.getMouth() != null) {
//                Bitmap stickBitmap = BitmapHelper.getBitmapFromPath(animation_view.getImageFolder() + animationName + File.separator + animationModel.getMouth().getImageList().get(i), option);
//                BitmapHelper.combineBitmap(frameBitmap, stickBitmap);
//            }
//            if (animationModel.getBottom() != null) {
//                Bitmap stickBitmap = BitmapHelper.getBitmapFromPath(animation_view.getImageFolder() + animationName + File.separator + animationModel.getBottom().getImageList().get(i), option);
//                BitmapHelper.combineBitmap(frameBitmap, stickBitmap);
//            }
//            BitmapHelper.saveBitmap(frameBitmap, videoImagePath + "image" + i + ".jpg");
//
//        }


    }

    private void imagesToVideo() {
        // 保存视频
        String sizeStr = "480x360";
        // String videoFileName = "putao_" + System.currentTimeMillis() / 1000 + ".mp4";
        String videoPath = CommonUtils.getOutputVideoFile().getAbsolutePath(); //getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator + videoFileName;
        File videoFile = new File(videoPath);
        if (videoFile.exists()) videoFile.delete();
        final String command = "-f image2 -i " + videoImagePath + "image%02d.jpg"
                + " -vcodec mpeg4 -r " + imageCount + " -b 200k -s " + sizeStr + " " + videoPath;
        Log.i(TAG, "videPath is:" + videoPath);
        VideoHelper.getInstance(this).exectueFFmpegCommand(command.split(" "), new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                // Log.i(TAG, "处理失败：" + s);
                ToasterHelper.show(PhotoARShowActivity.this, "处理失败:" + s);
            }

            @Override
            public void onSuccess(String s) {
                ToasterHelper.show(PhotoARShowActivity.this, "视频成功保存到相册");
                // ToasterHelper.show(PhotoARShowActivity.this, "处理成功:" + s);
            }

            @Override
            public void onProgress(String s) {
                Log.d(TAG, "progress : " + s);
            }

            @Override
            public void onStart() {
                Log.d(TAG, "Started command : ffmpeg " + command);
            }

            @Override
            public void onFinish() {
                // ToasterHelper.show(PhotoARShowActivity.this, "处理完成");
                progressDialog.hide();
                clearImageList();
            }
        });
    }


    /**
     * 清除生成视频用的临时文件
     */
    private void clearImageList() {
        File folder = new File(videoImagePath);
        File[] childFile = folder.listFiles();
        for (int i = 0; i < childFile.length; i++) {
            try {
                File file = childFile[i];
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap createBitmapWithAR() {

        Bitmap new_bitmap = null;
        return new_bitmap;
//        new_bitmap = Bitmap.createBitmap(photo_area_rl.getWidth(), photo_area_rl.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(new_bitmap);
//        photo_area_rl.draw(canvas);
//        Paint mPaint = new Paint();
//        mPaint.setAntiAlias(true);
//        mPaint.setFilterBitmap(true);
//        int area_width = photo_area_rl.getWidth();
//        int area_height = photo_area_rl.getHeight();
//        int actual_width = 0, actual_height = 0;
//        if (corpOriginImageBitmap != null) {
//            originImageBitmap = corpOriginImageBitmap;
//        }
//        if (originImageBitmap.getHeight() < area_height && originImageBitmap.getWidth() < area_width) {
//            actual_width = originImageBitmap.getWidth();
//            actual_height = originImageBitmap.getHeight();
//        } else {
//            float origin_ratio = (float) originImageBitmap.getWidth() / originImageBitmap.getHeight();
//            float current_ratio = (float) area_width / area_height;
//            if (origin_ratio >= current_ratio) {
//                actual_width = area_width;
//                actual_height = (int) (originImageBitmap.getHeight() * ((float) actual_width / originImageBitmap.getWidth()));
//            } else {
//                actual_height = area_height;
//                actual_width = (int) (originImageBitmap.getWidth() * ((float) actual_height) / originImageBitmap.getHeight());
//            }
//        }
//        int cut_x = (area_width - actual_width) / 2;
//        int cut_y = (area_height - actual_height) / 2;
//        return Bitmap.createBitmap(new_bitmap, cut_x, cut_y, actual_width, actual_height);
    }


    @Override
    public void onBackPressed() {
        showQuitTip();
    }

    void showQuitTip() {

        finish();
//        new AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认放弃当前编辑吗？").setPositiveButton("是", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                finish();
//            }
//        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn: {
                showQuitTip();
            }
            break;
            case R.id.btn_save:
                save();
                break;
            default:
                break;
        }
    }


}
