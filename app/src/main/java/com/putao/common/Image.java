package com.putao.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by guchenkai on 2016/1/5.
 */
public class Image implements Serializable {
    private List<String> imageName;

    public List<String> getImageName() {
        return imageName;
    }

    public void setImageName(List<String> imageName) {
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageName=" + imageName +
                '}';
    }
}
