
package com.putao.camera.application;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.putao.account.AccountApi;
import com.putao.account.AccountHelper;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.DatabaseServer;
import com.putao.camera.gps.CityMap;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.FileOperationHelper;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.UmengPushHelper;
import com.putao.camera.util.UmengUpdateHelper;
import com.putao.camera.util.WaterMarkHelper;
import com.putao.jpush.JPushHeaper;
import com.sunnybear.library.BasicApplication;
import com.sunnybear.library.controller.ActivityManager;
import com.sunnybear.library.util.AppUtils;
import com.sunnybear.library.util.Logger;
import com.sunnybear.library.util.SDCardUtils;

import java.io.File;

import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.ShareSDK;


public class MainApplication extends BasicApplication {
    private static Context globalContext;
    private WaterMarkConfigInfo mWaterMarkConfigInfo;
    public static LocationClient mLocationClient;
    public static MyLocationListener mMyLocationListener;
    private static DatabaseServer dbServer;
    private LocationMode tempMode = LocationMode.Battery_Saving;
    private String tempcoor = "gcj02";
    public static boolean isServiceClose;
    public static Intent redServiceIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        globalContext = this;
        DisplayHelper.init(globalContext);
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(globalContext));
        // Umeng统计参数设置
//        UmengAnalysisHelper.setCommonConfig();
        //app_id配置
        app_id = AppUtils.getMetaData(getApplicationContext(), KEY_APP_ID);
//        AccountApi.install("1", app_id, "515d7213721042a5ac31c2de95d2c7a7");
        AccountApi.install("1", app_id, "6a395698c5c243d0ba55ed2175c566ff");

        //开启shareSDK
        ShareSDK.initSDK(getApplicationContext());//开启shareSDK
        // Umeng更新参数设置
        UmengUpdateHelper.getInstance().setCommonConfig();
        // Umeng消息推送
        UmengPushHelper.getInstance().initPushAgent();
        //初始化城市列表
        CityMap.getInstance().init();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();
//        if (!mLocationClient.isStarted()) {
//            mLocationClient.start();
//        }
        //startService(new Intent(globalContext, GpsService.class));

        initAssetsDate();
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        //启动内部推送
       /* startRedDotService();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Fore_Message);
        intentFilter.addAction(Not_Fore_Message);
        registerReceiver(new HomeBroadcastReceiver(), intentFilter);*/
        startRedDotService();

//极光推送
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new JPushHeaper().setAlias(MainApplication.this, AccountHelper.getCurrentUid());
            }
        }, 3000);
    }


    public static Context getInstance() {
        return globalContext;
    }

    @Override
    protected String getBuglyKey() {
        return "900022345";
    }

    public static void stopLocationClient() {
        if (mLocationClient != null) {
            mLocationClient.stop();
            mLocationClient.unRegisterLocationListener(mMyLocationListener);
        }
    }

    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            SharedPreferencesHelper.saveStringValue(globalContext, PuTaoConstants.PREFERENC_LOCATION_LONGITUDE, location.getLongitude() + "");
            SharedPreferencesHelper.saveStringValue(globalContext, PuTaoConstants.PREFERENC_LOCATION_LATITUDE, location.getLatitude() + "");
            SharedPreferencesHelper.saveStringValue(globalContext, PuTaoConstants.PREFERENCE_CURRENT_CITY, location.getCity());
            //Receive Location 
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\ndirection : ");
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append(location.getDirection());
                sb.append("\ncity:");
                sb.append(location.getCity());
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ncity:");
                sb.append(location.getCity());
            }
            //Loger.d(sb.toString());
        }
    }

    private void InitLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);
        option.setCoorType(tempcoor);
        option.setScanSpan(10000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

    public void setWaterMarkConfigInfo(WaterMarkConfigInfo aWaterMarkConfigInfo) {
        mWaterMarkConfigInfo = aWaterMarkConfigInfo;
    }

    public WaterMarkConfigInfo getWaterMarkConfigInfo() {
        return mWaterMarkConfigInfo;
    }


    /*private void installDataBase() {

    }*/


    /**
     * 获取数据库服务
     *
     * @return
     */
    public static DatabaseServer getDBServer() {
        try {
            if (dbServer == null) {
                dbServer = new DatabaseServer(globalContext);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbServer;
    }

    public static SQLiteDatabase getSQLiteDatabase() {
        if (getDBServer() != null) {
            return getDBServer().getSQLiteDatabase();
        }
        return null;
    }


    public static int getVersionCode()//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = globalContext.getPackageManager().getPackageInfo(globalContext.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 水印/拼图等内置资源的初始化
     */
    public static void initAssetsDate() {
        boolean isFristUse = SharedPreferencesHelper.readBooleanValue(globalContext, PuTaoConstants.PREFERENC_FIRST_USE_APPLICATION, true);
        int lastVersionCode = SharedPreferencesHelper.readIntValue(globalContext, PuTaoConstants.PREFERENC_VERSION_CODE, 0);
        int curVersionCode = MainApplication.getVersionCode();

        boolean isFristUseCollage = SharedPreferencesHelper.readBooleanValue(globalContext, PuTaoConstants.PREFERENC_FIRST_USE_COLLAGE, true);

        if (isFristUse || lastVersionCode != curVersionCode) {
            if (isFristUseCollage) {
                doFirstInitWaterMarkFileCopy(PuTaoConstants.PAIPAI_WATERMARK_FLODER_NAME);
                doFirstInitCollageFileCopy(PuTaoConstants.PAIPAI_COLLAGE_FLODER_NAME);
                SharedPreferencesHelper.saveBooleanValue(globalContext, PuTaoConstants.PREFERENC_FIRST_USE_COLLAGE, false);
            }
        }

    }

    //第一次拼图资源初始化
    private static void doFirstInitCollageFileCopy(String unZipFileName) {
        boolean bSuccess = FileOperationHelper.copyAssetsFileToExternalFile(unZipFileName + ".zip");
        if (bSuccess) {
            try {
                FileOperationHelper.unZipFile(unZipFileName + ".zip");
                //读取
                String config_str = FileOperationHelper.readJsonFile(unZipFileName, "collage_config.json");
                //写入SharePreference
                //SharedPreferencesHelper.saveStringValue(this, PuTaoConstants.PREFERENC_COLLAGE_CONFIG_JSON, config_str);
                //写入数据库
                Gson gson = new Gson();
                CollageConfigInfo mCollageConfigInfo = gson.fromJson(config_str, CollageConfigInfo.class);
                CollageHelper.saveCollageConfigInfoToDB(globalContext, mCollageConfigInfo, "1");

                if (!PuTaoConstants.isDebug) {
                    new File(FileOperationHelper.getExternalFilePath() + "/" + unZipFileName + ".zip").delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    //第一次水印文件初始化
    private static void doFirstInitWaterMarkFileCopy(String unZipFileName) {
        boolean bSuccess = FileOperationHelper.copyAssetsFileToExternalFile(unZipFileName + ".zip");
        if (bSuccess) {
            try {
                FileOperationHelper.unZipFile(unZipFileName + ".zip");
                //读取
                String config_str = FileOperationHelper.readOldJsonFile("watermark", "watermark_config.json");
                //                Loger.i("config_str save:" + config_str);
                //                写入SharePreference
                SharedPreferencesHelper.saveStringValue(globalContext, PuTaoConstants.PREFERENC_WATERMARK_JSON, config_str);
                SharedPreferencesHelper.saveIntValue(globalContext, PuTaoConstants.PREFERENC_WATERMARK_SRC_VERSION_CODE, 1);
                //保存默认的水印到数据库
                WaterMarkConfigInfo ConfigInfo = new Gson().fromJson(config_str, WaterMarkConfigInfo.class);
                WaterMarkHelper.saveCategoryInfoToDb(ConfigInfo, "1");

                if (!PuTaoConstants.isDebug) {
                    new File(FileOperationHelper.getExternalFilePath() + "/" + unZipFileName + ".zip").delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        }
    }

    /**
     * debug模式
     *
     * @return 是否开启
     */
    public boolean isDebug() {
        return true;
    }

    @Override
    public String getPackageName() {
        return "com.putao.camera";
    }

    /**
     * 设置sdCard路径
     *
     * @return sdCard路径
     */
    protected String getSdCardPath() {
        return SDCardUtils.getSDCardPath() + File.separator + getLogTag();
    }

    /**
     * 设置调试日志标签名
     *
     * @return 调试日志标签名
     */
    protected String getLogTag() {
        return "putao_camera";
    }

    /**
     * 网络缓存文件大小
     *
     * @return 缓存文件大小
     */
    protected int getNetworkCacheSize() {
        return 20 * 1024 * 1024;
    }

    @Override
    protected int getNetworkCacheMaxAgeTime() {
        return 0;
    }

    /**
     * 捕捉到异常就退出App
     *
     * @param ex 异常信息
     */
    protected void onCrash(Throwable ex) {
        Logger.e("APP崩溃了,错误信息是" + ex.getMessage());
        ex.printStackTrace();
        ActivityManager.getInstance().killProcess(getApplicationContext());
    }

    protected String getNetworkCacheDirectoryPath() {
        return sdCardPath + File.separator + "http_cache";
    }

   /* *//**
     * 监听程序已经在后台
     *//*
    private class HomeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (isServiceClose) {
                startRedDotService();
                isServiceClose = false;
            }
        }
    }*/

    /**
     * 启动内部推送
     */
    private void startRedDotService() {
        if (TextUtils.isEmpty(AccountHelper.getCurrentUid())) return;
        redServiceIntent = new Intent(ACTION_PUSH_SERVICE);
        redServiceIntent.setPackage(getPackageName());
        startService(redServiceIntent);
        isServiceClose = true;
    }

    /**
     * 以下为通行证定义常量
     */
    private static final String KEY_APP_ID = "app_id";
    public static final String ACTION_PUSH_SERVICE = "com.putao.camera.PUSH";
    public static final String Fore_Message = "com.putao.isFore.message";
    public static final String Not_Fore_Message = "com.putao.isNotFore.message";
    //===================preference key===========================
    public static String app_id;
    public static final String PREFERENCE_KEY_UID = "uid";
    public static final String PREFERENCE_KEY_TOKEN = "token";
    public static final String PREFERENCE_KEY_NICKNAME = "nickname";
    public static final String PREFERENCE_KEY_EXPIRE_TIME = "expire_tim";
    public static final String PREFERENCE_KEY_REFRESH_TOKEN = "refresh_token";
    public static final String PREFERENCE_KEY_USER_INFO = "user_info";
    public static final String PREFERENCE_KEY_BABY_ID = "baby_id";
    public static final String WX_APP_ID = "wx1f67f2c75acfaf0c";
}