
package com.putao.camera.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;

import com.putao.camera.application.MainApplication;
import com.putao.camera.bean.PhotoInfo;
import com.putao.camera.constants.PuTaoConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class PhotoLoaderHelper {
    private static PhotoLoaderHelper instance;
    private Map<String, ArrayList<PhotoInfo>> mPhotoMapCategoryByDate;
    private Activity mActivity;

    PhotoLoaderHelper(Activity activity) {
        mPhotoMapCategoryByDate = new TreeMap<String, ArrayList<PhotoInfo>>(new Comparator<String>() {
            @Override
            public int compare(String obj1, String obj2) {
                return obj2.compareTo(obj1);
            }
        });
        mActivity = activity;
    }

    public static PhotoLoaderHelper getInstance(Activity activity) {
        if (instance == null) {
            instance = new PhotoLoaderHelper(activity);
            instance.queryAllPhoto();
        }
        return instance;
    }

    public Map<String, ArrayList<PhotoInfo>> getPhotoMapCategoryByDate(boolean bRefresh) {
        if (bRefresh == true) {
            queryAllPhoto();
        }
        return mPhotoMapCategoryByDate;
    }

    public Map<String, ArrayList<PhotoInfo>> getPhotoMapCategoryByDate(boolean bRefresh, int bucket_id) {
        if (bRefresh == true) {
            queryPhotoByBUCKET_ID(bucket_id);
        }
        return mPhotoMapCategoryByDate;
    }

    public ArrayList<String> getPhotoPathArray() {
        ArrayList<String> items = new ArrayList<String>();
        if (mPhotoMapCategoryByDate != null) {
            for (String key : mPhotoMapCategoryByDate.keySet()) {
                ArrayList<PhotoInfo> photoInfoArray = mPhotoMapCategoryByDate.get(key);
                Iterator<PhotoInfo> it = photoInfoArray.iterator();
                while (it.hasNext()) {
                    items.add(it.next()._DATA);
                }
            }
        }
        return items;
    }

    public ArrayList<PhotoInfo> getPhotoInfoArray() {
        ArrayList<PhotoInfo> items = new ArrayList<PhotoInfo>();
        if (mPhotoMapCategoryByDate != null) {
            for (String key : mPhotoMapCategoryByDate.keySet()) {
                ArrayList<PhotoInfo> photoInfoArray = mPhotoMapCategoryByDate.get(key);
                for (int i = photoInfoArray.size(); i > 0; i--) {
                    items.add(photoInfoArray.get(i - 1));
                }
            }
        }
        return items;
    }

    private ArrayList<PhotoInfo> getPhotoMapByDateKey(String dateKey) {
        if (mPhotoMapCategoryByDate != null) {
            ArrayList<PhotoInfo> array = mPhotoMapCategoryByDate.get(dateKey);
            if (array != null) {
                return array;
            } else {
                array = new ArrayList<PhotoInfo>();
                mPhotoMapCategoryByDate.put(dateKey, array);
                return array;
            }
        }
        return null;
    }

    /**
     * 获取最近的一张照片信息
     * by jdd
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public PhotoInfo getLastPhotoInfo() {
        PhotoInfo info = new PhotoInfo();
        String condition = MediaColumns.DATA + " like '%/" + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "/%' ";
        String[] projection = {BaseColumns._ID, MediaColumns.DATA, MediaColumns.DATE_ADDED, MediaColumns.SIZE, MediaColumns.TITLE,
                MediaColumns.MIME_TYPE};
        String sortString = MediaStore.Images.ImageColumns.DATE_MODIFIED + " desc";
        Cursor cursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, condition, null, sortString);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            info._ID = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
            info._DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaColumns.DATE_ADDED));
            info._SIZE = cursor.getString(cursor.getColumnIndex(MediaColumns.SIZE));
            info._TITLE = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
            info._MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE));
            info._DATA = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));

        }
        return info;
    }

    private void queryPhotoByBUCKET_ID(int bucket_id) {

        try {
            String[] projection = {BaseColumns._ID, MediaColumns.DATA, MediaColumns.DATE_ADDED, MediaColumns.SIZE, MediaColumns.TITLE, MediaColumns.MIME_TYPE,
                    MediaStore.Images.ImageColumns.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_ID};
            //查询条件
            String selection = MediaStore.Images.Media.BUCKET_ID + " = " + bucket_id;
            //排序语句
            String sortString = MediaStore.Images.ImageColumns.DATE_MODIFIED + " desc";
            @SuppressWarnings("deprecation")
            Cursor cursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, selection, null, sortString);

            if (cursor != null) {
                mPhotoMapCategoryByDate.clear();
                boolean isDataPresent = cursor.moveToFirst();
                if (isDataPresent) {
                    do {
                        String _ID = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                        long _ID_LONG = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                        String _DATA = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
                        String _DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaColumns.DATE_ADDED));
                        String _SIZE = cursor.getString(cursor.getColumnIndex(MediaColumns.SIZE));
                        String _TITLE = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
                        String _MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE));
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo._ID = _ID;
                        photoInfo._DATE_ADDED = _DATE_ADDED;
                        photoInfo._SIZE = _SIZE;
                        photoInfo._TITLE = _TITLE;
                        photoInfo._MIME_TYPE = _MIME_TYPE;
                        photoInfo._DATA = _DATA;
                        photoInfo._ID_LONG = _ID_LONG;
                        String a = CommonUtils.parseTime(photoInfo.getDate_Added(), "yyyy-MM-dd");
                        ArrayList<PhotoInfo> array = getPhotoMapByDateKey(a);
                        array.add(photoInfo);
                    }
                    while (cursor.moveToNext());
                }
                if (cursor != null) {
                    mActivity.stopManagingCursor(cursor);
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void queryAllPhoto() {
        //        String condition = MediaColumns.DATA + " like '%/"+PuTaoConstants.PAIAPI_PHOTOS_FOLDER+"/%' ";
        String[] projection = {BaseColumns._ID, MediaColumns.DATA, MediaColumns.DATE_ADDED, MediaColumns.SIZE, MediaColumns.TITLE,
                MediaColumns.MIME_TYPE};
        //        Vector<String> additionalFiles = null;
        try {
            //            if (additionalFiles == null)
            //            {
            //                additionalFiles = new Vector<String>();
            //            }
//            String[] selectionArgs = new String[] { "%" + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "%", "%DCIM%" };
            String[] selectionArgs = new String[]{"%" + PuTaoConstants.PAIAPI_PHOTOS_FOLDER + "%"};
            Cursor cursor = mActivity.managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.DATA
                    + " like ?  or " + MediaStore.Images.Media.DATA + " like ?  ", selectionArgs, null);
            if (cursor != null) {
                mPhotoMapCategoryByDate.clear();
                boolean isDataPresent = cursor.moveToFirst();
                if (isDataPresent) {
                    do {
                        String _ID = cursor.getString(cursor.getColumnIndex(BaseColumns._ID));
                        long _ID_LONG = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                        String _DATA = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA));
                        String _DATE_ADDED = cursor.getString(cursor.getColumnIndex(MediaColumns.DATE_ADDED));
                        String _SIZE = cursor.getString(cursor.getColumnIndex(MediaColumns.SIZE));
                        String _TITLE = cursor.getString(cursor.getColumnIndex(MediaColumns.TITLE));
                        String _MIME_TYPE = cursor.getString(cursor.getColumnIndex(MediaColumns.MIME_TYPE));
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo._ID = _ID;
                        photoInfo._DATE_ADDED = _DATE_ADDED;
                        photoInfo._SIZE = _SIZE;
                        photoInfo._TITLE = _TITLE;
                        photoInfo._MIME_TYPE = _MIME_TYPE;
                        photoInfo._DATA = _DATA;
                        photoInfo._ID_LONG = _ID_LONG;
                        String a = CommonUtils.parseTime(photoInfo.getDate_Added(), "yyyy-MM-dd");
                        ArrayList<PhotoInfo> array = getPhotoMapByDateKey(a);
                        array.add(photoInfo);
                    }
                    while (cursor.moveToNext());
                }
                if (cursor != null) {
                    mActivity.stopManagingCursor(cursor);
                    cursor.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param ID
     * @return
     */
    public static Bitmap getThumbnailLocalBitmap(String ID) {
        if (!StringHelper.isEmpty(ID)) {
            ContentResolver cr = MainApplication.getInstance().getContentResolver();
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, Long.valueOf(ID), MediaStore.Images.Thumbnails.MINI_KIND, null);
            return bitmap;
        }
        return null;
    }

    //    public static String getLocalThumbnailPath(String ID)
    //    {
    //        if (!StringHelper.isEmpty(ID))
    //        {
    //            ContentResolver cr = MainApplication.getInstance().getContentResolver();
    //            Cursor cursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(cr, Long.valueOf(ID).longValue(),
    //                    MediaStore.Images.Thumbnails.MICRO_KIND, null);
    //            if (cursor != null)
    //            {
    //                if (cursor.getCount() > 0)
    //                {
    //                    cursor.moveToFirst();
    //                    String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.THUMB_DATA));
    //                    cursor.close();
    //                    return "file://" + uri;
    //                }
    //                else
    //                {
    //                    cursor.close();
    //                }
    //            }
    //        }
    //        return null;
    //    }
    public static String getThumbnailPathForLocalFile(long origId) {
        Cursor thumbCursor = null;
        try {
            String[] PROJECTION = new String[]{MediaColumns._ID, MediaColumns.DATA};
            //            
            //            IMAGE_ID + " = " + origId + " AND " + KIND + " = " +
            //                    kind;
            String column = "image_id=";
            Uri baseUri = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
            ContentResolver cr = MainApplication.getInstance().getContentResolver();
            thumbCursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, PROJECTION, column + origId, null, null);
            //            thumbCursor = MediaStore.Images.Thumbnails.queryMiniThumbnail(MainApplication.getInstance().getContentResolver(), fileId,  MediaStore.Images.Thumbnails.MINI_KIND, null);
            //            thumbCursor = MediaStore.Images.Thumbnails.queryMiniThumbnails(MainApplication.getInstance().getContentResolver(),
            //                    MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (thumbCursor.moveToFirst()) {
                // the path is stored in the DATA column
                int dataIndex = thumbCursor.getColumnIndexOrThrow(MediaColumns.DATA);
                String thumbnailPath = thumbCursor.getString(dataIndex);
                return "file://" + thumbnailPath;
            } else {
                Uri uri = Uri.parse(baseUri.buildUpon().appendPath(String.valueOf(origId)).toString().replaceFirst("thumbnails", "media"));
                if (thumbCursor != null)
                    thumbCursor.close();
                thumbCursor = cr.query(uri, PROJECTION, null, null, null);
                if (thumbCursor.moveToFirst()) {
                    // the path is stored in the DATA column
                    int dataIndex = thumbCursor.getColumnIndexOrThrow(MediaColumns.DATA);
                    String thumbnailPath = thumbCursor.getString(dataIndex);
                    return "file://" + thumbnailPath;
                }
            }
        } finally {
            if (thumbCursor != null) {
                thumbCursor.close();
            }
        }
        return null;
    }

    /**
     * @param ID
     * @return
     */
    public static int DeleteFileFromDB(String ID) {
        if (!StringHelper.isEmpty(ID)) {
            ContentResolver cr = MainApplication.getInstance().getContentResolver();
            return cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + ID, null);
        }
        return -1;
    }

    /**
     * @param url
     * @return
     */
    public static Bitmap getLocalBitmap(String url, int inSampleSize) {
        //        Loger.i(url);
        File file = new File(url);
        if (file != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //            options.inPurgeable = true;
            options.inSampleSize = inSampleSize;
            options.inPreferredConfig = Config.RGB_565;
            return BitmapFactory.decodeFile(url, options);
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


}