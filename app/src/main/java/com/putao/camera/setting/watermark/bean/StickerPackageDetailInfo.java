
package com.putao.camera.setting.watermark.bean;

import com.putao.camera.base.BaseItem;

import java.util.ArrayList;

public class StickerPackageDetailInfo extends BaseItem {
    public PackageInfo data;
    static public class PackageInfo extends BaseItem {
        public int  id;
        public String name;
        public String icon;
        public String download_url;
        public String description;
        public String  size;
        public int num;
        public String banner_pic;
        public String cover_pic;
        public int release_time;
        public int is_new;
        public ArrayList<String> thumbnail_list;
    }


   /* public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIs_new() {
        return is_new;
    }

    public void setIs_new(int is_new) {
        this.is_new = is_new;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getBanner_pic() {
        return banner_pic;
    }

    public void setBanner_pic(String banner_pic) {
        this.banner_pic = banner_pic;
    }

    public String getCover_pic() {
        return cover_pic;
    }

    public void setCover_pic(String cover_pic) {
        this.cover_pic = cover_pic;
    }

    public int getRelease_time() {
        return release_time;
    }

    public void setRelease_time(int release_time) {
        this.release_time = release_time;
    }

    public ArrayList<String> getThumbnail_list() {
        return thumbnail_list;
    }

    public void setThumbnail_list(ArrayList<String> thumbnail_list) {
        this.thumbnail_list = thumbnail_list;
    }

    @Override
    public String toString() {
        return "StickerPackageDetailInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                ", download_url='" + download_url + '\'' +
                ", description='" + description + '\'' +
                ", size='" + size + '\'' +
                ", num=" + num +
                ", banner_pic='" + banner_pic + '\'' +
                ", cover_pic='" + cover_pic + '\'' +
                ", release_time=" + release_time +
                ", thumbnail_list=" + thumbnail_list +
                '}';
    }*/
}
