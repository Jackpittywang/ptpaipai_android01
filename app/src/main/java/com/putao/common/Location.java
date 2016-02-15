package com.putao.common;

import java.io.Serializable;

/**
 * Created by guchenkai on 2016/1/5.
 */
public class Location implements Serializable {
    private int width;
    private int height;
    private int distance;
    private int centerX;
    private int centerY;
    private float duration;
    private Image imageList;
    private long durationLong = 0;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
        durationLong = (long) (this.duration*1000);
    }

    public long getDurationLong(){
        return durationLong;
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
                "width=" + width +
                ", height=" + height +
                ", distance=" + distance +
                ", centerX=" + centerX +
                ", centerY=" + centerY +
                ", duration=" + duration +
                ", imageList=" + imageList +
                '}';
    }

}
