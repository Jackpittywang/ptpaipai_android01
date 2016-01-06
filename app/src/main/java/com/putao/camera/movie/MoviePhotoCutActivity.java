package com.putao.camera.movie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;
import com.putao.camera.base.BaseActivity;
import com.putao.camera.constants.PuTaoConstants;
import com.putao.camera.event.BasePostEvent;
import com.putao.camera.event.EventBus;
import com.putao.camera.util.ActivityHelper;
import com.putao.camera.util.BitmapHelper;
import com.putao.camera.util.CommonUtils;
import com.putao.camera.util.DisplayHelper;
import com.putao.camera.util.StringHelper;
import com.putao.widget.cropper.CropImageView;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by jidongdong on 15/3/16.
 */
public class MoviePhotoCutActivity extends BaseActivity implements View.OnClickListener {
    private CropImageView crop_image_view;
    private Bitmap originImageBitmap;
    private Button back_btn;
    private TextView title_tv;
    private Button right_btn;

    @Override
    public int doGetContentViewId() {
        return R.layout.activity_movie_editor_cut;
    }

    @Override
    public void doInitSubViews(View view) {

        title_tv = queryViewById(R.id.title_tv);
        title_tv.setText("照片裁切");

        right_btn = queryViewById(R.id.right_btn);
        right_btn.setText("下一步");

        crop_image_view = queryViewById(R.id.crop_image_view);

        back_btn = queryViewById(R.id.back_btn);

        crop_image_view.setFixedAspectRatio(true);
        crop_image_view.setAspectRatio(16, 9);

        addOnClickListener(back_btn, right_btn);
        EventBus.getEventBus().register(this);

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

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.right_btn:
                ProgressDialog pg;
                pg = new ProgressDialog(mContext);
                pg.setMessage("正在加载中...");
                pg.show();
                try {
                    File file = CommonUtils.getOutputMediaFile();
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    Bitmap corpImage = crop_image_view.getCroppedImage();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    corpImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bos.write(baos.toByteArray());
                    bos.flush();
                    fos.getFD().sync();
                    bos.close();

                    final String[] SCAN_TYPES = {"image/jpeg"};
                    MediaScannerConnection.scanFile(mContext, new String[]{file.getPath()}, SCAN_TYPES, null);
                    Bundle bundle = new Bundle();
                    bundle.putString("photo_data", file.getPath());
                    ActivityHelper.startActivity(mActivity, MovieMakeActivity.class, bundle);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    pg.dismiss();
                }
                break;
        }
    }

    public void onEvent(BasePostEvent event) {
        switch (event.eventCode) {
            case PuTaoConstants.FINISH_TO_MOVIE_MAKE_PAGE:
                finish();
                break;
            case PuTaoConstants.FINISH_TO_MENU_PAGE:
                finish();
                break;
        }
    }


}
