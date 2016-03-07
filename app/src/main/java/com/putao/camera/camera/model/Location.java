package com.putao.camera.camera.model;

import java.util.List;

public class Location {
	private int width;
	private int height;
	private int distance;
	private int centerX;
	private int centerY;
	private float duration;
	// xml 里面imagelist对应的字段，是个字符串
	private ImageList imageList;
	private List<String> imageNameList;
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
	public void setImageList(ImageList imageList){
		this.imageList = imageList;
	}

	public List<String> getImageList(){
		return imageList.getImageName();
	}

	public void setDuration(float duration) {
		this.duration = duration;
		durationLong = (long) (this.duration*1000);
	}
	
	public long getDurationLong(){
		return durationLong;
	}



	
}
