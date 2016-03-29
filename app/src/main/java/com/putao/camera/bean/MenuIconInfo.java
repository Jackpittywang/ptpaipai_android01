package com.putao.camera.bean;

import com.putao.camera.base.BaseItem;

/**
 * Created by Administrator on 2016/3/28.
 */
public class MenuIconInfo extends BaseItem {
    public PackageInfo data;

    static public class PackageInfo extends BaseItem {
        public String app_name;
        public String app_icon;
        public String h5_link_url;
        public String ios_link_url;
        public String android_link_url;
        public String bg_name;
        public String bg_url;
        public int core_app_id;
    }



}
