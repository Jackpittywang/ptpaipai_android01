package com.putao.common;

import java.io.Serializable;

/**
 * Created by guchenkai on 2016/1/5.
 */
public class Animation implements Serializable {
    private String icon;
    private Location eye;
    private Location mouth;
    private Location bottom;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Location getEye() {
        return eye;
    }

    public void setEye(Location eye) {
        this.eye = eye;
    }

    public Location getMouth() {
        return mouth;
    }

    public void setMouth(Location mouth) {
        this.mouth = mouth;
    }

    public Location getBottom() {
        return bottom;
    }

    public void setBottom(Location bottom) {
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "Animation{" +
                "icon='" + icon + '\'' +
                ", eye=" + eye +
                ", mouth=" + mouth +
                ", bottom=" + bottom +
                '}';
    }

}
