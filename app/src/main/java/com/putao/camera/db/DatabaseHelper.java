package com.putao.camera.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.putao.ahibernate.table.TableUtils;
import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.CollageConfigInfo.CollageItemInfo;
import com.putao.camera.bean.CollageConfigInfo.ConnectImageInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.bean.WaterMarkIconInfo;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "putao.camera.db";

    static DatabaseHelper mInstance;

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }

    ;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, MainApplication.getVersionCode());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            TableUtils.createTable(db, true, WaterMarkIconInfo.class);
            TableUtils.createTable(db, true, WaterMarkCategoryInfo.class);
            TableUtils.createTable(db, true, CollageItemInfo.class);
            TableUtils.createTable(db, true, ConnectImageInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            // 安装新版本,必要时,扩展数据库或者清除数据
            case 6:
                TableUtils.dropTable(db, WaterMarkIconInfo.class);
                TableUtils.dropTable(db, WaterMarkCategoryInfo.class);
                TableUtils.dropTable(db, CollageItemInfo.class);
                TableUtils.dropTable(db, ConnectImageInfo.class);
                onCreate(db);
                // 不需要 break
            case 7:

                // 不需要 break
            case 8:

                // 不需要 break
            default:
                break;
        }
    }
}
