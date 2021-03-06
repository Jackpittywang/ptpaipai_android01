package com.putao.camera.editor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.BeautifyInfo;
import com.putao.camera.bean.DynamicCategoryInfo;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.camera.model.FaceModel;
import com.putao.camera.camera.utils.RecorderManager;
import com.putao.camera.camera.view.AnimationImageView;
import com.putao.camera.camera.view.IntentARImageView;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.downlad.DownloadFileService;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.management.DynamicListInfo;
import com.putao.camera.setting.watermark.management.DynamicPicAdapter;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.BitmapToVideoUtil;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.NetManager;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.ToasterHelper;
import com.putao.video.VideoHelper;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.controller.eventbus.Subcriber;
import com.sunnybear.library.util.ToastUtils;
import com.sunnybear.library.view.recycler.BasicRecyclerView;
import com.sunnybear.library.view.recycler.listener.OnItemClickListener;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.OnClick;
import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;


public class PhotoDynamicActivity extends BasicFragmentActivity implements View.OnClickListener {
    private LinearLayout left_btn_ll;
    private String TAG = PhotoARShowActivity.class.getName();
    private TextView tv_save;
    private ImageView show_image;
    private Bitmap originImageBitmap;
    private GridView mGridView;
    private String imagePath = "";
    private String animationName = "";
    private String videoImagePath = "";
    private String PATH = "Android/data/com.putao.camera/files/";
    // 保存视频时候图片的张数
    private int imageCount = 36;
    private float screenDensity = 1.0f;

    private AnimationImageView animation_view;
    private int photoType;

    private ProgressDialog progressDialog = null;
    private GoogleApiClient client;
    private float[] landmarks;
    private DynamicPicAdapter mDynamicPicAdapter;
    private BasicRecyclerView rv_articlesdetail_applyusers;
    private List<DynamicIconInfo> nativeList = null;
    private int Viedheight;
    private int currentSelectDynamic = 0;
    private boolean haveNoFace = false;
    private String photo_data;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_photo_editor_dynamic;
    }

    @Override
    protected void onViewCreatedFinish(Bundle saveInstanceState) {
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        doInitSubViews();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mDynamicPicAdapter = new DynamicPicAdapter(mContext, null);
        rv_articlesdetail_applyusers.setAdapter(mDynamicPicAdapter);
        rv_articlesdetail_applyusers.setLayoutManager(linearLayoutManager);
        rv_articlesdetail_applyusers.setOnItemClickListener(new OnItemClickListener<DynamicIconInfo>() {
            @Override
            public void onItemClick(DynamicIconInfo dynamicIconInfo, int position) {
                Map<String, String> map = new HashMap<String, String>();
                List<DynamicIconInfo> list = null;
                map.put("cover_pic", dynamicIconInfo.cover_pic);
                try {
                    list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (null != list && list.size() > 0) {
                    if (animation_view.isAnimationLoading()) {
                        ToasterHelper.showShort(PhotoDynamicActivity.this, "动画加载中请稍后", R.drawable.img_blur_bg);
                        return;
                    }
                    mDynamicPicAdapter.getItem(currentSelectDynamic).setSelect(false);
                    mDynamicPicAdapter.notifyItemChanged(currentSelectDynamic);

                    mDynamicPicAdapter.getItem(position).setSelect(true);
//                    dynamicIconInfo.setSelect(true);
                    mDynamicPicAdapter.notifyItemChanged(position);
                    Loger.d("click");
//                    ToasterHelper.showShort(PhotoDynamicActivity.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
                    SharedPreferencesHelper.saveStringValue(mContext,"dynamic",list.get(0).zipName);
                    animation_view.clearData();
                    animation_view.setData(list.get(0).zipName, false);
                    //检测人脸
                    final YMDetector YMDetector = new YMDetector(PhotoDynamicActivity.this);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<YMFace> faces = YMDetector.onDetector(originImageBitmap);
                            if (faces != null && faces.size() > 0) {
                                haveNoFace = false;
                                YMFace face = faces.get(0);
                                landmarks = face.getLandmarks();
                                if (landmarks != null && landmarks.length > 0) {
                                    mHandle.sendEmptyMessage(123);
                                }
                            } else {
                                haveNoFace = true;
                                ToasterHelper.showShort(PhotoDynamicActivity.this, "检测不到人脸,请换一张试试吧", R.drawable.img_blur_bg);
                            }
                        }
                    }).start();
                    currentSelectDynamic = position;
                } else {
                    dynamicIconInfo.setShowProgress(true);
                    mDynamicPicAdapter.notifyItemChanged(position);

                    String path = CollageHelper.getCollageUnzipFilePath();
                    startDownloadService(dynamicIconInfo.download_url, path, position - nativeList.size());
                }


            }
        });
        doInitData();

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "0");
        try {
            nativeList = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDynamicPicAdapter.addAll(nativeList);


    }


    public void doInitSubViews() {
        rv_articlesdetail_applyusers = (BasicRecyclerView) findViewById(R.id.rv_articlesdetail_applyusers);
//        layout_sticker_list = (LinearLayout) findViewById(R.id.layout_sticker_list);
        left_btn_ll = (LinearLayout) findViewById(R.id.left_btn_ll);
//        back_btn = (Button) findViewById(R.id.back_btn);
        tv_save = (TextView) findViewById(R.id.tv_save);
        show_image = (ImageView) findViewById(R.id.show_image);
        animation_view = (AnimationImageView) findViewById(R.id.animation_view);
        animation_view.setImageFolder(FileUtils.getARStickersPath());
        animation_view.setScreenDensity(screenDensity);
    }

    public void doInitData() {
        //加载动态贴图
//        loadARThumbnail();
        queryCollageList();
        Intent intent = this.getIntent();
        if (intent == null) return;
        photo_data = intent.getStringExtra("photo_data");
//        animationName = intent.getStringExtra("animationName");
        Bitmap tempBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, DisplayHelper.getScreenWidth(),
                DisplayHelper.getScreenHeight());


        Bitmap bgImageBitmap = originImageBitmap = BitmapHelper.resizeBitmap(tempBitmap, 0.5f);
//            bgImageBitmap=BitmapHelper.imageCrop(bgImageBitmap,photoType);
        tempBitmap.recycle();
        // Bitmap bgImageBitmap = originImageBitmap = BitmapHelper.getBitmapFromPath(imagePath, null);
        Log.i(TAG, "image path is:" + imagePath);
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
        show_image.setImageBitmap(originImageBitmap);
        bgImageBitmap.recycle();
        resizedBgImage.recycle();

    }


    @OnClick({R.id.left_btn_ll, R.id.tv_save})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn_ll: {
                showQuitTip();
            }
            break;
            case R.id.tv_save:
                if (haveNoFace) {
                    ToasterHelper.showShort(PhotoDynamicActivity.this, "检测不到人脸,请换一张试试吧", R.drawable.img_blur_bg);
                } else {
                    save();
                }
                break;
        }
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

    @Subcriber(tag = PuTaoConstants.DOWNLOAD_FILE_FINISH + "")
    public void downLoadFinish(Bundle bundle) {
        final int percent = bundle.getInt("percent");
        final int position = bundle.getInt("position");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                dynamicIconInfo.setShowProgress(true);


                mDynamicPicAdapter.getItem(position + nativeList.size()).setShowProgress(false);
                mDynamicPicAdapter.notifyItemChanged(position + nativeList.size());
//                mDynamicPicAdapter.notifyDataSetChanged();
                ToasterHelper.showShort(PhotoDynamicActivity.this, "下载成功", R.drawable.img_blur_bg);
            }
        });

    }

    @Subcriber(tag = PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE + "")
    public void saveAR(Bundle bundle) {
        int with = bundle.getInt("backgroundWith");
        int height = bundle.getInt("backgroundHight");
        Viedheight = height * 480 / with;
        saveASVideo(originImageBitmap);
//        imagesToVideo();

    }


    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE:
                int with = event.bundle.getInt("backgroundWith");
                int height = event.bundle.getInt("backgroundHight");
                Viedheight = height * 480 / with;
                saveASVideo(originImageBitmap);
//                imagesToVideo();
                break;
            case PuTaoConstants.DOWNLOAD_FILE_FINISH: {
                Loger.d("DOWNLOAD_FILE_FINISH");
                final int percent = event.bundle.getInt("percent");
                final int position = event.bundle.getInt("position");
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        doInitData();
                        mDynamicPicAdapter.notifyDataSetChanged();
                        ToasterHelper.showShort(PhotoDynamicActivity.this, "下载成功", R.drawable.img_blur_bg);

//                        updateProgressPartly(percent, position);
                    }
                });
                break;
            }
        }
    }

    private void startDownloadService(final String url, final String folderPath, final int position) {
        boolean isExistRunning = CommonUtils.isServiceRunning(mContext, DownloadFileService.class.getName());
        if (isExistRunning) {
            Loger.i("startDownloadService:exist");
            return;
        } else {
            Loger.i("startDownloadService:run");
        }
        if (null == url || null == folderPath) return;
        mDynamicIconInfo.get(position).type = "dynamic";
        Intent bindIntent = new Intent(mContext, DownloadFileService.class);
        bindIntent.putExtra("item", mDynamicIconInfo.get(position));
        bindIntent.putExtra("position", position);
        bindIntent.putExtra("url", url);
        bindIntent.putExtra("floderPath", folderPath);
        bindIntent.putExtra("type", DownloadFileService.DOWNLOAD_TYPE_DYNAMIC);
        mContext.startService(bindIntent);
    }

    private DynamicListInfo aDynamicListInfo;
    ArrayList<DynamicIconInfo> mDynamicIconInfo;
    String downUrl;
    IntentARImageView arImageView2;

    public void queryCollageList() {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
//                final DynamicListInfo aDynamicListInfo;
                try {
                    Gson gson = new Gson();
                    aDynamicListInfo = (DynamicListInfo) gson.fromJson(json.toString(), DynamicListInfo.class);
                    Gson gson1 = new Gson();
                    mDynamicIconInfo = gson1.fromJson(json.toString(), DynamicCategoryInfo.class).data;
                    mDynamicPicAdapter.addAll(mDynamicIconInfo);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(PuTaoConstants.PAIPAI_MATTER_LIST_PATH + "?type=dynamic_pic&page=1", map, mWaterMarkUpdateCallback);
        mCacheRequest.startGetRequest();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        animation_view.clearData();
        animation_view = null;
        EventBus.getEventBus().unregister(this);
    }


    public void save() {
        getMassege();

        videoImagePath = Environment.getExternalStorageDirectory() + File.separator + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "/.temp/";
        clearImageList();
        initSharedPreferencesHelper();
        File file = new File(videoImagePath);
        if (file.exists() == false) file.mkdir();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存视频请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Bitmap tip = BitmapHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.tips, 220, 60);
        originImageBitmap = BitmapHelper.combineBitmap(originImageBitmap, tip, originImageBitmap.getWidth() - tip.getWidth() - 5, originImageBitmap.getHeight() - tip.getHeight() - 2);
//        BitmapHelper.saveBitmap(originImageBitmap,imagePath);
        animation_view.setSave(originImageBitmap, videoImagePath, imageCount);
    }

    private void imagesToVideo() {
        // 保存视频
        String sizeStr = "480x" + Viedheight;

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
//        ffmpeg -r 0.5 -i d:/tmpImg/image%04d.jpg -i d:/time.mp3 -vcodec mpeg4 d:/video5.avi
        final String command = "-f image2 -i " + videoImagePath + "image%02d.jpg"
                + " -vcodec mpeg4 -r " + 12 + " -b 200k -s " + sizeStr + " " + videoPath;
        Log.i(TAG, "videPath is:" + videoPath);
        VideoHelper.getInstance(this).exectueFFmpegCommand(command.split(" "), new ExecuteBinaryResponseHandler() {
            @Override
            public void onFailure(String s) {
                // Log.i(TAG, "处理失败：" + s);
                ToasterHelper.show(PhotoDynamicActivity.this, "处理失败:" + s);
            }

            @Override
            public void onSuccess(String s) {
                ToasterHelper.show(PhotoDynamicActivity.this, "视频成功保存到相册");
                MediaScannerConnection.scanFile(PhotoDynamicActivity.this, new String[]{videoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Bundle bundle = new Bundle();
                        bundle.putString("savefile", videoPath);
                        bundle.putString("imgpath", videoImagePath + "image00.jpg");
                        bundle.putString("from", "dynamic");
                        ActivityHelper.startActivity(PhotoDynamicActivity.this, PhotoShareActivity.class, bundle);
                        finish();
                    }
                });

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
                progressDialog.hide();
                progressDialog = null;
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
        if (null == childFile || childFile.length == 0) return;
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

    @Override
    public void onBackPressed() {
        showQuitTip();
    }

    void showQuitTip() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
        new AlertDialog.Builder(mContext).setTitle("提示").setMessage("确认放弃当前编辑吗？").setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();
    }


    @Override
    protected String[] getRequestUrls() {
        return new String[0];
    }


    /**
     * 保存有动态贴纸的视频
     *
     * @param bitmap
     */
    private boolean videoSaving = false;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private String videoPath;

    private void saveASVideo(final Bitmap bitmap) {
        videoSaving = true;
        final YMDetector detector = new YMDetector(mContext);
        singleThreadExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    FaceModel faceModel;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    options.inJustDecodeBounds = false;
                    Bitmap scaleImageBmp = BitmapFactory.decodeFile(photo_data, options);
                 /*   byte[] data = BitmapHelper.Bitmap2Bytes(bitmap);
                    Bitmap scaleImageBmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);*/
                    List<YMFace> faces = detector.onDetector(scaleImageBmp);
                    if (faces != null && faces.size() > 0 && faces.get(0) != null) {
                        YMFace face = faces.get(0);
                        faceModel = new FaceModel();
                        faceModel.landmarks = face.getLandmarks();
                        faceModel.emotions = face.getEmotions();
                        RectF rect = new RectF((int) face.getRect()[0], (int) face.getRect()[1], (int) face.getRect()[2], (int) face.getRect()[3]);
                        faceModel.rectf = rect;
                    } else {
                        Log.e("tag", "111111111111111");
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

                    MediaScannerConnection.scanFile(PhotoDynamicActivity.this, new String[]{videoPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
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
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                videoSaving = false;

                ToastUtils.showToastShort(mContext, "视频保存成功");

                Bundle bundle = new Bundle();
                bundle.putString("savefile", videoPath);
                bundle.putString("imgpath", videoImagePath + "image00.jpg");
                bundle.putString("from", "dynamic");
                ActivityHelper.startActivity(PhotoDynamicActivity.this, PhotoShareActivity.class, bundle);
                finish();
            } else if (msg.what == 0x201) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                videoSaving = false;
                ToastUtils.showToastShort(mContext, "视频保存失败");
            }
        }
    };
 /*BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;
        // Do not compress
        newOpts.inSampleSize = 2;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
       newOriginImageBitmap=BitmapFactory.decodeFile(photo_data, newOpts);

        int ss= newOriginImageBitmap.getRowBytes() * newOriginImageBitmap.getHeight();

       int hh= newOriginImageBitmap.getHeight();
        int ww=newOriginImageBitmap.getWidth();*/
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
        if (!NetManager.isNetworkAvailable(PhotoDynamicActivity.this)) {
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
        String filtername = SharedPreferencesHelper.readStringValue(mContext,"filtername","NONE");
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
