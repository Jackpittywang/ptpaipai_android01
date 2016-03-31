
package com.putao.camera.db;

import com.putao.ahibernate.dao.AhibernateDao;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.DynamicIconInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.util.StringHelper;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicDBHelper {
    static private AhibernateDao<DynamicIconInfo> mDao;
    static private DynamicDBHelper mInstance;

    public synchronized static DynamicDBHelper getInstance() {
        if (mInstance == null) {
            mInstance = new DynamicDBHelper();
            mDao = new AhibernateDao<DynamicIconInfo>(MainApplication.getSQLiteDatabase());
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
    public synchronized int insert(DynamicIconInfo info) {

        if (StringHelper.isEmpty(info.cover_pic)) {
            throw new RuntimeException("sample_image的值不能为空");
        }
//        List<DynamicIconInfo > infos = queryList(info);
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", String.valueOf(info.id));

        List<DynamicIconInfo > infos = queryList(info);
        if (infos.size() > 0) {
            // 已经存在
            return -1;
        }
        int resault = mDao.insert(info);
        return resault;
    }

    public List<DynamicIconInfo> queryList(Map<String, String> where) {
        return mDao.queryList(DynamicIconInfo.class, where);
    }

    public List<DynamicIconInfo> queryList(Map<String, String> where, String orderColume) {
        return mDao.queryList(DynamicIconInfo.class, where, "_id", true);
    }

    public List<DynamicIconInfo> queryList(Map<String, String> where, String orderColume, boolean isDesc) {
        return mDao.queryList(DynamicIconInfo.class, where, "_id", true);
    }

    public List<DynamicIconInfo > queryList(DynamicIconInfo  entity) {
        return mDao.queryList(entity);
    }

    public void update(DynamicIconInfo  entity, Map<String, String> where) {
        mDao.update(entity, where);
    }

    public void update(DynamicIconInfo  entity) {
        mDao.update(entity);
    }

    public void delete(Map<String, String> where) {
        mDao.delete(DynamicIconInfo .class, where);
    }

    public void delete(DynamicIconInfo  entity) {
        mDao.delete(entity);
        deleteFile(entity);
    }

    public void truncate() {
        mDao.truncate(DynamicIconInfo .class);
    }

    public AhibernateDao<DynamicIconInfo > getDao() {
        return mDao;
    }

    private void deleteFile(DynamicIconInfo  entity) {
        try {
//            new File(CollageHelper.getCollageFilePath() + entity.mask_image).delete();
            new File(CollageHelper.getCollageFilePath() + entity.cover_pic).delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
