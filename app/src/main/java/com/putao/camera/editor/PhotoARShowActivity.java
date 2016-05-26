package com.putao.camera.editor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.google.android.gms.common.api.GoogleApiClient;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.BeautifyInfo;
import com.putao.camera.camera.filter.CustomerFilter;
import com.putao.camera.camera.gpuimage.GPUImage;
import com.putao.camera.camera.model.FaceModel;
import com.putao.camera.camera.utils.RecorderManager;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.BitmapToVideoUtil;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.NetManager;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.putao.camera.util.ToasterHelper;
import com.putao.video.VideoHelper;
import com.sunnybear.library.util.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;

public class PhotoARShowActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = PhotoARShowActivity.class.getName();
    private Button backBtn, saveBtn;
    private ImageView show_image;
    private Bitmap originImageBitmap;
    private String imagePath = "";
    private String animationName = "";
    private String videoImagePath = "";
    private CustomerFilter.FilterType filterName = CustomerFilter.FilterType.NONE;
    private String PATH = "Android/data/com.putao.camera/files/";
    // 保存视频时候图片的张数
    private int imageCount = 36;

    private AnimationImageView animation_view;
    private int photoType;
    private Bitmap saveOriginImageBitmap;

    private ProgressDialog progressDialog = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private float[] landmarks;

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
        photoType = SharedPreferencesHelper.readIntValue(this, PuTaoConstants.CUT_TYPE, 0);
    }

    Bitmap bgImageBitmap;

    @Override
    public void doInitData() {
        Intent intent = this.getIntent();
        if (intent == null) return;
        imagePath = intent.getStringExtra("imagePath");
        filterName = (CustomerFilter.FilterType) intent.getSerializableExtra("filterName");
        animationName = intent.getStringExtra("animationName");
        if (StringHelper.isEmpty(imagePath)) return;
        GPUImage mGPUImage = new GPUImage(mContext);
        try {

            // 把图片缩放成屏幕的大小1:1，方便视频合成的时候调用
            Bitmap tempBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(imagePath, DisplayHelper.getScreenWidth(), DisplayHelper.getScreenHeight());

            bgImageBitmap = originImageBitmap = BitmapHelper.resizeBitmap(tempBitmap, 0.5f);
//            tempBitmap.recycle();

            CustomerFilter filter = new CustomerFilter();
            mGPUImage.setFilter(filter.getFilterByType(filterName,mContext));
//            mGPUImage.saveToPictures(originImageBitmap,  this.getApplicationContext().getFilesDir().getAbsolutePath() + File.separator, "temp.jpg",
            mGPUImage.saveToPictures(bgImageBitmap, FileUtils.getARStickersPath()+ File.separator, "temp.jpg",
                    new GPUImage.OnPictureSavedListener() {
                        @Override
                        public void onPictureSaved(final String path) {
                            try {
                                Bitmap bitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(path, DisplayHelper.getScreenWidth(),
                                        DisplayHelper.getScreenHeight());
//                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri);
                                originImageBitmap = bitmap;
                                show_image.setImageBitmap(originImageBitmap);
                                showPreview(originImageBitmap);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });


//            bgImageBitmap=BitmapHelper.imageCrop(bgImageBitmap,photoType);

            // Bitmap bgImageBitmap = originImageBitmap = BitmapHelper.getBitmapFromPath(imagePath, null);
           /* Log.i(TAG, "image path is:" + imagePath);
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

            //检测人脸
            final YMDetector YMDetector = new YMDetector(this);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<YMFace> faces = YMDetector.onDetector(originImageBitmap);
                    if (faces != null && faces.size() > 0) {
                        YMFace face = faces.get(0);
                        landmarks = face.getLandmarks();
                        if (landmarks != null && landmarks.length > 0) {
                            mHandle.sendEmptyMessage(123);
                        }
                    }
                }
            }).start();*/
//            bgImageBitmap.recycle();
//            resizedBgImage.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showPreview(Bitmap newOriginImageBitmap) {
        saveOriginImageBitmap = newOriginImageBitmap;
        int screenW = DisplayHelper.getScreenWidth();
        int screenH = DisplayHelper.getScreenHeight();
        float imageScaleW = (float) screenW / (float) newOriginImageBitmap.getWidth();
        float imageScaleH = (float) screenH / (float) newOriginImageBitmap.getHeight();
        float imageScale = imageScaleW;
        if (imageScaleH < imageScaleW) imageScale = imageScaleH;

        int bgImageOffsetX = (int) (DisplayHelper.getScreenWidth() - imageScale * newOriginImageBitmap.getWidth()) / 2;
        int bgImageOffsetY = (int) (DisplayHelper.getScreenHeight() - imageScale * newOriginImageBitmap.getHeight()) / 2;
        newOriginImageBitmap = Bitmap.createBitmap(screenW, screenH, Bitmap.Config.ARGB_8888);
        Bitmap resizedBgImage = BitmapHelper.resizeBitmap(bgImageBitmap, imageScale);
        newOriginImageBitmap = BitmapHelper.combineBitmap(newOriginImageBitmap, resizedBgImage, bgImageOffsetX, bgImageOffsetY);
        animation_view.setData(animationName, false);

//        show_image.setImageBitmap(newOriginImageBitmap);
//        show_image.setImageBitmap(newOriginImageBitmap);

        //检测人脸
        final YMDetector YMDetector = new YMDetector(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<YMFace> faces = YMDetector.onDetector(saveOriginImageBitmap);
                if (faces != null && faces.size() > 0) {
                    YMFace face = faces.get(0);
                    landmarks = face.getLandmarks();
                    if (landmarks != null && landmarks.length > 0) {
                        mHandle.sendEmptyMessage(123);
                    }
                }
            }
        }).start();

        bgImageBitmap.recycle();
        resizedBgImage.recycle();


    }

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 123) {
                if (animation_view == null) return;
                animation_view.setPositionAndStartAnimation(landmarks);
            }
        }
    };

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

    private int Viedheight;

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE:
                int with = event.bundle.getInt("backgroundWith");
                int height = event.bundle.getInt("backgroundHight");
                Viedheight = height * 480 / with;
                saveASVideo(originImageBitmap);
//                saveASVideo(saveOriginImageBitmap);
                break;
        }
    }


    public void save() {
        getMassege();

        videoImagePath = Environment.getExternalStorageDirectory() + File.separator + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "/.temp/";
        clearImageList();
        initSharedPreferencesHelper();
        File file = new File(videoImagePath);
        if (file.exists() == false) file.mkdir();
        saveDialog = new ProgressDialog(this);
        saveDialog.setMessage("正在保存视频请稍后...");
        saveDialog.setCancelable(false);
        saveDialog.show();
        Bitmap tip = BitmapHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.tips, 220, 60);
        saveOriginImageBitmap = BitmapHelper.combineBitmap(saveOriginImageBitmap, tip, saveOriginImageBitmap.getWidth() - tip.getWidth() - 5, saveOriginImageBitmap.getHeight() - tip.getHeight() - 2);
//        BitmapHelper.saveBitmap(saveOriginImageBitmap,imagePath);
        animation_view.setSave(saveOriginImageBitmap, videoImagePath, imageCount);
//        imagesToVideo();

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
        String sizeStr = "480x" + Viedheight;

//         String videoFileName = "VID_" + System.currentTimeMillis() / 1000 + ".mp4";
        String model = android.os.Build.MODEL.toLowerCase();
        String brand = Build.BRAND.toLowerCase();
        final String videoPath;
        if (model.contains("meizu") || brand.contains("meizu") || model.contains("mx5") || model.contains("mx4")) {
            videoPath = CommonUtils.getOutputVideoFileMX().getAbsolutePath();
        } else {
            videoPath = CommonUtils.getOutputVideoFile().getAbsolutePath();
        }
//      final  String   videoPath = CommonUtils.getOutputVideoFile().getAbsolutePath(); //getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator + videoFileName;
        System.out.print(videoPath);
//        String videoPath =getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() + File.separator + videoFileName;
        File videoFile = new File(videoPath);
        if (videoFile.exists()) videoFile.delete();
        final String command = "-f image2 -i " + videoImagePath + "image%02d.jpg"
                + " -vcodec mpeg4 -r " + 12 + " -b 200k -s " + sizeStr + " " + videoPath;
//        Log.i(TAG, "videPath is:" + videoPath);
        VideoHelper.getInstance(this).exectueFFmpegCommand(command.split(" "), new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                // Log.i(TAG, "处理失败：" + s);
                ToasterHelper.show(PhotoARShowActivity.this, "处理失败:" + s);
            }

            @Override
            public void onSuccess(String s) {
                ToasterHelper.show(PhotoARShowActivity.this, "视频成功保存到相册");
                MediaScannerConnection.scanFile(PhotoARShowActivity.this, new String[]{videoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Bundle bundle = new Bundle();
                        bundle.putString("savefile", videoPath);
                        bundle.putString("imgpath", videoImagePath + "image00.jpg");
                        bundle.putString("from", "dynamic");
                        ActivityHelper.startActivity(PhotoARShowActivity.this, PhotoShareActivity.class, bundle);
                        finish();
                    }
                });
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
                saveDialog.hide();
                saveDialog = null;
//                clearImageList();
            }
        });

    }


    /**
     * 清除生成视频用的临时文件
     */
    private void clearImageList() {
        File folder = new File(videoImagePath);
        File[] childFile = folder.listFiles();
        if (childFile == null) return;
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
        if (saveDialog != null) {
            saveDialog.hide();
            saveDialog = null;
        }
       /* if(!TextUtils.isEmpty(imagePath)){
            File image=new File(imagePath);
            image.delete();
        }*/
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


    /**
     * 保存有动态贴纸的视频
     *
     * @param bitmap
     */
    private ProgressDialog saveDialog;
    private boolean videoSaving = false;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private String videoPath;

    private void saveASVideo(final Bitmap scaleImageBmp) {
        videoSaving = true;
        final YMDetector detector = new YMDetector(mContext);
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    FaceModel faceModel;
 /*                     BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    byte[] data = BitmapHelper.Bitmap2Bytes(bitmap);
                    Bitmap scaleImageBmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);*/
//                    List<YMFace> faces = detector.onDetector(scaleImageBmp);
                    List<YMFace> faces = detector.onDetector(scaleImageBmp);
                    if (faces != null && faces.size() > 0 && faces.get(0) != null) {
                        YMFace face = faces.get(0);
                        faceModel = new FaceModel();
                        faceModel.landmarks = face.getLandmarks();
                        faceModel.emotions = face.getEmotions();
                        RectF rect = new RectF((int) face.getRect()[0], (int) face.getRect()[1], (int) face.getRect()[2], (int) face.getRect()[3]);
                        faceModel.rectf = rect;
                    } else {
                        handler.sendEmptyMessage(0x201);
                        return;
                    }
                    String model = android.os.Build.MODEL.toLowerCase();
                    String brand = Build.BRAND.toLowerCase();
                    if (model.contains("meizu") || brand.contains("meizu") || model.contains("mx5") || model.contains("mx4")) {
                        videoPath = CommonUtils.getOutputVideoFileMX().getAbsolutePath();
                    } else {
                        videoPath = CommonUtils.getOutputVideoFile().getAbsolutePath();
                    }
                    RecorderManager recorderManager = new RecorderManager(3 * 1000, scaleImageBmp.getWidth(), scaleImageBmp.getHeight(), videoPath);
                    final List<byte[]> combineBmps = BitmapToVideoUtil.getCombineData(faceModel, animation_view.getAnimationModel(), scaleImageBmp, animation_view.getEyesBitmapArr(), animation_view.getMouthBitmapArr(), animation_view.getBottomBitmapArr());

                    MediaScannerConnection.scanFile(mContext, new String[]{videoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {

                        }
                    });
                    //停止预览页面动态贴纸的显示
                    recorderManager.combineVideo(combineBmps);
                    handler.sendEmptyMessage(0x200);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(0x201);
                }
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x200) {
                if (saveDialog != null && saveDialog.isShowing()) {
                    saveDialog.dismiss();
                }

                videoSaving = false;
                ToastUtils.showToastShort(mContext, "视频保存成功");

                Bundle bundle = new Bundle();
                bundle.putString("savefile", videoPath);
                bundle.putString("imgpath", videoImagePath + "image00.jpg");
                bundle.putString("from", "dynamic");
                ActivityHelper.startActivity(PhotoARShowActivity.this, PhotoShareActivity.class, bundle);
                if(!TextUtils.isEmpty(imagePath)){
                    File image=new File(imagePath);
                    image.delete();
                }
                finish();
            } else if (msg.what == 0x201) {
                if (saveDialog != null && saveDialog.isShowing()) {
                    saveDialog.dismiss();
                }
                if(!TextUtils.isEmpty(imagePath)){
                    File image=new File(imagePath);
                    image.delete();
                }
                videoSaving = false;
                ToastUtils.showToastShort(mContext, "视频保存失败");
            }
        }
    };

    LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                latitude = location.getLatitude(); // 经度
                longitude = location.getLongitude(); // 纬度
            }
        }
    };

    private double latitude = 0.0;
    private double longitude = 0.0;
    private Location location;
    private LocationManager locationManager;

    public void getMassege() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.getProvider(LocationManager.GPS_PROVIDER) != null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);//高精度
                criteria.setAltitudeRequired(false);//不要求海拔
                criteria.setBearingRequired(false);//不要求方位
                criteria.setCostAllowed(true);//允许有花费
                criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
                String provider = locationManager.getBestProvider(criteria, true);
                locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);
                Location location = locationManager.getLastKnownLocation(provider);
                latitude = location.getLatitude();//经度
                longitude = location.getLongitude();//纬度
            }
        } else {
            //无法定位：1、提示用户打开定位服务；2、跳转到设置界面
            Toast.makeText(this, "无法定位，请打开定位服务", Toast.LENGTH_SHORT).show();
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(i);
        }

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        List<Address> locationList = null;
        try {
            //27.7328340000,111.3072050000
//            locationList = gc.getFromLocation(27.7328340000, 111.3072050000, 1);
            locationList = gc.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = locationList.get(0);//得到Address实例
        String ss = address.toString();
        String countryName = address.getCountryName();//得到国家名称，比如：中国
        String locality = address.getLocality();//得到城市名称，比如：北京市
        String thoroughfare = address.getThoroughfare();
        String addressLine = address.getAddressLine(0);
        /*for (int i = 0; address.getAddressLine(i) != null; i++) {
            String addressLine = address.getAddressLine(i);//得到周边信息，包括街道等，i=0，得到街道名称
        }*/


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日    HH:mm:ss     ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        long shooting_time=  System.currentTimeMillis()/1000;
        if (!NetManager.isNetworkAvailable(PhotoARShowActivity.this)) {
            uploadMassege(latitude, longitude,shooting_time);
        }

    }

    public void uploadMassege(double latitude,double longitude ,long str) {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                ToastUtils.showToast(mContext,"111111",Toast.LENGTH_SHORT);
                String ss=  json.toString();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                ToastUtils.showToast(mContext,"0000",Toast.LENGTH_SHORT);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(requestString(latitude,longitude,str),
                map, mWaterMarkUpdateCallback);
        mCacheRequest.startPostRequest();
    }
    public void  initSharedPreferencesHelper(){
        SharedPreferencesHelper.saveStringValue(mContext, "dynamic", "");
        SharedPreferencesHelper.saveStringValue(mContext, "sticker", "");
        SharedPreferencesHelper.saveStringValue(mContext, "template", "");
        SharedPreferencesHelper.saveStringValue(mContext, "filtername", "None");

    }
    public String requestString(double latitude,double longitude,long str){
        String dynamic = SharedPreferencesHelper.readStringValue(mContext, "dynamic", "");
        String sticker = SharedPreferencesHelper.readStringValue(mContext, "sticker", "");
        String template = SharedPreferencesHelper.readStringValue(mContext, "template", "");
        String filtername = SharedPreferencesHelper.readStringValue(mContext,"filtername","None");
        BeautifyInfo beautifyInfo = new BeautifyInfo();
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("filtername", filtername);
        beautifyInfo.setAndroid(objectObjectHashMap);
        String beautifyInfoStr = JSON.toJSONString(beautifyInfo);

        return PuTaoConstants.PAIPAI_MATTER_LIST_MATERIAL + "?appid=" + MainApplication.app_id + "&lat=" + latitude + "&lng=" + longitude +
                "&dynameic=" + dynamic + "&sticker=" + sticker + "&template=" + template +
                "&beautify=" + beautifyInfoStr +
                "&shooting_time=" + str;
    }


}
