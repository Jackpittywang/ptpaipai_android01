
package com.putao.camera.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.putao.camera.R;

public class CircleControlPanelView extends RelativeLayout {
    private Context mContext;
    public Button take_photo_btn;
    public RedPointBaseButton jigsaw_photo_btn;
    public RedPointBaseButton voice_photo_btn;
    public RedPointBaseButton water_mark_spring_photo_btn;

    public CircleControlPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.layout_circle_control_panel, this);
        take_photo_btn = (Button) this.findViewById(R.id.take_photo_btn);
        jigsaw_photo_btn = (RedPointBaseButton) this.findViewById(R.id.jigsaw_photo_btn);
        voice_photo_btn = (RedPointBaseButton) this.findViewById(R.id.voice_photo_btn);
        water_mark_spring_photo_btn = (RedPointBaseButton) this.findViewById(R.id.water_mark_spring_photo_btn);
    }
}
