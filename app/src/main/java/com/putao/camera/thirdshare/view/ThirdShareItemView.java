
package com.putao.camera.thirdshare.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.putao.camera.R;


public class ThirdShareItemView extends LinearLayout {
    private ImageView third_icon_iv;
    private TextView third_title_tv;

    public ThirdShareItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_third_share_item, this, true);
        third_icon_iv = (ImageView) this.findViewById(R.id.third_icon_iv);
        third_title_tv = (TextView) this.findViewById(R.id.third_title_tv);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom);
        third_icon_iv.setBackgroundResource(a.getResourceId(R.styleable.custom_item_background, R.drawable.third_share1_icon));
        third_title_tv.setText(a.getString(R.styleable.custom_text));
    }
}
