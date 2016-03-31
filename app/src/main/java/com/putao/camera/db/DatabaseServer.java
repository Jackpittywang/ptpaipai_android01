
package com.putao.camera.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.putao.ahibernate.dao.AhibernateDao;
import com.putao.camera.bean.StickerCategoryInfo;
import com.putao.camera.bean.StickerIconInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkIconInfo;
import com.putao.camera.collage.util.CollageHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseServer {
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mSQLiteDatabase;
    private AhibernateDao<WaterMarkIconInfo> mWaterMarkIconInfo;
    private AhibernateDao<WaterMarkCategoryInfo> mWaterMarkCategoryInfo;
    private AhibernateDao<StickerCategoryInfo> mStickerCategoryInfo;
    private AhibernateDao<StickerIconInfo> mStickerIconInfo;

    public DatabaseServer(Context context) {
        this.mContext = context;
        this.mDatabaseHelper = DatabaseHelper.getInstance(mContext);
        this.mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
        this.mWaterMarkIconInfo = new AhibernateDao<WaterMarkIconInfo>(this.mSQLiteDatabase);
        this.mWaterMarkCategoryInfo = new AhibernateDao<WaterMarkCategoryInfo>(this.mSQLiteDatabase);
        this.mStickerCategoryInfo = new AhibernateDao<StickerCategoryInfo>(this.mSQLiteDatabase);
        this.mStickerIconInfo = new AhibernateDao<StickerIconInfo>(this.mSQLiteDatabase);
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return mSQLiteDatabase;
    }

    /**
     * ===================WaterMarkIconInfo begin===========================
     */
    public List<WaterMarkIconInfo> getWaterMarkIconInfoByWhere(Map<String, String> where) {
        List<WaterMarkIconInfo> list = mWaterMarkIconInfo.queryList(WaterMarkIconInfo.class, where);
        return list;
    }

    public List<WaterMarkIconInfo> getAllWaterMarkIconInfo() {
        List<WaterMarkIconInfo> list = mWaterMarkIconInfo.queryList(WaterMarkIconInfo.class, null, "_id");
        return list;
    }

    public List<WaterMarkIconInfo> getWaterMarkIconInfos(WaterMarkIconInfo iconInfo) {
        List<WaterMarkIconInfo> list = mWaterMarkIconInfo.queryList(iconInfo);
        return list;
    }

    public List<StickerCategoryInfo> getStickerCategoryInfos(StickerCategoryInfo iconInfo) {
        List<StickerCategoryInfo> list = mStickerCategoryInfo.queryList(iconInfo);
        return list;
    }

    public int addWaterMarkIconInfo(WaterMarkIconInfo iconInfo) {
        List<WaterMarkIconInfo> list = getWaterMarkIconInfos(iconInfo);
        if (list.size() > 0) {
            // 已经存在
            return -1;
        }
        return mWaterMarkIconInfo.insert(iconInfo);
    }

    public int addStickerCategoryInfo(StickerCategoryInfo iconInfo) {
        List<StickerCategoryInfo> list = getStickerCategoryInfos(iconInfo);
        if (list.size() > 0) {
            // 已经存在
            return -1;
        }
        return mStickerCategoryInfo.insert(iconInfo);
    }


    public void updateWaterMarkIconInfo(WaterMarkIconInfo iconInfo, Map<String, String> where) {
        mWaterMarkIconInfo.update(iconInfo, where);
    }

    public void deleteWaterMarkIconInfo(Map<String, String> where) {
        mWaterMarkIconInfo.delete(WaterMarkIconInfo.class, where);
    }

    public void deleteWaterMarkIconInfo(WaterMarkIconInfo entity) {
        mWaterMarkIconInfo.delete(entity);
        try {
            new File(CollageHelper.getCollageFilePath() + entity.sample_image).delete();
            new File(CollageHelper.getCollageFilePath() + entity.watermark_image).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AhibernateDao<WaterMarkIconInfo> getWaterMarkIconInfoDao() {
        return mWaterMarkIconInfo;
    }

    /** ===================WaterMarkIconInfo end=============================== */
    /**
     * ===================WaterMarkCategoryInfo begin===========================
     */
    /**
     * @param where
     * @return
     */
    public List<WaterMarkCategoryInfo> getWaterMarkCategoryInfoByWhere(Map<String, String> where, boolean isDesc) {
        List<WaterMarkCategoryInfo> list = mWaterMarkCategoryInfo.queryList(WaterMarkCategoryInfo.class, where, "_id", isDesc);
        return list;
    }

    public List<WaterMarkCategoryInfo> getWaterMarkCategoryInfoByWhere(Map<String, String> where) {
        List<WaterMarkCategoryInfo> list = mWaterMarkCategoryInfo.queryList(WaterMarkCategoryInfo.class, where, "_id", true);
        return list;
    }
    public List<StickerIconInfo> getStickerIconInfoByWhere(Map<String, String> where) {
        List<StickerIconInfo> list = mStickerIconInfo.queryList(StickerIconInfo.class, where, "_id", true);
        return list;
    }

    public List<WaterMarkCategoryInfo> getAllWaterMarkCategoryInfo() {
        List<WaterMarkCategoryInfo> list = mWaterMarkCategoryInfo.queryList(WaterMarkCategoryInfo.class, null, "_id");
        return list;
    }

    public List<WaterMarkCategoryInfo> getWaterMarkCategoryInfos(WaterMarkCategoryInfo iconInfo) {
        List<WaterMarkCategoryInfo> list = mWaterMarkCategoryInfo.queryList(iconInfo);
        return list;
    }

    public List<StickerIconInfo> getStickerIconInfos(StickerIconInfo iconInfo) {
        List<StickerIconInfo> list = mStickerIconInfo.queryList(iconInfo);
        return list;
    }

    public int addWaterMarkCategoryInfo(WaterMarkCategoryInfo iconInfo) {
        List<WaterMarkCategoryInfo> list = getWaterMarkCategoryInfos(iconInfo);
        if (list.size() > 0) {
            // 已经存在
            return -1;
        }
        return mWaterMarkCategoryInfo.insert(iconInfo);
    }

    public int addStickerIconInfo(StickerIconInfo iconInfo) {
        List<StickerIconInfo> list = getStickerIconInfos(iconInfo);
        if (list.size() > 0) {
            // 已经存在
            return -1;
        }
        return mStickerIconInfo.insert(iconInfo);
    }

    public void updateWaterMarkCategoryInfo(WaterMarkCategoryInfo iconInfo, Map<String, String> where) {
        mWaterMarkCategoryInfo.update(iconInfo, where);
    }

    public void deleteWaterMarkCategoryInfo(Map<String, String> where) {
        mWaterMarkCategoryInfo.delete(WaterMarkCategoryInfo.class, where);
    }

    public void deleteWaterMarkCategoryInfo(WaterMarkCategoryInfo entity) {
        mWaterMarkCategoryInfo.delete(entity);
        try {
            new File(CollageHelper.getCollageFilePath() + entity.icon).delete();
            new File(CollageHelper.getCollageFilePath() + entity.icon_selected).delete();
            //            new File(CollageHelper.getCollageFilePath() + entity.sample_image).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 继续删除对应的IconInfo
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", entity.id);
        List<WaterMarkIconInfo> list = getWaterMarkIconInfoByWhere(map);
        for (int i = 0; i < list.size(); i++) {
            deleteWaterMarkIconInfo(list.get(i));
        }
    }

    public AhibernateDao<WaterMarkCategoryInfo> getWaterMarkCategoryInfoDao() {
        return mWaterMarkCategoryInfo;
    }
    /**
     * ===================WaterMarkCategoryInfo
     * end===============================
     */
}
