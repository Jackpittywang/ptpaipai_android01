
package com.putao.camera.downlad;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Handler;

import android.os.Bundle;

import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.Loger;

/**
 * Created by yanglun on 15/3/5.
 */
public class DownloadThread extends Thread {
    private String filename, filePath;
    private boolean isBreakpointConn = true;
    private int filesize;
    public static final int bufferSize = 512 * 1024;
    private File saveFile;
    private int errCode = 0, speed, newProgress;
    private int downLoadFileSize;
    private boolean isCancel = false;
    private String mUrl, mFloderPath;
    private static int TYPE_DOWNLOADING = 0, TYPE_FINISH = 1;

    public DownloadThread(String url, String floderPath) {
        this.mUrl = url;
        this.mFloderPath = floderPath;
    }

    public DownloadThread(String url, String floderPath, Handler handler) {
        this.mUrl = url;
        this.mFloderPath = floderPath;
    }

    public void run() {
        try {
            downloadFile(this.mUrl, this.mFloderPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                if (curTime - time > 1000) {
                    Loger.i("finishedSize:" + finishedSize);
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
            sendMsg(TYPE_FINISH);
        }
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
            // ?????
            int newProgress = (int) (((double) finishedSize / filesize) * 100);
            long curTime = System.currentTimeMillis();
            if (newProgress - progress > 0) {
                if (curTime - time > 1000) {
                    speed = (int) (((lencount * 1000) >> 10) / (curTime - time));
                    lencount = 0;
                    time = curTime;
                    downLoadFileSize = finishedSize;
                    Loger.i("downLoadFileSize:" + downLoadFileSize);
                    sendMsg(TYPE_DOWNLOADING);
                }
            }
            progress = newProgress;
        }
        if (isCancel && finishedSize != filesize) {
            Loger.d("22222222finished size not equal filesize");
        } else {
            // ????
            File newFile = new File(saveFile.getAbsolutePath().replace(".tmp", ".zip"));
            saveFile.renameTo(newFile);
            saveFile = newFile;
            sendMsg(TYPE_FINISH);
        }
    }

    private void sendMsg(int type) {
        if (type == TYPE_FINISH) {
            Bundle bundle = new Bundle();
            bundle.putString("save_file_path", saveFile.getPath());
            bundle.putString("save_file_name", saveFile.getName());
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.DOWNLOAD_FILE_FINISH, bundle));
        } else if (type == TYPE_DOWNLOADING) {
            Loger.i("sendMsg TYPE_DOWNLOADING:" + newProgress);
            Bundle bundle = new Bundle();
            bundle.putInt("percent", newProgress);
            EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.DOWNLOAD_FILE_DOWNLOADING, bundle));
        }
    }
}