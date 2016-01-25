package com.putao.common;

import java.io.Serializable;

/**
 * Created by guchenkai on 2016/1/5.
 */
public class Location implements Serializable {
    private String width;
    private String height;
    private String distance;
    private String centerX;
    private String centerY;
    private String duration;
    private Image imageList;

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCenterX() {
        return centerX;
    }

    public void setCenterX(String centerX) {
        this.centerX = centerX;
    }

    public String getCenterY() {
        return centerY;
    }

    public void setCenterY(String centerY) {
        this.centerY = centerY;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Image getImageList() {
        return imageList;
    }

    public void setImageList(Image imageList) {
        this.imageList = imageList;
    }

    @Override
    public String toString() {
        return "Location{" +
                "width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", distance='" + distance + '\'' +
                ", centerX='" + centerX + '\'' +
                ", centerY='" + centerY + '\'' +
                ", duration='" + duration + '\'' +
                ", imageList=" + imageList +
                '}';
    }
}
