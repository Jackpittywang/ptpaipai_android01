
package com.putao.camera.setting.watermark.management;

import com.putao.camera.base.BaseItem;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/7.
 */
public class WaterMarkPackageListInfo extends BaseItem {
    public ArrayList<PackageInfo> list;

    static public class PackageInfo extends BaseItem {
        public String wid;
        public String sample_image;
        public String category;
        public int totals;
        public String attachment_url;
        public String attachment_size;
        public int is_new;
        public String version;
    }
}


