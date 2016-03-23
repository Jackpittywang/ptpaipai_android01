package com.putao.camera.collage.util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.putao.camera.collage.mode.GalleryEntity;
import com.putao.camera.collage.mode.PhotoGridItem;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.util.DateUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

/**
 * Created by jidongdong on 15/2/3.
 */
public class CollagePhotoUtil {

    public static final String IS_CAMERA_ICON = "isCameraIcon";

    public static ArrayList<PhotoGridItem> QueryALLPhoto(Context context) {
        Map<String, Integer> sectionMap = new HashMap<String, Integer>();
        ArrayList<PhotoGridItem> result = new ArrayList<PhotoGridItem>();
        int section = 1;
        //需要查询的字段 date(date_added,'unixepoch','localtime')
        String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.ImageColumns._ID};
        //查询条件
        String selection = MediaStore.Images.Media.DATA + " like '%" + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "%'"; //+ "%' or " + MediaStore.Images.Media.DATA + " like '%DCIM%' ";
        //排序语句
        String sortString = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortString);

        boolean isAddCamera = false;
        String curdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

        if (cursor != null) {
            cursor.getCount();
            while (cursor.moveToNext()) {
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                if (!new File(path).exists()) {
                    continue;
                }
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String time = DateUtil.getDate(date);
                PhotoGridItem item = new PhotoGridItem(path, time, id);

                //在下一天之前添加相机
               /* if (time.compareTo(curdate) < 0 && !isAddCamera) {
                    PhotoGridItem cameraItem = new PhotoGridItem(IS_CAMERA_ICON, curdate, null);
                    result.add(cameraItem);
                    isAddCamera = true;
                }*/
                result.add(item);
            }
            cursor.close();
        }

        //只有今天的
        /*if (!isAddCamera) {
            PhotoGridItem cameraItem = new PhotoGridItem(IS_CAMERA_ICON, curdate, null);
            result.add(cameraItem);
            isAddCamera = true;
        }*/

        for (ListIterator<PhotoGridItem> it = result.listIterator(); it.hasNext(); ) {
            PhotoGridItem mGridItem = it.next();
            String ym = mGridItem.getTime();
            if (!sectionMap.containsKey(ym)) {
                mGridItem.setSection(section);
                sectionMap.put(ym, section);
                section++;
            } else {
                mGridItem.setSection(sectionMap.get(ym));
            }
        }
        return result;
    }


    public static ArrayList<PhotoGridItem> QueryPhotoByBUCKET_ID(Context context, int bucket_id) {
        Map<String, Integer> sectionMap = new HashMap<String, Integer>();
        ArrayList<PhotoGridItem> result = new ArrayList<PhotoGridItem>();
        int section = 1;
        //需要查询的字段 date(date_added,'unixepoch','localtime')
        String[] projection = new String[]{MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.ImageColumns._ID};
        //查询条件
        String selection = MediaStore.Images.Media.BUCKET_ID + " = " + bucket_id;
        //排序语句
        String sortString = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortString);

        boolean isAddCamera = false;
        String curdate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));

        if (cursor != null) {
            cursor.getCount();
            while (cursor.moveToNext()) {
                long date = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_MODIFIED));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                if (!new File(path).exists()) {
                    continue;
                }
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID));
                String time = DateUtil.getDate(date);
                PhotoGridItem item = new PhotoGridItem(path, time, id);

                //在下一天之前添加相机
                /*if (time.compareTo(curdate) < 0 && !isAddCamera) {
                    PhotoGridItem cameraItem = new PhotoGridItem(IS_CAMERA_ICON, curdate, null);
                    result.add(cameraItem);
                    isAddCamera = true;
                }*/
                result.add(item);
            }
            cursor.close();
        }

        //只有今天的
        /*if (!isAddCamera) {
            PhotoGridItem cameraItem = new PhotoGridItem(IS_CAMERA_ICON, curdate, null);
            result.add(cameraItem);
            isAddCamera = true;
        }*/

        for (ListIterator<PhotoGridItem> it = result.listIterator(); it.hasNext(); ) {
            PhotoGridItem mGridItem = it.next();
            String ym = mGridItem.getTime();
            if (!sectionMap.containsKey(ym)) {
                mGridItem.setSection(section);
                sectionMap.put(ym, section);
                section++;
            } else {
                mGridItem.setSection(sectionMap.get(ym));
            }
        }
        return result;
    }


    public static ArrayList<GalleryEntity> QueryALLGalleryList(Context context) {
        ArrayList<GalleryEntity> galleryList = new ArrayList<GalleryEntity>();

        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME, "COUNT(1) AS count"};
        String selection = "0==0) GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED;

        Cursor cur = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortOrder);
        if (cur != null) {
            if (cur.moveToFirst()) {
                int id_column = cur.getColumnIndex(MediaStore.Images.Media._ID);
                int image_id_column = cur.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucket_id_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                int bucket_name_column = cur.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                int count_column = cur.getColumnIndex("count");

                do {
                    int id = cur.getInt(id_column);
                    String image_path = cur.getString(image_id_column);
                    int bucket_id = cur.getInt(bucket_id_column);
                    String bucket_name = cur.getString(bucket_name_column);
                    if (bucket_name.contains(PuTaoConstants.PAIAPI_PHOTOS_FOLDER)) {
                        bucket_name = "葡萄相册";
                    }
                    if ("watermark".equalsIgnoreCase(bucket_name))
                        continue;
                    int count = cur.getInt(count_column);
                    GalleryEntity gallery = new GalleryEntity();
                    gallery.setId(id);
                    gallery.setImage_path(image_path);
                    gallery.setBucket_id(bucket_id);
                    gallery.setBucket_name(bucket_name);
                    gallery.setCount(count);
                    galleryList.add(gallery);
                } while (cur.moveToNext());
            }
            cur.close();
        }

        return galleryList;
    }
}
