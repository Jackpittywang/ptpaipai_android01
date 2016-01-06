package com.putao.camera.editor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.putao.widget.cropper.CropImageView;
import com.nineoldandroids.animation.ObjectAnimator;
import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.StringHelper;

/**
 * Created by yanglun on 15/3/11.
 */
public class PhotoEditorCutActivity extends BaseActivity implements View.OnClickListener {

    private FrameLayout photo_area_rl;
    private LinearLayout opt_button_bar, opt_button_bar2, btn_cut_image, mark_content;
    private ViewGroup title_bar_rl, option_bars;
    private CropImageView crop_image_view;
    private Bitmap originImageBitmap;
    private Button cut_freedom_btn, cut_1_1_btn, cut_3_4_btn, cut_4_3_btn, cut_9_16_btn, cut_16_9_btn;
    private Button edit_button_cancel, edit_button_save;


    @Override
    public int doGetContentViewId() {
        return R.layout.activity_photo_editor_cut;
    }

    @Override
    public void doInitSubViews(View view) {
        photo_area_rl = (FrameLayout) findViewById(R.id.photo_area_rl);
        title_bar_rl = (ViewGroup) findViewById(R.id.title_bar_rl);
        opt_button_bar = (LinearLayout) findViewById(R.id.opt_button_bar);
        opt_button_bar2 = (LinearLayout) findViewById(R.id.opt_button_bar2);
        option_bars = (ViewGroup) findViewById(R.id.option_bars);
        btn_cut_image = (LinearLayout) findViewById(R.id.btn_cut_image);
        mark_content = (LinearLayout) findViewById(R.id.mark_content);
        crop_image_view = (CropImageView) findViewById(R.id.crop_image_view);
        cut_freedom_btn = (Button) findViewById(R.id.cut_freedom_btn);
        cut_1_1_btn = (Button) findViewById(R.id.cut_1_1_btn);
        cut_3_4_btn = (Button) findViewById(R.id.cut_3_4_btn);
        cut_4_3_btn = (Button) findViewById(R.id.cut_4_3_btn);
        cut_9_16_btn = (Button) findViewById(R.id.cut_9_16_btn);
        cut_16_9_btn = (Button) findViewById(R.id.cut_16_9_btn);
        edit_button_cancel = (Button) findViewById(R.id.edit_button_cancel);
        edit_button_save = (Button) findViewById(R.id.edit_button_save);
        addOnClickListener(btn_cut_image, cut_freedom_btn, cut_1_1_btn, cut_3_4_btn, cut_4_3_btn, cut_9_16_btn, cut_16_9_btn,
                edit_button_cancel,
                edit_button_save
        );
    }

    @Override
    public void doInitData() {
        Intent intent = this.getIntent();
        String photo_data = intent.getStringExtra("photo_data");
        if (!StringHelper.isEmpty(photo_data)) {
            originImageBitmap = BitmapHelper.getInstance().getBitmapFromPathWithSize(photo_data, DisplayHelper.getScreenWidth(),
                    DisplayHelper.getScreenHeight());
            crop_image_view.setImageBitmap(originImageBitmap);
        }
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                hideTitleAni();
                showWaterMarkContent();

            }
        };
        mHandler.sendEmptyMessageDelayed(0, 100);
    }

    void hideTitleAni() {
        photo_area_rl.setLayoutParams(new RelativeLayout.LayoutParams(photo_area_rl.getWidth(), photo_area_rl.getHeight()));
        title_bar_rl.setLayoutParams(new RelativeLayout.LayoutParams(title_bar_rl.getWidth(), title_bar_rl.getHeight()));
        opt_button_bar.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        opt_button_bar2.setLayoutParams(new RelativeLayout.LayoutParams(option_bars.getWidth(), option_bars.getHeight()));
        ObjectAnimator.ofFloat(opt_button_bar2, "translationY", 0, option_bars.getHeight()).setDuration(10).start();
        opt_button_bar2.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(title_bar_rl, "translationY", 0, -(title_bar_rl.getHeight())).setDuration(500).start();
        ObjectAnimator.ofFloat(photo_area_rl, "translationY", title_bar_rl.getHeight(), 0).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar, "translationY", 0, option_bars.getHeight()).setDuration(500).start();
        ObjectAnimator.ofFloat(opt_button_bar2, "translationY", -option_bars.getHeight(), 0).setDuration(500).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cut_image:
                break;
            case R.id.cut_freedom_btn:
                crop_image_view.setFixedAspectRatio(false);
                crop_image_view.setAspectRatio(1, 1);

                break;
            case R.id.cut_1_1_btn:
                crop_image_view.setFixedAspectRatio(true);
                crop_image_view.setAspectRatio(1, 1);
                break;
            case R.id.cut_3_4_btn:
                crop_image_view.setFixedAspectRatio(true);
                crop_image_view.setAspectRatio(3, 4);
                break;
            case R.id.cut_4_3_btn:
                crop_image_view.setFixedAspectRatio(true);
                crop_image_view.setAspectRatio(4, 3);
                break;
            case R.id.cut_9_16_btn:
                crop_image_view.setFixedAspectRatio(true);
                crop_image_view.setAspectRatio(9, 16);
                break;
            case R.id.cut_16_9_btn:
                crop_image_view.setFixedAspectRatio(true);
                crop_image_view.setAspectRatio(16, 9);
                break;

            case R.id.edit_button_cancel:
                this.finish();
                break;
            case R.id.edit_button_save:
                Bitmap corpImage = crop_image_view.getCroppedImage();
//                crop_image_view.getActualCropRect()
//                crop_image_view.
                Bundle bundle = new Bundle();
                bundle.putParcelable("corpImage", corpImage);
                EventBus.getEventBus().post(new BasePostEvent(PuTaoConstants.PHOTO_EDIT_CUT_FINISH, bundle));

                this.finish();
                break;


        }
    }

    private void showWaterMarkContent() {
        mark_content.setVisibility(View.VISIBLE);
    }

}
