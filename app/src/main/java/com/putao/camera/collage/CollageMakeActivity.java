package com.putao.camera.collage;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.putao.camera.R;
import com.putao.camera.album.AlbumPhotoSelectActivity;
import com.putao.camera.application.MainApplication;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.bean.BeautifyInfo;
import com.putao.camera.bean.PintuInfo;
import com.putao.camera.bean.TemplateIconInfo;
import com.putao.camera.collage.mode.CollageSampleItem;
import com.putao.camera.collage.view.CollageView;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.constants.UmengAnalysisConstants;
import com.putao.camera.editor.PhotoShareActivity;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.http.CacheRequest;
import com.putao.camera.setting.watermark.management.TemplateManagemenActivity;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileUtils;
import com.putao.camera.util.NetManager;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;
import com.sunnybear.library.util.ToastUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by jidongdong on 15/1/27.
 */
public class CollageMakeActivity extends BaseActivity implements View.OnClickListener {
    private CollageView mCollageView;
    private ArrayList<String> selectImages;
    private TextView tv_save;
    private Button back_btn;
    private LinearLayout cur_cate_samples, panel_sample_list, ll_change_make;
    private HorizontalScrollView sl_sample_list;
    //    private CollageConfigInfo mCollageConfigInfo;
    private CollageSampleItem mCollageItemInfo;
    private TemplateIconInfo mTemplateIconInfo;
    private float sample_scale = 1.0f;
    private String filePath;
    private boolean mIsExpand = true;
    private ImageView tips;
    AlertDialog dialog;
    String zipName;
    private int mPhotoSelectIndex = -1;
    boolean isFirstUseMake;

    @Override
    public void doBefore() {
        super.doBefore();
        isFirstUseMake = SharedPreferencesHelper.readBooleanValue(this, "isFirstUseMake", true);
    }

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_collage;
    }

    @Override
    public void doInitSubViews(View view) {
        mCollageView = queryViewById(R.id.collage_view);
        tv_save = queryViewById(R.id.tv_save);
        back_btn = queryViewById(R.id.back_btn);
        tips = queryViewById(R.id.tips);

        cur_cate_samples = queryViewById(R.id.cur_cate_samples);
        panel_sample_list = queryViewById(R.id.panel_sample_list);
        sl_sample_list = queryViewById(R.id.sl_sample_list);
        ll_change_make = queryViewById(R.id.ll_change_make);
        mCollageView.setOnPhotoItemOnClick(mOnPhotoItemOnClick);
        addOnClickListener(tv_save, back_btn, ll_change_make, tips);
        EventBus.getEventBus().register(this);
        if (isFirstUseMake) {
            isFirstUseMake = false;
            SharedPreferencesHelper.saveBooleanValue(this, "isFirstUseMake", false);
            tips.setVisibility(View.VISIBLE);
        }
    }

    CollageView.OnPhotoItemOnClick mOnPhotoItemOnClick = new CollageView.OnPhotoItemOnClick() {
        @Override
        public void onClicked(int index) {
            mPhotoSelectIndex = index;
            showPhotoOptdialog();
        }
    };
    PintuInfo pintuInfo;

    @SuppressWarnings("unchecked")
    @Override
    public void doInitData() {
        sample_scale = DisplayHelper.getDensity() / 2;
//        mCollageConfigInfo = CollageHelper.getCollageConfigInfoFromDB(mContext);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
//            mCollageItemInfo = (CollageSampleItem) bundle.getSerializable("sampleinfo");
            selectImages = (ArrayList<String>) bundle.getSerializable("images");

            mTemplateIconInfo = (TemplateIconInfo) bundle.getSerializable("sampleinfo");
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", String.valueOf(mTemplateIconInfo.id));
            List<TemplateIconInfo> list = MainApplication.getDBServer().getTemplateIconInfoByWhere(map);
            zipName = list.get(0).zipName.substring(0, list.get(0).zipName.lastIndexOf(".zip"));
            Gson gson = new Gson();
            String pintuJson = list.get(0).pintuGson;
            if (pintuJson.contains("maskList\":{")) {
                pintuJson = pintuJson.replace("maskList\":{", "maskList\":[{");
                pintuJson = pintuJson.replace("\"}", "\"}]");
            }
            pintuInfo = gson.fromJson(pintuJson, PintuInfo.class);
            initCollageView();
        }
//        loadSamples();
    }

    public void initCollageView() {
        String imagName = pintuInfo.maskList.get(selectImages.size() - 1).imageName;
//        String mask_path = FileUtils.getSdcardPath() + File.separator + zipName + File.separator + imagName;
        String mask_path = FileUtils.getARStickersPath() + File.separator + zipName + File.separator + imagName;

        //边框图片,按照图片数量选择
        /*String mask_path = CollageHelper.getCollageFilePath()
                + mCollageItemInfo.mask_image;*/
        Bitmap bitmap = BitmapHelper.getInstance().getBitmapFromPath(mask_path);
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postScale(sample_scale, sample_scale);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            mCollageView.setSampleImage(bitmap);
            mCollageView.setImageList(getPhotoSetFromCollageItemInfo(mTemplateIconInfo));
//            mCollageView.setTextList(getCollageTextList(mTemplateIconInfo));
        }
    }

    private ArrayList<CollageView.CollagePhotoSet> getPhotoSetFromCollageItemInfo(
            TemplateIconInfo info) {
        //得到模板框内点的信息
        ArrayList<CollageView.CollagePhotoSet> photoSet = new ArrayList<CollageView.CollagePhotoSet>();
        if (photoSet != null) {
            //获取选中模板图片张数
            for (int i = 0; i < selectImages.size(); i++) {
                PintuInfo.Mask mask = pintuInfo.maskList.get(selectImages.size() - 1).mask.get(i);
//                PintuInfo.MaskInfo imageInfo = info.elements .get(0).datas.get(selectImages.size()).mask.get(i);

                if (i < selectImages.size()) {
                    Bitmap bitmap = BitmapHelper.getInstance()
                            .getBitmapFromPathWithSize(selectImages.get(i),
                                    DisplayHelper.getScreenWidth(),
                                    DisplayHelper.getScreenHeight());
                    if (bitmap != null) {
                        photoSet.add(mCollageView.new CollagePhotoSet(bitmap,
                                getImagePointsByCollageImageInfo(mask)));
                    }
                }
            }
        }
        return photoSet;
    }


    private CollageView.Area getImagePointsByCollageImageInfo(
            PintuInfo.Mask mask) {
        int count = mask.point.size();
        float[] p_x = new float[count];
        float[] p_y = new float[count];
        for (int i = 0; i < count; i++) {
            String pointNum = mask.point.get(i);
            String[] pointXY = pointNum.split(",");
            p_x[i] = Float.parseFloat(pointXY[0]);
            p_y[i] = Float.parseFloat(pointXY[1]);
        }

        CollageView.Area area = mCollageView.new Area(
                mCollageView.getScalePloyX(p_x, sample_scale),
                mCollageView.getScalePloyY(p_y, sample_scale));
        return area;
    }


    private File saveCollage() {
        mCollageView.setDrawingCacheEnabled(true);
        Bitmap bitmap = mCollageView.getDrawingCache();
        File pictureFile = CommonUtils.getOutputMediaFile();
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(pictureFile);
            // keep full quality of the image
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream);
            outStream.flush();
            outStream.close();
            // success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCollageView.setDrawingCacheEnabled(false);
        MediaScannerConnection.scanFile(this,
                new String[]{pictureFile.toString()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
        filePath = pictureFile.getPath();
        return pictureFile;
    }


    @Override
    public void onBackPressed() {
        showQuitTip();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.back_btn:
                showQuitTip();
                break;
            case R.id.tv_save:
                SharedPreferencesHelper.saveStringValue(mContext, "template", mTemplateIconInfo.id);
                getMassege();
                initSharedPreferencesHelper();
                doUmengEventAnalysis(UmengAnalysisConstants.UMENG_COUNT_EVENT_JIGSAW_SHARE_DONE);
                if (StringHelper.isEmpty(filePath)) {
                    saveCollage();
                }
                bundle.putString("savefile", filePath);
                bundle.putString("from", "collage");
                ActivityHelper.startActivity(mActivity, PhotoShareActivity.class, bundle);

                finish();
                break;
          /*  case R.id.btn_fold:
                int start = mIsExpand ? 0 : sl_sample_list.getHeight();
                int end = mIsExpand ? sl_sample_list.getHeight() : 0;
                int bg_res_id = mIsExpand ? R.drawable.template_button_fold2 : R.drawable.template_button_fold;
                mIsExpand = !mIsExpand;
                btn_fold.setBackgroundDrawable(getResources().getDrawable(bg_res_id));
                ObjectAnimator.ofFloat(panel_sample_list, "translationY", start, end).setDuration(300).start();
                break;*/
            case R.id.ll_change_make:
                bundle.putSerializable("sampleinfo", mTemplateIconInfo);
                bundle.putSerializable("images", selectImages);
                bundle.putInt("imgsum", selectImages.size());
                bundle.putString("from", "collageMake");
                ActivityHelper.startActivity(mActivity, TemplateManagemenActivity.class, bundle);
                finish();
                break;

            case R.id.ll_replace:
                bundle.putBoolean("from_collage_photo", true);
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class, bundle);
                dialog.dismiss();
                break;

            case R.id.iv_replace:
                bundle.putBoolean("from_collage_photo", true);
                ActivityHelper.startActivity(mActivity, AlbumPhotoSelectActivity.class, bundle);
                dialog.dismiss();
                break;
            case R.id.iv_rotate:
                if (mPhotoSelectIndex > -1) {
                    CollageView.CollagePhotoSet set = mCollageView.getPhotoList().get(mPhotoSelectIndex);
                    Matrix matrix = new Matrix();
                    matrix.set(set.matrix);
                    matrix.setRotate(90);
//                    matrix.postScale(-1, 1);
                    mCollageView.changeSourcePhotoSet(Bitmap.createBitmap(set.Photo, 0, 0, set.Photo.getWidth(), set.Photo.getHeight(), matrix, true), mPhotoSelectIndex);
                }
                dialog.dismiss();
                break;
            case R.id.ll_rotate:
                if (mPhotoSelectIndex > -1) {
                    CollageView.CollagePhotoSet set = mCollageView.getPhotoList().get(mPhotoSelectIndex);
                    Matrix matrix = new Matrix();
                    matrix.set(set.matrix);
                    matrix.setRotate(90);
//                    matrix.postScale(-1, 1);
                    mCollageView.changeSourcePhotoSet(Bitmap.createBitmap(set.Photo, 0, 0, set.Photo.getWidth(), set.Photo.getHeight(), matrix, true), mPhotoSelectIndex);
                }
                dialog.dismiss();
                break;
            case R.id.iv_mirror:
                if (mPhotoSelectIndex > -1) {
                    CollageView.CollagePhotoSet set = mCollageView.getPhotoList().get(mPhotoSelectIndex);
                    Matrix matrix = new Matrix();
                    matrix.set(set.matrix);
                    matrix.postScale(-1, 1);
                    mCollageView.changeSourcePhotoSet(Bitmap.createBitmap(set.Photo, 0, 0, set.Photo.getWidth(), set.Photo.getHeight(), matrix, true), mPhotoSelectIndex);
                }
                dialog.dismiss();
                break;
            case R.id.ll_mirror:
                if (mPhotoSelectIndex > -1) {
                    CollageView.CollagePhotoSet set = mCollageView.getPhotoList().get(mPhotoSelectIndex);
                    Matrix matrix = new Matrix();
                    matrix.set(set.matrix);
                    matrix.postScale(-1, 1);
                    mCollageView.changeSourcePhotoSet(Bitmap.createBitmap(set.Photo, 0, 0, set.Photo.getWidth(), set.Photo.getHeight(), matrix, true), mPhotoSelectIndex);
                }
                dialog.dismiss();
                break;
            case R.id.tips:
                tips.setVisibility(View.GONE);
            default:
                break;
        }
    }


    private double latitude = 0.0;
    private double longitude = 0.0;
    private Location location;
    private LocationManager locationManager;

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
        long shooting_time = System.currentTimeMillis() / 1000;
        if (!NetManager.isNetworkAvailable(CollageMakeActivity.this)) {
            uploadMassege(latitude, longitude, shooting_time);
        }

    }

    public void uploadMassege(double latitude, double longitude, long str) {
        CacheRequest.ICacheRequestCallBack mWaterMarkUpdateCallback = new CacheRequest.ICacheRequestCallBack() {
            @Override
            public void onSuccess(int whatCode, JSONObject json) {
                super.onSuccess(whatCode, json);
                ToastUtils.showToast(mContext, "111111", Toast.LENGTH_SHORT);
                String ss = json.toString();
            }

            @Override
            public void onFail(int whatCode, int statusCode, String responseString) {
                super.onFail(whatCode, statusCode, responseString);
                ToastUtils.showToast(mContext, "0000", Toast.LENGTH_SHORT);
            }
        };
        HashMap<String, String> map = new HashMap<String, String>();
        CacheRequest mCacheRequest = new CacheRequest(requestString(latitude, longitude, str),
                map, mWaterMarkUpdateCallback);
        mCacheRequest.startPostRequest();
    }

    public void initSharedPreferencesHelper() {
        SharedPreferencesHelper.saveStringValue(mContext, "dynamic", "");
        SharedPreferencesHelper.saveStringValue(mContext, "sticker", "");
        SharedPreferencesHelper.saveStringValue(mContext, "template", "");
        SharedPreferencesHelper.saveStringValue(mContext, "filtername", "None");

    }

    public String requestString(double latitude, double longitude, long str) {
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

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.EVENT_COLLAGE_PHOTO_SELECT:
                Bundle bundle = event.bundle;
                String path = bundle.getString("photo_path");
                if (!StringHelper.isEmpty(path)) {
                    Bitmap bitmap = BitmapHelper.getInstance().getBitmapFromPath(path);
                    if (bitmap != null && mPhotoSelectIndex > -1) {
                        mCollageView.changeSourcePhotoSet(bitmap, mPhotoSelectIndex);
                    }
                }
                break;

            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
            default:
                break;
        }
    }

    void showQuitTip() {
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

    void showPhotoOptdialog() {
        dialog = new AlertDialog.Builder(mContext).create();
        dialog.show();
        View parent = LayoutInflater.from(this).inflate(R.layout.layout_collage_image_opreate_dialog, null);
        LinearLayout ll_replace = queryViewById(parent, R.id.ll_replace);
        LinearLayout ll_rotate = queryViewById(parent, R.id.ll_rotate);
        LinearLayout ll_mirror = queryViewById(parent, R.id.ll_mirror);
        ImageView iv_replace = queryViewById(parent, R.id.iv_replace);
        ImageView iv_rotate = queryViewById(parent, R.id.iv_rotate);
        ImageView iv_mirror = queryViewById(parent, R.id.iv_mirror);
        Window window = dialog.getWindow();
        window.setContentView(parent);
        dialog.setCanceledOnTouchOutside(true);
        ll_replace.setOnClickListener(this);
        ll_rotate.setOnClickListener(this);
        ll_mirror.setOnClickListener(this);
        iv_replace.setOnClickListener(this);
        iv_rotate.setOnClickListener(this);
        iv_mirror.setOnClickListener(this);
    }




     /* private ArrayList<CollageView.CollagePhotoSet> getPhotoSetFromCollageItemInfo(
            CollageSampleItem info) {
        //得到模板框内点的信息
        ArrayList<CollageView.CollagePhotoSet> photoSet = new ArrayList<CollageView.CollagePhotoSet>();
        if (photoSet != null) {
            //获取选中模板图片张数
            for (int i = 0; i < info.imageElements.size(); i++) {
                CollageConfigInfo.CollageImageInfo imageInfo = info.imageElements
                        .get(i);
                if (i < selectImages.size()) {
                    Bitmap bitmap = BitmapHelper.getInstance()
                            .getBitmapFromPathWithSize(selectImages.get(i),
                                    DisplayHelper.getScreenWidth(),
                                    DisplayHelper.getScreenHeight());
                    if (bitmap != null) {
                        photoSet.add(mCollageView.new CollagePhotoSet(bitmap,
                                getImagePointsByCollageImageInfo(imageInfo)));
                    }
                }
            }
        }
        return photoSet;
    }*/




  /*  private CollageView.Area getImagePointsByCollageImageInfo(
            CollageConfigInfo.CollageImageInfo imageInfo) {
        int count = imageInfo.pointArray.size();
        float[] p_x = new float[count];
        float[] p_y = new float[count];
        for (int i = 0; i < count; i++) {
            p_x[i] = imageInfo.pointArray.get(i).point_x;
            p_y[i] = imageInfo.pointArray.get(i).point_y;
        }

        CollageView.Area area = mCollageView.new Area(
                mCollageView.getScalePloyX(p_x, sample_scale),
                mCollageView.getScalePloyY(p_y, sample_scale));
        return area;
    }*/

   /* private void loadSamples() {
        ArrayList<CollageConfigInfo.CollageItemInfo> list = getCollageSampleList(mCollageItemInfo.category);
        if (list.size() > 0) {
            for (CollageConfigInfo.CollageItemInfo item : list) {
                ImageView imageView = new ImageView(mActivity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160, 240);
                params.leftMargin = 10;
                params.rightMargin = 10;
                params.topMargin = 3;
                params.bottomMargin = 3;
                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setTag(item);
                String path = CollageHelper.getCollageFilePath() + item.sample_image;
                Bitmap bitmap = BitmapHelper.getInstance().getCenterCropBitmap(path, 160, 240);
                imageView.setImageBitmap(bitmap);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CollageConfigInfo.CollageItemInfo item_Info = (CollageConfigInfo.CollageItemInfo) v.getTag();
                        CollageSampleItem sampleItem = new CollageSampleItem(item_Info, mCollageItemInfo.category);
                        mCollageItemInfo = sampleItem;
                        initCollageView();
                    }
                });
                cur_cate_samples.addView(imageView);
            }
        }
    }*/


   /* protected ArrayList<CollageConfigInfo.CollageItemInfo> getCollageSampleList(String category) {
        if (mCollageConfigInfo == null) {
            return null;
        }
        ArrayList<CollageConfigInfo.CollageItemInfo> list = new ArrayList<CollageConfigInfo.CollageItemInfo>();
        for (int i = 0; i < mCollageConfigInfo.content.collage_image.size(); i++) {
            CollageConfigInfo.CollageCategoryInfo categoryInfo = mCollageConfigInfo.content.collage_image
                    .get(i);
            if (categoryInfo.category.equals(category)) {
                list = categoryInfo.elements;
                break;
            }
        }
        return list;
    }*/


     /*private ArrayList<CollageConfigInfo.CollageText> getCollageTextList(
            CollageSampleItem itemInfo) {
        ArrayList<CollageConfigInfo.CollageText> textlist = new ArrayList<CollageConfigInfo.CollageText>();
        if (itemInfo.textElements != null) {
            String current_city = SharedPreferencesHelper.readStringValue(
                    mContext, PuTaoConstants.PREFERENCE_CURRENT_CITY, "");
            String current_time = DateUtil.getStringDateShort();
            for (int i = 0; i < itemInfo.textElements.size(); i++) {
                CollageConfigInfo.CollageText textItem = mCollageConfigInfo.new CollageText();
                textItem.textColor = itemInfo.textElements.get(i).textColor;
                if (itemInfo.textElements.get(i)
                        .equals(CollageView.CollageTextType.CURRENT_CITY)) {
                    if (!StringHelper.isEmpty(current_city))
                        textItem.text = current_city;
                } else if (itemInfo.textElements.get(i).textType
                        .equals(CollageView.CollageTextType.CURRENT_TIME)) {
                    textItem.text = current_time;
                }
                textItem.left = itemInfo.textElements.get(i).left * sample_scale;
                textItem.right = itemInfo.textElements.get(i).right * sample_scale;
                textItem.bottom = itemInfo.textElements.get(i).bottom * sample_scale;
                textItem.top = itemInfo.textElements.get(i).top * sample_scale;
                textItem.textSize = (int) (itemInfo.textElements.get(i).textSize * sample_scale);
                textlist.add(textItem);
            }
        }
        return textlist;
    }*/


}
