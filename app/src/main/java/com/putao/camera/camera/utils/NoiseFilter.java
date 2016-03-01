package com.putao.camera.camera.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * NoiseFiltere noise = new NoiseFilter(5);
 * x = noise.dataFilter(x);
 * y = noise.dataFilter(y);
 * scale = noise.dataFilter(scale);
 * 
 * @author yanguoqiang
 *
 */
public class NoiseFilter {
	
	private int dataLength = 0;
	private List<Float> dataArray = new ArrayList<Float> ();
	private float curData = 0;
	
	public NoiseFilter(int length){
		dataLength = length;
	}
	
	public float dataFilter(float data){
		dataArray.add(data);
		if(dataArray.size()<2) return data;
		if(dataArray.size()>dataLength) dataArray.remove(0);
		curData = getAverage(dataArray);
		return curData;
	}
	
	private float getAverage(List<Float> dataArr){
		if(dataArr.size()==0) return 0;
		float total = 0;
		for(int i = 0; i<dataArr.size(); i++){
			total = total + dataArr.get(i);
		}
		return total/dataArr.size();
	}

	public float getData(){
		return curData;
	}

	public void clearData(){
		dataArray.clear();
	}
}
