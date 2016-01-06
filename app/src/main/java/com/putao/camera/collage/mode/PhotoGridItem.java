package com.putao.camera.collage.mode;

import com.putao.camera.base.BaseItem;

/**
 * Created by jidongdong on 15/2/4.
 */
public class PhotoGridItem extends BaseItem {
    private String path;
    private String time;
    private String id;
    private boolean selected;
    private int section;

    public PhotoGridItem(String path, String time, String id) {
        super();
        this.path = path;
        this.time = time;
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSection() {
        return section;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public String getId() {
        return id;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
