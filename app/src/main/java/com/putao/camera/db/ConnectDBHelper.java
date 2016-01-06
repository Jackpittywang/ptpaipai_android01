package com.putao.camera.db;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.putao.ahibernate.dao.AhibernateDao;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.CollageConfigInfo.ConnectImageInfo;
import com.putao.camera.collage.util.CollageHelper;

public class ConnectDBHelper {
    static private AhibernateDao<ConnectImageInfo> mDao;

    private static ConnectDBHelper mInstance;

    public synchronized static ConnectDBHelper getInstance() {
        if (mInstance == null) {
            mInstance = new ConnectDBHelper();
            mDao = new AhibernateDao<ConnectImageInfo>(MainApplication.getSQLiteDatabase());
        }
        return mInstance;
    }

    ;

    public int insert(ConnectImageInfo info) {
        List<ConnectImageInfo> infos = queryList(info);
        if (infos.size() > 0) {
            // 已经存在
            return -1;
        }

        int resault = mDao.insert(info);
        return resault;
    }

    public List<ConnectImageInfo> queryList(Map<String, String> where) {

        return mDao.queryList(ConnectImageInfo.class, where);
    }

    public List<ConnectImageInfo> queryList(Map<String, String> where, String orderColume) {
        return mDao.queryList(ConnectImageInfo.class, where, orderColume);
    }

    public List<ConnectImageInfo> queryList(ConnectImageInfo entity) {
        return mDao.queryList(entity);
    }

    public void update(ConnectImageInfo entity, Map<String, String> where) {
        mDao.update(entity, where);
    }

    public void update(ConnectImageInfo entity) {
        mDao.update(entity);
    }

    public void delete(Map<String, String> where) {
        mDao.delete(ConnectImageInfo.class, where);
    }

    public void delete(ConnectImageInfo entity) {
        mDao.delete(entity);
        deleteFile(entity);
    }

    public void truncate() {
        mDao.truncate(ConnectImageInfo.class);
    }

    public AhibernateDao<ConnectImageInfo> getDao() {
        return mDao;
    }

    private void deleteFile(ConnectImageInfo entity) {
        try {
            new File(CollageHelper.getCollageFilePath() + entity.background_image).delete();
            new File(CollageHelper.getCollageFilePath() + entity.mask_image).delete();
            new File(CollageHelper.getCollageFilePath() + entity.sample_image).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
