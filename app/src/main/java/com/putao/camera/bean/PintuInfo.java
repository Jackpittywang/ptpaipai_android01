package com.putao.camera.bean;

import com.putao.ahibernate.annotation.Column;
import com.putao.ahibernate.annotation.Id;
import com.putao.ahibernate.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

@Table(name = "PintuInfo")
public class PintuInfo implements Serializable {
    private static final long serialVersionUID = -7971488953798453388L;

    @Id
    private Long _id;

    @Column(name = "width")
    public String width;

    @Column(name = "height")
    public String height;


    public ArrayList<MaskList> datas = new ArrayList<MaskList>();


    @Table(name = "MaskList")
    static public class MaskList implements Serializable {
        private static final long serialVersionUID = -7971488953798453288L;

        @Id
        private Long _id;

        @Column(name = "imageName")
        public String imageName;

        @Column(name = "maskGson")
        public String maskGson;

        public ArrayList<MaskInfo> mask = new ArrayList<MaskInfo>();
    }

    public class MaskInfo implements Serializable {
        private static final long serialVersionUID = 1L;
        public ArrayList<MaskInfo> point;
    }




    public PintuInfo() {

    }

}
