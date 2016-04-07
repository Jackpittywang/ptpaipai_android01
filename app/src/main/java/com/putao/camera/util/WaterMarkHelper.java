
package com.putao.camera.util;

import android.content.Context;

import com.google.gson.Gson;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.StickerIconInfo;
import com.putao.camera.bean.StickerUnZipInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkConfigInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.DatabaseServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaterMarkHelper {
    // 文件夹名称必须和解压包解压出来的名称保持一致。
    public static String getWaterMarkFilePath() {
        String floderName = "/watermark/";
        return FileOperationHelper.getExternalFilePath() + floderName;
    }

    // 水印增量包路径
    public static String getWaterMarkUnzipFilePath() {
        String floderName = "/watermark_unzip/";
        return FileOperationHelper.getExternalFilePath() + floderName;
    }

    public static WaterMarkConfigInfo getWaterMarkConfigInfoFromExternalFile() {
        try {
            String config_str = FileOperationHelper.readJsonFile("watermark", "watermark_config.json");
            Gson gson = new Gson();
            WaterMarkConfigInfo mWaterMarkConfigInfo = gson.fromJson(config_str, WaterMarkConfigInfo.class);
            return mWaterMarkConfigInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static WaterMarkConfigInfo mWaterMarkConfigInfo;
    public static boolean bHasNewWaterMarkUpdate = false;
    public static String hasNewWaterMarkUpdateLink = "";
    public static int mark_resource_version_code = -1;

    public static void setWaterMarkConfigInfoFromSahrePreferences(Context context) {
        String config_str = getConfigStringFromPreference(context, PuTaoConstants.PREFERENC_WATERMARK_JSON);
        mWaterMarkConfigInfo = null;
        try {
            Gson gson = new Gson();
            mWaterMarkConfigInfo = gson.fromJson(config_str, WaterMarkConfigInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WaterMarkConfigInfo getWaterMarkConfigInfoFromPreference(Context context) {
        if (mWaterMarkConfigInfo == null) {
            String config_str = getConfigStringFromPreference(context, PuTaoConstants.PREFERENC_WATERMARK_JSON);
            try {
                Gson gson = new Gson();
                mWaterMarkConfigInfo = gson.fromJson(config_str, WaterMarkConfigInfo.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mWaterMarkConfigInfo;
    }

    public static WaterMarkConfigInfo getWaterMarkConfigInfoFromDB(Context context, boolean isDesc) {
        try {
            mWaterMarkConfigInfo = new WaterMarkConfigInfo();
            mWaterMarkConfigInfo.version = SharedPreferencesHelper.readStringValue(context, PuTaoConstants.PREFERENC_WATER_MARK_CONFIGINFO_VERSION,
                    "1");
            Map<String, String> map = new HashMap<String, String>();
            map.put("type", WaterMarkCategoryInfo.camera);
            List<WaterMarkCategoryInfo> camera_water_list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map, isDesc);
            mWaterMarkConfigInfo.content.camera_watermark.addAll(camera_water_list);
            getWaterMarkIconInfos(mWaterMarkConfigInfo.content.camera_watermark);
            map = new HashMap<String, String>();
            map.put("type", WaterMarkCategoryInfo.photo);
            mWaterMarkConfigInfo.content.photo_watermark.addAll(MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map, isDesc));
            getWaterMarkIconInfos(mWaterMarkConfigInfo.content.photo_watermark);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mWaterMarkConfigInfo;
    }

    public static WaterMarkConfigInfo getWaterMarkConfigInfoFromDB(Context context) {
        try {
            mWaterMarkConfigInfo = new WaterMarkConfigInfo();
            mWaterMarkConfigInfo.version = SharedPreferencesHelper.readStringValue(context, PuTaoConstants.PREFERENC_WATER_MARK_CONFIGINFO_VERSION,
                    "1");
            Map<String, String> map = new HashMap<String, String>();
            map.put("type", WaterMarkCategoryInfo.camera);
            List<WaterMarkCategoryInfo> camera_water_list = MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map);
            mWaterMarkConfigInfo.content.camera_watermark.addAll(camera_water_list);
            getWaterMarkIconInfos(mWaterMarkConfigInfo.content.camera_watermark);
            map = new HashMap<String, String>();
            map.put("type", WaterMarkCategoryInfo.photo);
            mWaterMarkConfigInfo.content.photo_watermark.addAll(MainApplication.getDBServer().getWaterMarkCategoryInfoByWhere(map));
            getWaterMarkIconInfos(mWaterMarkConfigInfo.content.photo_watermark);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mWaterMarkConfigInfo;
    }

    public static void getWaterMarkIconInfos(ArrayList<WaterMarkCategoryInfo> infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            WaterMarkCategoryInfo info = infoList.get(i);
            Map<String, String> map = new HashMap<String, String>();
            map.put("categoryId", info.id);
            List<WaterMarkIconInfo> list = MainApplication.getDBServer().getWaterMarkIconInfoByWhere(map);
            info.elements.addAll(list);
        }
    }
    public static void getStickerUnZipInfos(ArrayList<StickerCategoryInfo> infoList) {
        for (int i = 0; i < infoList.size(); i++) {
            StickerCategoryInfo info = infoList.get(i);
            Map<String, String> map = new HashMap<String, String>();
            map.put("parentid", info.id);
            List<StickerUnZipInfo> list = MainApplication.getDBServer().getStickerUnZipInfoByWhere(map);
            info.elements.addAll(list);
        }
    }

    private static String getConfigStringFromPreference(Context context, String PreferenceKey) {
        String config_str = SharedPreferencesHelper.readStringValue(context, PreferenceKey);
        // json.replaceAll(regularExpression, replacement)
        if (!StringHelper.isEmpty(config_str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(config_str);
            config_str = m.replaceAll("");
        }
        return config_str;
    }

    /**
     * @param ConfigInfo
     * @param isInner    是否是内置,"1"表示内置,"0"表示是下载的
     */
    public static void saveCategoryInfoToDb(WaterMarkConfigInfo ConfigInfo, String isInner) {
        try {
            SharedPreferencesHelper.saveStringValue(MainApplication.getInstance().getApplicationContext(),
                    PuTaoConstants.PREFERENC_WATER_MARK_CONFIGINFO_VERSION, ConfigInfo.version);
            WaterMarkHelper.saveCategoryInfoToDb(ConfigInfo.content.photo_watermark, WaterMarkCategoryInfo.photo, isInner);
            WaterMarkHelper.saveCategoryInfoToDb(ConfigInfo.content.camera_watermark, WaterMarkCategoryInfo.camera, isInner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 水印分类数组存储
     *
     * @param infos
     * @param type    是相机的水印,还是相册的水印,WaterMarkCategoryInfo.photo 或者WaterMarkCategoryInfo.camera
     * @param isInner 是否是内置:"1"表示是内置的,"0"表示是下载的
     */
    public static void saveCategoryInfoToDb(ArrayList<WaterMarkCategoryInfo> infos, String type, String isInner) {
        for (int i = 0; i < infos.size(); i++) {
            WaterMarkCategoryInfo categoryInfo = infos.get(i);
            saveCategoryInfoToDb(categoryInfo, type, isInner);
        }
    }

    /**
     * 单个水印分类存储
     *
     * @param aWaterMarkCategoryInfo
     * @param type                   是相机的水印,还是相册的水印,WaterMarkCategoryInfo.photo 或者WaterMarkCategoryInfo.camera
     * @param isInner                是否是内置:"1"表示是内置的,"0"表示是下载的
     */
    public static void saveCategoryInfoToDb(WaterMarkCategoryInfo aWaterMarkCategoryInfo, String type, String isInner) {
        aWaterMarkCategoryInfo.type = type;
        aWaterMarkCategoryInfo.isInner = isInner;
        MainApplication.getDBServer().addWaterMarkCategoryInfo(aWaterMarkCategoryInfo);
        ArrayList<WaterMarkIconInfo> elements = aWaterMarkCategoryInfo.elements;
        for (int j = 0; j < elements.size(); j++) {
            WaterMarkIconInfo info = elements.get(j);
            info.categoryId = aWaterMarkCategoryInfo.id;
            //            if (info.textElements != null && info.textElements.size() > 0) {
            //chen 存储 WaterText
            //info.textElement = info.textElements.toString()
            //            }
            MainApplication.getDBServer().addWaterMarkIconInfo(info);
        }
    }

    public static void saveCategoryInfoToDb(StickerIconInfo mStickerIconInfo) {
        DatabaseServer dbServer = MainApplication.getDBServer();
        dbServer.addStickerIconInfo(mStickerIconInfo);
        ArrayList<StickerCategoryInfo> data = mStickerIconInfo.data;
        for (int j = 0; j < data.size(); j++) {
            StickerCategoryInfo info = data.get(j);
            info.categoryId = mStickerIconInfo.id;
            //            if (info.textElements != null && info.textElements.size() > 0) {
            //chen 存储 WaterText
            //info.textElement = info.textElements.toString()
            //            }
            MainApplication.getDBServer().addStickerCategoryInfo(info);
        }
    }



}
