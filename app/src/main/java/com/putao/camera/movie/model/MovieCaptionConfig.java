package com.putao.camera.movie.model;

import com.putao.camera.base.BaseItem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jidongdong on 15/3/17.
 */
public class MovieCaptionConfig extends BaseItem {
    public String version;
    public ArrayList<MovieCaptionItem> movieLines;

    public class MovieCaptionItem implements Serializable {
        public String cn_line;
        public String en_line;
    }
}
