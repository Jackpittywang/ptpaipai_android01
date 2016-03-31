
package com.putao.camera.collage.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageCategoryInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageImageInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.bean.CollageConfigInfo.CollageText;
import com.putao.camera.bean.CollageConfigInfo.ConnectImageInfo;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.db.CollageDBHelper;
import com.putao.camera.db.ConnectDBHelper;
import com.putao.camera.util.FileOperationHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.SharedPreferencesHelper;
import com.putao.camera.util.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollageHelper {
    // 文件夹名称必须和解压包解压出来的名称保持一致。
    public static String getCollageFilePath() {
        return FileOperationHelper.getExternalFilePath() + PuTaoConstants.PAIPAI_COLLAGE_RESOURCE_PATH;
    }

    // 拼图增量包路径
    public static String getCollageUnzipFilePath() {
        return FileOperationHelper.getExternalFilePath() + PuTaoConstants.PAIPAI_COLLAGE_UPDATE_PACKAGE_PATH;
    }
    public static String getStickerUnzipFilePath() {
        return FileOperationHelper.getExternalFilePath() + PuTaoConstants.PAIPAI_STICKER_UPDATE_PACKAGE_PATH;
    }
    public static String getTemplateUnzipFilePath() {
        return FileOperationHelper.getExternalFilePath() + PuTaoConstants.PAIPAI_TEMPLATE_UPDATE_PACKAGE_PATH;
    }
    public static String getDynamicUnzipFilePath() {
        return FileOperationHelper.getExternalFilePath() + PuTaoConstants.PAIPAI_DYNAMIC_UPDATE_PACKAGE_PATH;
    }
    public static CollageConfigInfo getCollageConfigInfoFromExternalFile() {
        try {
            String config_str = FileOperationHelper
                    .readJsonFile(PuTaoConstants.PAIPAI_COLLAGE_FLODER_NAME, PuTaoConstants.PAIPAI_COLLAGE_CONFIG_NAME);
            Gson gson = new Gson();
            CollageConfigInfo mCollageConfigInfo = gson.fromJson(config_str, CollageConfigInfo.class);
            return mCollageConfigInfo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CollageConfigInfo getCollageConfigInfoFromPreferences(Context context) {
        String config_str = getConfigStringFromPreference(context, PuTaoConstants.PREFERENC_COLLAGE_CONFIG_JSON);
        CollageConfigInfo mCollageConfigInfo = null;
        try {
            Gson gson = new Gson();
            mCollageConfigInfo = gson.fromJson(config_str, CollageConfigInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCollageConfigInfo;
    }

    public static CollageConfigInfo getCollageConfigInfoFromDB(Context context) {
        CollageConfigInfo mCollageConfigInfo = new CollageConfigInfo();
        mCollageConfigInfo.version = SharedPreferencesHelper.readStringValue(context, PuTaoConstants.PREFERENC_COLLAGE_SRC_VERSION_CODE, "1");

        ArrayList<CollageCategoryInfo> infos = getCollageCategoryInfoFromDB(null, true);
        mCollageConfigInfo.content.collage_image.addAll(infos);

        List<ConnectImageInfo> connectIfos = ConnectDBHelper.getInstance().queryList(null, "_id");
        mCollageConfigInfo.content.connect_image.addAll(connectIfos);
        return mCollageConfigInfo;
    }

    public static ArrayList<CollageCategoryInfo> getCollageCategoryInfoFromDB(Map<String, String> map, boolean isDesc) {
        ArrayList<CollageCategoryInfo> infos = new ArrayList<CollageCategoryInfo>();

        List<CollageItemInfo> allInfos = CollageDBHelper.getInstance().queryList(map, "_id", isDesc);
        Gson gson = new Gson();
        for (int i = 0; i < allInfos.size(); i++) {
            CollageItemInfo info = allInfos.get(i);
            info.textElements = gson.fromJson(info.textElementsGson, new TypeToken<ArrayList<CollageText>>() {
            }.getType());
            info.imageElements = gson.fromJson(info.imageElementsGson, new TypeToken<ArrayList<CollageImageInfo>>() {
            }.getType());
            boolean isExist = false;
            for (int j = 0; j < infos.size(); j++) {
                if (info.parentCategory.equals(infos.get(j).category)) {
                    // 有元素CollageCategoryInfo,扩充CollageCategoryInfo的elements
                    isExist = true;
                    infos.get(j).elements.add(info);
                    break;
                }
            }
            if (!isExist) {
                CollageCategoryInfo CategoryInfo = new CollageCategoryInfo();
                CategoryInfo.id = info.parentId;
                CategoryInfo.category = info.parentCategory;
                CategoryInfo.elements.add(info);
                infos.add(CategoryInfo);
            }
        }
        return infos;
    }

    public static int saveCollageConfigInfoToDB(Context context, CollageConfigInfo info, String isInner) {
        Loger.d("save collage to db............");
        int result = -1;
        SharedPreferencesHelper.saveStringValue(context, PuTaoConstants.PREFERENC_COLLAGE_SRC_VERSION_CODE, info.version);
        // 存储 collage_image的信息
//        ArrayList<CollageCategoryInfo> collages = info.content.collage_image;
        ArrayList<CollageCategoryInfo> collages = info.content.collage_image;
        Gson gson = new Gson();
        int g=collages.size();
        for (int i = 0; i < collages.size(); i++) {
            CollageCategoryInfo collage = collages.get(i);
            for (int j = 0; j < collage.elements.size(); j++) {
                CollageItemInfo mItemInfo = collage.elements.get(j);
                mItemInfo.textElementsGson = gson.toJson(collage.elements.get(j).textElements);
                mItemInfo.imageElementsGson = gson.toJson(collage.elements.get(j).imageElements);
                mItemInfo.parentId = collage.id;
                mItemInfo.parentCategory = collage.category;
                mItemInfo.isInner = isInner;
                result = CollageDBHelper.getInstance().insert(mItemInfo);
                if (result == -1) {
                    return result;
                }
            }
        }
        // 存储 collage_image的信息
        ArrayList<ConnectImageInfo> connects = info.content.connect_image;
        for (int i = 0; i < connects.size(); i++) {
            ConnectDBHelper.getInstance().insert(connects.get(i));
        }
        return result;
    }


    private static String getConfigStringFromPreference(Context context, String PreferenceKey) {
        String config_str = SharedPreferencesHelper.readStringValue(context, PreferenceKey);
        if (!StringHelper.isEmpty(config_str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(config_str);
            config_str = m.replaceAll("");
        }
        return config_str;
    }
}
