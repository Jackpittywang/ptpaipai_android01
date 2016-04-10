package com.putao.camera.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class PintuInfo implements Serializable {
    public String width;
    public String height;
    public ArrayList<MaskList> maskList;


    public class MaskList implements Serializable {
        public String imageName;
        public ArrayList<Mask> mask;
    }

    public class Mask implements Serializable {
        public ArrayList<String> point;
    }
}
