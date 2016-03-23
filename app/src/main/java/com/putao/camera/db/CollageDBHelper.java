
package com.putao.camera.db;

import com.putao.ahibernate.dao.AhibernateDao;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollageDBHelper {
    static private AhibernateDao<CollageItemInfo> mDao;
    static private CollageDBHelper mInstance;

    public synchronized static CollageDBHelper getInstance() {
        if (mInstance == null) {
            mInstance = new CollageDBHelper();
            mDao = new AhibernateDao<CollageItemInfo>(MainApplication.getSQLiteDatabase());
        }
        return mInstance;
    }
   /* String sql;
    sql = new Insert(entity).toStatementString();
    Log.d(TAG, "insert sql:" + sql);
    SQLiteStatement stmt = null;
    try {
        stmt = db.compileStatement(sql);
        long rowId = stmt.executeInsert();
        return 1;
    } catch (android.database.SQLException e) {
        Log.e(TAG, "inserting to database failed: " + sql, e);
        return -1;
    } finally {
        if (stmt != null) {
            stmt.close();
        }
    }*/
//   private SQLiteDatabase db;
    public synchronized int insert(CollageItemInfo info) {

        if (StringHelper.isEmpty(info.sample_image)) {
            throw new RuntimeException("sample_image的值不能为空");
        }
//        List<CollageItemInfo> infos = queryList(info);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));

        List<CollageItemInfo> infos = queryList(info);
        if (infos.size() > 0) {
            // 已经存在
            return -1;
        }
        int resault = mDao.insert(info);
        return resault;
    }

    public List<CollageItemInfo> queryList(Map<String, String> where) {
        return mDao.queryList(CollageItemInfo.class, where);
    }

    public List<CollageItemInfo> queryList(Map<String, String> where, String orderColume) {
        return mDao.queryList(CollageItemInfo.class, where, "_id", true);
    }

    public List<CollageItemInfo> queryList(Map<String, String> where, String orderColume, boolean isDesc) {
        return mDao.queryList(CollageItemInfo.class, where, "_id", true);
    }

    public List<CollageItemInfo> queryList(CollageItemInfo entity) {
        return mDao.queryList(entity);
    }

    public void update(CollageItemInfo entity, Map<String, String> where) {
        mDao.update(entity, where);
    }

    public void update(CollageItemInfo entity) {
        mDao.update(entity);
    }

    public void delete(Map<String, String> where) {
        mDao.delete(CollageItemInfo.class, where);
    }

    public void delete(CollageItemInfo entity) {
        mDao.delete(entity);
        deleteFile(entity);
    }

    public void truncate() {
        mDao.truncate(CollageItemInfo.class);
    }

    public AhibernateDao<CollageItemInfo> getDao() {
        return mDao;
    }

    private void deleteFile(CollageItemInfo entity) {
        try {
            new File(CollageHelper.getCollageFilePath() + entity.mask_image).delete();
            new File(CollageHelper.getCollageFilePath() + entity.sample_image).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
