
package com.putao.camera.setting.watermark.management;

import com.putao.camera.base.BaseItem;

import java.util.ArrayList;

/**
 * Created by yanglun on 15/4/7.
 */
public class CollageListInfo extends BaseItem {
    public ArrayList<PackageInfo> list;

    static public class PackageInfo extends BaseItem {
        public String collage_id;
        public String attachment_name;
        public int is_new;
        public String sample_image;
        public String attachment_url;
    }
}
