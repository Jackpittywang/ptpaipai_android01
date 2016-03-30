
package com.putao.camera.downlad;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.google.gson.Gson;
import com.putao.camera.bean.CollageConfigInfo;
import com.putao.camera.bean.WaterMarkCategoryInfo;
import com.putao.camera.collage.util.CollageHelper;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.FileOperationHelper;
import com.putao.camera.util.Loger;
import com.putao.camera.util.WaterMarkHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by yanglun on 15/3/10.
 */
public class DownloadFileService extends Service {
    private String filename, filePath;
    private boolean isBreakpointConn = true;
    private int filesize;
    public static final int bufferSize = 512 * 1024;
    private File saveFile;
    private int errCode = 0, speed, newProgress;
    private int downLoadFileSize;
    private boolean isCancel = false;
    private String mUrl, mFloderPath;
    private static int TYPE_DOWNLOADING = 0, TYPE_DOWNLOAD_FINISH = 1, TYPE_UNZIP_FINISH = 2;
    private MyBinder mBinder = new MyBinder();
    private int mPosition;
    public static int DOWNLOAD_TYPE_WATER_MARK = 0;
    public static int DOWNLOAD_TYPE_COLLAGE = 1;
    public static int DOWNLOAD_TYPE_TEMPLATE = 2;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void downloadFile(String url, String floderPath) throws IOException {
        URL downloadURL = new URL(url);
        String fileName = "";
        if (url.indexOf(".zip") != -1) {
            fileName = url.substring(url.lastIndexOf("/") + 1);
        }
        this.filePath = floderPath + fileName;
        saveFile = new File(filePath.replace(".zip", ".tmp"));
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();

        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        RandomAccessFile fos = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) downloadURL.openConnection();
            if (isBreakpointConn) {
                conn.setAllowUserInteraction(true);
                conn.setRequestProperty("Range", "bytes=" + saveFile.length() + "-");
            }
            int statucode = conn.getResponseCode();
            switch (statucode) {
                case HttpURLConnection.HTTP_OK: {
                    if (saveFile.exists()) {
                        saveFile.delete();
                        saveFile.createNewFile();
                    }
                    bis = new BufferedInputStream(conn.getInputStream(), bufferSize);
                    bos = new BufferedOutputStream(new FileOutputStream(saveFile), bufferSize);
                    this.filesize = conn.getContentLength();
                    Loger.i("filesize HTTP_OK:" + filesize);
                    readFromInputStream(bis, bos);
                    break;
                }
                case HttpURLConnection.HTTP_PARTIAL: {
                    bis = new BufferedInputStream(conn.getInputStream(), bufferSize);
                    fos = new RandomAccessFile(saveFile, "rw");
                    filesize = (int) saveFile.length() + conn.getContentLength();
                    Loger.i("filesize HTTP_PARTIAL:" + filesize);
                    readFromInputStream(bis, fos, (int) saveFile.length());
                    break;
                }
                default:
                    errCode = statucode;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void readFromInputStream(BufferedInputStream bis, RandomAccessFile fos, int position) throws IOException {
        byte[] buf = new byte[bufferSize];
        int progress = 0;
        int finishedSize = position;
        int readLen = -1;
        long time = System.currentTimeMillis();
        int lencount = 0;
        fos.seek(position);
        // sendMsg(0);
        while ((readLen = bis.read(buf)) != -1 && !isCancel) {
            fos.write(buf, 0, readLen);
            finishedSize += readLen;
            lencount += readLen;
            newProgress = (int) (((double) finishedSize / filesize) * 100);
            long curTime = System.currentTimeMillis();
            if (newProgress - progress > 0) {
                if (curTime - time > 100) {
                    Loger.i("finishedSize:" + finishedSize);
                    Loger.i("filesize:" + filesize);
                    Loger.i("percent:" + newProgress + "%");
                    speed = (int) (((lencount * 1000) >> 10) / (curTime - time));
                    lencount = 0;
                    time = curTime;
                    downLoadFileSize = finishedSize;
                    sendMsg(TYPE_DOWNLOADING);
                }
            }
            progress = newProgress;
        }
        if (isCancel && finishedSize != filesize) {
        } else {
            File newFile = new File(saveFile.getAbsolutePath().replace(".tmp", ".zip"));
            saveFile.renameTo(newFile);
            saveFile = newFile;
            newProgress = 100;
            sendMsg(TYPE_DOWNLOAD_FINISH);
        }
        this.stopSelf();
    }

    private void readFromInputStream(BufferedInputStream bis, BufferedOutputStream bos) throws IOException {
        byte[] buf = new byte[bufferSize];
        int progress = 0;
        int finishedSize = 0;
        int readLen = -1;
        long time = System.currentTimeMillis();
        int lencount = 0;
        // sendMsg(0);
        while ((readLen = bis.read(buf)) != -1 && !isCancel) {
            bos.write(buf, 0, readLen);
            finishedSize += readLen;
            lencount += readLen;
            int newProgress = (int) (((double) finishedSize / filesize) * 100);
            long curTime = System.currentTimeMillis();
            if (newProgress - progress > 0) {
                if (curTime - time > 1000) {
                    Loger.i("finishedSize:" + finishedSize);
                    Loger.i("filesize:" + filesize);
                    Loger.i("percent:" + newProgress + "%");
                    speed = (int) (((lencount * 1000) >> 10) / (curTime - time));
                    lencount = 0;
                    time = curTime;
                    downLoadFileSize = finishedSize;
                    sendMsg(TYPE_DOWNLOADING);
                }
            }
            progress = newProgress;
        }
        if (isCancel && finishedSize != filesize) {
            Loger.d("finished size not equal filesize");
        } else {
            File newFile = new File(saveFile.getAbsolutePath().replace(".tmp", ".zip"));
            saveFile.renameTo(newFile);
            saveFile = newFile;
            sendMsg(TYPE_DOWNLOAD_FINISH);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPosition = intent.getIntExtra("position", 0);
        final String url = intent.getStringExtra("url");
        final String floderPath = intent.getStringExtra("floderPath");
        final int type = intent.getIntExtra("type", DOWNLOAD_TYPE_WATER_MARK);
        new Thread() {
            public void run() {
                try {
                    downloadFile(url, floderPath);
                    if (type == DOWNLOAD_TYPE_WATER_MARK) {
                        unZipWaterMarkFile(saveFile, "watermark_config.json");
                    } else if(type ==DOWNLOAD_TYPE_TEMPLATE){
                        unZipCollageFile(saveFile, "collage_config.json");
                    }
                    else {
                        unZipCollageFile(saveFile, "collage_config.json");
                    }
                    sendMsg(TYPE_UNZIP_FINISH);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stopSelf();
                }
            }
        }.start();
        return START_STICKY;
    }

    private void unZipWaterMarkFile(File zipFile, String unZipJsonName) {
        try {
            FileOperationHelper.unZipFilePath(zipFile);
            File file_old = new File(WaterMarkHelper.getWaterMarkFilePath());
            if (file_old.exists()) {
                file_old.delete();
            }
            String upZipFloderName = zipFile.getName().substring(0, zipFile.getName().indexOf("."));
            FileOperationHelper.copyFolder(WaterMarkHelper.getWaterMarkUnzipFilePath() + upZipFloderName, WaterMarkHelper.getWaterMarkFilePath());
            String watermark_config = FileOperationHelper.readJsonFile(PuTaoConstants.PAIPAI_WATERMARK_FLODER_NAME, unZipJsonName);
            Gson gson = new Gson();
            WaterMarkCategoryInfo mWaterMarkCategoryInfo = gson.fromJson(watermark_config, WaterMarkCategoryInfo.class);
            WaterMarkHelper.saveCategoryInfoToDb(mWaterMarkCategoryInfo, WaterMarkCategoryInfo.photo, "0");
            if (!PuTaoConstants.isDebug) {
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unZipCollageFile(File zipFile, String unZipJsonName) {
        try {
            FileOperationHelper.unZipFilePath(zipFile);
            File file_old = new File(WaterMarkHelper.getWaterMarkFilePath());
            if (file_old.exists()) {
                file_old.delete();
            }
            String upZipFloderName = zipFile.getName().substring(0, zipFile.getName().indexOf("."));
            FileOperationHelper.copyFolder(CollageHelper.getCollageUnzipFilePath() + upZipFloderName, CollageHelper.getCollageFilePath());
//            FileOperationHelper.copyFolder(CollageHelper.getTemplateUnzipFilePath() + upZipFloderName, CollageHelper.getCollageFilePath());
            String watermark_config = FileOperationHelper.readJsonFile(PuTaoConstants.PAIPAI_COLLAGE_FLODER_NAME, unZipJsonName);

//            String a = "textElements";
//            String b = "imageElements";
//            int start = watermark_config.indexOf(a);
//            int stop = watermark_config.indexOf(b);
//            String result = watermark_config.substring(0, start + a.length()) + watermark_config.substring(stop, watermark_config.length());
//            watermark_config = result.replace("textElementsimageElements", "textElements\":[],\"imageElements");

//            watermark_config = watermark_config.replace("{\"text\":\"\",\"textColor\":\"\",\"textSize\":\"\",\"textAlign\":\"\",\"left\":\"\",\"top\":\"\",\"right\":\"\",\"bottom\":\"\",\"textType\":\"\"}", "");
            Gson gson = new Gson();
            CollageConfigInfo mCollageConfigInfo = gson.fromJson(watermark_config, CollageConfigInfo.class);
            CollageHelper.saveCollageConfigInfoToDB(getBaseContext(), mCollageConfigInfo, "0");
//            CollageHelper.saveNewCollageConfigInfoToDB(getBaseContext(), mCollageConfigInfo, "1");

            if (!PuTaoConstants.isDebug) {
                zipFile.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMsg(int type) {
        if (type == TYPE_DOWNLOAD_FINISH) {
            Bundle bundle = new Bundle();
            bundle.putInt("percent", newProgress);
            bundle.putInt("position", mPosition);
            bundle.putString("save_file_path", saveFile.getPath());
            bundle.putString("save_file_name", saveFile.getName());
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.DOWNLOAD_FILE_FINISH, bundle));
        } else if (type == TYPE_DOWNLOADING) {
            Loger.i("sendMsg TYPE_DOWNLOADING:percent" + newProgress + "position:" + mPosition);
            Bundle bundle = new Bundle();
            bundle.putInt("percent", newProgress);
            bundle.putInt("position", mPosition);
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.DOWNLOAD_FILE_DOWNLOADING, bundle));
        } else if (type == TYPE_UNZIP_FINISH) {
            Bundle bundle = new Bundle();
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.UNZIP_FILE_FINISH, bundle));
        }
    }

    public class MyBinder extends Binder {
        public void startDownload(final String url, final String floderPath) {
            new Thread() {
                public void run() {
                    try {
                        downloadFile(url, floderPath);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        public void setPosition(int iPosition) {
            mPosition = iPosition;
        }
    }
}
