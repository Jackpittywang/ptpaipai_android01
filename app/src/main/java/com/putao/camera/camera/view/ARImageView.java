package com.putao.camera.camera.view;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.putao.camera.R;
import com.putao.camera.util.BitmapHelper;

/**
 * Created by yanguoqiang on 16/2/18.
 */
public class ARImageView extends RelativeLayout {

    // 高度宽度写死了 80dp
    private ImageView img_ar_icon;
    private ImageView img_ar_checked;

    public ARImageView(Context context) {
        super(context);

        initComponent(context);
    }

    public void setData(String imagePath) {
        img_ar_icon.setImageBitmap(BitmapHelper.getBitmapFromPath(imagePath));
    }
    public void setDataFromInternt(String imagePath) {
        img_ar_icon.setImageURI(Uri.parse(imagePath));
    }

    private void initComponent(Context context) {
        RelativeLayout.inflate(context, R.layout.layout_ar_sticker, this);

        img_ar_icon = (ImageView) this.findViewById(R.id.img_ar_icon);
        img_ar_checked = (ImageView) this.findViewById(R.id.img_ar_checked);
    }

    public void setChecked(boolean flag) {
        if (flag) img_ar_checked.setVisibility(View.VISIBLE);
        else img_ar_checked.setVisibility(View.INVISIBLE);
    }




}
