
package com.putao.camera.setting.watermark.management;

import com.putao.camera.base.BaseItem;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/7.
 */
public class StickerListInfo extends BaseItem {
    public ArrayList<PackageInfo> data;

    static public class PackageInfo extends BaseItem {
        public int  id;
        public String name;
        public String download_url;
        public String  size;
        public int num;
        public String icon;
        public String cover_pic;
        public int release_time;
        public int is_new;
    }
}


