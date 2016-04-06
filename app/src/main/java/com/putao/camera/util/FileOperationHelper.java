
package com.putao.camera.util;

import android.content.res.AssetManager;

import com.putao.camera.application.MainApplication;
import com.sunnybear.library.util.Logger;

import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public abstract class FileOperationHelper {
    public static boolean copyAssetsFileToExternalFile(String assetFileName) {
        boolean bSuccess = false;
        AssetManager assetManager = MainApplication.getInstance().getAssets();
        String filename = assetFileName;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File(getExternalFilePath(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            bSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bSuccess;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static boolean copyFileToExternalFile(String assetFileName) {
        boolean bSuccess = false;
        AssetManager assetManager = MainApplication.getInstance().getAssets();
        String filename = assetFileName;
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            File outFile = new File(getExternalFilePath(), filename);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            bSuccess = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bSuccess;
    }

    public static void unZipFile(File zipFile, String unZipFilePath) throws ZipException, IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = unZipFilePath + "/" + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(unZipFilePath, ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    public static void unZipFile(File zipFile) throws ZipException, IOException {
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = zipFile.getParent() + "/" + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(zipFile.getParent(), ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
    }

    public double unZipFileWithProgress(File zipFile) throws ZipException, IOException {
        double blockSize = getFileSizes(zipFile) / 1024;
        ZipFile zfile = new ZipFile(zipFile);
        Enumeration zList = zfile.entries();
        ZipEntry ze = null;
        byte[] buf = new byte[1024];
        while (zList.hasMoreElements()) {
            ze = (ZipEntry) zList.nextElement();
            if (ze.isDirectory()) {
                String dirstr = zipFile.getParent() + "/" + ze.getName();
                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                File f = new File(dirstr);
                f.mkdir();
                upZipProgress(ze.getName());
                continue;
            }
            OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(zipFile.getParent(), ze.getName())));
            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
            int readLen = 0;
            while ((readLen = is.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, readLen);
            }
            is.close();
            os.close();
        }
        zfile.close();
        return blockSize;
    }

    public abstract void upZipProgress(String name);

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static double getFileSizes(File f) throws IOException {
        double size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static double getFileSize(File file) throws IOException {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Logger.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    public static void unZipFilePath(File zipFile) throws IOException {
        unZipFile(zipFile);
    }

    public static void unZipFilePath(String zipFilePath) throws ZipException, IOException {
        File zipFile = new File(zipFilePath);
        unZipFile(zipFile);
    }

    public static void unZipFile(String zipFileName) throws ZipException, IOException {
        File zipFile = new File(MainApplication.getInstance().getExternalFilesDir(null), zipFileName);
        unZipFile(zipFile);
    }

    public static void unZipFile(String floderPath, String zipFileName) throws ZipException, IOException {
        File zipFile = new File(floderPath, zipFileName);
        unZipFile(zipFile);
    }

    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     *
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    //substr.trim();
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = new File(ret, substr);
            }
            //             Log.d("upZipFile", "1ret = "+ret);
            if (!ret.exists())
                ret.mkdirs();
            substr = dirs[dirs.length - 1];
            try {
                //substr.trim();
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                //                 Log.d("upZipFile", "substr = "+substr);
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            //             Log.d("upZipFile", "2ret = "+ret);
            return ret;
        }
        return ret;
    }

    /**
     * 复制整个文件夹内容
     *
     * @param oldPath String 原文件路径 如：c:/fqf
     * @param newPath String 复制后路径 如：f:/fqf/ff
     * @return boolean
     */
    public static boolean copyFolder(String oldPath, String newPath) {
        boolean isok = true;
        try {
            (new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
            File a = new File(oldPath);
            String[] file = a.list();
            File temp = null;
            for (int i = 0; i < file.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + file[i]);
                } else {
                    temp = new File(oldPath + File.separator + file[i]);
                }
                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
                    byte[] b = new byte[1024 * 5];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//如果是子文件夹
                    copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
                }
            }
        } catch (Exception e) {
            isok = false;
        }
        return isok;
    }

    /**
     * 读取文件
     *
     * @param dirPath
     * @param fileName
     * @return
     */
    public static String readJsonFile(String zipFloderName, String fileName) {
        String reslt = "";
        try {
            String path = getExternalFilePath() + "/" + zipFloderName;
            File jsonFile = new File(path, fileName);
            FileInputStream fin = new FileInputStream(jsonFile);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            reslt = EncodingUtils.getString(buffer, "UTF-8");
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reslt;
    }

    public static String getExternalFilePath() {
        String path = null;
        try {
            path = MainApplication.getInstance().getExternalFilesDir(null).getPath();
        } catch (Exception e) {
            e.printStackTrace();
            path = MainApplication.getInstance().getFilesDir().getPath();
        }

        File noMedia = new File(path, ".nomedia");
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return path;
    }
    //    //文件夹名称必须和解压包解压出来的名称保持一致。
    //    public static String getWaterMarkFilePath()
    //    {
    //        String floderName = "/watermark/";
    //        return getExternalFilePath() + floderName;
    //    }
    //
    //    //水印增量包路径
    //    public static String getWaterMarkUnzipFilePath()
    //    {
    //        String floderName = "/watermark_unzip/";
    //        return getExternalFilePath() + floderName;
    //    }
    //
    //    public static WaterMarkConfigInfo getWaterMarkConfigInfo()
    //    {
    //        try
    //        {
    //            String config_str = FileOperationHelper.readJsonFile("watermark", "watermark_config.json");
    //            JSONObject json = new JSONObject(config_str);
    //            JSONObject configurationObj = json.getJSONObject("configuration");
    //            Gson gson = new Gson();
    //            WaterMarkConfigInfo mWaterMarkConfigInfo = gson.fromJson(configurationObj.toString(), WaterMarkConfigInfo.class);
    //            return mWaterMarkConfigInfo;
    //        }
    //        catch (Exception e)
    //        {
    //            e.printStackTrace();
    //            return null;
    //        }
    //    }
}
