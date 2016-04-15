package com.putao.camera.editor;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.DynamicCategoryInfo;
import com.putao.camera.bean.DynamicIconInfo;
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
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.Loger;
import com.putao.camera.util.ToasterHelper;
import com.putao.video.VideoHelper;
import com.sunnybear.library.controller.BasicFragmentActivity;
import com.sunnybear.library.controller.eventbus.Subcriber;
import com.sunnybear.library.view.recycler.BasicRecyclerView;
import com.sunnybear.library.view.recycler.listener.OnItemClickListener;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import mobile.ReadFace.YMDetector;
import mobile.ReadFace.YMFace;


public class PhotoDynamicActivity extends BasicFragmentActivity implements View.OnClickListener {
    private LinearLayout left_btn_ll;
    private String TAG = PhotoARShowActivity.class.getName();
    //    private Button back_btn;
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
                    Loger.d("click");
                    ToasterHelper.showShort(PhotoDynamicActivity.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
                    if (animation_view.isAnimationLoading()) {
                        ToasterHelper.showShort(PhotoDynamicActivity.this, "动画加载中请稍后", R.drawable.img_blur_bg);
                        return;
                    }
                    animation_view.clearData();
                    animation_view.setData(list.get(0).zipName, false);
                    //检测人脸
                    final YMDetector YMDetector = new YMDetector(PhotoDynamicActivity.this);
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
                    }).start();

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
        String photo_data = intent.getStringExtra("photo_data");
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
                save();
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

    @Subcriber(tag = PuTaoConstants.DOWNLOAD_FILE_FINISH+"")
    public void downLoadFinish(Bundle bundle) {
        final int percent = bundle.getInt("percent");
        final int position =bundle.getInt("position");
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDynamicPicAdapter.getItem(position+nativeList.size()).setShowProgress(false);
                mDynamicPicAdapter.notifyDataSetChanged();
                ToasterHelper.showShort(PhotoDynamicActivity.this, "下载成功", R.drawable.img_blur_bg);
            }
        });

    }


    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.SAVE_AR_SHOW_IMAGE_COMPELTE:
                int with = event.bundle.getInt("backgroundWith");
                int height = event.bundle.getInt("backgroundHight");
                Viedheight = height * 480 / with;
                imagesToVideo();
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

    //    private DynamicManagementAdapter mManagementAdapter;
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
                   /* for (int i = 0; i < aDynamicListInfo.data.size(); i++) {
                        imagePath = aDynamicListInfo.data.get(i).cover_pic;
                        if (StringHelper.isEmpty(imagePath)) continue;
                        arImageView2 = new IntentARImageView(mContext);
                        arImageView2.setDataFromInternt(imagePath);
                        downUrl = aDynamicListInfo.data.get(i).download_url;
//                        String tag= downUrl.substring(downUrl.indexOf("file/")+5, downUrl.lastIndexOf(".zip"));
                        arImageView2.setTag(downUrl);
                        arImageView2.setPosition(i);
                        arImageView2.setOnClickListener(arIntentStickerOnclickListener);
                        layout_sticker_list.addView(arImageView2);

                    }*/
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



    /**
     * 加载相机拍照界面动态贴图缩略图
     */
   /* private void loadARThumbnail() {
//        layout_sticker_list.removeAllViews();
        // 第一版本数据写死，因为后台接口都没有通，以后的版本此处要包括随app打包的和从服务器上下载的所有ar贴纸
        ArrayList<String> elements = new ArrayList<String>();
        elements.add("cn");
        elements.add("fd");
        elements.add("hy");
        elements.add("hz");
        elements.add("kq");
        elements.add("mhl");
        elements.add("xhx");
        elements.add("xm");

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 5, 0, 5);

        for (int i = 0; i < elements.size(); i++) {
            String iconInfo = elements.get(i);
            if (StringHelper.isEmpty(iconInfo)) continue;
            ARImageView arImageView = new ARImageView(mContext);
//iconInfo 为包名
            String imagePath = FileUtils.getARStickersPath() + iconInfo + "_icon.png";
            arImageView.setData(imagePath);
            arImageView.setTag(iconInfo);
            arImageView.setOnClickListener(arStickerOnclickListener);

//            layout_sticker_list.addView(arImageView);

        }

        //增加网络数据
        queryCollageList();


    }*/

  /*  // 点击动态贴图时候的处理逻辑，跟静态贴图分开处理
    View.OnClickListener arStickerOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ToasterHelper.showShort(PhotoDynamicActivity.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
            if (animation_view.isAnimationLoading()) {

                ToasterHelper.showShort(PhotoDynamicActivity.this, "动画加载中请稍后", R.drawable.img_blur_bg);
                return;
            }
            animation_view.clearData();
            animationName = (String) v.getTag();
            animation_view.setData(animationName, false);
            //检测人脸
            final YMDetector YMDetector = new YMDetector(PhotoDynamicActivity.this);
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
            }).start();

        }
    };*/

  /*  View.OnClickListener arIntentStickerOnclickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            IntentARImageView view = (IntentARImageView) v;
            int position = view.getPosition();
            animationName = (String) v.getTag();
            Map<String, String> map = new HashMap<String, String>();
            map.put("download_url", animationName);
            List<DynamicIconInfo> list = null;
            list = MainApplication.getDBServer().getDynamicIconInfoByWhere(map);
            if (list.size() == 0) {
                ToasterHelper.showShort(PhotoDynamicActivity.this, "开始下载", R.drawable.img_blur_bg);
                String path = CollageHelper.getCollageUnzipFilePath();
                startDownloadService(animationName, path, position);

            } else {
                ToasterHelper.showShort(PhotoDynamicActivity.this, "请将正脸置于取景器内", R.drawable.img_blur_bg);
                if (animation_view.isAnimationLoading()) {
                    ToasterHelper.showShort(PhotoDynamicActivity.this, "动画加载中请稍后", R.drawable.img_blur_bg);
                    return;
                }
                animation_view.clearData();
                String tag = animationName.substring(animationName.indexOf("file/") + 5, animationName.lastIndexOf(".zip"));
                animation_view.setData(tag, false);
                //检测人脸
                final YMDetector YMDetector = new YMDetector(PhotoDynamicActivity.this);
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
                }).start();

            }
        }
    };*/



    @Override
    protected void onDestroy() {
        super.onDestroy();
        animation_view.clearData();
        animation_view = null;
        EventBus.getEventBus().unregister(this);
    }




    public void save() {
        videoImagePath = Environment.getExternalStorageDirectory() + File.separator + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "/temp/";
        File file = new File(videoImagePath);
        if (file.exists() == false) file.mkdir();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("正在保存视频请稍后...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Bitmap tip = BitmapHelper.decodeSampledBitmapFromResource(getResources(), R.drawable.tips, 220, 60);
        originImageBitmap = BitmapHelper.combineBitmap(originImageBitmap, tip, originImageBitmap.getWidth() - tip.getWidth() - 5, originImageBitmap.getHeight() - tip.getHeight() - 2);
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


}
