package com.putao.camera.camera.model;


import java.io.Serializable;

/**
 * Created by yanguoqiang on 2016/1/5.
 */
public class AnimationModel implements Serializable {
    private String icon;
    private Location eye;
    private Location mouth;
    private Location bottom;
    private int distance;
    private int centerX;
    private int centerY;
    private float duration;
    private long durationLong;

    public int getAnimationImageSize() {
        return animationImageSize;
    }

    public void setAnimationImageSize(int animationImageSize) {
        this.animationImageSize = animationImageSize;
    }

    private int animationImageSize;

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

    public long getDurationLong(){
        return durationLong;
    }

    public void setDuration(float duration) {
        this.duration = duration;
        durationLong = (long) (duration*1000);
    }

    public float getDuration() {
        return duration;
    }


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
